package org.concord.LabBook;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;
import extra.util.CCUnit;
import org.concord.waba.extra.event.*;
//LabObject implements Storable
public class LObjImageView extends LabObjectView implements ActionListener
{
	Button clearButton,convertButton,doneButton,dirButton;
	Choice catChoice;
	Choice currChoiceFrom,currChoiceTo;
	Edit	numberLeft,numberRight;
	boolean leftToRight = true;
    Menu menu = null;
	ImagePane	imagePane = null;
	//	private 	byte []bytes = null;
	Label	nameLabel;
	Edit 	nameEdit;
	boolean	nameEditWasAdded = false;
	
	
	private int			imageBPP 					= -1;
	private int			[]imageCMAP					= null;
	private int			imageScanlen 				= -1;
	private int			imageHeight 				= -1;
	private int			imageWidth					= -1;
	private byte		[]imagePixels				= null;
	private boolean		needCreateImageAfterRead 	= false;
    private boolean isWinCE = false;
	
	LObjImage iObj;

	
	public LObjImageView(ViewContainer vc, LObjImage image,boolean edit){
		super(vc, (LabObject)image, null);
		if(waba.sys.Vm.getPlatform().equals("WinCE")) isWinCE = true;
		iObj = image;
	}

	public void delMenus(){
		if(container != null) container.getMainView().delMenu(this, menu);
	}
	public void addMenus(){
		addMenus(container);
	}

	public void addMenus(ViewContainer vc){
		
		if(menu == null){
			menu = new Menu("Image");
			menu.add("Load Image ...");
			menu.addActionListener(this);
		}
		if(vc != null) vc.getMainView().addMenu(this, menu);
	}
	public void setEmbeddedState(boolean embeddedState){
		boolean oldState = getEmbeddedState();
		super.setEmbeddedState(embeddedState);
		if(oldState != getEmbeddedState()){
			if(nameEdit != null){
				if(imagePane != null) remove(imagePane);
				if(getEmbeddedState()){
					if(nameEditWasAdded){
						remove(nameEdit);
						if(nameLabel != null) remove(nameLabel);
						if(imagePane != null) add(imagePane);
					}
					nameEditWasAdded = false;
				}else{
					if(!nameEditWasAdded){
						if(nameLabel != null) add(nameLabel);
						add(nameEdit);
						if(imagePane != null) add(imagePane);
					}
					nameEditWasAdded = true;
				}
			}
			if(getEmbeddedState()){
				if(imagePane != null) imagePane.setRect(0,0);
			}else{
				if(imagePane != null) imagePane.setRect(0,17);
			}
		}
	}

    public void onPaint(waba.fx.Graphics g){

    	super.onPaint(g);
    }
    public void writeExternal(DataStream out){
  		if(imageCMAP == null || imagePixels == null || imageScanlen <=0 || imageHeight <= 0 || 
		   imageWidth <= 0 || (imageBPP != 1 && imageBPP != 4 && imageBPP != 8)){
    		out.writeBoolean(false); //error state
			return;
		}
    	out.writeBoolean(true); //error state
    	out.writeInt(2); //version
    	out.writeInt(imageBPP);
    	out.writeInt(imageScanlen);
    	out.writeInt(imageHeight);
    	out.writeInt(imageWidth);
    	
		int [] curColorTable = CCPalette.getSystemPalette();
		boolean systemPalette = false;
		if(imageCMAP.length == curColorTable.length){
			systemPalette = true;
			for(int j=0; j < imageCMAP.length; j++){
				if(imageCMAP[j] != (curColorTable[j] & 0xFFFFFF)){
					systemPalette = false;
					break;
				}
			}
		} 

		if(systemPalette){
			// version -1 of the system palette
			out.writeInt(-1);
		} else {
			out.writeInt(imageCMAP.length); //length of array
			for(int i = 0; i < imageCMAP.length; i++){
				out.writeInt(imageCMAP[i]);
			}
		}

 		out.writeInt(imagePixels.length);
		out.writeBytes(imagePixels,0,imagePixels.length);
    }

