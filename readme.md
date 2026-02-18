# enumerive

Have you ever wanted proper enum derivation support for Scala 3 where it actually encoded strings?

Well I decided to write a library for it.

## Modules

| Module | Artifact | Description |
|--------|----------|-------------|
| `enumerive-circe` | `"net.andimiller" %% "enumerive-circe"` | Circe `Encoder`, `Decoder`, and `Codec` using case labels |
| `enumerive-tapir` | `"net.andimiller" %% "enumerive-tapir"` | Tapir `Schema` and `Codec` using case labels, with `@description` support |

## Circe

The circe module provides derivable `Encoder`, `Decoder`, and `Codec` instances that serialize enums as their case label strings.

### LabelEncoder / LabelDecoder

Derive encoder and decoder separately:

```scala
import net.andimiller.enumerive.circe.{LabelEncoder, LabelDecoder}

enum Feature derives LabelEncoder, LabelDecoder:
  case DarkMode, BetaAccess, Premium
```

```scala
import io.circe.syntax.*

Feature.DarkMode.asJson          // "DarkMode"
io.circe.parser.decode[Feature]("\"DarkMode\"") // Right(DarkMode)
io.circe.parser.decode[Feature]("\"Nope\"")     // Left(DecodingFailure(...))
```

### LabelCodec

Or derive both at once:

```scala
import net.andimiller.enumerive.circe.LabelCodec

enum Feature derives LabelCodec:
  case DarkMode, BetaAccess, Premium
```

This provides a combined `Codec[Feature]` that encodes to and decodes from label strings.

## Tapir

The tapir module provides derivable `Schema` and `Codec` instances for enums.

### LabelSchema

Derives a `Schema[T]` with `SString` type and an enumeration validator:

```scala
import net.andimiller.enumerive.tapir.{LabelSchema, given}

enum Feature derives LabelSchema:
  case DarkMode, BetaAccess, Premium

val schema = summon[sttp.tapir.Schema[Feature]]
// SString schema with Validator.enumeration containing all cases
```

#### `@description` annotations

`LabelSchema` reads tapir `@description` annotations from the enum type and its cases, and includes them in the generated schema:

```scala
import net.andimiller.enumerive.tapir.{LabelSchema, given}
import sttp.tapir.Schema.annotations.description

@description("Available colours")
enum Colour derives LabelSchema:
  @description("The red colour") case Red
  @description("The green colour") case Green
  case Blue
```

The resulting schema's `description` will be:

```
Available colours

Red: The red colour, Green: The green colour
```

Unannotated cases (like `Blue`) are omitted from the description. When no `@description` annotations are present, `schema.description` is `None`.

### LabelCodec

Derives a tapir `Codec[String, T, TextPlain]` that encodes/decodes enums as label strings, using the schema from `LabelSchema`:

```scala
import net.andimiller.enumerive.tapir.{LabelSchema, LabelCodec, given}

enum Feature derives LabelSchema, LabelCodec:
  case DarkMode, BetaAccess, Premium

val codec = summon[sttp.tapir.Codec[String, Feature, sttp.tapir.CodecFormat.TextPlain]]
```
