main: bin/pila bin/pilrc

bin: 
	mkdir bin

bin/pila: pila/pila bin
	cp pila/pila bin

bin/pilrc: pilrc-2.8p6/pilrc bin
	cp pilrc-2.8p6/pilrc bin

FORCE:

pila/pila: FORCE
ifeq ($(shell uname),Linux)
	echo "Pila currently doesn't compile on Darwin"
	echo "You can download a compiled version from: "
	[ -f bin/pila ] 
else
	cd pila; make
endif

pilrc-2.8p6/pilrc: pilrc-2.8p6/Makefile FORCE
	cd pilrc-2.8p6; make

pilrc-2.8p6/Makefile:
	cd pilrc-2.8p6; ./configure

clean:
# do nothing for now

include ../ReleaseSystem/Makefile.inc
