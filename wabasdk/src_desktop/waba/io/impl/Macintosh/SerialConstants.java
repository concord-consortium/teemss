/*
Copyright (c) 2001 Concord Consortium  All rights reserved.
*/

package waba.io.impl;
/**
 * The interface <CODE>SerialConstants</CODE> contains java equivalents for all constants<BR>
 * defined in the C header <CODE>Serial.h</CODE><BR>
 * <BR>
 * When writing a java class which needs these constants, you can either use the <BR>
 * fully qualified name of the constant (e.g. <CODE>option = SerialConstants.kWilyBit</CODE>)<BR>
 * or you can declare that your class <CODE>implements SerialConstants<CODE> and then use the<BR>
 * simple name (e.g. <CODE>option = kWilyBit</CODE>)<BR>
 */
public interface SerialConstants {

	/**
		in C: <CODE>763</CODE>
	*/
	public final int 	baud150							= 763;

	/**
		in C: <CODE>380</CODE>
	*/
	public final int 	baud300							= 380;

	/**
		in C: <CODE>189</CODE>
	*/
	public final int 	baud600							= 189;

	/**
		in C: <CODE>94</CODE>
	*/
	public final int 	baud1200						= 94;

	/**
		in C: <CODE>62</CODE>
	*/
	public final int 	baud1800						= 62;

	/**
		in C: <CODE>46</CODE>
	*/
	public final int 	baud2400						= 46;

	/**
		in C: <CODE>30</CODE>
	*/
	public final int 	baud3600						= 30;

	/**
		in C: <CODE>22</CODE>
	*/
	public final int 	baud4800						= 22;

	/**
		in C: <CODE>14</CODE>
	*/
	public final int 	baud7200						= 14;

	/**
		in C: <CODE>10</CODE>
	*/
	public final int 	baud9600						= 10;

	/**
		in C: <CODE>6</CODE>
	*/
	public final int 	baud14400						= 6;

	/**
		in C: <CODE>4</CODE>
	*/
	public final int 	baud19200						= 4;

	/**
		in C: <CODE>2</CODE>
	*/
	public final int 	baud28800						= 2;

	/**
		in C: <CODE>1</CODE>
	*/
	public final int 	baud38400						= 1;

	/**
		in C: <CODE>0</CODE>
	*/
	public final int 	baud57600						= 0;


	/**
		in C: <CODE>16384</CODE>
	*/
	public final int 	stop10							= 16384;

	/**
		in C: <CODE>-32768L</CODE>
	*/
	public final int 	stop15							= -32768;

	/**
		in C: <CODE>-16384</CODE>
	*/
	public final int 	stop20							= -16384;


	/**
		in C: <CODE>0</CODE>
	*/
	public final int 	noParity						= 0;

	/**
		in C: <CODE>4096</CODE>
	*/
	public final int 	oddParity						= 4096;

	/**
		in C: <CODE>12288</CODE>
	*/
	public final int 	evenParity						= 12288;


	/**
		in C: <CODE>0</CODE>
	*/
	public final int 	data5							= 0;

	/**
		in C: <CODE>2048</CODE>
	*/
	public final int 	data6							= 2048;

	/**
		in C: <CODE>1024</CODE>
	*/
	public final int 	data7							= 1024;

	/**
		in C: <CODE>3072</CODE>
	*/
	public final int 	data8							= 3072;


	/**
		in C: <CODE>6</CODE><BR>
		 channel A data in or out (historical) 
	*/
	public final int 	aData							= 6;

	/**
		in C: <CODE>2</CODE><BR>
		 channel A control (historical) 
	*/
	public final int 	aCtl							= 2;

	/**
		in C: <CODE>4</CODE><BR>
		 channel B data in or out (historical) 
	*/
	public final int 	bData							= 4;

	/**
		in C: <CODE>0</CODE><BR>
		 channel B control (historical) 
	*/
	public final int 	bCtl							= 0;


	/**
		in C: <CODE>2</CODE><BR>
		 flag for SerShk.evts 
	*/
	public final byte 	dsrEvent						= (byte)2;

	/**
		in C: <CODE>4</CODE><BR>
		 flag for SerShk.evts 
	*/
	public final byte 	riEvent							= (byte)4;

