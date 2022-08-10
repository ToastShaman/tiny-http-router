dependencies {
    implementation(project(":core"))
    compileOnly("jakarta.servlet:jakarta.servlet-api:_")

    testImplementation("org.eclipse.jetty:jetty-server:_")
}