plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    application
}

application {
    mainClass.set("com.timor.epub.EpubExtractorCli")  // 메인 클래스 지정
}
// Kotlin 의존성을 JAR에 포함시키는 태스크 추가
tasks.register<Jar>("fatJar") {
    dependsOn(tasks.named("compileKotlin"))
    archiveClassifier.set("fat")

    from(sourceSets.main.get().output)

    // 모든 의존성 포함
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    manifest {
        attributes["Main-Class"] = "com.timor.epub.EpubExtractorCli"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    // Kotlin 표준 라이브러리
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")

    // Kotlin 코루틴 지원
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // JSON 처리 라이브러리
    implementation("org.json:json:20231013")

    // 테스트 의존성
    testImplementation("junit:junit:4.13.2")
}
