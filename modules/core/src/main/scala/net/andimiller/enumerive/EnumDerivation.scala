package net.andimiller.enumerive

import scala.deriving.Mirror
import scala.compiletime.{constValueTuple, summonInline}

object EnumDerivation:

  inline def enumLabels[T](using m: Mirror.SumOf[T]): List[String] =
    constValueTuple[m.MirroredElemLabels].toList.asInstanceOf[List[String]]

  inline def enumValues[T](using m: Mirror.SumOf[T]): List[T] =
    summonValues[m.MirroredElemTypes].asInstanceOf[List[T]]

  inline def enumMap[T](using m: Mirror.SumOf[T]): Map[String, T] =
    enumLabels[T].zip(enumValues[T]).toMap

  private inline def summonValues[T <: Tuple]: List[Any] =
    inline scala.compiletime.erasedValue[T] match
      case _: EmptyTuple => Nil
      case _: (h *: t)   => summonInline[ValueOf[h]].value :: summonValues[t]