	/**
		in C: <CODE>8</CODE><BR>
		 flag for SerShk.evts 
	*/
	public final byte 	dcdEvent						= (byte)8;

	/**
		in C: <CODE>32</CODE><BR>
		 flag for SerShk.evts 
	*/
	public final byte 	ctsEvent						= (byte)32;

	/**
		in C: <CODE>-128</CODE><BR>
		 flag for SerShk.evts 
	*/
	public final byte 	breakEvent						= (byte)-128;


	/**
		in C: <CODE>-128</CODE><BR>
		 flag for SerStaRec.xOffSent 
	*/
	public final byte 	xOffWasSent						= (byte)-128;

	/**
		in C: <CODE>64</CODE><BR>
		 flag for SerStaRec.xOffSent 
	*/
	public final byte 	dtrNegated						= (byte)64;

	/**
		in C: <CODE>32</CODE><BR>
		 flag for SerStaRec.xOffSent 
	*/
	public final byte 	rtsNegated						= (byte)32;


	/**
		in C: <CODE>-6</CODE><BR>
		 serial port A input 
	*/
	public final short 	ainRefNum						= (short)-6;

	/**
		in C: <CODE>-7</CODE><BR>
		 serial port A output 
	*/
	public final short 	aoutRefNum						= (short)-7;

	/**
		in C: <CODE>-8</CODE><BR>
		 serial port B input 
	*/
	public final short 	binRefNum						= (short)-8;

	/**
		in C: <CODE>-9</CODE><BR>
		 serial port B output 
	*/
	public final short 	boutRefNum						= (short)-9;


	/**
		in C: <CODE>1</CODE><BR>
		 serial driver error masks 
	*/
	public final byte 	swOverrunErr					= (byte)1;

	/**
		in C: <CODE>8</CODE><BR>
		 serial driver error masks 
	*/
	public final byte 	breakErr						= (byte)8;

	/**
		in C: <CODE>16</CODE><BR>
		 serial driver error masks 
	*/
	public final byte 	parityErr						= (byte)16;

	/**
		in C: <CODE>32</CODE><BR>
		 serial driver error masks 
	*/
	public final byte 	hwOverrunErr					= (byte)32;

	/**
		in C: <CODE>64</CODE><BR>
		 serial driver error masks 
	*/
	public final byte 	framingErr						= (byte)64;


	/**
		in C: <CODE>128</CODE><BR>
		 option bit used with Control code 16 
	*/
	public final int 	kOptionPreserveDTR				= 128;

	/**
		in C: <CODE>64</CODE><BR>
		 option bit used with Control code 16 
	*/
	public final int 	kOptionClockX1CTS				= 64;


	/**
		in C: <CODE>-128</CODE><BR>
		 flag for SerShk.fCTS 
	*/
	public final byte 	kUseCTSOutputFlowControl		= (byte)-128;

	/**
		in C: <CODE>64</CODE><BR>
		 flag for SerShk.fCTS 
	*/
	public final byte 	kUseDSROutputFlowControl		= (byte)64;

	/**
		in C: <CODE>-128</CODE><BR>
		 flag for SerShk.fDTR 
	*/
	public final byte 	kUseRTSInputFlowControl			= (byte)-128;

	/**
		in C: <CODE>64</CODE><BR>
		 flag for SerShk.fDTR 
	*/
	public final byte 	kUseDTRInputFlowControl			= (byte)64;


	/**
		in C: <CODE>0</CODE><BR>
		 Macintosh modem port 
	*/
	public final byte 	sPortA							= (byte)0;

	/**
		in C: <CODE>1</CODE><BR>
		 Macintosh printer port 
	*/
	public final byte 	sPortB							= (byte)1;

	/**
		in C: <CODE>2</CODE><BR>
		 RS-232 port COM1 
	*/
	public final byte 	sCOM1							= (byte)2;

	/**
		in C: <CODE>3</CODE><BR>
		 RS-232 port COM2 
	*/
	public final byte 	sCOM2							= (byte)3;


	/**
		in C: <CODE>8</CODE><BR>
		 program port speed, bits/char, parity, and stop bits 
	*/
	public final short 	kSERDConfiguration				= (short)8;

