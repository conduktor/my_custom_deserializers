package io.example.conduktor.custom.deserializers

import org.apache.kafka.common.serialization.Deserializer

import java.util
import scala.jdk.CollectionConverters.MapHasAsScala

case object MyConfigurableDeserializerException
    extends RuntimeException(
      "ConfigurableDeserializer fail when its `::configure` method is called without `output` property"
    )

final class MyConfigurableDeserializer extends Deserializer[Any] {

  var output: Any = "unconfigured"

  override def deserialize(topic: String, data: Array[Byte]): Any = output

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit =
    configs.asScala.get("output").map(_.asInstanceOf[String]) match {
      case Some("config") => output = configs
      case Some(value)    => output = value
      case None           => throw MyConfigurableDeserializerException
    }
}
