package net.andimiller.enumerive.tapir

import scala.quoted.*
import sttp.tapir.Schema

object DescriptionMacro:

  inline def enumDescription[T]: Option[String]           = ${ enumDescriptionImpl[T] }
  inline def enumCaseDescriptions[T]: Map[String, String] = ${ enumCaseDescriptionsImpl[T] }

  private def enumDescriptionImpl[T: Type](using Quotes): Expr[Option[String]] =
    import quotes.reflect.*
    val descSymbol = TypeTree.of[Schema.annotations.description].tpe.typeSymbol
    TypeRepr.of[T].typeSymbol.getAnnotation(descSymbol) match
      case Some(Apply(_, List(Literal(StringConstant(text))))) => '{ Some(${ Expr(text) }) }
      case _                                                   => '{ None }

  private def enumCaseDescriptionsImpl[T: Type](using Quotes): Expr[Map[String, String]] =
    import quotes.reflect.*
    val descSymbol                            = TypeTree.of[Schema.annotations.description].tpe.typeSymbol
    val entries: List[Expr[(String, String)]] =
      TypeRepr.of[T].typeSymbol.children.flatMap { child =>
        child.getAnnotation(descSymbol).collect { case Apply(_, List(Literal(StringConstant(text)))) =>
          Expr(child.name -> text)
        }
      }
    '{ Map(${ Varargs(entries) }: _*) }
