package io.example.conduktor.custom.deserializers

import com.google.protobuf.{DynamicMessage, Message}
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaReference
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException
import io.confluent.kafka.schemaregistry.client.{SchemaMetadata, SchemaRegistryClient}
import io.confluent.kafka.schemaregistry.protobuf.{MessageIndexes, ProtobufSchema, ProtobufSchemaProvider}
import io.confluent.kafka.schemaregistry.{ParsedSchema, SchemaProvider}
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDe.{idSize, toKafkaException}
import io.confluent.kafka.serializers.protobuf.{AbstractKafkaProtobufDeserializer, ProtobufSchemaAndValue}
import org.apache.kafka.common.config.ConfigException
import org.apache.kafka.common.errors.{InvalidConfigurationException, SerializationException}

import java.io.{ByteArrayInputStream, IOException}
import java.lang.reflect.Method
import java.nio.ByteBuffer
import java.util
import java.util.{Collections, Optional}
import scala.jdk.CollectionConverters.SeqHasAsJava

final class GenericProto(schema: String) extends io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer[Message] {

  private val subjectNameFunction: Method =
    classOf[AbstractKafkaProtobufDeserializer[Message]].getDeclaredMethod(
      "subjectName",
      classOf[String],
      classOf[java.lang.Boolean],
      classOf[ProtobufSchema]
    )
  subjectNameFunction.setAccessible(true)

  private def subjectNameF(topic: String, isKey: Boolean, schemaFromRegistry: ProtobufSchema): String =
    subjectNameFunction.invoke(topic, isKey, schemaFromRegistry).asInstanceOf[String]

  private val schemaWithNameFunction: Method =
    classOf[AbstractKafkaProtobufDeserializer[Message]].getDeclaredMethod(
      "schemaWithName",
      classOf[ProtobufSchema],
      classOf[String]
    )
  schemaWithNameFunction.setAccessible(true)

  def schemaWithNameF(schema: ProtobufSchema, name: String): ProtobufSchema =
    schemaWithNameFunction.invoke(schema, name).asInstanceOf[ProtobufSchema]

  private val schemaForDeserializeFunction: Method =
    classOf[AbstractKafkaProtobufDeserializer[Message]].getDeclaredMethod(
      "schemaForDeserialize",
      Integer.TYPE,
      classOf[ProtobufSchema],
      classOf[String],
      classOf[java.lang.Boolean]
    )
  schemaForDeserializeFunction.setAccessible(true)

  def schemaForDeserializeF(id: Int, schemaFromRegistry: ProtobufSchema, subject: String, isKey: Boolean): ProtobufSchema =
    schemaForDeserializeFunction.invoke(id, schemaFromRegistry, subject, isKey).asInstanceOf[ProtobufSchema]

  private val deriveTypeFunction =
    classOf[AbstractKafkaProtobufDeserializer[Message]].getDeclaredMethod(
      "deriveType",
      classOf[ByteBuffer],
      classOf[ProtobufSchema]
    )
  deriveTypeFunction.setAccessible(true)

  def deriveTypeF(buffer: ByteBuffer, schema: ProtobufSchema): Any =
    deriveTypeFunction.invoke(buffer, schema)

  private val schemaVersionFunction =
    classOf[AbstractKafkaProtobufDeserializer[Message]].getDeclaredMethod(
      "schemaVersion",
      classOf[String],            // String topic
      classOf[java.lang.Boolean], // Boolean isKey
      Integer.TYPE,               // int id,
      classOf[String],            // String subject
      classOf[ProtobufSchema],    // ProtobufSchema schema
      classOf[Object]             // Object value
    )
  schemaVersionFunction.setAccessible(true)

  def schemaVersionF(topic: String, isKey: Boolean, id: Int, subject: String, schema: ProtobufSchema, value: Any): Int =
    schemaVersionFunction.invoke(topic, isKey, id, subject, schema, value).asInstanceOf[Int]

  private val schemaProvider: SchemaProvider = new ProtobufSchemaProvider
  private val parsedSchema: ProtobufSchema   =
    schemaProvider.parseSchema(schema, scala.List.empty.asJava, true).get().asInstanceOf[ProtobufSchema]

