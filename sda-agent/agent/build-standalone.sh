#!/bin/sh

SRC_JAVA=./src
BUILD=$(pwd)/target
LIB=$(pwd)/target/dependency
CLASSES=$BUILD/classes:$BUILD/generated-code/resources
BIN=$(pwd)/bin
RETVAL=0
prog="build.sh"

###################################################################

build() {
    mvn clean install -Dmaven.test.skip=true
    mvn dependency:copy-dependencies
    mkdir bin

echo "
#!/bin/sh
BUILD=$BUILD
LIB=$LIB
" > ./bin/sda-agent.sh

echo '

for i in $(ls $LIB |grep ".jar"); do
        CLASSES=$CLASSES:$LIB/$i
done

for i in $(ls $BUILD |grep ".jar"); do
        CLASSES=$CLASSES:$BUILD/$i
done



echo
echo

CP=:$CLASSPATH:$CLASSES:.
java -classpath $CP org.sead.sda.agent.service.ServiceLauncher $1
' >> ./bin/sda-agent.sh
chmod 755 ./bin/sda-agent.sh




	return $RETVAL
}
###################################################################
clean(){
	rm -rf $BIN
	mvn clean
	return $RETVAL
}
###################################################################
case "$1" in
  clean)
        clean
        ;;
  *)
        #clean
        build
        RETVAL=$?
        ;;
esac

exit $RETVAL

