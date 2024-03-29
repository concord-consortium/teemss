package org.concord.waba.extra.ui;

import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.util.CCUtil;
import org.concord.waba.extra.event.*;
import waba.fx.Rect;
import waba.fx.Image;
import waba.fx.Graphics;

public class CCScrollBar extends Control{
	Rect	rBody;
	Rect	rDecrement;
	Rect	rIncrement;
	Rect	rValue;

	Image	pattern;

	int	cValue = 0;

	int		minValue = 0;
	int		maxValue = 0;
	int		value;

	int		allAreaValue = 0;
	int		visAreaValue = 0;
	ScrollListener	listener;

	int	pressState = 0;

	int incValue 		= 0;
	int pageIncValue 	= 0;
	boolean	forceNotify = false;

	public CCScrollBar(ScrollListener listener){
		this.listener = listener;
		rBody 		= new Rect(0,0,0,0);
		rDecrement 	= new Rect(0,0,0,0);
		rIncrement 	= new Rect(0,0,0,0);
		rValue 		= new Rect(0,0,0,0);
		value = this.minValue;
	}
	public void setMinMaxValues(int minValue,int maxValue){
		this.minValue = minValue;
		this.maxValue = maxValue;
		if(this.maxValue < this.minValue) this.maxValue = this.minValue;
		forceNotify = true;
	}

	public void setPageIncValue(int pageIncValue){
		this.pageIncValue = pageIncValue;
		if(this.pageIncValue < incValue) this.pageIncValue = incValue;
		if(this.pageIncValue > (maxValue - minValue)) this.pageIncValue = (maxValue - minValue);
		forceNotify = true;
	}
	
	public void setIncValue(int incValue){
		this.incValue = incValue;
		if(this.incValue < 1) this.incValue = 1;
		if(this.incValue > (maxValue - minValue)) this.incValue = (maxValue - minValue);
		forceNotify = true;
	}

	public void setAreaValues(int allAreaValue,int visAreaValue){
		this.allAreaValue = allAreaValue;
		this.visAreaValue = visAreaValue;
		if(this.allAreaValue < this.visAreaValue) this.allAreaValue = this.visAreaValue;
		if(rValue == null) return;
    	if(this.allAreaValue > this.visAreaValue){
			int dh = 1 + ((width+1) / 2);
    		float fh = 1.0f - (float)(this.allAreaValue - this.visAreaValue)/(float)(this.allAreaValue + this.visAreaValue);
    		int h = (int)((float)rBody.height*fh + 0.5f);
    		if(h < dh) h = dh;
    		if(cValue + h > rBody.height) cValue = rBody.height - h;
    		if(cValue < 0) cValue = 0;
    		CCUtil.setRect(rValue,rBody.x,rBody.y + cValue,rBody.width,h);
    	}else{
    		CCUtil.setRect(rValue,rBody.x,rBody.y,rBody.width,rBody.height);
    	}
		forceNotify = true;
	}


	public void close(){
		if(pattern != null) pattern.free();
		pattern = null;
	}

	public void createPattern(){
		if(pattern != null || rBody == null) return;
		pattern = new Image(rBody.width,rBody.height);
		if(pattern != null){
			Graphics ig = new Graphics(pattern);
			Rect rPat = new Rect(0,0,rBody.width,rBody.height);
			CCUtil.fillWithDotPattern(ig,rPat,255,255,255,0,0,0);
			if(ig != null){
				ig.free();
			}else{
				pattern.free();
				pattern = null;
			}
		}
	}

	public void onPaint(Graphics g){
		if(g == null) return;

    	if(pattern == null)	createPattern();

		g.setColor(0,0,0);
    	if(pattern != null){
    		g.copyRect(pattern,0,0,rBody.width,rBody.height,rBody.x,rBody.y);
    	}
		if(visAreaValue < allAreaValue) CCUtil.fillRect(g,rValue);
		drawUpDownRect(g,true);
		drawUpDownRect(g,false);
	}