    public void readExternal(DataStream in){
		needCreateImageAfterRead = false;
   		if(!in.readBoolean()) return;
		if(in.readInt() != 2) return; //I'm not ready for version not equal to 2
		imageBPP  		= in.readInt();
		imageScanlen	= in.readInt();
		imageHeight		= in.readInt();
		imageWidth		= in.readInt();
		int len = in.readInt();
		if(len == -1){
			// this is version -1 of the system palette
			imageCMAP = CCPalette.getSystemPalette();
		} else {
			imageCMAP = new int[len];
			for(int i = 0; i < len; i++){
				imageCMAP[i] = in.readInt();
			}
		}

		len = in.readInt();
		imagePixels = new byte[len];
		in.readBytes(imagePixels,0,len);
		
    	if(imagePane != null){
    		remove(imagePane);
    	}
     	imagePane = null;
     	needCreateImageAfterRead = true;
	}
    public void layout(boolean sDone){
    
		if(didLayout) return;
		didLayout = true;
		if(doneButton != null){
			remove(doneButton);
		}
		
		showDone = sDone;
		if(showDone){
			if(doneButton == null) doneButton = new Button("Done");
			add(doneButton);
		}
		if(nameEdit == null) nameEdit = new Edit();
		nameEdit.setText(getLabObject().getName());
		if(nameLabel == null) nameLabel = new Label("Name");
		if(!getEmbeddedState()){
			add(nameLabel);
			add(nameEdit);
			nameEditWasAdded = true;
		}

	}

	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		if(!didLayout) layout(showDone);

		if(doneButton != null){
			doneButton.setRect(width-30,1,30,15);
		}
		if(!getEmbeddedState() && nameEdit != null && nameEditWasAdded){
			if(nameLabel != null) nameLabel.setRect(1,1,30,15);
			int editW = (showDone)?width - 62:width - 32;
			if(nameEdit != null) nameEdit.setRect(30, 1, editW, 15);
		}

