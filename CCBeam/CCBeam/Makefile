###################################################
# Matthew Cruz's Generic MakeFile
# File: Makefile
# 11/08/01
###################################################

#**************************************************
# Make Commands

CC	= m68k-palmos-gcc
PILRC	= pilrc
BUILD	= build-prc
#**************************************************

#**************************************************
# Compiler Flags
CCFLAGS = -O2 -Wall #-palmos3.5

# Compile Rules
.SUFFIXES: .c .o

.c.o:
	$(CC) -c $(CCFLAGS) $<
#**************************************************

#**************************************************
# VARIABLES - BUILD
NAME	= "CCBeam"
CODE	= cCCB

#--------------------------------------------------
# VARIABLES - CODE EXE
EXE	= ccbeam
OBJS	= AppMain.o

#--------------------------------------------------
# VARIABLES - RESOURCES
BIN1	= Res_App
BIN2	= Res_Delete
BIN3	= Res_Details
BIN4	= Res_List
BIN5	= Res_Misc
BIN6	= Res_Prefs
BIN7	= Res_Survey

BIN1_RCS =
BIN2_RCS =
BIN3_RCS =
BIN4_RCS =
BIN5_RCS =
BIN6_RCS =
BIN7_RCS =

#BINS	= $(BIN1).bin $(BIN2).bin $(BIN3).bin $(BIN4).bin $(BIN5).bin $(BIN6).bin $(BIN7).bin
BINS = $(BIN1).bin
#**************************************************
# Conditions

#--------------------------------------------------
# PALM APPLICATION - .PRC
$(EXE).prc: $(EXE) $(BINS)
	$(BUILD) --hidden $(EXE).prc $(NAME) $(CODE) $(EXE) *.bin
	rm -f *.bin

#--------------------------------------------------
# CODE EXE
$(EXE): $(OBJS)
	$(CC) $(OBJS) $(CCFLAGS) -o $(EXE)

#--------------------------------------------------
# RESOURCES
$(BIN1).bin: $(BIN1).rcp $(BIN1_RSCS)
	$(PILRC) -q $(BIN1).rcp

$(BIN2).bin: $(BIN2).rcp $(BIN2_RSCS)
	$(PILRC) -q $(BIN2).rcp

$(BIN3).bin: $(BIN3).rcp $(BIN3_RSCS)
	$(PILRC) -q $(BIN3).rcp

$(BIN4).bin: $(BIN4).rcp $(BIN4_RSCS)
	$(PILRC) -q $(BIN4).rcp

$(BIN5).bin: $(BIN5).rcp $(BIN5_RSCS)
	$(PILRC) -q $(BIN5).rcp

$(BIN6).bin: $(BIN6).rcp $(BIN6_RSCS)
	$(PILRC) -q $(BIN6).rcp

$(BIN7).bin: $(BIN7).rcp $(BIN7_RSCS)
	$(PILRC) -q $(BIN7).rcp

#--------------------------------------------------
clean:
	rm -f *.o *.bin $(EXE)

empty:
	rm -f *.prc *.o *.bin $(EXE)
#--------------------------------------------------

