https://gist.github.com/fernandezpablo85/03cf8b0cd2e7d8527063

mvn install:install-file -DgroupId=YOUR_GROUP -DartifactId=YOUR_ARTIFACT -Dversion=YOUR_VERSION -Dfile=YOUR_JAR_FILE -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true

# run it into the GIT mvn-repo branch (-DlocalRepositoryPath=.)
mvn install:install-file -DgroupId=cc -DartifactId=jcg -Dversion=1.0 -Dfile=C:\Users\abigger\git\JavaCodeGenerator\JCG\target\jcg-1.0.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true