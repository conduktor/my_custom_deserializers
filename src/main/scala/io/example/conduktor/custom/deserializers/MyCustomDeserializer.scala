package io.example.conduktor.custom.deserializers

import org.apache.kafka.common.serialization.{Deserializer, Serializer}

import java.nio.charset.StandardCharsets

final case class MyMessage(value: String)

final class MyCustomSerializer extends Serializer[MyMessage] {
  override def serialize(topic: String, data: MyMessage): Array[Byte] = s"-- ${data.value}".getBytes(StandardCharsets.UTF_8)
}

final class MyCustomDeserializer extends Deserializer[MyMessage] {
  override def deserialize(topic: String, data: Array[Byte]): MyMessage =
    new String(data, StandardCharsets.UTF_8) match {
      case s"-- $data" => MyMessage(value = data)
      case m           => throw new RuntimeException(s"Invalid format received for message: $m")
    }
}
