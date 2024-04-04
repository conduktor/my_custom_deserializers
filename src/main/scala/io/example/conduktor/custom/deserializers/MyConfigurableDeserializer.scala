package io.example.conduktor.custom.deserializers

import org.apache.kafka.common.serialization.Deserializer

import java.util
import scala.jdk.CollectionConverters.MapHasAsScala

case object MyConfigurableDeserializerException
    extends RuntimeException(
      "ConfigurableDeserializer fail when its `::configure` method is called without `output` property"
    )

sealed trait Output
final case class Constant(value: String)             extends Output
final case class Config(config: util.Map[String, _]) extends Output
final case object Passthrough                        extends Output
final case object Unconfigured                       extends Output

final class MyConfigurableDeserializer extends Deserializer[Any] {

  var output: Output = Unconfigured

  override def deserialize(topic: String, data: Array[Byte]): Any = output match {
    case Constant(value) => value
    case Config(config)  => config
    case Passthrough     => data
    case Unconfigured    => throw MyConfigurableDeserializerException
  }

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit =
    configs.asScala.get("output").map(_.asInstanceOf[String]) match {
      case Some("config")      => output = Config(configs)
      case Some("passthrough") => output = Passthrough
      case Some(value)         => output = Constant(value)
      case None                => throw MyConfigurableDeserializerException
    }
}
