package net.andimiller.enumerive.tapir

import sttp.tapir.{Codec, CodecFormat, DecodeResult, Schema, ValidationError, Validator}
import scala.deriving.Mirror
import net.andimiller.enumerive.EnumDerivation

final class LabelCodec[T](val codec: Codec[String, T, CodecFormat.TextPlain])

object LabelCodec:
  given [T](using vc: LabelCodec[T]): Codec[String, T, CodecFormat.TextPlain] = vc.codec

  inline def derived[T](using m: Mirror.SumOf[T], vs: LabelSchema[T]): LabelCodec[T] =
    val labels = EnumDerivation.enumLabels[T]
    val lookup = EnumDerivation.enumMap[T]
    val c      = Codec.string
      .mapDecode { s =>
        lookup.get(s) match
          case Some(v) => DecodeResult.Value(v)
          case None    => DecodeResult.InvalidValue(List(ValidationError(Validator.enumeration(lookup.values.toList), s)))
      } { v =>
        labels(m.ordinal(v))
      }
      .schema(vs.schema)
    new LabelCodec[T](c)
