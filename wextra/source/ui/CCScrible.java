package org.concord.waba.extra.ui;
import waba.ui.*;
import waba.fx.*;
import extra.io.DataStream;

public class CCScrible extends Container{
DrawArea drawArea;
Button 			clearButton, modeButton,choosePenButton;
CCColorChooser	colorChooser;
CCPenChooser 	penChooser;
Container  		scribbleChooser;
boolean 		isChooserUp = false;
MainWindow 		owner;
static final 	int DATA_PEN		=	1;
static final 	int DATA_PATH	=	2;
private boolean  wasAddComponent = true;
	public CCScrible(MainWindow owner){
		this.owner = owner;
		drawArea = new DrawArea();
		clearButton = new Button("Clear");
		modeButton = new Button("Eraser");
		choosePenButton = new Button("Pen");
		wasAddComponent = false;
	}
	public CCScrible(MainWindow owner,int x,int y,int w,int h){
		this.owner = owner;
		setRect(x, y, w, h);
		createScribleChooser();

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
	
	public boolean isAddComponent(){return wasAddComponent;}
	public boolean isChooserUp(){return isChooserUp;}
	
	public void closeChooser(){
		if(!isChooserUp || scribbleChooser == null) return;
		remove(scribbleChooser);
		add(drawArea);
		drawArea.setPenColor(colorChooser.getChosenColor());
		byte s = (byte)penChooser.getChosenPenSize();
		drawArea.setPenSize(s,s);
		isChooserUp = false;
		drawArea.setMode(DrawArea.MODE_NORMAL);
		modeButton.setText("Eraser");
	}
	
	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		if(wasAddComponent){
			if(drawArea != null) 			remove(drawArea);
			if(clearButton != null) 		remove(clearButton);
			if(modeButton != null) 			remove(modeButton);
			if(choosePenButton != null) 	remove(choosePenButton);
		}
		if(drawArea != null){
			if(embeddedState){
				drawArea.setRect(0, 20, this.width, this.height);
			}else{
				drawArea.setRect(0, 20, this.width, this.height - 20);
			}
			add(drawArea);
		}
		if(!embeddedState){
			if(clearButton != null){
				clearButton.setRect(0, 2, 35, 15);
				add(clearButton);
			}
			if(modeButton != null){
				modeButton.setRect(40, 2, 35, 15);
				add(modeButton);
			}
			if(choosePenButton != null){
				choosePenButton.setRect(80, 2, 35, 15);
				add(choosePenButton);
			}
		}
		wasAddComponent = true;
	}
	
	boolean embeddedState = false;
	public void setEmbeddedState(boolean embeddedState){
//		this.embeddedState = embeddedState;
		this.embeddedState = false;
		if(drawArea != null) drawArea.setEmbeddedState(this.embeddedState);
		Rect r = getRect();
		setRect(r.x,r.y,r.width,r.height);
	}
	
	public void writeExternal(DataStream out){
		if(drawArea == null){
			out.writeBoolean(false); //error state
			return;
		}
		out.writeBoolean(true); //numb of pathes
		drawArea.writeExternal(out);
	}

	public void readExternal(DataStream in){
		if(!in.readBoolean()) return;
		drawArea.readExternal(in);
	}
	
	public void createScribleChooser(){
		scribbleChooser = new Container();
		scribbleChooser.setRect(0, 20, this.width, this.height);
		colorChooser = new CCColorChooser();
		colorChooser.setRect(0, 0, this.width, this.height);
		scribbleChooser.add(colorChooser);
		Rect r = colorChooser.getRect();
		penChooser = new CCPenChooser();
		penChooser.setRect(128, 0, this.width, r.height);
		scribbleChooser.add(penChooser);
	}
	
