

dependencies {
    compileOnly(project(":project:common"))
    // 服务端
    compileOnly("ink.ptms.core:v12101:12101-minimize:mapped")
}

taboolib {
    subproject = true
}
