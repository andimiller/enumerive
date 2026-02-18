package net.andimiller.enumerive.circe

import io.circe.{Encoder, Json}
import scala.deriving.Mirror
import net.andimiller.enumerive.EnumDerivation

trait LabelEncoder[T] extends Encoder[T]

object LabelEncoder:
  inline def derived[T](using m: Mirror.SumOf[T]): LabelEncoder[T] =
    val labels = EnumDerivation.enumLabels[T].toVector
    new LabelEncoder[T]:
      def apply(a: T): Json = Json.fromString(labels(m.ordinal(a)))
