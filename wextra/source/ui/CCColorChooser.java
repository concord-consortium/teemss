package org.concord.waba.extra.ui;

public class CCColorChooser extends waba.ui.Control{
waba.fx.Color	[]colors = new waba.fx.Color[256];
public int colorIndex = -1;
waba.fx.Image bufIm = null;
int dx = 8;
int dy = 6;
	public CCColorChooser(){
		int r,g,b,i;
		int count = 0;
		colors[count++] = new waba.fx.Color(0,0,0);
		colors[count++] = new waba.fx.Color(255,255,255);
		for(r = 0; r <=255; r += 51){
			for(b = 0; b <=255; b += 51){
				for(g = 0; g <=255; g += 51){
					if(r == g && r == b && r == 0) continue;
					if(r == g && r == b && r == 255) continue;
					colors[count++] = new waba.fx.Color(r,g,b);
				}
			}
		}

		g = b = 0;
		for(i = 1; i <= 10; i++){
			r = (int)(255f*i/11f+0.5);
			colors[count++] = new waba.fx.Color(r,g,b);
		} 

		r = b = 0;
		for(i = 1; i <= 10; i++){
			g = (int)(255f*i/11f+0.5);
			colors[count++] = new waba.fx.Color(r,g,b);
		} 

		r = g = 0;
		for(i = 1; i <= 10; i++){
			b = (int)(255f*i/11f+0.5);
			colors[count++] = new waba.fx.Color(r,g,b);
		} 

		for(i = 1; i <= 10; i++){
			r = (int)(255f*i/11f+0.5);
			colors[count++] = new waba.fx.Color(r,r,r);
		} 
	}
	
	public void setRect(int x,int y,int w,int h){
		super.setRect(x,y,128,96);
	}
	public void onPaint(waba.fx.Graphics gr){
		if(colors == null) return;
		if(bufIm == null) bufIm=new waba.fx.Image(width,height);
		 waba.fx.Graphics ig = new waba.fx.Graphics(bufIm);
		int xx = 0;
		int yy = 0;

		int xCurr = -1;
		int yCurr = -1;
		for(int i = 0; i < colors.length; i++){
			ig.setColor(colors[i].getRed(),colors[i].getGreen(),colors[i].getBlue());
			ig.fillRect(xx,yy,dx,dy);
			if(i == colorIndex){
				xCurr = xx;
				yCurr = yy;
			}
			if(((i+1) % 16) == 0){
				xx = 0;
				yy += dy;
			}else{
				xx+=dx;
			}
		}
		if(xCurr >= 0 && yCurr >= 0){
			ig.setColor(255,255,255);
			ig.drawRect(xCurr-1,yCurr-1,dx+2,dy+2);
			ig.setColor(0,0,0);
			ig.drawRect(xCurr,yCurr,dx,dy);
		}
		ig.setColor(0,0,0);
		ig.drawRect(0,0,width,height);
     	gr.copyRect(bufIm,0,0,width,height,0,0);
     	ig.free();
	}

	public void onEvent(waba.ui.Event event){
		if (event.type == waba.ui.PenEvent.PEN_DOWN){
			waba.ui.PenEvent penEvent = (waba.ui.PenEvent)event;
			int px = penEvent.x / dx;
			int py = penEvent.y / dy;
			if(px < 0 || px > 15) return;
			if(py < 0 || py > 15) return;
			colorIndex = py*16 + px;
			repaint();
		}
	}
	
	public waba.fx.Color getChosenColor(){
		if(colorIndex >= 0){
			return colors[colorIndex];
		}
		return null;
	}

}
