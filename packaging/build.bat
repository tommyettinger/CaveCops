copy /Y ..\lwjgl3\build\lib\CaveCops-Demo.jar CaveCops-Demo.jar && ^
C:\d\jvm\graal11_old\bin\native-image.cmd ^
-J-Xmx6G ^
-jar CaveCops-Demo.jar ^
-Dorg.lwjgl.librarypath=. ^
-H:ReflectionConfigurationFiles=config/reflect-config.json ^
-H:JNIConfigurationFiles=config/jni-config.json ^
-H:DynamicProxyConfigurationFiles=config/proxy-config.json ^
-H:SerializationConfigurationFiles=config/serialization-config.json ^
-H:ResourceConfigurationFiles=config/resource-config.json ^
-H:+ReportExceptionStackTraces ^
-H:-CheckToolchain ^
--report-unsupported-elements-at-runtime ^
--no-fallback ^
--allow-incomplete-classpath && ^
ping 127.0.0.1 -n 2 > nul & ^
copy /Y CaveCops-Demo.exe agent\CaveCops.exe