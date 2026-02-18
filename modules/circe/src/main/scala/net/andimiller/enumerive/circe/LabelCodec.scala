package net.andimiller.enumerive.circe

import io.circe.{Codec, Decoder, DecodingFailure, HCursor, Json}
import scala.deriving.Mirror
import net.andimiller.enumerive.EnumDerivation

trait LabelCodec[T] extends Codec[T]

object LabelCodec:
  inline def derived[T](using m: Mirror.SumOf[T]): LabelCodec[T] =
    val labels      = EnumDerivation.enumLabels[T].toVector
    val lookup      = EnumDerivation.enumMap[T]
    val validValues = lookup.keys.toList.sorted.mkString(", ")
    new LabelCodec[T]:
      def apply(a: T): Json                    = Json.fromString(labels(m.ordinal(a)))
      def apply(c: HCursor): Decoder.Result[T] =
        c.as[String].flatMap { s =>
          lookup.get(s) match
            case Some(v) => Right(v)
            case None    => Left(DecodingFailure(s"Invalid value: $s. Valid values: $validValues", c.history))
        }