  private val srClient: SchemaRegistryClient =
    new SchemaRegistryClient {
      override def parseSchema(schemaType: String, schemaString: String, references: util.List[SchemaReference]): Optional[ParsedSchema] =
        ???

      override def register(subject: String, schema: ParsedSchema): Int = ???

      override def register(subject: String, schema: ParsedSchema, version: Int, id: Int): Int = ???

      override def getSchemaById(id: Int): ParsedSchema = ???

      override def getSchemaBySubjectAndId(subject: String, id: Int): ParsedSchema = parsedSchema

      override def getAllSubjectsById(id: Int): util.Collection[String] = ???

      override def getLatestSchemaMetadata(subject: String): SchemaMetadata = ???

      override def getSchemaMetadata(subject: String, version: Int): SchemaMetadata = ???

      override def getVersion(subject: String, schema: ParsedSchema): Int = ???

      override def getAllVersions(subject: String): util.List[Integer] = ???

      override def testCompatibility(subject: String, schema: ParsedSchema): Boolean = ???

      override def updateCompatibility(subject: String, compatibility: String): String = ???

      override def getCompatibility(subject: String): String = ???

      override def setMode(mode: String): String = ???

      override def setMode(mode: String, subject: String): String = ???

      override def getMode: String = ???

      override def getMode(subject: String): String = ???

      override def getAllSubjects: util.Collection[String] = ???

      override def getId(subject: String, schema: ParsedSchema): Int = ???

      override def reset(): Unit = ()
    }

  schemaRegistry = srClient

  override def getByteBuffer(payload: Array[Byte]): ByteBuffer = ByteBuffer.wrap(payload)

  def readFrom(buffer: ByteBuffer): MessageIndexes = {
    val size    = buffer.limit() - buffer.position()
    if (size == 0) { // optimization
      return new MessageIndexes(Collections.singletonList(0))
    }
    val indexes = new util.ArrayList[Integer](size)
    for (_ <- 0 until size) {
      indexes.add(buffer.get())
    }
    new MessageIndexes((0 to buffer.limit()).toList.map(Integer.valueOf(_)).asJava)
  }

  // The Object return type is a bit messy, but this is the simplest way to have
  // flexible decoding and not duplicate deserialization code multiple times for different variants.
  @throws[SerializationException]
  @throws[InvalidConfigurationException]
  def toto(
    includeSchemaAndVersion: Boolean,
    topic: String,
    isKey: Boolean,
    payload: Array[Byte]
  ): Object = {
    // Even if the caller requests schema & version, if the payload is null we cannot include it.
    // The caller must handle this case.
    if (payload == null) return null
    var id = -1
    try {
      val buffer     = getByteBuffer(payload)
      id = buffer.getInt
      var subject    =
        if (!isKey || strategyUsesSchema(isKey)) getContextName(topic)
        else subjectNameF(topic, isKey, null)
      var schema     = schemaRegistry.getSchemaBySubjectAndId(subject, id).asInstanceOf[ProtobufSchema]
      val indexes    = readFrom(buffer)
      val name       = schema.toMessageName(indexes)
      schema = schemaWithNameF(schema, name)
      if (includeSchemaAndVersion) {
        subject = subjectNameF(topic, isKey, schema)
        schema = schemaForDeserializeF(id, schema, subject, isKey)
        schema = schemaWithNameF(schema, name)
      }
      val length     = (buffer.limit: Int) - 1 - idSize
      val start      = (buffer.position: Int) + buffer.arrayOffset
      var value: Any = null
      if (parseMethod != null)
        try value = parseMethod.invoke(null, buffer)
        catch {
          case e: Exception =>
            throw new ConfigException("Not a valid protobuf builder", e)
        }
      else if (deriveType) value = deriveTypeF(buffer, schema)
      else {
        val descriptor = schema.toDescriptor
        if (descriptor == null) throw new SerializationException("Could not find descriptor with name " + schema.name)
        value = DynamicMessage.parseFrom(descriptor, new ByteArrayInputStream(buffer.array, start, length))
      }
      if (includeSchemaAndVersion) { // Annotate the schema with the version. Note that we only do this if the schema +
        // version are requested, i.e. in Kafka Connect converters. This is critical because that
        // code *will not* rely on exact schema equality. Regular deserializers *must not* include
        // this information because it would return schemas which are not equivalent.
        //
        // Note, however, that we also do not fill in the connect.version field. This allows the
        // Converter to let a version provided by a Kafka Connect source take priority over the
        // schema registry's ordering (which is implicit by auto-registration time rather than
        // explicit from the Connector).
        val version = schemaVersionF(topic, isKey, id, subject, schema, value)
        return new ProtobufSchemaAndValue(schema.copy(version), value)
      }

      value.asInstanceOf[Object]
    } catch {
      case e @ (_: IOException | _: RuntimeException) =>
        throw new SerializationException("Error deserializing Protobuf message for id " + id, e)
      case e: RestClientException =>
        throw toKafkaException(e, "Error retrieving Protobuf schema for id " + id)
    }
  }

  override def deserialize(payload: Array[Byte]): Message =
    this.toto(false, null, false, payload).asInstanceOf[Message]

}
