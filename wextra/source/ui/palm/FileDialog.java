
package org.concord.waba.extra.ui;


public class FileDialog{
public final static int		FILE_LOAD = 0;
    public final static int     FILE_SAVE = 1;
	private FileDialog(int type,String []extensions){
	}
	public static FileDialog getFileDialog(int type,String []extensions){
		return null;
	}
	
	public void show(){
	}
	
    public String getDirectory(){
    	return null;
    }
    public String getFile(){
    	return null;
    }
    public String getFilePath(){
    	return null;
    }
    public byte []getBytesFromFile(){
    	return null;
    }

    public void setFile(String file){}

}
