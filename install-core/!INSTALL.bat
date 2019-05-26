rem >>>>> run before: mvn clean compile assembly:single
cd ../target
CALL mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file -Dfile=gdxg-core-0.3.2.jar -DgroupId=gdxg -DartifactId=gdxg-art -Dversion=0.3.2 -Dpackaging=jar

pause