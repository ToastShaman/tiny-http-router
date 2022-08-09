dependencies {
    implementation(project(":core"))
    compileOnly("javax.servlet:javax.servlet-api:_")

    testImplementation("javax.servlet:javax.servlet-api:_")
}