	/**
		in C: <CODE>9</CODE><BR>
		 set buffer for chars received with no read pending 
	*/
	public final short 	kSERDInputBuffer				= (short)9;

	/**
		in C: <CODE>10</CODE><BR>
		 equivalent to SerHShake, largely obsolete 
	*/
	public final short 	kSERDSerHShake					= (short)10;

	/**
		in C: <CODE>11</CODE><BR>
		 assert break signal on output 
	*/
	public final short 	kSERDClearBreak					= (short)11;

	/**
		in C: <CODE>12</CODE><BR>
		 negate break state on output 
	*/
	public final short 	kSERDSetBreak					= (short)12;

	/**
		in C: <CODE>13</CODE><BR>
		 set explicit baud rate, other settings unchanged 
	*/
	public final short 	kSERDBaudRate					= (short)13;

	/**
		in C: <CODE>14</CODE><BR>
		 superset of 10, honors setting of fDTR 
	*/
	public final short 	kSERDHandshake					= (short)14;

	/**
		in C: <CODE>15</CODE><BR>
		 clock externally on CTS with specified multiplier 
	*/
	public final short 	kSERDClockMIDI					= (short)15;

	/**
		in C: <CODE>16</CODE><BR>
		 select clock source and DTR behavior on close 
	*/
	public final short 	kSERDMiscOptions				= (short)16;

	/**
		in C: <CODE>17</CODE><BR>
		 assert DTR output 
	*/
	public final short 	kSERDAssertDTR					= (short)17;

	/**
		in C: <CODE>18</CODE><BR>
		 negate DTR output 
	*/
	public final short 	kSERDNegateDTR					= (short)18;

	/**
		in C: <CODE>19</CODE><BR>
		 select char to replace chars with invalid parity 
	*/
	public final short 	kSERDSetPEChar					= (short)19;

	/**
		in C: <CODE>20</CODE><BR>
		 select char to replace char that replaces chars with invalid parity 
	*/
	public final short 	kSERDSetPEAltChar				= (short)20;

	/**
		in C: <CODE>21</CODE><BR>
		 set XOff output flow control (same as receiving XOff) 
	*/
	public final short 	kSERDSetXOffFlag				= (short)21;

	/**
		in C: <CODE>22</CODE><BR>
		 clear XOff output flow control (same as receiving XOn) 
	*/
	public final short 	kSERDClearXOffFlag				= (short)22;

	/**
		in C: <CODE>23</CODE><BR>
		 send XOn if input flow control state is XOff 
	*/
	public final short 	kSERDSendXOn					= (short)23;

	/**
		in C: <CODE>24</CODE><BR>
		 send XOn regardless of input flow control state 
	*/
	public final short 	kSERDSendXOnOut					= (short)24;

	/**
		in C: <CODE>25</CODE><BR>
		 send XOff if input flow control state is XOn 
	*/
	public final short 	kSERDSendXOff					= (short)25;

	/**
		in C: <CODE>26</CODE><BR>
		 send XOff regardless of input flow control state 
	*/
	public final short 	kSERDSendXOffOut				= (short)26;

	/**
		in C: <CODE>27</CODE><BR>
		 reset serial I/O channel hardware 
	*/
	public final short 	kSERDResetChannel				= (short)27;

	/**
		in C: <CODE>28</CODE><BR>
		 extension of 14, allows full RS-232 hardware handshaking 
	*/
	public final short 	kSERDHandshakeRS232				= (short)28;

	/**
		in C: <CODE>29</CODE><BR>
		 use mark/space parity 
	*/
	public final short 	kSERDStickParity				= (short)29;

	/**
		in C: <CODE>30</CODE><BR>
		 assert RTS output 
	*/
	public final short 	kSERDAssertRTS					= (short)30;

	/**
		in C: <CODE>31</CODE><BR>
		 negate RTS output 
	*/
	public final short 	kSERDNegateRTS					= (short)31;

	/**
		in C: <CODE>115</CODE><BR>
		 set 115.2K baud data rate 
	*/
	public final short 	kSERD115KBaud					= (short)115;

	/**
		in C: <CODE>230</CODE><BR>
		 set 230.4K baud data rate 
	*/
	public final short 	kSERD230KBaud					= (short)230;


