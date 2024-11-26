
val kotlinVersionNum: String
    get() = project.kotlin.coreLibrariesVersion.replace(".", "")


taboolib {
    description {
        name(rootProject.name)
        desc("${rootProject.name} 是一个高效的 Minecraft 插件")
        contributors {
            name("廖爷爷")
        }
        dependencies {

        }
    }

    env {
        enableIsolatedClassloader = false
        version {
            coroutines = null
        }

    }

    // hikari
    relocate("com.zaxxer.hikari", "${rootProject.group}.libraries.zaxxer.hikari")

    // redis
    relocate("redis.clients", "${rootProject.group}.libraries.redis.clients")
}

tasks {
    jar {
        // 构件名
        archiveFileName.set("${rootProject.name}-${archiveFileName.get().substringAfter('-')}")
        // 打包子项目源代码
        rootProject.subprojects.forEach { from(it.sourceSets["main"].output) }
    }
    kotlinSourcesJar {
        // 构件名
        archiveBaseName.set("${rootProject.name}-${archiveFileName.get().substringAfter('-')}")
        // 打包子项目源代码
        rootProject.subprojects.forEach { from(it.sourceSets["main"].allSource) }
    }
}
