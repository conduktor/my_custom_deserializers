package io.example.conduktor.custom.deserializers.constant

import org.apache.kafka.common.serialization.Deserializer

class ConstantString  extends Deserializer[String]  {
  override def deserialize(topic: String, data: Array[Byte]): String = "this is a message"
}
class ConstantChar    extends Deserializer[Char]    {
  override def deserialize(topic: String, data: Array[Byte]): Char = 'c'
}
class ConstantInt     extends Deserializer[Int]     {
  override def deserialize(topic: String, data: Array[Byte]): Int = 1
}
class ConstantDouble  extends Deserializer[Double]  {
  override def deserialize(topic: String, data: Array[Byte]): Double = 1.234
}
class ConstantFloat   extends Deserializer[Float]   {
  override def deserialize(topic: String, data: Array[Byte]): Float = 1.456f
}
class ConstantShort   extends Deserializer[Short]   {
  override def deserialize(topic: String, data: Array[Byte]): Short = 2
}
class ConstantBoolean extends Deserializer[Boolean] {
  override def deserialize(topic: String, data: Array[Byte]): Boolean = true
}
class ConstantByte    extends Deserializer[Byte]    {
  override def deserialize(topic: String, data: Array[Byte]): Byte = 6.toByte
}
class ConstantNull    extends Deserializer[Any]     {
  override def deserialize(topic: String, data: Array[Byte]): Any = null
}
