; Sample.asm
;
; This sample is meant to serve as a prototype for assembly language Pilot
; applications. It uses the standard startup code and implements a typical
; message loop that handles some common events. Several resources (e.g.,
; menu, icon) are included, a few inline and others from external files
; generated by PilRC.
;
; NOTE: For clarity's sake this code is not highly optimized.
;
; NOTE: Where possible this sample uses PalmOS APIs to do conversion between
; integers and strings (hex and decimal). The Palm APIs have some interesting
; limitations. StrAToI accepts signed 16-bit ints. StrIToA accepts signed
; 32-bit ints. StrIToH accepts signed 32-bit ints. My implementation of htoi
; accepts unsigned 32-bit ints. The least common denominator for all these
; routines is a signed 16-bit int so I've placed that limit on the
; conversions.
;
; Formatted for 8-space tabs. Assemble with "pila sample.asm" after creating
; the resources with "pilrc sample.rcp ."
;
; By Darrin Massena
; 29 Jul 96
; (updated 10 Sep 96)

; The 'Appl' directive sets the application's name and four character id.

        Appl    "Sample App", 'samp'

; Pilot.inc contains PalmOS constants, structure offsets, and API trap codes.

        include "pilot.inc"


; Startup.inc contains a standard startup function. This function must be
; the first within an application and is called by the PalmOS after the app
; is loaded. The startup function in Startup.inc (__Startup__) calls the
; application-defined function PilotMain.

        include "startup.inc"


; Application-defined resource ids

kidbAIB         equ             $7FFE
kidrPREF        equ             1
kidrTVER        equ             1
kidrTAIB        equ             1000
kidfMain        equ             1000
kidmMain        equ             1000

kidmAbout       equ             1002
kidcConvert     equ             1003
kidcDecimal     equ             1004
kidcHex         equ             1005

kidrAboutAlert  equ             1000
kidrInputErrorAlert equ         1001
kidrRangeErrorAlert equ         1002

; Some global variables (a5-relative)

        data

global gpfldDecimal.l                   ;pointer to the Decimal field
global gpfldHex.l                       ;pointer to the Hex field

        code

; ---------------------------------------------------------------------------
; DWord PilotMain(Word cmd, void *cmdPBP, Word launchflags)
; PilotMain is called by the startup code and implements a simple event
; handling loop.

proc PilotMain(cmd.w, cmdPBP.l, launchFlags.w)
local   err.w
local   evt.EventType

