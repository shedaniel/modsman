import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `maven-publish`
    id("de.fuerstenau.buildconfig") version "1.1.8"
}

dependencies {
    compile(kotlin("stdlib-jdk8", "1.3.61"))
    compile(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.3.3")
    compile(group = "com.squareup.retrofit2", name = "retrofit", version = "2.7.1")
    compile(group = "com.squareup.retrofit2", name = "converter-gson", version = "2.7.1")
    compile(group = "com.jakewharton.retrofit", name = "retrofit2-kotlin-coroutines-adapter", version = "0.9.2")
    compile(group = "com.sangupta", name = "murmur", version = "1.0.0")
}

sourceSets {
    getByName("main") {
        java.srcDir("build/gen/buildconfig/src/main")
    }
}

val compileJava = tasks.getByName<JavaCompile>("compileJava") {
    doFirst {
        options.compilerArgs = listOf("--module-path", classpath.asPath)
        classpath = files()
    }
}

val compileKotlin = tasks.getByName<KotlinCompile>("compileKotlin") {
    destinationDir = compileJava.destinationDir
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xallow-result-return-type")
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val jar = tasks.getByName<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(sourcesJar.get()) {
                builtBy(sourcesJar)
            }
        }
    }

    repositories {
        mavenLocal()
    }
}
