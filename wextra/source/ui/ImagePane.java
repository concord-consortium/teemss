package org.concord.waba.extra.ui;

public class ImagePane extends waba.ui.Control 
	implements PreferredSize
{
	String imagePath = null;
	waba.fx.Image wImage = null;
	boolean		freeOnPaint = false;
    public static boolean showImages = true;

    protected ImagePane(){};

    public ImagePane(String path){
		imagePath = path;
		freeOnPaint = true;
    }
    public ImagePane(waba.fx.Image wImage){
    	this.wImage = wImage;
		freeOnPaint = false;
    }

    public void onPaint(waba.fx.Graphics g){
		wImage = getImage();
		g.drawImage(wImage,0,0);
		if(freeOnPaint) freeImage();
    }

    protected waba.fx.Image getImage()
    {
    	if(wImage == null){
			wImage = new waba.fx.Image(imagePath);
		}
		return wImage;
    }
    
    public void setRect(int x,int y){
    	setRect(x,y,getImage().getWidth(),getImage().getHeight());
    }
  	public int getPreferredWidth(waba.fx.FontMetrics fm){
  		return (wImage == null)?10:wImage.getWidth()+2;
  	}

  	public int getPreferredHeight(waba.fx.FontMetrics fm){
  		return (wImage == null)?10:wImage.getHeight()+2;
  	}
  
	private Dimension preferrDimension;
	public Dimension getPreferredSize(){
		if(preferrDimension == null){
			preferrDimension = new Dimension(getPreferredWidth(null),getPreferredHeight(null));
		}else{
			preferrDimension.width = getPreferredWidth(null);
			preferrDimension.height = getPreferredHeight(null);
		}
		return preferrDimension;
	}

    public waba.fx.Image getWabaImage(){
		return wImage;
    }
    public void freeImage(){
    	if(wImage != null){
			wImage.free();
			wImage = null;
    	}
    }
    
}
