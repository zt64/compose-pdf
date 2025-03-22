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
// To display a PDF file, first you need a PDF state
// It can be created using the `rememberPdfState` function
// The function has several available overloads, for example, you can pass a `File` object
val pdfState = rememberPdfState(File("path/to/file.pdf"))

// Then you can use the included composables to display the PDF
// For example to display all pages in a vertical list you can use the `PdfColumn` composable
PdfColumn(pdfState)

// Or to display in a vertical list with snapping you can use the `PdfVerticalPager` composable
PdfVerticalPager(pdfState)

// And likewise for a horizontal list with snapping you can use the `PdfHorizontalPager` composable
PdfHorizontalPager(pdfState)

// You can also use the `PdfPage` composable directly to display a single page and have more control over the layout
PdfPage(pdfState, pageNumber = 0)
```

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
