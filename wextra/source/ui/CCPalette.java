package org.concord.waba.extra.ui;

import extra.util.Maths;

public class CCPalette{
public static waba.fx.Color	[]colors = null;
public static int CC_VERSION_PALETTE = 0xCC;
int	paletteVersion = CC_VERSION_PALETTE;
	private CCPalette(){

	}
	
	private static void createPalette(boolean web){
		int r,g,b,i;
		int count = 0;
		if(colors == null || colors.length != 216){
			colors = new waba.fx.Color[216];
		}
		for(r = 0; r <=255; r += 51){
			for(g = 0; g <=255; g += 51){
				for(b = 0; b <=255; b += 51){
					colors[count++] = new waba.fx.Color(r,g,b);
				}
			}
		}
	}
	private static void createPalette(){
		createPalette(true);
	}
	
	public int getPaletteVersion(){return paletteVersion;}
	
	public static waba.fx.Color[] getPalette(){
		return getPalette(true);
	}
	public static waba.fx.Color[] getPalette(boolean web){
		if(colors != null) return colors;
		createPalette(web);
		return colors;
	}
	
	public static int findNearestColor(int []cmap,int r, int g, int b){
		int index = -1;
		if(cmap == null) return index;
		int minOffset = 0xFFFF;
		for(int i = 0; i < cmap.length; i++){
			int rPal = (cmap[i] & 0xFF0000); rPal >>>= 16;
			int gPal = (cmap[i] & 0xFF00); gPal >>>= 8;
			int bPal = (cmap[i] & 0xFF);
			int tOffset = (Maths.abs(rPal - r) +Maths.abs(gPal - g)+Maths.abs(bPal - b));
			if(tOffset < minOffset){
				index = i;
				minOffset = tOffset;
			}
		}
		return index;
	}
	
	
	
	public static int findNearestColor(int r, int g, int b){
		int index = -1;
		waba.fx.Color	[]cPal = getPalette();
		if(cPal == null) return index;
		int minOffset = 0xFFFF;
		for(int i = 0; i < cPal.length; i++){
			int tOffset = (Maths.abs(cPal[i].getRed() - r) +Maths.abs(cPal[i].getGreen() - g)+Maths.abs(cPal[i].getBlue() - b));
			if(tOffset < minOffset){
				index = i;
				minOffset = tOffset;
			}
		}
		return index;
	}
	public static int findNearestColor(waba.fx.Color col){
		return findNearestColor(col.getRed(), col.getGreen(), col.getBlue());
	}
	public static int findNearestColor(int col){
		int blue 	= (col & 0xFF);	
		int green 	= ((col >> 8) & 0xFF);	
		int red 	= ((col >> 16) & 0xFF);	
//		System.out.println("red "+ red + " green " + green + " blue " + blue);
		
		int index = findNearestColor(red, green, blue);
/*
		if(index >= 0){
			System.out.println("red "+ colors[index].getRed() + " green " + colors[index].getGreen() + " blue " + colors[index].getBlue());
		}
*/
		return index;
	}
	
	
	public static int []getPaletteAsInt(){
		int cmap[] = null;
		waba.fx.Color	[]cPal = getPalette();
		if(cPal == null) return cmap;
		int numColors = cPal.length;
		cmap = new int[numColors];

		for (int i = 0; i < numColors; i++){
			byte red 	= (byte)cPal[i].getRed();
			byte green 	= (byte)cPal[i].getGreen();
			byte blue 	= (byte)cPal[i].getBlue();
			cmap[i] = ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
		}
		return cmap;
	}

}
