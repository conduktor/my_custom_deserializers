package io.example.conduktor.custom.deserializers

import org.apache.kafka.common.serialization.{Deserializer, Serializer}
import io.example.conduktor.custom.deserializers.addressbook._

final class MyCustomProtobufSerializer extends Serializer[Person] {
  override def serialize(topic: String, data: Person): Array[Byte] = data.toByteArray
}

final class MyCustomProtobufDeserializer extends Deserializer[Person] {
  override def deserialize(topic: String, data: Array[Byte]): Person = {
      Person.parseFrom(data)
  }
}