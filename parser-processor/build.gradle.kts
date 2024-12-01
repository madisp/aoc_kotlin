plugins {
    kotlin("jvm")
}

dependencies {
  implementation(libs.ksp.api)
}



java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
    vendor = JvmVendorSpec.AZUL
  }
}