    	if(needCreateImageAfterRead){
    		if(imageWidth > 0 && imageHeight > 0){
	 			waba.fx.Image wabaImage = new waba.fx.Image(imageWidth,imageHeight);
				if(wabaImage != null){
				    int tmpCmap [] = imageCMAP;
				    if(isWinCE){
						tmpCmap = new int [256];
						waba.sys.Vm.copyArray(imageCMAP, 0, tmpCmap, 0, imageCMAP.length);
				    }

					/*
					int [] paletteInts = CCPalette.getSystemPalette();
					int [] curColorTable = paletteInts;
					if(imageCMAP.length == curColorTable.length){
						for(int j=0; j < imageCMAP.length; j++){
							if(imageCMAP[j] != (curColorTable[j] & 0xFFFFFF)){
								break;
							}
						}
					} 
					*/

					wabaImage.setPixels(imageBPP,tmpCmap,imageScanlen,imageHeight,0,imagePixels);
					imagePane = new ImagePane(wabaImage);
					add(imagePane);
					if(getEmbeddedState()){
						imagePane.setRect(0,0);
					}else{
						imagePane.setRect(0,17);
					}
				}
			}
			needCreateImageAfterRead = false;
   		}
	}
	public int getPreferredWidth(){
		if(imageWidth <= 0) return 10;
		return imageWidth;
	}

	public int getPreferredHeight(){
		if(imageHeight <= 0) return 10;
		return imageHeight;
	}

	private extra.ui.Dimension preferrDimension;
	public extra.ui.Dimension getPreferredSize(){
		if(preferrDimension == null){
			preferrDimension = new extra.ui.Dimension(getPreferredWidth(),getPreferredHeight());
		}else{
			preferrDimension.width = getPreferredWidth();
			preferrDimension.height = getPreferredHeight();
		}
		return preferrDimension;
	}

	// override this to not store the image everytime its closed
    public void close(){
		if(imagePane != null) imagePane.freeImage();
		imagePane = null;
		needCreateImageAfterRead = true;
		setShowMenus(false);
		if(session != null) session = null;
    	if(nameEdit != null){
    		getLabObject().setName(nameEdit.getText());
    	}
    }

	public void onEvent(Event e){
		if(e.target == doneButton &&
		   e.type == ControlEvent.PRESSED){
			if(container != null){
				container.done(this);
			}	
		}
	}

    public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Load Image ...")){
			openFileDialog();
		}
    }
    
    public void openFileDialog(){
    	String []extensions = {".bmp",".BMP"};
    	FileDialog fd = FileDialog.getFileDialog(FileDialog.FILE_LOAD,extensions);
    	if(fd == null) return;
    	fd.show();
    	byte []bytes = fd.getBytesFromFile();
    	if(bytes == null) return;
    	if(imagePane != null){
    		remove(imagePane);
    	}
		waba.fx.Image wabaImage = createImage(bytes);
		if(wabaImage == null) return;
		imagePane = new ImagePane(wabaImage);
		add(imagePane);
		if(getEmbeddedState()){
			imagePane.setRect(0,0);
		}else{
			imagePane.setRect(0,17);
		}
		iObj.setName(fd.getFile());
		nameEdit.setText(iObj.getName());
    	
    }
    
    public void loadImage(byte []buffer){
    	if(buffer == null) return;
		createImage(buffer,false);
    	if(imagePane != null){
    		remove(imagePane);
    		imagePane = null;
    	}
    }

    public void loadImage(String url){
    	if(url == null) return;
    	byte bytes[] = null;
		waba.io.File wabaFile = new waba.io.File(url, waba.io.File.READ_ONLY);
		if(wabaFile.exists()){
	    	int length = wabaFile.getLength();
	    	if(length > 0){
	    		bytes = new byte[length];
	    		int nBytes = wabaFile.readBytes(bytes,0,length);
	    		if(nBytes != length){
	    			bytes = null;
	    		}
	    	}
	    	wabaFile.close();
		}
		
		if(bytes == null) return;
		createImage(bytes,false);
    	if(imagePane != null){
    		remove(imagePane);
    		imagePane = null;
    	}
    }
    
	private static int inGetUInt32(byte bytes[], int off)
	{
		return ((bytes[off + 3]&0xFF) << 24) | ((bytes[off + 2]&0xFF) << 16) |
			((bytes[off + 1]&0xFF) << 8) | (bytes[off]&0xFF);
	}


	// Intel architecture getUInt16

	private static int inGetUInt16(byte bytes[], int off)
	{
		return ((bytes[off + 1]&0xFF) << 8) | (bytes[off]&0xFF);
	}

	private waba.fx.Image createImage(byte data[]){
		return createImage(data,true);
	}

	private waba.fx.Image createImage(byte data[], boolean doSetPixel)
	{
		// read header (54 bytes)
		// 0-1   magic chars 'BM'
		// 2-5   uint32 filesize (not reliable)
		// 6-7   uint16 0
		// 8-9   uint16 0
		// 10-13 uint32 bitmapOffset
		// 14-17 uint32 info size
		// 18-21 int32  width
		// 22-25 int32  height
		// 26-27 uint16 nplanes
		// 28-29 uint16 bits per pixel
		// 30-33 uint32 compression flag
		// 34-37 uint32 image size in bytes
		// 38-41 int32  biXPelsPerMeter (unused)
		// 32-45 int32  biYPelsPerMeter (unused)
		// 46-49 uint32 colors used (unused)
		// 50-53 uint32 important color count (unused)
		byte header[] = new byte[54];
		int dataOffset = 0;
		if (!waba.sys.Vm.copyArray(data,dataOffset,header,0,54)){
			//		System.out.println("ERROR: can't read image header");
			return null;
		}

		dataOffset += 54;
		if (header[0] != 'B' || header[1] != 'M') {
			//		System.out.println("ERROR:  is not a BMP image");
			return null;
		}

		int bitmapOffset = inGetUInt32(header, 10);
		int infoSize = inGetUInt32(header, 14);
		if (infoSize != 40){
			//		System.out.println("ERROR:  is old-style BMP");
			return null;
		}

		int width = inGetUInt32(header, 18);
		int height = inGetUInt32(header, 22);
		if (width < 0 || height < 0 || width > 65535 || height > 65535){
			//		System.out.println("ERROR:  has invalid width/height");
			return null;
		}

		int bpp = inGetUInt16(header, 28);
		if (bpp != 1 && bpp != 4 && bpp != 8){
			//		System.out.println("ERROR:  is not a 2, 16 or 256 color image");
			return null;
		}

		int compression = inGetUInt32(header, 30);
		if (compression != 0) {
			//		System.out.println("ERROR:  is a compressed image");
			return null;
		}

		int numColors = 1 << bpp;// it's wrong
		int scanlen = (width * bpp + 7) / 8; // # bytes
		scanlen = ((scanlen + 3) / 4) * 4; // end on 32 bit boundry

		// read colormap
		//
		// 0-3 uint32 col[0]
		// 4-7 uint32 col[1]
		// ...
		int cmapSize = bitmapOffset - 54;
		byte cmapData[] = new byte[cmapSize];
		if (!waba.sys.Vm.copyArray(data,dataOffset,cmapData,0,cmapSize)){
			//		System.out.println("ERROR: can't read colormap");
			return null;
		}
		dataOffset += cmapSize;
		//dima
		numColors = cmapSize / 4; //4 is size of RGBQUAD structure
		//dima

		int cmap[] = new int[numColors]; 
		int j = 0;
		for (int i = 0; i < numColors; i++){
			byte blue = cmapData[j++];
			byte green = cmapData[j++];
			byte red = cmapData[j++];
			j++; // skip reserved
			cmap[i] = ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
		}
	
		byte pixels[] = new byte[scanlen*height];
		int pixelOffset = pixels.length - scanlen;
		do{
			if (!waba.sys.Vm.copyArray(data,dataOffset,pixels,pixelOffset,scanlen)){
				return null;
			}
			dataOffset += scanlen;
			pixelOffset -= scanlen;
		}while(pixelOffset >= 0);
	
		if(bpp == 8){
			byte [] transColMap = new byte [cmap.length];
			for(int i=0; i < cmap.length; i++){
				int col = cmap[i];
				transColMap[i] = CCPalette.getColorIndex(col >> 16 & 0xFF,
														 col >> 8 & 0xFF,
														 col & 0xFF);
			}

			byte [] oldPixels = pixels;
			byte [] newPixels = new byte [pixels.length];
			for(int i=0; i<pixels.length; i++){
				if((int)(pixels[i] & 0xFF) < transColMap.length){  
					newPixels[i] = transColMap[oldPixels[i] & 0x0FF];
				} else {
					newPixels[i] = 0;
				}
			}
			pixels = newPixels;
			cmap = CCPalette.getSystemPalette();
		}

		imageHeight = height;
		imageBPP	= bpp;
		imageCMAP	= cmap;
		imagePixels = pixels;
		imageScanlen = scanlen;
		imageWidth = width;
	
		int tmpCmap [] = imageCMAP;
		if(isWinCE){
			tmpCmap = new int [256];
			waba.sys.Vm.copyArray(imageCMAP, 0, tmpCmap, 0, imageCMAP.length);
		}
	
		waba.fx.Image wi = null;
		if(doSetPixel){
			wi = new waba.fx.Image(width,height);
			if(wi != null) wi.setPixels(imageBPP,tmpCmap,imageScanlen,imageHeight,0,imagePixels);
		}
		
		iObj.store();
		return wi;

	}
	
	public void loadImage(int imageWidth,int imageHeight,int imageBPP,int []imageCMAP,
						  int imageScanlen,byte []imagePixels){
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.imageBPP = imageBPP;
		this.imageCMAP = imageCMAP;
		this.imageScanlen = imageScanlen;
		this.imagePixels = imagePixels;
		iObj.store();
	}

	private void pixelsToRGB(int bitsPerPixel, int width, byte pixels[], int pixelOffset,
							 int rgb[], int rgbOffset, int cmap[])
	{
		int mask, step;
		if (bitsPerPixel == 1){
			mask = 0x1;
			step = 1;
		} else if (bitsPerPixel == 4) {
			mask = 0x0F;
			step = 4;
		} else { // bitsPerPixel == 8
			mask = 0xFF;
			step = 8;
		}

		int bit = 8 - step;
		int bytnum = pixelOffset;
		int byt = pixels[bytnum++];
		int x = 0;
		while (true){
			int colorIndex = ((mask << bit) & byt) >> bit;
			rgb[rgbOffset++] = cmap[colorIndex] | (0xFF << 24);
			if (++x >= width)
				break;
			if (bit == 0){
				bit = 8 - step;
				byt = pixels[bytnum++];
			} else {
				bit -= step;
			}
		}
	}
}
