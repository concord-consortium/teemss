import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class GIFReader extends java.awt.Canvas{
Image image = null;
	public static void main(String args[]){
		GIFReader gr = new GIFReader();
		try{
			String ImageName = "Inform";
		
		
		
		
			InputStream is = gr.getClass().getResourceAsStream("icons/windows/"+ImageName+".bmp");
			byte []bt = getBufferFromInputStream(is);
			System.out.println("length "+bt.length);
			Image im = gr.getToolkit().createImage(bt);
      			ImageInfoGrabber iig = new ImageInfoGrabber();
        		if (!iig.grabInfo(im.getSource())) {
            			System.err.println("Error fetching image "+ImageName);
//            			System.exit(2);
        		}
	  		System.out.println("size = " + iig.width + "x" + iig.height);
	        	if(! (iig.colorModel instanceof IndexColorModel)) {
 //           			System.exit(1);
	        	}
	            	IndexColorModel cm = (IndexColorModel)iig.colorModel;
	            	int[] pixelValueCount = new int[1<<cm.getPixelSize()];
	            	int transparentPixelIndex = cm.getTransparentPixel();
	            	System.out.println("transparent pixel index = " +transparentPixelIndex);
			// Count number of times pixel values are used.
			for (int i=0; i<iig.bytePixels.length; i++) {
				pixelValueCount[iig.bytePixels[i]&0xff]++;
			}
			
			int colorCount = 0;
			for (int i=0; i<pixelValueCount.length; i++) {
				if(pixelValueCount[i] > 0) colorCount++;
			}
	            	System.out.println("colorCount = " +colorCount);
			
			{
				Color backColor = new Color(200,200,200);
				FileOutputStream fo = new FileOutputStream(ImageName+"Image.java");
				PrintStream dos = new PrintStream(fo);
				dos.println("import java.awt.Color;");
				dos.println("public class "+ImageName+"Image{");
				dos.println("public int w = "+iig.width + ";");
				dos.println("public int h = "+iig.height + ";");
				dos.println("public Color[] colorMap = 		{");
				for(int i = 0; i < colorCount;i++){
					dos.print("							new Color(");
					if(i == transparentPixelIndex){
						dos.print((backColor.getRed() & 0xFF)+","+(backColor.getGreen() & 0xFF)+","+(backColor.getBlue() & 0xFF));
					}else{
						dos.print((cm.getRed(i) & 0xFF)+","+(cm.getGreen(i) & 0xFF)+","+(cm.getBlue(i) & 0xFF));
					}
					dos.print(")");
					if(i != colorCount - 1) dos.println(",");
					else dos.println();
				}
				dos.println("						};");
				dos.println("public byte[] pixels =		{");
				int colorInLine = -1;
				for(int i = 0; i < iig.bytePixels.length; i++){
					if(colorInLine == -1) dos.print("							");
					dos.print(iig.bytePixels[i]&0xff);
					if(i != iig.bytePixels.length - 1){
						dos.print(",");
					}
					if((++colorInLine == 19) || (i == iig.bytePixels.length - 1)){
						dos.println();
						colorInLine = -1;
					}
				}
				dos.println("						};");
				dos.println("}");
				dos.close();
			}
			Frame f = new Frame("ImageTest");
			f.add(gr);
			f.pack();
			f.reshape(200,200,300,40);
			f.show();
			
			System.out.println("FINISH");
		}catch(Exception e){
			System.out.println("Exception "+e);
		}
	}

	public void paint(Graphics g){
		if(image == null){
			ErrorImageTest it = new ErrorImageTest();
			image = it.createImage();
//			MediaTracker md = new MediaTracker(gr);
//			md.addImage(im,0);
//			md.waitForAll();
		}
		if(image != null){
			g.drawImage(image,0,0,this);
		}
	}
/*
	public void reportParam(){
		if(im == null) return;
		System.out.println("h "+im.getHeight(this) + " w "+im.getWidth(this));
	}
	public boolean imageUpdate(Image img,int infoFlags,int x,int y,int w,int h){
		if((infoFlags & HEIGHT) != 0){
			System.out.println("HEIGHT "+h);
		}
		if((infoFlags & WIDTH) != 0){
			System.out.println("WIDTH "+w);
		}
		if((infoFlags & ALLBITS) != 0){
			reportParam();
		}
		return false;
	}
*/	
	static public	byte[]  getBufferFromInputStream(InputStream is){
		byte []buffer = null;
		if(is == null) return buffer;
		try{
			int	initBufferLength = 4096;
			buffer = new byte[initBufferLength];
			boolean	exitFromLoop = false;
			int		offset = 0;
			while(!exitFromLoop){
				if(buffer.length <= offset){
					byte []newbuffer = new byte[buffer.length + initBufferLength];
					System.arraycopy(buffer,0,newbuffer,0,buffer.length);
					buffer = newbuffer;
				}
				int readed = is.read(buffer,offset,buffer.length - offset);
				exitFromLoop = (readed < 0);
				if(!exitFromLoop){
					offset+=readed;
				}
			}
			if(buffer.length > offset){
				byte []newbuffer = new byte[offset];
				System.arraycopy(buffer,0,newbuffer,0,offset);
				buffer = newbuffer;
			}
		}catch(Exception e){
			buffer = null;
		}
		return buffer;
	}
}
class ImageInfoGrabber implements ImageConsumer {
    // These are the public fields which the client can
    // use to retrieve the image info.
    public int width;
    public int height;
    public int hints;
    public Hashtable properties;
    public ColorModel colorModel;
    public Vector additionalColorModels = new Vector();

