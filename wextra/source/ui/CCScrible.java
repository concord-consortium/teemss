package org.concord.waba.extra.ui;
import waba.ui.*;
import waba.fx.*;

public class CCScrible extends Container{
DrawArea drawArea;
Button 			clearButton, modeButton,choosePenButton;
CCColorChooser	colorChooser;
CCPenChooser 	penChooser;
Container  		scribbleChooser;
boolean 		isChooserUp = false;
MainWindow 		owner;

	public CCScrible(MainWindow owner,int x,int y,int w,int h){
		this.owner = owner;
		setRect(x, y, w, h);
		scribbleChooser = new Container();
		scribbleChooser.setRect(0, 20, this.width, this.height);
		colorChooser = new CCColorChooser();
		colorChooser.setRect(0, 0, this.width, this.height);
		scribbleChooser.add(colorChooser);
		Rect r = colorChooser.getRect();
		penChooser = new CCPenChooser();
		penChooser.setRect(128, 0, this.width, r.height);
		scribbleChooser.add(penChooser);

		drawArea = new DrawArea();
		drawArea.setRect(0, 20, this.width, this.height - 20);
		add(drawArea);
		
		clearButton = new Button("Clear");
		clearButton.setRect(0, 2, 35, 15);
		add(clearButton);
		
		modeButton = new Button("Eraser");
		modeButton.setRect(40, 2, 35, 15);
		add(modeButton);
		
		choosePenButton = new Button("Pen");
		choosePenButton.setRect(80, 2, 35, 15);
		add(choosePenButton);
	}
	
	public void onPaint(Graphics g){
		paintChildren(g,0,0,width,height);
	}
	
	public void onEvent(Event event){
		if (event.type == ControlEvent.PRESSED){
			if (event.target == clearButton){
				drawArea.clear();
			}else if (event.target == modeButton){
				if(drawArea.getMode() == DrawArea.MODE_NORMAL){
					drawArea.setMode(DrawArea.MODE_ERASE);
					modeButton.setText("Normal");
					owner.setFocus(drawArea);
				}else{
					drawArea.setMode(DrawArea.MODE_NORMAL);
					modeButton.setText("Eraser");
				}
			}else if (event.target == choosePenButton){
				if(isChooserUp){
					remove(scribbleChooser);
					drawArea.setPenColor(colorChooser.getChosenColor());
					byte s = (byte)penChooser.getChosenPenSize();
					drawArea.setPenSize(s,s);
					add(drawArea);
					isChooserUp = false;
				}else{
					remove(drawArea);
					add(scribbleChooser);
					isChooserUp = true;
				}
				drawArea.setMode(DrawArea.MODE_NORMAL);
				modeButton.setText("Eraser");
			}
		}
	}
}

class DrawArea extends Control{
Graphics drawg;
int lastX, lastY;
int lastMoveX, lastMoveY;
org.concord.waba.extra.ui.CCPen	pen;
public static final int MODE_NORMAL = 0;
public static final int MODE_ERASE = 1;

public int	mode = MODE_NORMAL;

boolean	wasFirstEraseRect = false;

waba.fx.Image bufIm = null;
CCDrawPath	pathList = null;
CCDrawPath	currPath = null;
	public DrawArea(){
		pen = new org.concord.waba.extra.ui.CCPen();
		pen.setPenSize((byte)1,(byte)1);
		pen.setPenColor(0,0,0);
	}
	public void setPenColor(int red,int green,int blue){
		pen.setPenColor(red,green,blue);
		if(drawg != null){
			drawg.setColor(pen.red,pen.green,pen.blue);
		}
	}
	public void setPenSize(byte w,byte h){
		pen.setPenSize(w,h);
	}
	public void setPenColor(waba.fx.Color color){
		if(color == null) return;
		setPenColor(color.getRed(),color.getGreen(),color.getBlue());
	}
	
	public int getMode(){return mode;}
	
	public void setMode(int modeScribble){
		int oldMode = mode;
		mode = MODE_NORMAL;
		if(modeScribble == MODE_ERASE){
			mode = MODE_ERASE;
		}
		boolean drawgWasCreated = false;
		if (drawg == null){
			drawg = createGraphics();
			if(drawg != null){
				drawg.setClip(0, 0, this.width, this.height);
				drawg.setColor(pen.red,pen.green,pen.blue);
				drawgWasCreated = true;
			}
		}
		if(drawg != null){
			drawg.setDrawOp(Graphics.DRAW_OVER);
			if(!drawgWasCreated && oldMode == MODE_ERASE){
				drawg.setDrawOp(Graphics.DRAW_XOR);
				drawg.drawRect(lastMoveX - 10,lastMoveY - 10,20,20);
				drawg.setDrawOp(Graphics.DRAW_OVER);
			}
		}
		if(mode == MODE_ERASE){
			wasFirstEraseRect = false;
		}
	}
	
	void drawLine(Graphics g,int x1,int y1,int x2, int y2){
		for(int i = 0; i < pen.w; i++){
			for(int j = 0; j < pen.h; j++){
				g.drawLine(x1+i, y1+j, x2+i, y2+j);
			}
		}
	}

