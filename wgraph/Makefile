WABA_DIR= /home/scytacki/waba/superwaba/vmsrc/classes_java
WEXTRAS_DIR= /home/scytacki/waba/superwaba/wextras/classes

export CLASSPATH:= ${CLASSPATH}:${WABA_DIR}:${WEXTRAS_DIR}

SRC= *.java

comp:
	javac -O -d classes $(SRC:%=source/%)