	boolean firstPress = true;
	boolean	startDrag = false;
	int		firstClickY = 0;
	int		firstRValueY = 0;
	ScrollEvent scrollEvent;

    public void onEvent(Event e){
		if (e instanceof PenEvent){
			PenEvent pe = (PenEvent)e;
			if (pe.type == PenEvent.PEN_DOWN){
				if (!firstPress) return;
				firstPress = false;
				pressState = 0;
				if(CCUtil.ptInRect(pe.x,pe.y,rDecrement)){
					pressState = -1;
				}else if(CCUtil.ptInRect(pe.x,pe.y,rIncrement)){
					pressState = 1;
				}else if((allAreaValue > visAreaValue) && CCUtil.ptInRect(pe.x,pe.y,rValue)){
					firstClickY = pe.y;
					firstRValueY = rValue.y;
					startDrag = true;
				}else if(CCUtil.ptInRect(pe.x,pe.y,rBody)){
					if(pe.y < rValue.y){
						pressState = -2;
					}else if(pe.y > rValue.y+rValue.height){
						pressState = 2;
					}
				}
				if(pressState == -1 || pressState == 1){
					Graphics g = createGraphics();
					if(g != null){
						drawUpDownRect(g,(pressState == -1));
						g.free();
					}
				}
			}else if (pe.type == PenEvent.PEN_UP) {
				int scrollType = 0;
				boolean doNotify = false;
				if(startDrag){
					startDrag = false;
					scrollType = ScrollEvent.SCROLL_DRAG_FINISH;
					doNotify = true;
				}else if(pressState != 0){
					int oldCValue = cValue;
					int oldValue = value;
					if(pressState == -1){
						scrollType = ScrollEvent.SCROLL_DECREMENT;
						setValue(value - incValue);
					}else if(pressState == 1){
						scrollType = ScrollEvent.SCROLL_INCREMENT;
						setValue(value + incValue);
					}else if(pressState == -2){
						scrollType = ScrollEvent.SCROLL_PAGE_DEC;
						setValue(value - pageIncValue);
					}else if(pressState == 2){
						scrollType = ScrollEvent.SCROLL_PAGE_INC;
						setValue(value + pageIncValue);
					}
					setRValueRect();
					doNotify = (forceNotify || (oldCValue != cValue));	
					if(forceNotify) forceNotify = false;
				}
				firstPress = true;
				if(scrollType != 0 && doNotify && listener != null){
					if(scrollEvent == null){
						scrollEvent = new ScrollEvent(this,scrollType,value);
					}else{
						scrollEvent.type = scrollType;
						scrollEvent.scrollValue = value;
					}
					listener.scrollValueChanged(scrollEvent);
				}
				if(pressState != 0){
					pressState = 0;
					repaint();
				}
			}else if(pe.type == PenEvent.PEN_DRAG){
				if(startDrag){
					int oldValue = cValue;
					int newY = firstRValueY + (pe.y - firstClickY);
					setRValue(newY - rBody.y);
					value = minValue;
					if(rBody.height > rValue.height){
						float v = (float)cValue *(float)(maxValue - minValue)/(float)(rBody.height - rValue.height);
						value = (v >= 0f) ? value + (int)(v+0.5f) : value + (int)(v-0.5f);
					}
					if(oldValue != cValue){
						setRValueRect();
						Graphics g = createGraphics();
						if(g != null){
							g.setColor(0,0,0);
					    	if(pattern != null){
					    		g.copyRect(pattern,0,0,rBody.width,rBody.height,rBody.x,rBody.y);
					    	}
							CCUtil.fillRect(g,rValue);
							g.free();
						}
						if(listener != null){
							if(scrollEvent == null){
								scrollEvent = new ScrollEvent(this,ScrollEvent.SCROLL_DRAG_INPROCESS,value);
							}else{
								scrollEvent.type = ScrollEvent.SCROLL_DRAG_INPROCESS;
								scrollEvent.scrollValue = value;
							}
							listener.scrollValueChanged(scrollEvent);
						}
					}
				}
			}

		} else if(e instanceof KeyEvent){
		  KeyEvent ev = (KeyEvent)e;
		  
		  move(ev.key);
		}
    }

