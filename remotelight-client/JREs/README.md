The OpenJDK 8 JRE builds are available from https://adoptopenjdk.net. They are in unpacked and unmodified form and are needed to build a bundled application.

Included versions:
- [OpenJDK-8-u292b10-JRE for Window x64](https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u292-b10/OpenJDK8U-jre_x64_windows_hotspot_8u292b10.zip)
- [OpenJDK-8-u292b10-JRE for Linux x64](https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u292-b10/OpenJDK8U-jre_x64_linux_hotspot_8u292b10.tar.gz)
- [OpenJDK-8-u302b08-JRE for macOS x64](https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u302-b08/OpenJDK8U-jdk_x64_mac_hotspot_8u302b08.tar.gz)

Other OpenJDK builds can be downloaded from the [AdoptOpenJDK download page](https://adoptopenjdk.net/releases.html) *(or any other OpenJDK provider)*.

To create a bundled application for another operating system, the correct JRE must be downloaded from the above link, unpacked and moved to the corresponding folder. Modifications in the [remotelight-client/pom.xml](https://github.com/Drumber/RemoteLight/blob/master/remotelight-client/pom.xml) might be needed. Then run `mvn package -DpackagerPhase=package` and it should create an application bundled with the individual JRE.

*More details about Java licensing and distribution can be found [here](https://www.java.com/en/download/faq/distribution.xml).*