SRC_DIRS = waba/applet waba/io waba/io/impl waba/ui waba/fx waba/sys waba/util cc_util
SKIP_FILES = waba/io/impl/JDirectSerialPort.java waba/io/impl/SerialPortImpl.java


main:
	javac -classpath /home/scytacki/attachments/MRJClasses.zip -d ../classes $(filter-out $(SKIP_FILES), $(wildcard $(SRC_DIRS:%=%/*.java)))
