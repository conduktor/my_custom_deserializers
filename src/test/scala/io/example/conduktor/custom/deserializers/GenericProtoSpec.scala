package io.example.conduktor.custom.deserializers

import io.example.conduktor.custom.deserializers.addressbook.{Gender, Person}

class GenericProtoSpec extends munit.FunSuite {

  test("Hello World") {
    val obtained = 42
    val expected = 42
    assertEquals(obtained, expected)
  }

  val schema: String =
    """
      |syntax = "proto3";
      |
      |package io.example.conduktor.custom.deserializers;
      |
      |message Person {
      |  string name = 1;
      |  int32 age = 2;
      |  Gender gender = 3;
      |}
      |
      |enum Gender {
      |  MALE = 0;
      |  FEMALE = 1;
      |}
      |""".stripMargin.trim

  test("toto") {
    val deser  = new GenericProto(schema)
    val person = Person(
      name = "Jules",
      age = 33,
      gender = Gender.MALE,
    )
    val result = deser.deserialize("", person.toByteArray).asInstanceOf[Object]
    assertEquals(result, "")
  }

}
