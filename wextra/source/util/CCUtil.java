package extra.util;
import waba.fx.Rect;
import waba.fx.Image;
import waba.fx.Graphics;

public class CCUtil{

	public static void fillRectWithPattern(Graphics g, Rect r,Image p){
		if(g == null || r == null || p == null) return;
		int pw = p.getWidth();
		int ph = p.getHeight();
		if(pw <= 0 || ph <= 0) return;
		int xend = r.x + r.width;
		int yend = r.y + r.height;
		
		
		int xstart = r.x;
		int ystart = r.y;
		
		int xCurr;
		
		g.setClip(r.x,r.y,r.width,r.height);
		
		while(ystart < yend){
			xCurr = xstart;
			while(xCurr < xend){
				g.copyRect(p,0,0,pw,ph,xCurr,ystart);
				xCurr += pw;
			}
			ystart += ph;
		}
		g.clearClip();
	}

    public static void drawRect(Graphics g, Rect r){
    	if(g == null || r == null) return;
    	g.drawRect(r.x,r.y,r.width,r.height);
    }
    public static void fillRect(Graphics g, Rect r){
    	if(g == null || r == null) return;
    	g.fillRect(r.x,r.y,r.width,r.height);
    }
    public static void setRect(Rect r,int x, int y, int width, int height){
    	if(r == null) return;
    	r.x 		= x;
    	r.y 		= y;
    	r.width 	= width;
    	r.height 	= height;
    }
    public static boolean ptInRect(int x,int y,Rect r){
    	if(r == null) return false;
    	if(x < r.x || x > r.x + r.width) return false;
    	if(y < r.y || y > r.y + r.height) return false;
    	return true;
    }

	public static void fillWithDotPattern(Graphics g, Rect r,
										  int r1, int g1, int b1,int r2, int g2, int b2){
		if(g == null || r == null) return;
		boolean drawFirst = true;
		boolean firstDrawFirst = true;
		for(int i = r.y; i < r.y + r.height; i++){
			drawFirst = firstDrawFirst;
			for(int j = r.x; j < r.x + r.width; j++){
				if(drawFirst){
					g.setColor(r1,g1,b1);
				}else{
					g.setColor(r2,g2,b2);
				}
				g.drawLine(j,i,j,i);
				drawFirst = !drawFirst;
			}
			firstDrawFirst = !firstDrawFirst;
		}
	}


}
