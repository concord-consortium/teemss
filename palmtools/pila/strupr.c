#include <ctype.h>

#include "strupr.h"

void strupr(char *s) {
    while (*s) {
        *s = toupper(*s);
        s++;
    }
}

