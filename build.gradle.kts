plugins {
    val kotlinVersion = "1.6.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.11.1"
}

group = "net.zhuruoling"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies{
    implementation("com.google.code.gson:gson:2.9.1")
}
