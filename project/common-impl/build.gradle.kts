dependencies {
    // 引入 API
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-nms"))

    // google
    compileOnly("com.google.code.gson:gson:2.8.5")
    compileOnly("com.google.guava:guava:21.0")
}

taboolib {
    subproject = true
}