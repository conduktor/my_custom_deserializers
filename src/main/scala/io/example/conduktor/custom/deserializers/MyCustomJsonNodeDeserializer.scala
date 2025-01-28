package io.example.conduktor.custom.deserializers

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import org.apache.kafka.common.serialization.Deserializer

class MyCustomJsonNodeDeserializer extends Deserializer[JsonNode] {
  override def deserialize(topic: String, data: Array[Byte]): JsonNode = {
    val mapper = new ObjectMapper()
    mapper.readTree(data)
  }
}