    public void move(int key)
    { 
	  int scrollType = 0;

	  if(key == IKeys.UP){
		scrollType = ScrollEvent.SCROLL_DECREMENT;
		setValue(value - incValue);
	  } else if(key == IKeys.DOWN){
		scrollType = ScrollEvent.SCROLL_INCREMENT;
		setValue(value + incValue);
	  } else if(key == IKeys.PAGE_UP){
		scrollType = ScrollEvent.SCROLL_PAGE_DEC;
		setValue(value - pageIncValue);
	  } else if(key == IKeys.PAGE_DOWN){
		scrollType = ScrollEvent.SCROLL_PAGE_INC;
		setValue(value + pageIncValue);
	  }
	  setRValueRect();
	  if(scrollType != 0 && listener != null){
		if(scrollEvent == null){
		  scrollEvent = new ScrollEvent(this,scrollType,value);
		}else{
		  scrollEvent.type = scrollType;
		  scrollEvent.scrollValue = value;
		}
		listener.scrollValueChanged(scrollEvent);
		repaint();
	  }

	}
    
    public void setRect(int x, int y, int width, int height){
		super.setRect(x, y, width, height);

		int dh = 2 + ((width+1) / 2);
    	CCUtil.setRect(rBody,2,dh,width - 4,height - 2*dh);
    	CCUtil.setRect(rDecrement,0,0,width,dh);
    	CCUtil.setRect(rIncrement,0,height-dh,width,dh);
    	setRValueRect();
    }
    public void setRValueRect(){
    	if(rValue == null) return;
    	if(allAreaValue > visAreaValue){
			int dh = 1 + ((width+1) / 2);
    		int h = (int)((float)rBody.height*(float)visAreaValue/(float)allAreaValue + 0.5f);
    		if(h < dh) h = dh;
    		CCUtil.setRect(rValue,rBody.x,rBody.y + cValue,rBody.width,h);
    	}else{
    		CCUtil.setRect(rValue,rBody.x,rBody.y,rBody.width,rBody.height);
    	}
    }
    
    public void setValue(int value){
    	this.value = value;
    	if(this.value < minValue) this.value = minValue;
    	if(this.value > maxValue) this.value = maxValue;
		if(maxValue > minValue && (rBody != null) && (rValue != null)){
			setRValue((int)(0.5f+(float)(value - minValue)*(float)(rBody.height - rValue.height)/(float)(maxValue - minValue)));
		}else{
			setRValue(0);
		}
    }
    
    public int getValue(){return value;}
    
	public void setRValue(int cValue){
		this.cValue = cValue;
		if(this.cValue > rBody.height - rValue.height) this.cValue = rBody.height - rValue.height; 
		if(this.cValue < 0) this.cValue = 0; 
	}

    public void drawUpDownRect(Graphics g,boolean up){
    	if(g == null) return;
    	Rect rForDraw = (up)?rDecrement:rIncrement;
    	if(rForDraw == null) return;
		int dh = (width+1) / 2;
    	int yStart = rForDraw.y + 1;
    	int xStart = (up)?rForDraw.x+((width-1) / 2):rForDraw.x;
    	int l = (up)?0:width - 1;
    	int delta = (up)?1:-1;
    	g.setColor(0,0,0);
    	boolean press = (up)?(pressState == -1):(pressState == 1);
    	if(press){
    		CCUtil.fillRect(g,rForDraw);
    		g.setColor(255,255,255);
    	}
    	for(int i = 0; i < dh; i++){
    		g.drawLine(xStart,yStart,xStart+l,yStart);
    		yStart++;
    		xStart -= delta;
    		l += 2*delta;
    	}
    	g.setColor(0,0,0);
    }
    

}
