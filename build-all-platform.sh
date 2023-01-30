./mvnw clean package  -Djavafx.platform=win -Djar.finalName=Dope-Text-win
mv target/Dope-Text-win.jar ./dist/
./mvnw clean package  -Djavafx.platform=linux -Djar.finalName=Dope-Text-linux
mv target/Dope-Text-linux.jar ./dist/
./mvnw clean package  -Djavafx.platform=mac -Djar.finalName=Dope-Text-mac
mv target/Dope-Text-mac.jar ./dist/

./mvnw clean