	/**
		in C: <CODE>2</CODE><BR>
		 return characters available (SerGetBuf) 
	*/
	public final short 	kSERDInputCount					= (short)2;

	/**
		in C: <CODE>8</CODE><BR>
		 return characters available (SerStatus) 
	*/
	public final short 	kSERDStatus						= (short)8;

	/**
		in C: <CODE>9</CODE><BR>
		 return version number in first byte of csParam 
	*/
	public final short 	kSERDVersion					= (short)9;

	/**
		in C: <CODE>256</CODE><BR>
		 get instantaneous state of DCD (GPi) 
	*/
	public final short 	kSERDGetDCD						= (short)256;


	/**
		in C: <CODE>kOptionClockX1CTS</CODE><BR>
		 option bit used with Control code 16 
	*/
	public final int 	serdOptionClockExternal			= 64;

	/**
		in C: <CODE>kOptionPreserveDTR</CODE><BR>
		 option bit used with Control code 16 
	*/
	public final int 	serdOptionPreserveDTR			= 128;


	/**
		in C: <CODE>kSERDConfiguration</CODE>
	*/
	public final int 	serdReset						= 8;

	/**
		in C: <CODE>kSERDInputBuffer</CODE>
	*/
	public final int 	serdSetBuf						= 9;

	/**
		in C: <CODE>kSERDSerHShake</CODE>
	*/
	public final int 	serdHShake						= 10;

	/**
		in C: <CODE>kSERDClearBreak</CODE>
	*/
	public final int 	serdClrBrk						= 11;

	/**
		in C: <CODE>kSERDSetBreak</CODE>
	*/
	public final int 	serdSetBrk						= 12;

	/**
		in C: <CODE>kSERDBaudRate</CODE>
	*/
	public final int 	serdSetBaud						= 13;

	/**
		in C: <CODE>kSERDHandshake</CODE>
	*/
	public final int 	serdHShakeDTR					= 14;

	/**
		in C: <CODE>kSERDClockMIDI</CODE>
	*/
	public final int 	serdSetMIDI						= 15;

	/**
		in C: <CODE>kSERDMiscOptions</CODE>
	*/
	public final int 	serdSetMisc						= 16;

	/**
		in C: <CODE>kSERDAssertDTR</CODE>
	*/
	public final int 	serdSetDTR						= 17;

	/**
		in C: <CODE>kSERDNegateDTR</CODE>
	*/
	public final int 	serdClrDTR						= 18;

	/**
		in C: <CODE>kSERDSetPEChar</CODE>
	*/
	public final int 	serdSetPEChar					= 19;

	/**
		in C: <CODE>kSERDSetPEAltChar</CODE>
	*/
	public final int 	serdSetPECharAlternate			= 20;

	/**
		in C: <CODE>kSERDSetXOffFlag</CODE>
	*/
	public final int 	serdSetXOff						= 21;

	/**
		in C: <CODE>kSERDClearXOffFlag</CODE>
	*/
	public final int 	serdClrXOff						= 22;

	/**
		in C: <CODE>kSERDSendXOn</CODE>
	*/
	public final int 	serdSendXOnConditional			= 23;

	/**
		in C: <CODE>kSERDSendXOnOut</CODE>
	*/
	public final int 	serdSendXOn						= 24;

	/**
		in C: <CODE>kSERDSendXOff</CODE>
	*/
	public final int 	serdSendXOffConditional			= 25;

	/**
		in C: <CODE>kSERDSendXOffOut</CODE>
	*/
	public final int 	serdSendXOff					= 26;

	/**
		in C: <CODE>kSERDResetChannel</CODE>
	*/
	public final int 	serdChannelReset				= 27;

	/**
		in C: <CODE>kSERD230KBaud</CODE><BR>
		 set 230K baud data rate 
	*/
	public final int 	serdSet230KBaud					= 230;


	/**
		in C: <CODE>kSERDInputCount</CODE>
	*/
	public final int 	serdGetBuf						= 2;

	/**
		in C: <CODE>kSERDStatus</CODE>
	*/
	public final int 	serdStatus						= 8;

	/**
		in C: <CODE>kSERDVersion</CODE>
	*/
	public final int 	serdGetVers						= 9;


}
