package org.concord.LabBook;

import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import waba.ui.*;
import waba.fx.*;
import waba.sys.*;
import waba.util.Vector;
import extra.io.*;

class RowOffsets{
	int startChar;
	int count;
	int startX;
	int length;
}

public class CCStringWrapper{
String str;
CCTextArea owner = null;
int  	beginPos 	= 5;
int		endPos 		= 50;

int		beginRow	= -1;
int		endRow		= -1;
char 	[]chars = null;
	Vector delimiters = null;
byte	[]charWidthMappers = null;
int		rColor = 0;
int		gColor = 0;
int		bColor = 0;

boolean		link 		= false;
int			indexInDict = -1;

	CCStringWrapper(CCTextArea owner,String str,int beginRow){
		this.str = str;
		init(owner, beginRow);
	}

	CCStringWrapper(DataStream in){
		str = in.readString();
		if(str == null) str = "";
		link = in.readBoolean();
		rColor = in.readByte() & 0xFF;
		gColor = in.readByte() & 0xFF;
		bColor = in.readByte() & 0xFF;
		indexInDict = in.readInt();
	}

	public void init(CCTextArea owner, int beginRow)
	{
		this.owner = owner;
		if(owner != null){
			Rect r = owner.getRect();
			beginPos 	= r.x + owner.insetLeft;
			endPos 		= r.x + r.width - owner.insetRight;
			setStr(str,beginRow);
		}		
	}
	
	public void writeExternal(DataStream out){
		out.writeString(str);
		out.writeBoolean(link);
		out.writeByte((byte)rColor);
		out.writeByte((byte)gColor);
		out.writeByte((byte)bColor);
		out.writeInt(indexInDict);
	}

	private void createCharWidthMappers(CCTextArea owner){
		if(charWidthMappers != null || owner == null) return;
		charWidthMappers = owner.getCharWidths();
	}
	
	public String getSubString(int rowIndex)
	{
		RowOffsets rOffset = (RowOffsets)delimiters.get(rowIndex);
		return str.substring(rOffset.startChar, rOffset.startChar + rOffset.count - 1);
	}

	String getStr(){return str;}
	void setStr(String str,int beginRow){
		this.beginRow 	= beginRow;
		if(owner == null){
			this.str = null;
			return;
		}
		
		if(str == null) str = "";

		int    numbTotalRows = (owner.rows == null)?0:owner.rows.getCount();		
		
		this.str = str;
		int currRow = beginRow;
		int x 			= (currRow >= numbTotalRows || owner.rows == null)?beginPos:((CCTARow)owner.rows.get(currRow)).beginPos;
		int limitX = (currRow >= numbTotalRows || owner.rows == null)?endPos:((CCTARow)owner.rows.get(currRow)).endPos;
		int lastWord 	= 0;
		int lastWordX    = x;
		RowOffsets rOffsets = new RowOffsets();
		rOffsets.startChar = 0;
		rOffsets.startX = x;

		int i = 0;
		
		// We should do this on a global basis
		if(charWidthMappers == null){
			createCharWidthMappers(owner);
		}

		int nLines = 0;

		// should guess at how many delimiters we might need
		delimiters = new Vector();
		delimiters.add(rOffsets);

		chars = str.toCharArray();
		while(i < str.length()){
			char c = str.charAt(i);
			int w = charWidthMappers[(int)c];
			if(c == ' '){
				lastWord = i+1;
				lastWordX = x;
			}
			
			if(x + w >= limitX){
				if(lastWord == rOffsets.startChar){
					lastWord = i;
				}else{
					i = lastWord;
				}
				rOffsets.count = lastWord - rOffsets.startChar;
				rOffsets.length = lastWordX - rOffsets.startX;

				rOffsets = new RowOffsets();
				currRow++;
				x = (currRow >= numbTotalRows || owner.rows == null)?beginPos:((CCTARow)owner.rows.get(currRow)).beginPos;
				limitX = (currRow >= numbTotalRows || owner.rows == null)?endPos:((CCTARow)owner.rows.get(currRow)).endPos;
				rOffsets.startChar = i;
				rOffsets.startX = x;
				delimiters.add(rOffsets);			   
			}else{
				x += w;
			}
			i++;
		}

		rOffsets.count = chars.length - rOffsets.startChar;
		rOffsets.length = x - rOffsets.startX;

		this.endRow 	= this.beginRow + delimiters.getCount();
	}
	
	public int getRows(){
		return (endRow - beginRow);
	}
	
	String getFullStr(){
		return str;
	}
	
	public void setMargins(int beginPos,int endPos){
		this.beginPos 	= beginPos;
		this.endPos 	= endPos;
	}
	public void setRows(int beginRow,int endRow){
		this.beginRow 	= beginRow;
		this.endRow 	= endRow;
	}
	
	public void draw(Graphics gr,int firstRow){
		if(gr == null || delimiters == null || owner == null) return;
		int    numbTotalRows = (owner.rows == null)?0:owner.rows.getCount();
		int h = owner.getItemHeight();

		int limitRow = delimiters.getCount();
		int endRow = this.endRow;
		
		if(endRow - beginRow > limitRow) endRow = limitRow + beginRow;
		gr.setColor(rColor,gColor,bColor);

		RowOffsets rOffsets;
		for(int i = beginRow; i < endRow; i++){
			if(i < firstRow) continue;

			int y = CCTextArea.yTextBegin + (i - firstRow)*h;

			rOffsets = (RowOffsets)delimiters.get(i-beginRow);
			int x = rOffsets.startX;

			gr.drawText(chars,rOffsets.startChar, rOffsets.count, x, y);

			if(link){
				int xEnd = x + rOffsets.length;
				int lineY = h - 1;
				if(xEnd > x) gr.drawLine(x,y + lineY,xEnd,y + lineY);
			}
		}
		gr.setColor(0,0,0);

	}
	public static boolean isWordDelimiter(char c){
		switch(c){
		case ' ':
		case '\t':
		case ';':
		case '.':
		case ',':
			return true;
		}
		return false;
	}
	

private static char []wordDelimChars = {' ','\t',';','.',',','/','\\'};	

}

