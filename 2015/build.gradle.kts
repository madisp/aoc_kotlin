plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":lib"))

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.3")
}
