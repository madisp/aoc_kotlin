plugins {
    kotlin("jvm")
}

dependencies {
  implementation("com.google.devtools.ksp:symbol-processing-api:1.9.21-1.0.15")
}



java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
    vendor = JvmVendorSpec.AZUL
  }
}

