# Microsoft Developer Studio Project File - Name="pila" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 5.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) External Target" 0x0106

CFG=pila - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "pila.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "pila.mak" CFG="pila - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "pila - Win32 Release" (based on "Win32 (x86) External Target")
!MESSAGE "pila - Win32 Debug" (based on "Win32 (x86) External Target")
!MESSAGE 

# Begin Project
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""

!IF  "$(CFG)" == "pila - Win32 Release"

# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Cmd_Line "NMAKE /f pila.mak"
# PROP BASE Rebuild_Opt "/a"
# PROP BASE Target_File "pila.exe"
# PROP BASE Bsc_Name "pila.bsc"
# PROP BASE Target_Dir ""
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Cmd_Line "NMAKE /f pila.mak CFG="PilA - Win32 Release""
# PROP Rebuild_Opt "/a"
# PROP Target_File "pila.exe"
# PROP Bsc_Name "pila.bsc"
# PROP Target_Dir ""

!ELSEIF  "$(CFG)" == "pila - Win32 Debug"

# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Cmd_Line "NMAKE /f pila.mak"
# PROP BASE Rebuild_Opt "/a"
# PROP BASE Target_File "pila.exe"
# PROP BASE Bsc_Name "pila.bsc"
# PROP BASE Target_Dir ""
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Cmd_Line "NMAKE /f pila.mak"
# PROP Rebuild_Opt "/a"
# PROP Target_File "pila.exe"
# PROP Bsc_Name "pila.bsc"
# PROP Target_Dir ""

!ENDIF 

# Begin Target

# Name "pila - Win32 Release"
# Name "pila - Win32 Debug"

!IF  "$(CFG)" == "pila - Win32 Release"

!ELSEIF  "$(CFG)" == "pila - Win32 Debug"

!ENDIF 

# Begin Source File

SOURCE=.\asm.h
# End Source File
# Begin Source File

SOURCE=.\assemble.c
# End Source File
# Begin Source File

SOURCE=.\build.c
# End Source File
# Begin Source File

SOURCE=.\codegen.c
# End Source File
# Begin Source File

SOURCE=.\directiv.c
# End Source File
# Begin Source File

SOURCE=.\error.c
# End Source File
# Begin Source File

SOURCE=.\eval.c
# End Source File
# Begin Source File

SOURCE=.\globals.c
# End Source File
# Begin Source File

SOURCE=.\instlook.c
# End Source File
# Begin Source File

SOURCE=.\insttabl.c
# End Source File
# Begin Source File

SOURCE=.\lex.c
# End Source File
# Begin Source File

SOURCE=.\lex.h
# End Source File
# Begin Source File

SOURCE=.\listing.c
# End Source File
# Begin Source File

SOURCE=.\main.c
# End Source File
# Begin Source File

SOURCE=.\movem.c
# End Source File
# Begin Source File

SOURCE=.\object.c
# End Source File
# Begin Source File

SOURCE=.\opparse.c
# End Source File
# Begin Source File

SOURCE=.\pila.h
# End Source File
# Begin Source File

SOURCE=.\pila.mak
# End Source File
# Begin Source File

SOURCE=.\pp.c
# End Source File
# Begin Source File

SOURCE=.\pp.h
# End Source File
# Begin Source File

SOURCE=.\prc.c
# End Source File
# Begin Source File

SOURCE=.\prc.h
# End Source File
# Begin Source File

SOURCE=.\proto.h
# End Source File
# Begin Source File

SOURCE=.\std.h
# End Source File
# Begin Source File

SOURCE=.\strupr.c
# End Source File
# Begin Source File

SOURCE=.\strupr.h
# End Source File
# Begin Source File

SOURCE=.\symbol.c
# End Source File
# Begin Source File

SOURCE=.\util.h
# End Source File
# End Target
# End Project
