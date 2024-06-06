plugins {
    kotlin("multiplatform") version "2.0.0"
}

group = "io.sakurasou"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "arch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        compilations.getByName("main") {
            // First, cinterops is added, and then an entry for each def file.
            cinterops {
                // By default, the name of the file is used.
                // You can override this with additional parameters:
                val libcurl by creating {
                    val include = "C:/Program Files (x86)/mingw64/include"
                    definitionFile.set(project.file("src/nativeInterop/cinterop/libcurl.def"))
                    packageName("io.sakurasou.c.http")
                    compilerOpts("-I/$include")
                    includeDirs.allHeaders(include)
                }
            }
        }
        binaries {
            executable {
                entryPoint = "io.sakurasou.main"
            }
        }
    }
    sourceSets {
        val nativeMain by getting
        val nativeTest by getting
    }
}