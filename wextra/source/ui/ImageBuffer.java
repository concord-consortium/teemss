package org.concord.waba.extra.ui;

import waba.ui.*;
import waba.fx.*;

//##################################################################
public class ImageBuffer{
	//##################################################################
	public Image image;
	//==================================================================
	public void free()
	//==================================================================
	{
		if (image != null) image.free();
		image = null;
	}
	//==================================================================
	public boolean isSameSize(int width,int height)
	//==================================================================
	{
		if (image == null) return false;
		if (width <= 0) width = 1;
		if (height <= 0) height = 1;
		return (image.getWidth() == width && image.getHeight() == height);
	}
	//==================================================================
	public Graphics get(int width,int height)
	//==================================================================
	{
		if (width <= 0) width = 1;
		if (height <= 0) height = 1;
		if (image != null) 
			if (image.getWidth() != width || image.getHeight() != height)
				free();
		if (image == null) image = new Image(width,height);
		return new Graphics(image);
	}
	//##################################################################
}
//##################################################################

