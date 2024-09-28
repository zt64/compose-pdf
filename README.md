# Compose PDF

[![Maven Central Version](https://img.shields.io/maven-central/v/dev.zt64/compose-pdf?link=https%3A%2F%2Fcentral.sonatype.com%2Fartifact%2Fdev.zt64%2Fcompose-pdf)](https://central.sonatype.com/artifact/dev.zt64/compose-pdf)
![GitHub](https://img.shields.io/github/license/zt64/compose-pdf) 
<br>
![badge-platform-jvm]
![badge-platform-android]

Compose Multiplatform library to display PDF files.

## Supported platforms:

- Desktop (JVM) through [ICEpdf](https://github.com/pcorless/icepdf)
- Android
- iOS (planned) https://github.com/zt64/compose-pdf/issues/28

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

## Planned features
- [ ] Unified local and remote pdf state
- [ ] Using Canvas instead of an Image to render

## License

[MIT](LICENSE)

[badge-platform-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat

[badge-platform-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat

[badge-platform-js]: http://img.shields.io/badge/-js-F8DB5D.svg?style=flat

[badge-platform-js-node]: https://img.shields.io/badge/-nodejs-68a063.svg?style=flat

[badge-platform-linux]: http://img.shields.io/badge/-linux-2D3F6C.svg?style=flat

[badge-platform-windows]: http://img.shields.io/badge/-windows-4D76CD.svg?style=flat

[badge-platform-macos]: http://img.shields.io/badge/-macos-111111.svg?style=flat

[badge-platform-ios]: http://img.shields.io/badge/-ios-CDCDCD.svg?style=flat

[badge-platform-tvos]: http://img.shields.io/badge/-tvos-808080.svg?style=flat

[badge-platform-watchos]: http://img.shields.io/badge/-watchos-C0C0C0.svg?style=flat

[badge-platform-wasm]: https://img.shields.io/badge/-wasm-624FE8.svg?style=flat
