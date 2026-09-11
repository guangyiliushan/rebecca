plugins {
    // detekt 规则模块必须纯 kotlin("jvm")，无 Android 依赖（detekt 2.0 官方要求）
    alias(libs.plugins.kotlinJvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // 官方 note：detekt-api 必须 compileOnly（运行时由 detekt core 的 classloader 提供）
    compileOnly("dev.detekt:detekt-api:2.0.0-alpha.6")
    testImplementation("dev.detekt:detekt-test:2.0.0-alpha.6")
    testImplementation("dev.detekt:detekt-test-assertj:2.0.0-alpha.6")
    testImplementation(kotlin("test"))
}
