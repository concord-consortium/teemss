/***********************************************************************
 *
 *      MAIN.C
 *      Main Module for 68000 Assembler
 *
 *    Function: main()
 *      Parses the command line, opens the input file and
 *      output files, and calls processFile() to perform the
 *      assembly, then closes all files.
 *
 *   Usage: main(argc, argv);
 *      int argc;
 *      char *argv[];
 *
 *      Author: Paul McKee
 *      ECE492    North Carolina State University
 *
 *        Date: 12/13/86
 *
 ************************************************************************/

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

extern FILE *gpfilList; /* List file */
extern char line[256];  /* Source line */
extern int errorCount, warningCount;    /* Number of errors and warnings */

extern char cexFlag;    /* True is Constants are to be EXpanded */

int gfDebugOutput = FALSE;
int gfResourcesOnly = FALSE;
int gfEmitProcSymbols = FALSE;
int gfListOn = FALSE;
char gszAppName[_MAX_PATH];
FourCC gfcPrcType = MAKE4CC('a','p','p','l');

char pilafilename[_MAX_PATH];

int main(int argc, char *argv[])
{
    extern int SetArgFlags(char **apszArgs, int cpszArgs);
    extern long gcbDataCompressed;
    char pszFile[_MAX_PATH], outName[_MAX_PATH], *p;
    int i;
    long cbRes, cbPrc;
    char szErrors[80];

    sprintf( pilafilename, "%s", argv[0] );

    puts("Pila 1.0 Beta 3 Fluff 7\n");

    i = SetArgFlags(argv, argc);
    if (!i) {
        help();
    }

    gszAppName[0] = 0;

    /* Check whether a name was specified */

    if (i >= argc) {
        fputs("No input file specified\n\n", stdout);
        help();
    }

    if (!strcmp("?", argv[i])) {
        help();
    }

    strcpy(pszFile, argv[i]);

    /* Process output file names in their own buffer */
    strcpy(outName, pszFile);

    /* Change extension to .LIS */
    p = strchr(outName, '.');
    if (!p) {
        p = outName + strlen(outName);
    }
    if (gfListing) {
        strcpy(p, ".LIS");
        initList(outName);
    }

    strcpy(p, ".prc");

    /* Assemble the file */
    InitSym();
    processFile(pszFile);

    /* Close files and print error and warning counts */
    //PopSourceFile();

    // Get the resource total before WritePrc adds in the code and data
    // resources.
    cbRes = gcbResTotal;

    // If no errors, write the PRC file.
    cbPrc = 0;
    if (errorCount == 0) {
        cbPrc = WritePrc(outName, gszAppName, gpbCode, gulCodeLoc, gpbData, gulDataLoc);
        fprintf(stdout, "Code: %ld bytes\nData: %ld bytes (%ld compressed)\n"
                "Res:  %ld bytes\nPRC:  %ld bytes\n",
                gulCodeLoc, gulDataLoc, gcbDataCompressed, cbRes, cbPrc);
    }

    sprintf(szErrors, "%d error%s, %d warning%s\n",
            errorCount, (errorCount != 1) ? "s" : "",
            warningCount, (warningCount != 1) ? "s" : "");

    fprintf(stdout, szErrors);

    if (gfListing) {
        putc('\n', gpfilList);
        fprintf(gpfilList, szErrors);
        fclose(gpfilList);
    }

    return errorCount;
}


// Uppercase everything that isn't inside of single or double quotes
// 1998-02-17 Michael Dreher: changed to handle also mixed single/double 
// quote char cases (e.g. "my sister's husband" or just "'")
int strcap(char *d, char *s)
{
    char quoteChar;

    quoteChar = '\0';
    while (*s) {
        if (!quoteChar) {
            // start of a quoted string?
            if (*s == '\'' || *s == '\"') {
                quoteChar = *s; // remember the quote char
            }
#ifdef CASE_SENSITIVE
            *d = *s;
#else
            *d = toupper(*s);
#endif
        } else {
            // end of quoted string?
            if (*s == quoteChar) {
                quoteChar = '\0';
            }
            *d = *s;
        }
        d++;
        s++;
    }
    *d = '\0';

    return NORMAL;
}


char *skipSpace(char *p)
{
    while (isspace(*p)) {
        p++;
    }

    return p;
}

int SetArgFlags(char **apszArgs, int cpszArgs)
{
    int i;

    for (i = 1; i < cpszArgs && apszArgs[i][0] == '-'; i++) {
        char ch;
        char *pszArg = apszArgs[i] + 1, *pch;

        while ((ch = *pszArg++) != 0) {
            switch (ch) {
            case 'd':
                gfDebugOutput = TRUE;
                break;
            case 'c':
                cexFlag = TRUE;
                break;
            case 'l':
                gfListing = TRUE;
                gfListOn = TRUE;
                break;
            case 'r':
                gfResourcesOnly = TRUE;
                break;
            case 'h':
            case '?':
                help();
                break;
            case 's':
                gfEmitProcSymbols = TRUE;
                break;
            case 't':
                if (*pszArg != 0) {
                    fprintf(stdout, "-t must be followed by a space and a "
                            "four character type.\n");
                    return 0;
                }

                if (i + 1 >= cpszArgs) {
                    fprintf(stdout, "-t requires four character type.\n");
                    return 0;
                }

                pch = apszArgs[++i];
                if (strlen(pch) != 4) {
                    fprintf(stdout, "-t requires four character type.\n");
                    return 0;
                }

                gfcPrcType = ntohl(*(ulong *)pch);
                break;

            default:
                fprintf(stdout, "Unknown option -%c\n", ch);
                return 0;
            }
        }
    }

    return i;
}


/**********************************************************************
 *
 *  Function help() prints out a usage explanation if a bad
 *  option is specified or no filename is given.
 *
 *********************************************************************/

void help()
{
    puts("Usage: pila [-cldrs] [-t TYPE] infile.ext\n");
    puts("Options: -c  Show full constant expansions for DC directives");
    puts("         -l  Produce listing file (infile.lis)");
    puts("         -d  Debugging output");
    puts("         -r  Resources only, don't generate code or data");
    puts("         -s  Include debugging symbols in output");
    puts("    -t TYPE  Specify the PRC type, e.g., appl");
    exit(0);
}
