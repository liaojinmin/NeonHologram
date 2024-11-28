

dependencies {
    compileOnly(project(":project:common-nms-data-serializer"))
    // 服务端
    compileOnly("ink.ptms.core:v12004:12004-minimize:mapped")
    compileOnly("ink.ptms.core:v11604:11604")
}
taboolib {
    subproject = true
}

