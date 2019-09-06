set groupId=cc
set artifactId=jcg
set version=1.2
set PROJECT_PATH=..\JavaCodeGenerator\JCG
set LOCAL_MVN_REPO_PATH=.

mvn -f %PROJECT_PATH% install
mvn -f %PROJECT_PATH% jar:jar
mvn install:install-file -DgroupId=%groupId% -DartifactId=%artifactId% -Dversion=%version% -Dfile=%PROJECT_PATH%\target\%artifactId%-%version%.jar -Dpackaging=jar -DpomFile=%PROJECT_PATH%\pom.xml -DlocalRepositoryPath=%LOCAL_MVN_REPO_PATH% -DcreateChecksum=true
