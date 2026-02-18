package net.andimiller.enumerive.circe

import munit.FunSuite
import io.circe.{Json, DecodingFailure}
import io.circe.syntax.*

enum Color derives LabelEncoder, LabelDecoder:
  case Red, Green, Blue

class LabelEncoderDecoderSpec extends FunSuite:

  test("encode enum value to JSON string") {
    assertEquals(Color.Red.asJson, Json.fromString("Red"))
    assertEquals(Color.Green.asJson, Json.fromString("Green"))
    assertEquals(Color.Blue.asJson, Json.fromString("Blue"))
  }

  test("decode valid JSON string to enum value") {
    assertEquals(Json.fromString("Red").as[Color], Right(Color.Red))
    assertEquals(Json.fromString("Blue").as[Color], Right(Color.Blue))
  }

  test("round-trip encode then decode") {
    Color.values.foreach { c =>
      assertEquals(c.asJson.as[Color], Right(c))
    }
  }

  test("invalid value produces descriptive error") {
    val result = Json.fromString("Yellow").as[Color]
    assert(result.isLeft)
    val error  = result.left.toOption.get.message
    assert(error.contains("Invalid value: Yellow"), s"Expected error to mention invalid value, got: $error")
    assert(error.contains("Red"), s"Expected error to list valid values, got: $error")
  }
