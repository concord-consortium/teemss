package org.concord.waba.extra.ui;

public class CCColorChooser extends waba.ui.Control{
waba.fx.Color	[]colors = null;
public int colorIndex = -1;
waba.fx.Image bufIm = null;
int dx = 8;
int dy = 6;
	public CCColorChooser(){
		colors = CCPalette.getPalette();
	}
	
	public void destroy(){
		if(bufIm != null){
			bufIm.free();
			bufIm = null;
		}
	}
	
	public void setRect(int x,int y,int w,int h){
		super.setRect(x,y,130,86);
	}
	
	public void createOffImage(){
		if(bufIm != null) return;
		bufIm=new waba.fx.Image(width,height);
		 waba.fx.Graphics ig = new waba.fx.Graphics(bufIm);
		int xx = 1;
		int yy = 1;
		ig.setColor(200,200,200);
		ig.fillRect(xx,yy,width,height);
		for(int i = 0; i < colors.length; i++){
			ig.setColor(colors[i].getRed(),colors[i].getGreen(),colors[i].getBlue());
			ig.fillRect(xx,yy,dx,dy);
			if(((i+1) % 16) == 0){
				xx = 1;
				yy += dy;
			}else{
				xx+=dx;
			}
		}
		ig.setColor(0,0,0);
		ig.drawRect(0,0,width,height);
		ig.drawLine(width,height-7,width-64,height - 7);
		ig.drawLine(width-64,height - 7,width-64,height - 1);
		ig.setColor(200,200,200);
		ig.drawLine(width-63,height-1,width-1,height-1);
		ig.drawLine(width-1,height-6,width-1,height - 1);
    	ig.free();
	}
	
	public void onPaint(waba.fx.Graphics g){
		if(colors == null) return;
		createOffImage();
     		g.copyRect(bufIm,0,0,width,height,0,0);
		int xCurr = dx * (colorIndex % 16);
		int yCurr = dy * (colorIndex / 16);
		drawChosenRectFrame(g,xCurr,yCurr);
	}

	public void drawChosenRectFrame(waba.fx.Graphics g, int x, int y){
		if(x >= 0 && y >= 0){
			g.setColor(255,255,255);
//			g.drawRect(x-1,y-1,dx+2,dy+2);
			g.drawRect(x,y,dx,dy);
			g.setColor(0,0,0);
//			g.drawRect(x,y,dx,dy);
			g.drawRect(x+1,y+1,dx-2,dy-2);
		}
	}

	public void onEvent(waba.ui.Event event){
		if (event.type == waba.ui.PenEvent.PEN_DOWN){		
			waba.ui.PenEvent penEvent = (waba.ui.PenEvent)event;
			int px = (penEvent.x - 1) / dx;
			int py = (penEvent.y - 1) / dy;
			if(px < 0 || px > 15) return;
			if(py < 0 || py > 15) return;
			int oldIndex = colorIndex;
			colorIndex = py*16 + px;
			if(colorIndex >= colors.length){
				colorIndex = oldIndex;
				return;
			}
			waba.fx.Graphics g =  createGraphics();
			if(g == null){
				repaint();
				return;
			}else{
				if(oldIndex >= 0){
					int xOld = 1 + dx * (oldIndex % 16);
					int yOld = 1 + dy * (oldIndex / 16);
					g.setColor(colors[oldIndex].getRed(),colors[oldIndex].getGreen(),colors[oldIndex].getBlue());
					g.fillRect(xOld,yOld,dx,dy);
				}
				int xNew = 1 + dx * (colorIndex % 16);
				int yNew = 1 + dy * (colorIndex / 16);
				drawChosenRectFrame(g,xNew,yNew);
			} 
		}
	}
	
	public waba.fx.Color getChosenColor(){
		if(colorIndex >= 0){
			return colors[colorIndex];
		}
		return null;
	}

}
