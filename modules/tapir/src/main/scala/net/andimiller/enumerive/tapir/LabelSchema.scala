package net.andimiller.enumerive.tapir

import sttp.tapir.{Schema, SchemaType, Validator}
import scala.deriving.Mirror
import net.andimiller.enumerive.EnumDerivation

final class LabelSchema[T](val schema: Schema[T])

object LabelSchema:
  given [T](using vs: LabelSchema[T]): Schema[T] = vs.schema

  inline def derived[T](using m: Mirror.SumOf[T]): LabelSchema[T] =
    val values    = EnumDerivation.enumValues[T]
    val labels    = EnumDerivation.enumLabels[T]
    val enumDesc  = DescriptionMacro.enumDescription[T]
    val caseDescs = DescriptionMacro.enumCaseDescriptions[T]
    val s0        = Schema[T](SchemaType.SString())
      .validate(Validator.enumeration(values, v => Some(labels(m.ordinal(v)))))
    val s         =
      val parts = List.concat(
        enumDesc.toList,
        Option.when(caseDescs.nonEmpty) {
          labels
            .flatMap { label =>
              caseDescs.get(label).map(d => s"$label: $d")
            }
            .mkString(", ")
        }
      )
      if parts.isEmpty then s0
      else s0.description(parts.mkString("\n\n"))
    new LabelSchema[T](s)
