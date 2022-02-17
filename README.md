# Conduktor custom deserializers examples

This repository contains examples of custom deserializers usable in Conduktor with plugin mechanism.

You can download the latest jar containing these deserializers [my_custom_deserializers_2.13-2.0.0.jar](https://github.com/conduktor/my_custom_deserializers/releases/download/2.0.0/my_custom_deserializers_2.13-2.0.0.jar)

To learn how to use the "custom deserializer" feature see [Conduktor documentation](https://docs.conduktor.io/features/consuming-data/custom-deserializers)

## Simple example 

`io.example.conduktor.custom.deserializers.MyCustomDeserializer`

[visible here](./src/main/scala/io/example/conduktor/custom/deserializers/MyCustomDeserializer.scala)


This deserializer transforms the data (bytes) it receives from Kafka into a `String` (text),     
then sees if it matches then following format:
```
-- this is the serialized data
```
- If the message received from Kafka effectively starts with a `--<space>` characters sequence then followed by some text, 
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
  - Give a simplified example of "deserialization" (the message has to respect of certain format so that the deserializer can extract the data)


To see simple example around constants, jump [here](./doc/details.md)

## Protobuf example
`io.example.conduktor.custom.deserializers.MyCustomProtobufDeserializer`

[located here](./src/main/scala/io/example/conduktor/custom/deserializers/MyCustomProtobufDeserializer.scala)


This example allow to deserialize a protobuf payload corresponding to this schema : 

```
message Person {
  required string name = 1;
  required int32 id = 2;
  optional string email = 3;

  enum PhoneType {
    MOBILE = 0;
    HOME = 1;
    WORK = 2;
  }

  message PhoneNumber {
    required string number = 1;
    optional PhoneType type = 2 [default = HOME];
  }

  repeated PhoneNumber phones = 4;
}
```
