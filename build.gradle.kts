plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.tecnio"
version = "4.0.2"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()

    maven("https://jitpack.io")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://nexus.funkemunky.cc/content/repositories/releases/")
}

dependencies {
    compileOnly("org.github.spigot:1.8.8:1.8.8")
    compileOnly("org.projectlombok:lombok:1.18.32")

    annotationProcessor("org.projectlombok:lombok:1.18.32")

    implementation("com.github.artemisac.artemis-packet-api:api:2.0.0-beta-3")
    implementation("com.github.artemisac.artemis-packet-api:spigot:2.0.0-beta-3")
    implementation("org.atteo.classindex:classindex:3.13")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.0.2")
}

tasks.shadowJar {
    archiveFileName = "AntiHaxerman.jar"
    minimize()

    relocate("org.atteo.classindex", "me.tecnio.ahm.libs.classindex")
    relocate("co.aikar", "me.tecnio.ahm.libs.aikar")
    relocate("org.bstats", "me.tecnio.ahm.libs.bstats")
    relocate("ac.artemis.packet", "me.tecnio.ahm.libs.artemis")
    relocate("cc.ghast.packet", "me.tecnio.ahm.libs.ghast")
    relocate("com.github.steveice10.opennbt", "me.tecnio.ahm.libs.opennbt")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}