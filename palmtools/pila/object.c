// object.c
//
// Author: Darrin Massena (darrin@massena.com)
// Date: 6/24/96

#include "pila.h"
#include "asm.h"
#include "prc.h"

// for htonl and ntohl
#ifndef unix
    #include <winsock.h>
#else
#ifndef __APPLE__
    #include <asm/byteorder.h>
#endif
#endif

int outputObj(long lOutLoc, long data, int size)
{
    unsigned char *pbOutput = gpbOutput + lOutLoc;

    switch (size) {
    case BYTE:
        *(unsigned char *)pbOutput = (unsigned char)data;
        break;
    case WORD:
        *(unsigned short *)pbOutput = htons((unsigned short)data);
        break;
    case LONG:
        *(unsigned long *)pbOutput = htonl((unsigned long)data);
        break;
    }

    return NORMAL;
}

