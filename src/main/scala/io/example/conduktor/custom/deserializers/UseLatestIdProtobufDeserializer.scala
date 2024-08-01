package io.example.conduktor.custom.deserializers

import com.google.protobuf.Message
import com.typesafe.scalalogging.Logger
import io.confluent.kafka.schemaregistry.client.{CachedSchemaRegistryClient, SchemaMetadata, SchemaRegistryClient}
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import org.apache.kafka.common.serialization.Deserializer

import java.nio.ByteBuffer

class UseLatestIdProtobufDeserializer extends Deserializer[Message] {
  private val logger = Logger(getClass.getName)

  private val inner: KafkaProtobufDeserializer[Message] = new KafkaProtobufDeserializer[Message]
  private var client: SchemaRegistryClient = _
  private var subjectSuffix: String = _

  override def configure(configs: java.util.Map[String, _], isKey: Boolean): Unit = {
    inner.configure(configs, isKey)
    val url: String = configs.get("schema.registry.url").toString
    logger.info(s"Configuring Schema Registry URL $url")
    client = new CachedSchemaRegistryClient(url, 100, configs)
    subjectSuffix = if (isKey) "-key" else "-value"
  }

  override def deserialize(topic: String, bytes: Array[Byte]): Message = {
    try {
      val subject = topic + subjectSuffix
      val latestSchemaMetadata: SchemaMetadata = client.getLatestSchemaMetadata(subject)
      val id: Int = latestSchemaMetadata.getId
      logger.info(s"Got latest schema ID $id for subject $subject")
      val bytesWithId: Array[Byte] = ByteBuffer.allocate(bytes.length + 5).put(0.toByte).putInt(id).put(bytes).array
      inner.deserialize(topic, bytesWithId)
    } catch {
      case e: Exception =>
        throw new RuntimeException(e)
    }
  }
}


