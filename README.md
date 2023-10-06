# Compose PDF

[![Maven Central](https://img.shields.io/maven-central/v/dev.zt64.compose/compose-pdf)](https://search.maven.org/artifact/dev.zt64.compose/compose-pdf)

## Compose Multiplatform library to display PDF files.

## Supported platforms:
- Desktop (JVM) through [ICEpdf](https://github.com/pcorless/icepdf)
- Android
- iOS (planned)

## Installation
<details>
<summary>Kotlin Build script</summary>

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.zt64.compose:compose-pdf:0.1.0")
}
```
</details>
<details>
<summary>Version catalog</summary>

```toml
[versions]
compose-pdf = "0.1.0"

[libraries]
compose-pdf = { module = "dev.zt64.compose:compose-pdf", version.ref = "compose-pdf" }
```
</details>

## Usage
```kotlin
val pdf = rememberPdfState(File("path/to/file.pdf"))
//      = rememberPdfState(URL("https://example.com/file.pdf"))

Pdf(pdf)
```

## License
```
MIT License
```
