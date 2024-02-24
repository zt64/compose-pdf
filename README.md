# Compose PDF

![Maven Central Version](https://img.shields.io/maven-central/v/dev.zt64/compose-pdf)

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
    implementation("dev.zt64:compose-pdf:1.0.0")
}
```

</details>
<details>
<summary>Version catalog</summary>

```toml
[versions]
compose-pdf = "1.0.0"

[libraries]
compose-pdf = { module = "dev.zt64:compose-pdf", version.ref = "compose-pdf" }
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
