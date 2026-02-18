package net.andimiller.enumerive.tapir

import munit.FunSuite
import sttp.tapir.{Schema, SchemaType, Validator}
import sttp.tapir.Schema.annotations.description
import net.andimiller.enumerive.tapir.given

enum Feature derives LabelSchema:
  case DarkMode, BetaAccess, Premium

@description("Available colors")
enum Color derives LabelSchema:
  @description("The red color") case Red
  @description("The green color") case Green
  case Blue

enum CasesOnly derives LabelSchema:
  @description("First option") case A
  case B

enum NoDescriptions derives LabelSchema:
  case Alpha, Beta

class LabelSchemaSpec extends FunSuite:

  test("schema type is SString") {
    val schema = summon[Schema[Feature]]
    schema.schemaType match
      case SchemaType.SString() => () // ok
      case other                => fail(s"Expected SString, got $other")
  }

  test("schema has enumeration validator with all values") {
    val schema = summon[Schema[Feature]]
    schema.validator match
      case Validator.Enumeration(values, _, _) =>
        assertEquals(values, Feature.values.toList)
      case other                               =>
        fail(s"Expected enumeration validator, got $other")
  }

  test("Color: description starts with top-level description and includes case descriptions") {
    val schema = summon[Schema[Color]]
    val desc   = schema.description.getOrElse(fail("Expected description to be set"))
    assert(desc.startsWith("Available colors"), s"Expected to start with 'Available colors', got: $desc")
    assert(desc.contains("Red: The red color"), s"Missing Red case description in: $desc")
    assert(desc.contains("Green: The green color"), s"Missing Green case description in: $desc")
    assert(!desc.contains("Blue"), s"Blue should not appear (no annotation) in: $desc")
  }

  test("CasesOnly: description contains only case descriptions") {
    val schema = summon[Schema[CasesOnly]]
    val desc   = schema.description.getOrElse(fail("Expected description to be set"))
    assert(desc.contains("A: First option"), s"Missing A case description in: $desc")
    assert(!desc.startsWith("\n\n"), s"Description should not start with newlines: $desc")
  }

  test("NoDescriptions: schema description is None") {
    val schema = summon[Schema[NoDescriptions]]
    assertEquals(schema.description, None)
  }

  test("enumeration validator still works with descriptions present") {
    val schema = summon[Schema[Color]]
    schema.validator match
      case Validator.Enumeration(values, _, _) =>
        assertEquals(values, Color.values.toList)
      case other                               =>
        fail(s"Expected enumeration validator, got $other")
  }
