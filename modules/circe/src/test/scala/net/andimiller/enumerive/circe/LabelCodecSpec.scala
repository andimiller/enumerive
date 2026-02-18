package net.andimiller.enumerive.circe

import munit.FunSuite
import io.circe.{Decoder, Encoder, Json}
import io.circe.syntax.*

enum Shape derives LabelCodec:
  case Circle, Square, Triangle

class LabelCodecSpec extends FunSuite:

  test("encode enum value to JSON string") {
    assertEquals(Shape.Circle.asJson, Json.fromString("Circle"))
    assertEquals(Shape.Square.asJson, Json.fromString("Square"))
    assertEquals(Shape.Triangle.asJson, Json.fromString("Triangle"))
  }

  test("decode valid JSON string to enum value") {
    assertEquals(Json.fromString("Circle").as[Shape], Right(Shape.Circle))
    assertEquals(Json.fromString("Triangle").as[Shape], Right(Shape.Triangle))
  }

  test("round-trip encode then decode") {
    Shape.values.foreach { s =>
      assertEquals(s.asJson.as[Shape], Right(s))
    }
  }

  test("invalid value produces descriptive error") {
    val result = Json.fromString("Hexagon").as[Shape]
    assert(result.isLeft)
    val error  = result.left.toOption.get.message
    assert(error.contains("Invalid value: Hexagon"), s"Expected error to mention invalid value, got: $error")
    assert(error.contains("Circle"), s"Expected error to list valid values, got: $error")
  }

  test("derives provides both Encoder and Decoder") {
    assert(summon[Encoder[Shape]].isInstanceOf[Encoder[Shape]])
    assert(summon[Decoder[Shape]].isInstanceOf[Decoder[Shape]])
  }
