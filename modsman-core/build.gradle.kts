import com.palantir.gradle.gitversion.VersionDetails
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `maven-publish`
    id("de.fuerstenau.buildconfig") version "1.1.8"
    id("com.palantir.git-version")
}

val versionDetails: groovy.lang.Closure<VersionDetails> by extra

dependencies {
    api(kotlin("stdlib-jdk8", "1.3.31"))
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.2.1")
    implementation(group = "com.squareup.retrofit2", name = "retrofit", version = "2.5.0")
    implementation(group = "com.squareup.retrofit2", name = "converter-gson", version = "2.5.0")
    implementation(group = "com.jakewharton.retrofit", name = "retrofit2-kotlin-coroutines-adapter", version = "0.9.2")
    implementation(group = "com.sangupta", name = "murmur", version = "1.0.0")
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
    kotlinOptions.jvmTarget = "1.8"
    destinationDir = compileJava.destinationDir
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val jar = tasks.getByName<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

if (versionDetails().isCleanTag) {
    publishing {
        publications {
            register("mavenJava", MavenPublication::class) {
                artifact(jar) {
                    builtBy(jar)
                }
                artifact(sourcesJar.get()) {
                    builtBy(sourcesJar)
                }
            }
        }

        repositories {
            if (project.hasProperty("publish_maven_s3_url")) {
                maven {
                    setUrl(project.property("publish_maven_s3_url")!!)
                    credentials(AwsCredentials::class) {
                        accessKey = project.property("publish_maven_s3_access_key") as String
                        secretKey = project.property("publish_maven_s3_secret_key") as String
                    }
                }
            }
        }
    }
}