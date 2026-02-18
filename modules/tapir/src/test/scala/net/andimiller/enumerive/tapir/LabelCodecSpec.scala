package net.andimiller.enumerive.tapir

import munit.FunSuite
import sttp.tapir.{Codec, CodecFormat, DecodeResult}
import net.andimiller.enumerive.tapir.given

enum Tier derives LabelSchema, LabelCodec:
  case Free, Pro, Enterprise

class LabelCodecSpec extends FunSuite:

  test("codec encodes enum to string") {
    val codec = summon[Codec[String, Tier, CodecFormat.TextPlain]]
    assertEquals(codec.encode(Tier.Free), "Free")
    assertEquals(codec.encode(Tier.Pro), "Pro")
    assertEquals(codec.encode(Tier.Enterprise), "Enterprise")
  }

  test("codec decodes valid string to enum") {
    val codec = summon[Codec[String, Tier, CodecFormat.TextPlain]]
    assert(codec.decode("Free") == DecodeResult.Value(Tier.Free))
    assert(codec.decode("Enterprise") == DecodeResult.Value(Tier.Enterprise))
  }

  test("codec round-trips all values") {
    val codec = summon[Codec[String, Tier, CodecFormat.TextPlain]]
    Tier.values.foreach { t =>
      assert(codec.decode(codec.encode(t)) == DecodeResult.Value(t), s"Failed round-trip for $t")
    }
  }

  test("codec returns error for invalid input") {
    val codec  = summon[Codec[String, Tier, CodecFormat.TextPlain]]
    val result = codec.decode("Invalid")
    assert(result.isInstanceOf[DecodeResult.InvalidValue], s"Expected InvalidValue, got $result")
  }
