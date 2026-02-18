package net.andimiller.enumerive.circe

import io.circe.{Decoder, DecodingFailure, HCursor}
import scala.deriving.Mirror
import net.andimiller.enumerive.EnumDerivation

trait LabelDecoder[T] extends Decoder[T]

object LabelDecoder:
  inline def derived[T](using m: Mirror.SumOf[T]): LabelDecoder[T] =
    val lookup           = EnumDerivation.enumMap[T]
    lazy val validValues = lookup.keys.toList.sorted.mkString(", ")
    new LabelDecoder[T]:
      def apply(c: HCursor): Decoder.Result[T] =
        c.as[String].flatMap { s =>
          lookup.get(s) match
            case Some(v) => Right(v)
            case None    => Left(DecodingFailure(s"Invalid value: $s. Valid values: $validValues", c.history))
        }
