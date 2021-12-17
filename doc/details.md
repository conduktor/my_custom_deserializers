
## Deserializers implementation details

Here's the list of the deserializers available in this jar and a quick explanation of each deserializer behaviour.    
These deserializers are for demo/test purpose only.

#### `io.example.conduktor.custom.deserializers.constant.ConstantString`

This deserializer completely ignores the content of the messages it receives from Kafka.     
For each message it receives from Kafka, it returns `this is a message`.

#### `io.example.conduktor.custom.deserializers.constant.ConstantChar`

This deserializer completely ignores the content of the messages it receives from Kafka.     
For each message it receives from Kafka, it returns the letter `c`.

#### `io.example.conduktor.custom.deserializers.constant.ConstantInt`

This deserializer completely ignores the content of the messages it receives from Kafka.     
For each message it receives from Kafka, it returns the value `1`.

#### `io.example.conduktor.custom.deserializers.constant.ConstantDouble`

This deserializer completely ignores the content of the messages it receives from Kafka.     
For each message it receives from Kafka, it returns the value `1.234`.

#### `io.example.conduktor.custom.deserializers.constant.ConstantFloat`

This deserializer completely ignores the content of the messages it receives from Kafka.     
For each message it receives from Kafka, it returns the value `1.456`.

#### `io.example.conduktor.custom.deserializers.constant.ConstantShort`

This deserializer completely ignores the content of the messages it receives from Kafka.     
For each message it receives from Kafka, it returns the value `2`.

#### `io.example.conduktor.custom.deserializers.constant.ConstantBoolean`

This deserializer completely ignores the content of the messages it receives from Kafka.     
For each message it receives from Kafka, it returns the value `true`.

#### `io.example.conduktor.custom.deserializers.constant.ConstantByte`

This deserializer completely ignores the content of the messages it receives from Kafka.     
For each message it receives from Kafka, it returns the value `6`.

#### `io.example.conduktor.custom.deserializers.constant.ConstantNull`

This deserializer completely ignores the content of the messages it receives from Kafka.     
For each message it receives from Kafka, it returns `null`.
