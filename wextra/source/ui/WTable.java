package org.concord.waba.extra.ui;
import waba.ui.*;
import waba.fx.*;
import extra.util.*;

public class WTable extends Container{
Image bufIm = null;
int		startX;
int		startY;
int		startPenX;
int		startPenY;
boolean	startdrugging = false;
int		selectIndex = 0;

int needPosition = 0;
int pos[] = {0,40};
Column columns = null;
Column currColumn = null;
waba.ui.Edit		activeEditor = new waba.ui.Edit();
boolean			editorActive = false;
Window 		owner;
boolean		columnMoveable = false;
boolean		columnWidthChangeable = false;
boolean		clickable = true;


	public WTable(Window owner){
		this.owner = owner;
	}
	public void deActivateEditor(){
		String fromEditor = null;
		if(editorActive){
			fromEditor = activeEditor.getText();
			remove(activeEditor);
		}
		editorActive = false;
		activeEditor.setText("");
		Column c = columns;
		while(c != null){
			c.restoreCellAfterEditor(fromEditor);
			c = c.getNextColumn();
		}
		
	}
	public void activateEditor(Column cnt,int index){
		deActivateEditor();
		if(!cnt.isEditable()) return;
		if(index < 1 || index> cnt.getRowsCount()) return;
		Control c = cnt.getCell(index);
		if(c instanceof CCLabel){
			activeEditor.setText(((CCLabel)c).getText());
		}
		Rect r = c.getRect();
		Rect rControl = cnt.getRect();
		activeEditor.setRect(r.x+rControl.x,r.y+rControl.y,r.width,r.height);
		editorActive = true;
		add(activeEditor);
		cnt.prepareCellForEditor(index);
	}
	
	public void addColumn(String name){
		addColumn(name,false);
	}
	public void addColumn(String name,boolean editable){
		if(columns == null){
			columns = new Column(name);
			Column.lastColumn = columns;
			columns.setRect(0,0,40,height);
			columns.setEditable(editable);
		}else{
			Rect rLast = Column.lastColumn.getRect();
			Column c = new Column(name);
			c.setPrevColumn(Column.lastColumn);
			Column.lastColumn.setNextColumn(c);
			Column.lastColumn = c;
			c.setRect(rLast.x+rLast.width-1,0,40,height);
			c.setEditable(editable);
		}
/*
		System.out.println("addColumn begin");
		Column c = columns;
		do{
			Rect r = c.getRect();
			System.out.println("Column "+c+" name "+c.getText()+" x "+r.x+" y "+r.y+" w "+r.width+" h "+r.height);
			c = c.next;
		}while(c != null);
		System.out.println("addColumn end");
*/		
		
	}
	public boolean isColumnMoveable(){return columnMoveable;}
	public void setColumnMoveable(boolean columnMoveable){this.columnMoveable = columnMoveable;}
	public boolean isColumnWidthChangeable(){return columnWidthChangeable;}
	public void setColumnWidthChangeable(boolean columnWidthChangeable){this.columnWidthChangeable = columnWidthChangeable;}
	
	public boolean isClickable(){return clickable;}
	public void setClickable(boolean clickable){this.clickable = clickable;}
	
	
	public void selectRow(int index){
		Column c = columns;
		while(c != null){
			c.setSelectIndex(index);
			c = c.getNextColumn();
		}
	}
	
	public int getSelectIndex(){
		if(columns == null) return -1;
		return columns.getSelectIndex();
	}
	
	public Object getCell(int colIndex,int rowIndex){
		int currColIndex = 0;
		Column c = columns;
		while(c != null){
			if(currColIndex == colIndex){
				return c.getCell(rowIndex);
			}
			currColIndex++;
			c = c.next;
		}
		return null;
	}
	
	
	public void drawColumns(Graphics g){
		Column c = columns;
		do{
			Rect r = c.getRect();
			g.translate(r.x,r.y);
			c.onPaint(g);
			g.translate(-r.x,-r.y);
			c = c.next;
		}while(c != null);
		
/*
		if(currColumn != null){
			Rect rDraggedCurrent = currColumn.getDraggedRect();
			g.drawRect(rDraggedCurrent.x,rDraggedCurrent.y,rDraggedCurrent.width,rDraggedCurrent.height);
		}
*/
	}
	
	public Column findColumn(PenEvent e,boolean fixed){
		Column retValue = null;
		Column c = columns;
		do{
			if(currColumn != null && currColumn == c && fixed){
				c = c.next;
				continue;
			}
//			Rect r = (fixed)?c.getFixedRect():c.getRect();
			Rect r = c.getRect();
			if(inRect(e,r)){
				retValue = c;
				break;
			}
			c = c.next;
		}while(c != null);
		return retValue;
	}
	