	public void close(){
		if(drawArea != null) drawArea.close();
		if(isChooserUp){
			if(scribbleChooser != null) remove(scribbleChooser);
		}
		if(colorChooser != null){
			if(scribbleChooser != null) scribbleChooser.remove(colorChooser);
			colorChooser.destroy();
			colorChooser = null;
		}
		if(penChooser != null){
			if(scribbleChooser != null) scribbleChooser.remove(penChooser);
			penChooser.destroy();
			penChooser = null;
		}
		scribbleChooser = null;
	}
	public void destroy(){
		if(drawArea != null){
			drawArea.destroy();
			drawArea = null;
		}
		if(colorChooser != null){
			colorChooser.destroy();
			colorChooser = null;
		}
		if(penChooser != null){
			penChooser.destroy();
			penChooser = null;
		}
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
				if(scribbleChooser == null){
					createScribleChooser();
					isChooserUp = false;
				}
				if(isChooserUp){
					remove(scribbleChooser);
					add(drawArea);
					drawArea.setPenColor(colorChooser.getChosenColor());
					byte s = (byte)penChooser.getChosenPenSize();
					drawArea.setPenSize(s,s);
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
static final 	int DATA_PEN		=	1;
static final 	int DATA_PATH	=	2;
boolean embeddedState = false;
	public DrawArea(){
		pen = new org.concord.waba.extra.ui.CCPen();
		pen.setPenSize((byte)1,(byte)1);
		pen.setPenColor(0,0,0);
	}
	
	public void setEmbeddedState(boolean embeddedState){
		this.embeddedState = embeddedState;
		close();
	}
	public void writeExternal(extra.io.DataStream out){
		out.writeInt(DATA_PATH);
		if(pathList == null){
			out.writeBoolean(false);
		}else{
			out.writeBoolean(true);
			pathList.writeExternal(out);
		}
		out.writeInt(DATA_PEN);
		if(pen == null){
			out.writeBoolean(false);
		}else{
			out.writeBoolean(true);
			pen.writeExternal(out);
		}
	}
	public void readExternal(extra.io.DataStream in){
		int temp = in.readInt();
		if(temp != DATA_PATH) return;
		if(in.readBoolean()){
			pathList = new CCDrawPath(in);
		}
		CCDrawPath path = pathList;
		while(path != null){
			if(path.getNext() == null){
				currPath = path;
			}
			path = path.getNext();
		}
		temp = in.readInt();
		if(temp != DATA_PEN) return;
		if(in.readBoolean()){
			if(pen == null) pen = new CCPen();
			pen.readExternal(in);
		}
	}
	
	public CCDrawPath getPathList(){return pathList;}
	public org.concord.waba.extra.ui.CCPen getDrawPen(){return pen;}
	
	public void close(){
		freeOffImage();
		CCDrawPath path = pathList;
		while(path != null){
			path.setDirty(true);
			path = path.getNext();
		}
	}
	
	public void freeOffImage(){
		if(bufIm != null){
			bufIm.free();
			bufIm = null;
		}
	}
	
	public void destroy(){
		freeOffImage();
		currPath = null;
		pathList = null;
	}
	
	public void setPenColor(int red,int green,int blue){
		pen.setPenColor(red,green,blue);
		createOffGraphics();
		if(drawg != null) drawg.setColor(pen.red,pen.green,pen.blue);
	}
	public void setPenSize(byte w,byte h){
		pen.setPenSize(w,h);
	}
	public void setPenColor(waba.fx.Color color){
		if(color == null) return;
		setPenColor(color.getRed(),color.getGreen(),color.getBlue());
	}
	
	public int getMode(){return mode;}
	
	
	public boolean createOffGraphics(){
		boolean retValue = true;
		if (drawg != null) return retValue;
		drawg = createGraphics();
		retValue = (drawg != null);
		if(drawg != null){
			drawg.setClip(0, 0, this.width, this.height);
		}
		return retValue;
	}
	
	public void setMode(int modeScribble){
		int oldMode = mode;
		mode = MODE_NORMAL;
		if(modeScribble == MODE_ERASE){
			mode = MODE_ERASE;
		}
		boolean drawgWasCreated = false;
		if (drawg == null){
			drawgWasCreated = createOffGraphics();
		}
		if(drawg != null){
			drawg.setDrawOp(Graphics.DRAW_OVER);
			if(!drawgWasCreated && oldMode == MODE_ERASE){
				drawg.setDrawOp(Graphics.DRAW_XOR);
				drawg.drawRect(lastMoveX - 10,lastMoveY - 10,20,20);
				drawg.setDrawOp(Graphics.DRAW_OVER);
			}
			if(modeScribble == MODE_ERASE){
				drawg.setColor(0,0,0);
			}else{
				drawg.setColor(pen.red,pen.green,pen.blue);
			}
		}
		if(mode == MODE_ERASE){
			wasFirstEraseRect = false;
		}
	}
	
	void drawLine(Graphics g,int x1,int y1,int x2, int y2){
		if(g == null) return;
		for(int i = 0; i < pen.w; i++){
			for(int j = 0; j < pen.h; j++){
				g.drawLine(x1+i, y1+j, x2+i, y2+j);
			}
		}
	}

	public void onEvent(Event event){
		if(embeddedState) return;
		
		if (drawg == null){
			createOffGraphics();
			if(drawg != null) drawg.setColor(pen.red,pen.green,pen.blue);
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
			}else if(drawg != null){
				drawg.setDrawOp(Graphics.DRAW_XOR);
				drawg.drawRect(lastMoveX - 10,lastMoveY - 10,20,20);
				drawg.setDrawOp(Graphics.DRAW_OVER);
				drawg.setColor(255,255,255);
				drawg.fillRect(penEvent.x - 10,penEvent.y - 10,20,20);
				drawg.setColor(0,0,0);
				drawg.setDrawOp(Graphics.DRAW_XOR);
				drawg.drawRect(penEvent.x - 10,penEvent.y - 10,20,20);
//				drawg.setColor(pen.red,pen.green,pen.blue);
				drawg.setDrawOp(Graphics.DRAW_OVER);
			}
			lastX = penEvent.x;
			lastY = penEvent.y;
			lastMoveX = lastX;
			lastMoveY = lastY;
			if(currPath != null){
				currPath.addPixel((short)lastX,(short)lastY);
			}
		}else if((event.type == PenEvent.PEN_MOVE) && (mode == MODE_ERASE)){
			PenEvent penEvent = (PenEvent)event;
			if(!wasFirstEraseRect){
				lastMoveX = penEvent.x;
				lastMoveY = penEvent.y;
				if(drawg != null) drawg.drawRect(lastMoveX - 10,lastMoveY - 10,20,20);
				wasFirstEraseRect = true;
			}else{
				if(drawg != null){
					drawg.setDrawOp(Graphics.DRAW_XOR);
					drawg.drawRect(lastMoveX - 10,lastMoveY - 10,20,20);
				}
				lastMoveX = penEvent.x;
				lastMoveY = penEvent.y;
				if(drawg != null){
					drawg.drawRect(lastMoveX - 10,lastMoveY - 10,20,20);
					drawg.setDrawOp(Graphics.DRAW_OVER);
				}
			}
		}
	}

	public void clear(){
		setMode(MODE_NORMAL);
		currPath = null;
		pathList = null;
     		if(bufIm != null){
			waba.fx.Graphics ig = new waba.fx.Graphics(bufIm);
			ig.setColor(255, 255, 255);
			ig.fillRect(0, 0, this.width, this.height);
	     		ig.free();
     		}
		repaint();
	}
	public void createOffImage(){
     	if(bufIm != null){
     		if(bufIm.getWidth() != width || bufIm.getHeight() != height){
     			freeOffImage();
     		}
     	}
     	if(bufIm == null){
	     	bufIm=new waba.fx.Image(width,height);
			waba.fx.Graphics ig = new waba.fx.Graphics(bufIm);
			ig.setColor(255, 255, 255);
			ig.fillRect(0, 0, this.width, this.height);
	     	ig.free();
	     }
	}

	public void onPaint(Graphics g){
		createOffImage();
     		if(bufIm == null) return;
		g.copyRect(bufIm,0,0,width,height,0,0);
		waba.fx.Graphics ig = new waba.fx.Graphics(bufIm);
		CCDrawPath path = pathList;
		while(path != null){
			if(path.getDirty()){
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
				path.setDirty(false);
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
boolean		dirty = true;
public final static int BEGIN_PATH_ITEM = 10000;
public final static int END_PATH_ITEM = 10001;
public final static int END_PATH_LIST = 10002;

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
		dirty = true;
	}
	public CCDrawPath(extra.io.DataStream in){
		int temp = in.readInt();
		if(temp != BEGIN_PATH_ITEM) return;
		type = in.readInt();
		in.readBoolean();//colorMode skip
		wPen = in.readByte();
		hPen = in.readByte();
		rPen = in.readInt();
		gPen = in.readInt();
		bPen = in.readInt();
		currPos = in.readInt();
		temp = in.readInt();
		if(temp > 0){
		    points = new short[temp];
		    for(int i = 0; i < points.length;i++){
			points[i] = in.readShort();
		    }

		}
		temp = in.readInt();
		if(temp == END_PATH_ITEM){
			next = new CCDrawPath(in);
		}
	}
	
	public void writeExternal(extra.io.DataStream out){
		out.writeInt(BEGIN_PATH_ITEM);
		out.writeInt(type);
		out.writeBoolean(colorMode);
		out.writeByte(wPen);
		out.writeByte(hPen);
		out.writeInt(rPen);
		out.writeInt(gPen);
		out.writeInt(bPen);
		out.writeInt(currPos);
		if(points == null){
			out.writeInt(0);
		}else{
			out.writeInt(points.length);
			for(int i = 0; i < points.length; i++){
			    out.writeShort(points[i]);
			}
		}
		if(next != null){
			out.writeInt(END_PATH_ITEM);
			next.writeExternal(out);
		}
		else	out.writeInt(END_PATH_LIST);
	}
	
	public void 		setNext(CCDrawPath next){this.next = next;}
	public CCDrawPath 	getNext(){return next;}
	
	public void 		setDirty(boolean dirty){this.dirty = dirty;}
	public boolean 		getDirty(){return dirty;}

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
		if(currPos + 2 <= points.length){
			points[currPos++] = x;
			points[currPos++] = y;
		}
	}
	
	public String toString(){
		return ("CCDrawPath type "+type+" color "+colorMode+" wPen "+wPen+" hPen "+hPen+" rPen "+rPen+" gPen "+gPen+" bPen "+bPen+" currPos "+currPos);
	}
}

