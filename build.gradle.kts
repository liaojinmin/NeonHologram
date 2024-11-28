
import io.izzel.taboolib.gradle.*
import io.izzel.taboolib.gradle.TabooLibExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "1.8.22" apply true
    id("io.izzel.taboolib") version "2.0.22" apply false
}

subprojects {
    apply<JavaPlugin>()
    apply(plugin = "io.izzel.taboolib")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    configure<TabooLibExtension> {

        env{
            install("basic-configuration")

            // minecraft
            install("minecraft-kether", "minecraft-chat", "minecraft-i18n")

            // bukkit
            //install("bukkit-util", "bukkit-xseries", "bukkit-hook")
            install("bukkit-util", "bukkit-xseries",  "bukkit-nms", "bukkit-hook")


            //install(BukkitNMSDataSerializer)

            install(Metrics, CommandHelper)

            install(Bukkit)

        }
        version { taboolib = "6.2.0" }
    }

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.tabooproject.org/repository/releases")
        maven("https://jitpack.io")
        maven("https://libraries.minecraft.net")
        maven("https://repo1.maven.org/maven2")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://repo.codemc.io/repository/nms/")
        maven("http://sacredcraft.cn:8081/repository/releases") { isAllowInsecureProtocol = true }
    }

    dependencies {
        compileOnly(kotlin("stdlib"))
        // server
        compileOnly("ink.ptms.core:v11604:11604")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjvm-default=all", "-Xextended-compiler-checks")
        }
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

gradle.buildFinished {
    buildDir.deleteRecursively()
}