	final static public int DRAG_NONE				= 0;
	final static public int DRAG_COLUMN_MOVE		= 1;
	final static public int DRAG_COLUMN_SIZE		= 2;
	
	public int dragOperation = DRAG_NONE;
	public void onEvent(Event event){

	   	if(event.type == ControlEvent.FOCUS_OUT){
	   		if(event.target instanceof waba.ui.Edit){
				deActivateEditor();
	   		}
			return;
	   	}
		if(event.type == KeyEvent.KEY_PRESS){
	   		if(event.target instanceof waba.ui.Edit){
				KeyEvent ke = (KeyEvent)event;
				//System.out.println("KeyEvent.KEY_PRESS "+ke.key);
				if (ke.key == IKeys.ENTER){
					deActivateEditor();
				}
	   		}
			return;
		}
		if (event.type == PenEvent.PEN_UP){
			PenEvent penEvent = (PenEvent)event;
			if(startdrugging && currColumn != null){
				switch(dragOperation){
					case DRAG_COLUMN_MOVE:
						updateColumnRects();
						break;
				}
				Graphics g = createGraphics();
				onPaint(g);
				startdrugging = false;
				g.free();
			}
		}else if (isClickable() && (event.type == PenEvent.PEN_DOWN)){
			if(event.target instanceof waba.ui.Edit) return;
			dragOperation = DRAG_NONE;
			PenEvent penEvent = (PenEvent)event;
			Column c = findColumn(penEvent,false);
			if(c == null){
				selectRow(-1);
				Graphics g = createGraphics();
				onPaint(g);
				g.free();
				return;
			}
			Rect rColumn = c.getRect();
			startPenX = penEvent.x;
			int rowIndex = c.getNearestRow(penEvent.y);
			if(rowIndex > 0){
				deActivateEditor();
				selectRow(rowIndex);
				if(c.isEditable()){
					activateEditor(c,rowIndex);	
				}			
				Graphics g = createGraphics();
				onPaint(g);
				g.free();
				if(c.isEditable()){
					owner.setFocus(activeEditor);
				}
//				activeEditor.onEvent(new ControlEvent(ControlEvent.FOCUS_IN,this));
			}else if(rowIndex == 0){
				if(isColumnWidthChangeable() && ((Maths.abs(rColumn.x - penEvent.x) < 4) || (Maths.abs(rColumn.x + rColumn.width - penEvent.x) < 4))){
					if(c != columns || penEvent.x -  rColumn.x > 4){
						dragOperation = DRAG_COLUMN_SIZE;
						deActivateEditor();
						if(Maths.abs(rColumn.x - penEvent.x) < 4){
							currColumn = c.getPrevColumn();
							startX = rColumn.x;
						}else{
							currColumn = c;
							startX = rColumn.x+rColumn.width;
						}
						startdrugging = true;
						return;
					}
				}
				if(isColumnMoveable() && c != null){
					deActivateEditor();
					//System.out.println("Column "+c.getTitle());
					startdrugging = true;
					startX = rColumn.x;
					currColumn = c;
					dragOperation = DRAG_COLUMN_MOVE;
				}
			}
		}else if (event.type == PenEvent.PEN_DRAG){
			PenEvent penEvent = (PenEvent)event;
//			System.out.println("PEN_DRAG  penEvent "+penEvent.x+" y "+penEvent.y);

			if(startdrugging && currColumn != null){
				Rect r = null;
				switch(dragOperation){
					case DRAG_COLUMN_SIZE:
						Column cs = currColumn;
						r = cs.getRect();
						if(startX + penEvent.x - startPenX - r.x < 10) break;
						int delta = startX + penEvent.x - startPenX - r.x - r.width;
						if(getTotalColumWidth() + delta > width) break;

						cs.setRect(r.x,r.y,r.width + delta,r.height);
						while(cs != null){
							cs = cs.getNextColumn();
							if(cs != null){
								r = cs.getRect();
								cs.setRect(r.x + delta,r.y,r.width,r.height);
							}
						}
						break;
					case DRAG_COLUMN_MOVE:
						int xx = startX + penEvent.x - startPenX;
						currColumn.setDraggedRect(xx,currColumn.getRect().y,currColumn.getRect().width,currColumn.getRect().height);
						Column c = findColumn(penEvent,true);
						
						if(c != null && c != currColumn){
							Rect rC = c.getRect();
							Rect rCurr = currColumn.getRect();
							if(rC.x + rCurr.width > penEvent.x){
								changeColumns(currColumn,c);
								updateColumnRects();
							}
						}
						Rect rd = currColumn.getDraggedRect();
						currColumn.setRect(rd.x,rd.y,rd.width,rd.height);
						break;
				}
				Graphics g = createGraphics();
				onPaint(g);
				g.free();
			}

		}
	}

