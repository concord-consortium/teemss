main: bin/pila bin/pilrc

bin: 
	mkdir bin

bin/pila: pila/pila bin
	-[ -f pila/pila ] && cp pila/pila bin

bin/pilrc: pilrc-2.8p6/pilrc bin
	cp pilrc-2.8p6/pilrc bin

FORCE:

pila/pila: FORCE
	cd pila; make

pilrc-2.8p6/pilrc: pilrc-2.8p6/Makefile FORCE
	cd pilrc-2.8p6; make

pilrc-2.8p6/Makefile:
	cd pilrc-2.8p6; ./configure

clean:
# do nothing for now

include ../ReleaseSystem/Makefile.inc
