package org.concord.waba.extra.ui;

import java.awt.datatransfer.*;
import java.awt.Toolkit;


public class CCClipboard {

	private CCClipboard(){}
	
	public static boolean isClipboardEmpty(){
		boolean retValue = true;
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		if(t == null) return retValue;
		try{
			if(t.isDataFlavorSupported(DataFlavor.stringFlavor)){
				retValue = false;
			}
		 }catch(Exception e){
			retValue = true;
		 }
		 return retValue;
	}
	
	public static String getStringContent(){
		String retString = null;
		if(isClipboardEmpty()) return retString;
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		try{
			Object o = t.getTransferData(DataFlavor.stringFlavor);
			if(o instanceof String){
				retString = (String)o;
			}
		 }catch(Exception e){
			retString = null;
		 }
		 return retString;
	}

}