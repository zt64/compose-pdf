# Compose PDF

![Maven Central Version](https://img.shields.io/maven-central/v/dev.zt64/compose-pdf?link=https%3A%2F%2Fcentral.sonatype.com%2Fartifact%2Fdev.zt64%2Fcompose-pdf)
![GitHub](https://img.shields.io/github/license/zt64/compose-pdf)

## Compose Multiplatform library to display PDF files.

## Supported platforms:

- Desktop (JVM) through [ICEpdf](https://github.com/pcorless/icepdf)
- Android
- iOS (planned)

## Setup

<details>
<summary>Kotlin Build script</summary>

```kotlin
dependencies {
    implementation("dev.zt64:compose-pdf:x.y.z")
}
```

</details>
<details>
<summary>Version catalog</summary>

```toml
[versions]
compose-pdf = "x.y.z"

[libraries]
compose-pdf = { module = "dev.zt64:compose-pdf", version.ref = "compose-pdf" }
```

</details>

## Usage

```kotlin
val pdfState = rememberLocalPdfState(File("path/to/file.pdf"))

PdfColumn(pdfState)
```

## License

```
MIT License
```
