
package org.concord.waba.extra.ui;


public class FileDialog{
public final static int		FILE_LOAD = java.awt.FileDialog.LOAD;
    public final static int     FILE_SAVE = java.awt.FileDialog.SAVE;
java.awt.FileDialog 		fileDialog = null;
	private FileDialog(int type,String []extensions){
		int tp = type;
    	fileDialog = new java.awt.FileDialog((java.awt.Frame)waba.applet.Applet.currentApplet.getParent(),null,tp);
    	MyFilter fl = new MyFilter(fileDialog,extensions);
    	fileDialog.setFilenameFilter(fl);
	}
	public static FileDialog getFileDialog(int type,String []extensions){
		return new FileDialog(type,extensions);
	}
	
	public void show(){
		if(fileDialog == null) return;
		fileDialog.show();
	}
	
    public String getDirectory(){
    	if(fileDialog == null) return null;
    	return fileDialog.getDirectory();
    }
    public String getFile(){
    	if(fileDialog == null) return null;
    	return fileDialog.getFile();
    }

    public void setFile(String file){
	if(fileDialog == null) return;
	fileDialog.setFile(file);
    }
    public String getFilePath(){
    	if(fileDialog == null) return null;
    	String dirName = 	getDirectory();
    	String fileName = 	getFile();
    	if(dirName == null || fileName == null) return null;
    	java.io.File file = new java.io.File(dirName,fileName);
    	String retValue = null;
    	try{
    		retValue = file.getCanonicalPath();
    	}catch(Exception e){
    		retValue = null;
    	}
    	return retValue;
    }
    
    public byte []getBytesFromFile(){
    	byte bytes[] = null;
    	if(fileDialog == null) return bytes;
    	String filePath = getFilePath();
    	if(filePath == null) return bytes;
    	waba.io.File wabaFile = new waba.io.File(filePath, waba.io.File.READ_ONLY);
    	if(!wabaFile.exists()) return bytes;
    	
    	int length = wabaFile.getLength();
    	if(length > 0){
    		bytes = new byte[length];
    		int nBytes = wabaFile.readBytes(bytes,0,length);
    		if(nBytes != length){
    			bytes = null;
    		}
    	}
    	wabaFile.close();
    	return bytes;
    }
    
}
class MyFilter implements java.io.FilenameFilter{
	java.awt.FileDialog fd = null;
	String []extensions = null;
	public MyFilter(java.awt.FileDialog fd,String []extensions){
		this.fd 			= fd;
		this.extensions 	= extensions;
	}
	public boolean accept(java.io.File dir,String name){
		if(extensions == null) return true;
		if(name == null || fd == null || fd.getMode() != java.awt.FileDialog.LOAD) return false;
		for(int i = 0; i < extensions.length; i++){
			if(name.endsWith(extensions[i])) return true;
		}
		return false;
	}
}