	public void onEvent(Event event){
		if (drawg == null){
			drawg = createGraphics();
			drawg.setClip(0, 0, this.width, this.height);
			drawg.setColor(pen.red,pen.green,pen.blue);
			setMode(mode);
		}
		
		if (event.type == PenEvent.PEN_DOWN){
			PenEvent penEvent = (PenEvent)event;
			lastX = penEvent.x;
			lastY = penEvent.y;
			if(mode == MODE_NORMAL){
				drawLine(drawg,lastX,lastY,penEvent.x,penEvent.y);
			}
			CCDrawPath newPath = new CCDrawPath((mode == MODE_ERASE)?1:0,waba.sys.Vm.isColor(),pen.w,pen.h,
							(mode == MODE_ERASE)?255:pen.red,
							(mode == MODE_ERASE)?255:pen.green,
							(mode == MODE_ERASE)?255:pen.blue);
			if(currPath != null) currPath.setNext(newPath);
			currPath = newPath;
			if(pathList == null) pathList = currPath;
			currPath.openPath();
			if(mode != MODE_NORMAL){
				currPath.addPixel((short)lastX,(short)lastY);
			}
		}else if (event.type == PenEvent.PEN_UP){
			if(currPath != null) currPath.closePath();
		}else if (event.type == PenEvent.PEN_DRAG){
			PenEvent penEvent = (PenEvent)event;
			if(mode == MODE_NORMAL){
				drawLine(drawg,lastX,lastY,penEvent.x,penEvent.y);
			}else{
				drawg.setDrawOp(Graphics.DRAW_XOR);
				drawg.drawRect(lastMoveX - 10,lastMoveY - 10,20,20);
				drawg.setDrawOp(Graphics.DRAW_OVER);
				drawg.setColor(255,255,255);
				drawg.fillRect(penEvent.x - 10,penEvent.y - 10,20,20);
				drawg.setColor(0,0,0);
				drawg.setDrawOp(Graphics.DRAW_XOR);
				drawg.drawRect(penEvent.x - 10,penEvent.y - 10,20,20);
				drawg.setColor(pen.red,pen.green,pen.blue);
				lastMoveX = penEvent.x;
				lastMoveY = penEvent.y;
				drawg.setDrawOp(Graphics.DRAW_OVER);
			}
			lastX = penEvent.x;
			lastY = penEvent.y;
			if(currPath != null){
				currPath.addPixel((short)lastX,(short)lastY);
			}
		}else if((event.type == PenEvent.PEN_MOVE) && (mode == MODE_ERASE)){
			PenEvent penEvent = (PenEvent)event;
			drawg.setColor(0,0,0);
			if(!wasFirstEraseRect){
				lastMoveX = penEvent.x;
				lastMoveY = penEvent.y;
				drawg.drawRect(lastMoveX - 10,lastMoveY - 10,20,20);
				wasFirstEraseRect = true;
			}else{
				drawg.setDrawOp(Graphics.DRAW_XOR);
				drawg.drawRect(lastMoveX - 10,lastMoveY - 10,20,20);
				lastMoveX = penEvent.x;
				lastMoveY = penEvent.y;
				drawg.drawRect(lastMoveX - 10,lastMoveY - 10,20,20);
				drawg.setDrawOp(Graphics.DRAW_OVER);
			}
			drawg.setColor(pen.red,pen.green,pen.blue);
		}
	}

	public void clear(){
		setMode(MODE_NORMAL);
		currPath = null;
		pathList = null;
		repaint();
	}

	public void onPaint(Graphics g){
     		if(bufIm == null) bufIm=new waba.fx.Image(width,height);
		waba.fx.Graphics ig = new waba.fx.Graphics(bufIm);
		ig.setColor(255, 255, 255);
		ig.fillRect(0, 0, this.width, this.height);
		CCDrawPath path = pathList;
		while(path != null){
			int xc = 0,yc = 0;
			int xf = 0,yf = 0;
			ig.setColor(path.rPen,path.gPen,path.bPen);
			for(int p= 0; p < path.currPos; p+=2){
				if(p == 0){
					xc = path.points[p];
					yc = path.points[p+1];
					xf = xc;
					yf = yc;
				}else{
					xf = path.points[p];
					yf = path.points[p+1];
				}
				if(path.type == 0){
					for(int i = 0; i < path.wPen; i++){
						for(int j = 0; j < path.hPen; j++){
							ig.drawLine(xc+i, yc+j, xf+i, yf+j);
						}
					}
				}else{
					ig.fillRect(xc-10,yc-10,20,20);
				}
				xc = xf;
				yc = yf;
			}
			path = path.getNext();
		}
		ig.setColor(0,0,0);
     		g.copyRect(bufIm,0,0,width,height,0,0);
     		ig.free();
	}
}

class CCDrawPath{
CCDrawPath 	next = null;
boolean		colorMode = true;
int			type;
byte 			wPen = 1;
byte 			hPen = 1;
int			rPen = 0;
int			gPen = 0;
int			bPen = 0;
short		[]points = null;
int			currPos;
	public CCDrawPath(int type,boolean colorMode,byte wPen,byte hPen,int rPen,int gPen,int bPen){
		this.type = type;
		this.colorMode = colorMode;
		this.wPen = wPen;
		this.hPen = hPen;
		this.rPen = rPen;
		this.gPen = gPen;
		this.bPen = bPen;
		currPos = 0;
		points = null;
	}
	
	public void 		setNext(CCDrawPath next){this.next = next;}
	public CCDrawPath 	getNext(){return next;}
	
	public void openPath(){
		currPos = 0;
		if(points == null){
			points = new short[2000];
		}
	}
	public void closePath(){
		if(points == null) return;
		if(currPos == 2000) return;
		short []newpoints = new short[currPos];
		waba.sys.Vm.copyArray(points, 0,newpoints,0,currPos);
		points = newpoints;
	}
	public void addPixel(short x,short y){
		if(points == null) return;
		points[currPos++] = x;
		points[currPos++] = y;
	}
	
	public String toString(){
		return ("CCDrawPath type "+type+" color "+colorMode+" wPen "+wPen+" hPen "+hPen+" rPen "+rPen+" gPen "+gPen+" bPen "+bPen+" currPos "+currPos);
	}
}