beginproc
        tst.w   cmd(a6)                 ;sysAppLaunchCmdNormalLaunch is 0
        bne     PmReturn                ;not a normal launch, bag out

        systrap FrmGotoForm(#kidfMain.w)

PmEventLoop
; Doze until an event arrives

        systrap EvtGetEvent(&evt(a6), #evtWaitForever.w)

; System gets first chance to handle the event

        systrap SysHandleEvent(&evt(a6))

        tst.b   d0                      ;handled?
        bne.s   PmEventDone             ;yep

; Menu handler gets second chance to handle the event

        systrap MenuHandleEvent(&0, &evt(a6), &err(a6))

        tst.b   d0                      ;handled?
        bne.s   PmEventDone             ;yep

; Application handler gets third chance to handle the event

        call    ApplicationHandleEvent(&evt(a6))

        tst.b   d0                      ;handled?
        bne.s   PmEventDone             ;yep

; Form handler gets fourth chance to handle the event

        call    MainFormHandleEvent(&evt(a6))

        tst.b   d0                      ;handled?
        bne.s   PmEventDone             ;yep

; Still not handled. We're not interested in it anymore so let the
; default form handler take it.

        systrap FrmGetActiveForm()
        systrap FrmHandleEvent(a0.l, &evt(a6))

; Return from PilotMain when an appStopEvent is received

PmEventDone
        cmpi.w  #appStopEvent,evt+EventType.eType(a6) ;time to stop?
        bne.s   PmEventLoop             ;nope, loop until it is

PmReturn
        moveq   #0,d0
endproc


; ---------------------------------------------------------------------------
; Boolean ApplicationHandleEvent(EventType *pevt)
; Handles these events:
;       frmLoadEvent

proc ApplicationHandleEvent(pevt.l)
beginproc
        movem.l a3,-(a7)                ;save registers we're going to trash

        movea.l pevt(a6),a0             ;a0 = pevt

; Handle frmLoadEvents

        cmpi.w  #frmLoadEvent,EventType.eType(a0) ;frmLoadEvent?
        bne     AHENotHandled           ;no

; Initialize the form and make it active

        systrap FrmInitForm(EventType.data+frmLoad.formID(a0).w)
        move.l  a0,a3                   ;save a copy of FormPtr for later

        systrap FrmSetActiveForm(a0.l)

; Get pointers to the two fields we'll be working with (decimal, hex)

        systrap FrmGetObjectIndex(a3.l, #kidcDecimal.w)
        systrap FrmGetObjectPtr(a3.l,d0.w)
        move.l  a0,gpfldDecimal(a5)     ;save the field ptr away

        systrap FrmGetObjectIndex(a3.l, #kidcHex.w)
        systrap FrmGetObjectPtr(a3.l,d0.w)
        move.l  a0,gpfldHex(a5)         ;save the field ptr away

        moveq.l #1,d0                   ;event handled
        bra.s   AHEReturn

AHENotHandled
        clr.b   d0

AHEReturn
        movem.l (a7)+,a3
endproc


; ---------------------------------------------------------------------------
; Boolean MainFormHandleEvent(EventType *pevt)
; Handles these events:
;

proc MainFormHandleEvent(pevt.l)
beginproc

        movea.l pevt(a6),a0             ;a0 = pevt
        move.w  EventType.eType(a0),d0

; Handle frmOpenEvent

        cmp.w   #frmOpenEvent,d0
        bne     MFH1

; Draw the form

        systrap FrmGetActiveForm()
        systrap FrmDrawForm(a0.l)

        moveq.l #1,d0
        bra     MFHReturn

; Handle frmMenuEvent
MFH1
        cmp.w   #menuEvent,d0
        bne     MFH2

        cmp.w   #kidmAbout,EventType.data+menu.itemID(a0)
        bne     MFHNotHandled

        systrap FrmAlert(#kidrAboutAlert.w)

        moveq.l #1,d0
        bra     MFHReturn

; Handle ctlSelectEvent
MFH2
        cmp.w   #ctlSelectEvent,d0
        bne     MFHNotHandled

; Is the convert button being clicked?

        cmp.w   #kidcConvert,EventType.data+ctlEnter.controlID(a0)
        bne     MFHNotHandled

        jsr     Convert(pc)             ;yes, convert the number
        bra     MFHReturn

MFHNotHandled
        clr.b   d0

MFHReturn
endproc


; ---------------------------------------------------------------------------
; void Convert(void)
;

proc Convert()
local   szT.12
beginproc
        movem.l d1-d3/a3,-(a7)

; Yes. Convert the number

; 1. Figure out which field was changed (if any)
        systrap FldDirty(gpfldDecimal(a5).l)
        tst.w   d0
        bne     CVT2

        systrap FldDirty(gpfldHex(a5).l)
        tst.w   d0
        beq     CVTReturn
        clr.w   d0
CVT2
        move.w  d0,d3                   ;d3 = fDecChanged

; 2. Get its text and convert it from ASCII to integer

        move.l  gpfldHex(a5),a0         ;assume Hex changed
        tst.w   d3                      ;Decimal changed?
        beq     CVT3                    ;no

        move.l  gpfldDecimal(a5),a0
CVT3
        systrap FldGetTextPtr(a0.l)

        cmp.l   #0,a0                   ;is there any text in the field?
        bne     CVT31                   ;yes

        systrap FrmAlert(#kidrInputErrorAlert.w)           ; a comment
        bra     CVTReturn

CVT31
        tst.w   d3                      ;convert from decimal?
        beq     CVT4                    ;no

; PalmOS includes a function for converting from ASCII to integer

        systrap StrAToI(a0.l)
        bra     CVT5

; Must implement our own ASCII-hex to integer converter

CVT4
        move.l  a0,a3                   ;save a copy of the char *
        systrap StrLen(a0.l)

        move.w  d0,d2                   ;convert loop counter
        subq.l  #1,d2
        sub.l   d0,d0
CVT41
        lsl.l   #4,d0
        move.b  (a3)+,d1
        sub.b   #'0',d1
        cmp.b   #9,d1
        ble     CVT42
        sub.b   #7,d1
        cmp.b   #$f,d1
        ble     CVT42
        sub.b   #$20,d1
CVT42
        or.b    d1,d0
        dbra    d2,CVT41

        cmp.l   #$10000,d0
        blt     CVT5

        systrap FrmAlert(#kidrRangeErrorAlert.w)
        bra     CVTReturn

; 3. Convert from integer to ASCII-hex or ASCII-decimal, as appropriate

CVT5
        move.l  d0,-(a7)                ;push integer
        pea.l   szT(a6)                 ;push pointer to string buffer

        tst.w   d3                      ;convert from decimal?
        bne     CVT6                    ;yes

        trap    #15
        dc.w    sysTrapStrIToA
        addq.l  #8,a7                   ;pop the args off the stack
        bra     CVT7

CVT6
        trap    #15
        dc.w    sysTrapStrIToH
        addq.l  #8,a7                   ;pop the args off the stack

CVT7
;        addq.l  #8,a7                   ;pop the args off the stack

; 4. Place the new ASCII string in the appropriate field

        move.l  gpfldDecimal(a5),a3     ;assume Hex changed
        tst.w   d3                      ;Decimal changed?
        beq     CVT8                    ;no

        move.l  gpfldHex(a5),a3
CVT8
        ; erase the old string

        systrap FldFreeMemory(a3.l)

        ; insert the new string

        systrap    StrLen(&szT(a6))


        lea.l   szT(a6),a0
        tst.w   d3                      ;converting to decimal?
        beq     CVT83                   ;yes

        ; truncate hex numbers to 4 digits

        move.w  d0,d1
        subq.w  #4,d1
        blt     CVT83
        sub.w   d1,d0
        add.w   d1,a0
        bra     CVT83

        ; strip off leading zeros
CVT82
        cmp.b   #'0',(a0)
        bne     CVT81
        addq.l  #1,a0
        subq.w  #1,d0
CVT83
        cmp.w   #1,d0
        bgt     CVT82

CVT81
        systrap FldInsert(a3.l,a0.l,d0.w)

CVTReturn
        ; Clear the dirty bits on both fields

        systrap FldSetDirty(gpfldDecimal(a5).l,#0.b)
        systrap FldSetDirty(gpfldHex(a5).l,#0.b)

        movem.l  (a7)+,d1-d3/a3
endproc

;
; Resources -----------------------------------------------------------------
;

; Application Icon Bitmap resource. Is automatically converted from Windows
; format to Pilot format and written as a 'tAIB' rather than 'Tbmp' because
; kidbAIB is a special value ($7ffe)

        res 'WBMP', kidbAIB, "sample.bmp"

; 'pref' resource. Defines app launch flags, stack and heap size

        res 'pref', kidrPREF
        dc.w    sysAppLaunchFlagNewStack|sysAppLaunchFlagNewGlobals|sysAppLaunchFlagUIApp|sysAppLaunchFlagSubCall
        dc.l    $1000                           ; stack size
        dc.l    $1000                           ; heap size

; Form resource

        res 'tFRM', kidfMain, "tFRM03e8.bin"

; Version resource

        res 'tver', kidrTVER
        dc.b    '1.0', 0

; Menu resource

        res 'MBAR', kidmMain, "MBAR03e8.bin"

; Alert resources

        res 'Talt', kidrAboutAlert, "Talt03e8.bin"
        res 'Talt', kidrInputErrorAlert, "Talt03e9.bin"
        res 'Talt', kidrRangeErrorAlert, "Talt03ea.bin"
