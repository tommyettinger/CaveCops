"C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvarsall.bat" x86_amd64 && ^
copy /Y ..\desktop\build\lib\CaveCops-Demo.jar CaveCops.jar && ^
zip -d CaveCops.jar *.dylib & ^
zip -d CaveCops.jar *.so & ^
C:\d\jvm\graal11_old\bin\native-image.cmd ^
-J-Xmx6G ^
-jar CaveCops.jar ^
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
copy /Y CaveCops.exe build\CaveCops.exe