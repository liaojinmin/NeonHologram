dependencies {
    // 引入 API
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-nms"))
    compileOnly(project(":project:common-nms-data-serializer"))

    // google
    compileOnly("com.google.code.gson:gson:2.8.5")
    compileOnly("com.google.guava:guava:21.0")
}

taboolib {
    subproject = true
}