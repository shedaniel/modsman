import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    compile(project(":modsman-core"))
    compile(group = "com.beust", name = "jcommander", version = "1.71")
}

val compileKotlin = tasks.getByName<KotlinCompile>("compileKotlin") {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "modsman.cli.MainKt"
    }
    from(configurations.compile.get().map { if (it.isDirectory) it else zipTree(it) })
}
