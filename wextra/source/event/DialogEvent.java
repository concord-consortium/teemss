package org.concord.waba.extra.event;



import waba.ui.Event;

import waba.ui.Control;

import waba.sys.Vm;



public class DialogEvent extends Event

{

public Object source;

String 	actionCommand;
Object	info;
int            infoType;
public final static int   UNKNOWN 		= 0; 
public final static int   EDIT 			= 1; 
public final static int   CHOICE 		= 2; 
public final static int   PROPERTIES 	= 3; 
public final static int   OBJECT 		= 4; 
	public DialogEvent(Object source, Control c,String actionCommand,Object info){
		this(source,c,actionCommand,info,UNKNOWN);
	}
	public DialogEvent(Object source, Control c,String actionCommand,Object info,int type){

		this.source = source;
		this.info = info;

		this.actionCommand = actionCommand;

		target = c;

		timeStamp = Vm.getTimeStamp();
		infoType = type;
	}
	public DialogEvent(Object source, Control c,String actionCommand){
		this(source,c,actionCommand,null,UNKNOWN);
	}
    public Object getInfo(){return info;}
    public int getInfoType(){return infoType;}
	

    public Object getSource() {return source;}

    /**

     * Returns the command name associated with this action.

     */

    public String getActionCommand() {

        return actionCommand;

    }

}

