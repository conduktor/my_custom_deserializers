# my_custom_deserializers

Kafka deserializers examples to use to demo/test the Conduktor "custom deserializers" feature.

You can download the latest jar containing these deserializers here: TODO Jules

## Deserializers implementation details

Here's the list of the deserializers available in this jar and a quick explanation of each deserializer behaviour.    
These deserializers are for demo/test purpose only.

### io.example.conduktor.custom.deserializers.constant.ConstantString

This deserializer completely ignores the messages it receives from Kafka.     
For each message it receives from Kafka, it returns `this is a message`.

### io.example.conduktor.custom.deserializers.constant.ConstantChar

This deserializer completely ignores the messages it receives from Kafka.     
For each message it receives from Kafka, it returns the letter `c`.

### io.example.conduktor.custom.deserializers.constant.ConstantInt

This deserializer completely ignores the messages it receives from Kafka.     
For each message it receives from Kafka, it returns the value `1`.

### io.example.conduktor.custom.deserializers.constant.ConstantDouble

This deserializer completely ignores the messages it receives from Kafka.     
For each message it receives from Kafka, it returns the value `1.234`.

### io.example.conduktor.custom.deserializers.constant.ConstantFloat

This deserializer completely ignores the messages it receives from Kafka.     
For each message it receives from Kafka, it returns the value `1.456`.

### io.example.conduktor.custom.deserializers.constant.ConstantShort

This deserializer completely ignores the messages it receives from Kafka.     
For each message it receives from Kafka, it returns the value `2`.

### io.example.conduktor.custom.deserializers.constant.ConstantBoolean

This deserializer completely ignores the messages it receives from Kafka.     
For each message it receives from Kafka, it returns the value `true`.

### io.example.conduktor.custom.deserializers.constant.ConstantByte

This deserializer completely ignores the messages it receives from Kafka.     
For each message it receives from Kafka, it returns the value `6`.

### io.example.conduktor.custom.deserializers.constant.ConstantNull

This deserializer completely ignores the messages it receives from Kafka.     
For each message it receives from Kafka, it returns `null`.

### io.example.conduktor.custom.deserializers.MyCustomDeserializer

This deserializer transforms the data (bytes) it receives from Kafka into a `String` (text),     
then sees if it matches then following format:
```
-- this is the serialized data
```
- If the message received from Kakfa effectively starts with a `--<space>` characters sequence then followed by some text, 
it creates a new instance of a data structure named `MyMessage`, that contains only one field named `value` and is of type `String`, as following:     
    ```scala
    MyMessage(value = "this is the serialized data")
    ```

    In Conduktor, this data structure will be interpreted and displayed as JSON:     
    ```json
    { "value": "this is the serialized data" }
    ```
  
- If the message received from Kafka doesn't match the expected format, then the deserializer fails with an error message:
  ```
  Invalid format received for message: <the received message>
  ```

This simple example is here to demonstrate 2 things:
  - What's happening when a custom deserializer fails to deserialize a message.
  - Give a simple example of "deserialization" (the message has to respect of certain format so that the deserializer can extract the data)



