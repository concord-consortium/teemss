#include <windows.h>
#include <crtl.h>
void main(int argc, char *argv[]);

int WINAPI
WinMain(HINSTANCE inst, HINSTANCE prevInst, LPSTR cmdLine, int cmdShow)
{
   	char **__initenv;
    char par1[MAX_PATH],par2[MAX_PATH];
    char *pBuf[2];
	GetModuleFileName(0, par1, MAX_PATH);
	strcpy(par2,"JavaFrame");
	pBuf[0] = par1;
	pBuf[1] = par2;
   	__initenv = environ;
	main(2, pBuf);
	return 1;

}

