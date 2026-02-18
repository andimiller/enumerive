package net.andimiller.enumerive

import munit.FunSuite

enum Planet:
  case Mercury, Venus, Earth, Mars

enum Singleton:
  case Only

enum BacktickCases:
  case `content-type`
  case `accept-encoding`
  case Normal

enum WithParam:
  case Simple
  case Parameterized(x: Int)

class EnumDerivationSpec extends FunSuite:

  test("enumLabels returns all case names in declaration order") {
    assertEquals(
      EnumDerivation.enumLabels[Planet],
      List("Mercury", "Venus", "Earth", "Mars")
    )
  }

  test("enumValues returns all case instances in declaration order") {
    assertEquals(
      EnumDerivation.enumValues[Planet],
      List(Planet.Mercury, Planet.Venus, Planet.Earth, Planet.Mars)
    )
  }

  test("enumMap maps each label to its corresponding value") {
    val m = EnumDerivation.enumMap[Planet]
    assertEquals(m.size, 4)
    assertEquals(m("Mercury"), Planet.Mercury)
    assertEquals(m("Venus"), Planet.Venus)
    assertEquals(m("Earth"), Planet.Earth)
    assertEquals(m("Mars"), Planet.Mars)
  }

  test("enumMap does not contain unlisted keys") {
    val m = EnumDerivation.enumMap[Planet]
    assertEquals(m.get("Jupiter"), None)
  }

  test("enumLabels works for single-case enum") {
    assertEquals(EnumDerivation.enumLabels[Singleton], List("Only"))
  }

  test("enumValues works for single-case enum") {
    assertEquals(EnumDerivation.enumValues[Singleton], List(Singleton.Only))
  }

  test("enumMap works for single-case enum") {
    assertEquals(EnumDerivation.enumMap[Singleton], Map("Only" -> Singleton.Only))
  }

  test("enumLabels and enumValues have matching lengths") {
    val labels = EnumDerivation.enumLabels[Planet]
    val values = EnumDerivation.enumValues[Planet]
    assertEquals(labels.length, values.length)
  }

  test("enumMap round-trips: looking up each value's label yields itself") {
    val labels = EnumDerivation.enumLabels[Planet]
    val values = EnumDerivation.enumValues[Planet]
    val m      = EnumDerivation.enumMap[Planet]
    labels.zip(values).foreach { (label, value) =>
      assertEquals(m(label), value)
    }
  }

  test("enumLabels preserves backtick case names") {
    assertEquals(
      EnumDerivation.enumLabels[BacktickCases],
      List("content-type", "accept-encoding", "Normal")
    )
  }

  test("enumMap works with backtick case names") {
    val m = EnumDerivation.enumMap[BacktickCases]
    assertEquals(m("content-type"), BacktickCases.`content-type`)
    assertEquals(m("accept-encoding"), BacktickCases.`accept-encoding`)
    assertEquals(m("Normal"), BacktickCases.Normal)
  }

  test("enumMap fails to compile for enum with parameterized cases") {
    val errors = scala.compiletime.testing.typeCheckErrors(
      "EnumDerivation.enumMap[WithParam]"
    )
    assert(errors.nonEmpty, "expected a compile error for parameterized enum")
    assert(
      errors.exists(_.message.contains("No singleton value available")),
      s"expected error about singleton value, got: ${errors.map(_.message).mkString(", ")}"
    )
  }