    // One of the following fields is null and the other is not.
    public int[] intPixels;
    public byte[] bytePixels;

    // Private fields
    private int status;
    private ImageProducer producer;

    // Returns true if the image was fetched successfully; false otherwise.
    public synchronized boolean grabInfo(ImageProducer ip) {
        status = 0;
        producer = ip;
        producer.startProduction(this);
        try {
            while (status == 0) {
                wait();
            }
        } catch (InterruptedException e) {
            return false;
        }
        return status > 0;
    }

    public void setDimensions(int w, int h) {
        width = w;
        height = h;
    }

    public void setHints(int h) {
        hints = h;
    }

    public void setProperties(Hashtable props) {
        properties = props;
    }

    public void setColorModel(ColorModel cm) {
        colorModel = cm;
    }

    public void setPixels(int srcX, int srcY, int srcW, int srcH,
                          ColorModel cm, byte pixels[], int srcOff, int srcScan) {
        if (cm != colorModel) {
            if (!additionalColorModels.contains(cm)) {
                additionalColorModels.addElement(cm);
            }
        }
        if (bytePixels == null) {
            bytePixels = new byte[width * height];
        }
        for (int x=srcX; x<srcX + srcW; x++) {
            for (int y=srcY; y<srcY + srcH; y++) {
                bytePixels[y * width + x] = 
                    pixels[(y-srcY) * srcScan + (x-srcX) + srcOff];
            }
        }
    }

    public void setPixels(int srcX, int srcY, int srcW, int srcH,
                          ColorModel cm, int pixels[], int srcOff, int srcScan) {
        if (cm != colorModel) {
            if (!additionalColorModels.contains(cm)) {
                additionalColorModels.addElement(cm);
            }
        }
        if (intPixels == null) {
            intPixels = new int[width * height];
        }
        for (int x=srcX; x<srcX + srcW; x++) {
            for (int y=srcY; y<srcY + srcH; y++) {
                intPixels[y * width + x] = 
                    pixels[(y-srcY) * srcScan + (x-srcX) + srcOff];
            }
        }
    }

    public synchronized void imageComplete(int s) {
        switch (s) {
        case STATICIMAGEDONE:
        case SINGLEFRAMEDONE:
            status = 1;
            break;
        default:
        case IMAGEERROR:
            System.out.println("IMAGEERROR");
        case IMAGEABORTED:
            status = -1;
            System.out.println("IMAGEABORTED");
            break;
        }
        producer.removeConsumer(this);
        notifyAll();
    }
}