	public void changeColumns(Column X,Column Y){
		Column A = null,B = null,C = null,D = null;
		if(X == null || Y == null) return;
		if(X.getNextColumn() != Y && X.getPrevColumn() != Y){
			B = X.getNextColumn();
			A = X.getPrevColumn();
			D = Y.getNextColumn();
			C = Y.getPrevColumn();
			if(A != null) A.setNextColumn(Y);
			Y.setPrevColumn(A);
			if(B != null) B.setPrevColumn(Y);
			Y.setNextColumn(B);
			if(C != null) C.setNextColumn(X);
			X.setPrevColumn(C);
			if(D != null) D.setPrevColumn(X);
			X.setNextColumn(D);
		}else if(X.getNextColumn() == Y){
			A = X.getPrevColumn();
			D = Y.getNextColumn();
			Y.setPrevColumn(A);
			Y.setNextColumn(X);
			X.setPrevColumn(Y);
			X.setNextColumn(D);
			if(A != null) A.setNextColumn(Y);
			if(D != null) D.setPrevColumn(X);
		}else if(Y.getNextColumn() == X){	
			A = Y.getPrevColumn();
			D = X.getNextColumn();
			X.setPrevColumn(A);
			X.setNextColumn(Y);
			Y.setPrevColumn(X);
			Y.setNextColumn(D);
			if(A != null) A.setNextColumn(X);
			if(D != null) D.setPrevColumn(Y);
		}					
		if(X.getNextColumn() == null) Column.lastColumn = X;
		if(Y.getNextColumn() == null) Column.lastColumn = Y;
		if(X.getPrevColumn() == null) columns = X;
		if(Y.getPrevColumn() == null) columns = Y;
	}


	public void updateColumnRects(){
		Column c = columns;
		int newx = 0;
		while(c != null){
			Rect r = c.getRect();
			c.setRect(newx,r.y,r.width,r.height);
			newx = newx+r.width - 1;
			c = c.getNextColumn();
		}
	}
	
	protected int getTotalColumWidth(){
		Column c = columns;
		int retValue = 0;
		while(c != null){
			Rect r = c.getRect();
			retValue += r.width;
			c = c.getNextColumn();
		}
		return retValue;
	}
	
	
	public void setRect(int x,int y,int width,int height){
		int y0 = 0;
/*
		if(header == null) y0 += header.getRect().height;
		if(rows != null){
			index i = rows.getCount() - 1;
			if(i >= 0){
				y0 += ((Control)rows.get(i)).getrect().height;
			}
		}
*/
		super.setRect(x,y,width,height);
		if(bufIm != null){
			bufIm.free();
			bufIm=new Image(width,height);
		}
		//System.out.println("setRect  w "+this.width + " h "+this.height);
	}
	
	
	public void pack(){
		Column c = columns;
		int nRows = c.getRowsCount() + 1;
		height = nRows*(16 - 1);
		Rect rTable = getRect();
		int nColumns = 0;
		while(c != null){
			nColumns++;
			c = c.getNextColumn();
		}
		int colWidth = (int)((float)rTable.width/(float)nColumns + 0.5f);
		c = columns;
		int x0 = 0;
		while(c != null){
			Rect r = c.getRect();
			c.setRect(x0,r.y,colWidth,height);
			x0 += (colWidth - 1);
			c = c.getNextColumn();
		}
	}
	
	public void onPaint(Graphics g){
/*
		g.setColor(255, 255, 255);
		g.fillRect(0, 0, this.width, this.height);
		drawColumns(g);
*/
      		if(bufIm == null) bufIm=new Image(width,height);
		Graphics ig = new Graphics(bufIm);
		ig.setColor(255, 255, 255);
		ig.fillRect(0, 0, this.width, this.height);
		drawColumns(ig);
     		g.copyRect(bufIm,0,0,width,height,0,0);
     		ig.free();
		if(editorActive){
			Graphics ge = activeEditor.createGraphics();
			activeEditor.onPaint(ge);
			ge.free();
		}
	}
	
	
	public void addRow(String []s){
		CCLabel []l = new CCLabel[s.length];
		for(int i = 0; i < s.length; i++){
			l[i] = new CCLabel(s[i],Label.CENTER);
		}
		addRow(l);
	}


	public void addRow(Control []obj){
		Column c = columns;
		int index = 0;
		while(c != null){
			if(index < obj.length) c.addRow(obj[index++]);
			c = c.getNextColumn();
		}
	}
	public boolean inRect(PenEvent e,Rect r){
		boolean retValue = false;
		if(e == null || r ==null) return retValue;
		retValue = ((e.x >= r.x) && (e.y >= r.y) && (e.x <= r.x+r.width) && (e.y <= r.y+r.height));
		return retValue;
	}
	public void translateEvent(PenEvent e){
		e.x -= x;
		e.y -= y;
	}
}
