# tech.cybersword.usb

BETA: tool for folder observation on USB

#Maven build  
`~/java_env/maven/bin/mvn archetype:generate -DgroupId=tech.cybersword -DartifactId=tech.cybersword.usb -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false`  
#build  
`~/java_env/maven/bin/mvn clean package`  
#run  
`~/java_env/jdk/Contents/Home/bin/java -jar target/tech.cybersword.usb-1.0-SNAPSHOT.jar`  
#scp  
`scp /lokaler/pfad/ benutzer@192.168.1.5:/home/benutzer/dokument.txt`  
