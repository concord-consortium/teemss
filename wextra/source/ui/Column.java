package org.concord.waba.extra.ui;
import waba.ui.*;
import waba.fx.*;


public class Column extends Container{
Column next = null;
Column prev = null;
public static Column	firstColumn = null;
public static Column	lastColumn = null;
Rect	draggedRect;
CCLabel	header;
waba.util.Vector	rows;	
int		selectIndex = 0;
Control			removedControl = null;
boolean	editable = true;
	public Column(String name){
		this(name,null);
	}
	public Column(String name,Column next){
//		super(name,Label.CENTER);
		super();
		header = new CCLabel(name,Label.CENTER);
		add(header);
		header.setRect(0,0,0,0);
		this.next = next;
		if(firstColumn == null) 	firstColumn = this;
	}
	
	public boolean isEditable(){return editable;}
	public void setEditable(boolean editable){this.editable = editable;}
	
	public String getTitle(){return (header == null)?null:header.getText();}
	
	public String toString(){
		String s = "Column "+getTitle()+";  PREV ";
		if(prev == null) s += "NULL";
		else s += prev.getTitle();
		s += "; NEXT ";
		if(next == null) s += "NULL";
		else s += next.getTitle();
		s += ("   x="+x+" y="+y+" w="+width+" h="+height);
		return s;
	}
	
	public void prepareCellForEditor(int index){
		if(index < 1 || index> getRowsCount() || !editable) return;
		Control c = (Control)rows.get(index - 1);
		remove(c);
		removedControl = c;
	}
	public void restoreCellAfterEditor(String fromEditor){
		if(removedControl == null || !editable) return;
		add(removedControl);
		if(fromEditor != null && (removedControl instanceof CCLabel)) ((CCLabel)removedControl).setText(fromEditor);
	}
	
	public int getNearestRow(int yy){
		int index = -1;
		Rect r = header.getRect();
		if(r.y < yy && yy < r.y + r.height){
			index = 0;
		}else{
			for(int i = 0; i < getRowsCount(); i++){
				Control c = (Control)rows.get(i);
				r = c.getRect();
				if(r.y < yy && yy < r.y + r.height){
					index = i + 1;
					break;
				}
			}
		}
		return index;
	}
	public int getSelectIndex(){return selectIndex;}
	public void setSelectIndex(int selectIndex){
		this.selectIndex = selectIndex;
	}
	
	public int getRowsCount(){
		if(rows == null) return 0;
		return rows.getCount();
	}
	public Control getCell(int index){
		Control c = null;
		if(index < 1 || index > getRowsCount()) return c;
		return (Control)rows.get(index - 1);
	}
	public void addRow(Control c){
		if(rows == null) 	rows = new waba.util.Vector();
		rows.add(c);
		add(c);
		int index = rows.getCount() - 2;
		Control prev = null;
		if(index < 0){
			prev = header;
		}else{
			prev = (Control)rows.get(index);
		}
		if(prev == null) return;
		Rect r = prev.getRect();
		c.setRect(r.x,r.y+r.height - 1,r.width,16);
	}
	

	public void onPaint(Graphics g){
		Rect rC = null;
		CCLabel label = null;
		if(selectIndex > 0 && selectIndex <= rows.getCount()){
			Control c = (Control)rows.get(selectIndex - 1);
			if(c instanceof CCLabel){
				label = (CCLabel)c;
				label.setBackColor(0,0,255);
				label.setTextColor(255,255,255);
			}
		}
		paintChildren(g,0,0,width,height);
		if(label != null){
			label.setTextColor(0,0,0);
			label.setBackColor(255,255,255);
		}
		g.setColor(0,0,0);
		rC = header.getRect();
		g.drawRect(rC.x,rC.y,rC.width,rC.height);
		for(int r = 0; r < rows.getCount(); r++){
			Control c = (Control)rows.get(r);
			rC = c.getRect();
			g.drawRect(rC.x,rC.y,rC.width,rC.height);
		}
		g.drawRect(0,0,width,height);
	}
	
	public Column getNextColumn(){return next;}
	public Column getPrevColumn(){return prev;}
	
	public void setNextColumn(Column next){
		this.next = next;
	}
	public void setPrevColumn(Column prev){
		this.prev = prev;
	}
	public void setLastColumn(Column lastColumn){
		this.lastColumn = lastColumn;
	}
	public void onEvent(Event event){
		if (event.type == PenEvent.PEN_DOWN){
			System.out.println("PEN_DOWN Column  name "+getTitle()+" x "+x+" y "+y+" w "+width+" h "+height);
		}
	}
	public void setRect(int x,int y,int w,int h){
		super.setRect(x,y,w,h);
		header.setRect(0,0,w,16);
		if(rows == null) return;
		for(int i = 0; i < rows.getCount(); i++){
			Control c = (Control)rows.get(i);
			Rect r = c.getRect();
			c.setRect(r.x,r.y,w,r.height);
		}
	}
	
	public Rect getHeaderRect(){return header.getRect();}
	
	public void setDraggedRect(int x,int y,int w,int h){
		if(draggedRect == null) draggedRect = new Rect(x,y,w,h);
		else{
			draggedRect.x = x;
			draggedRect.y = y;
			draggedRect.width = w;
			draggedRect.height = h;
		}
	}
	public Rect getDraggedRect(){return draggedRect;}

}
