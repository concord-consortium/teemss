package org.concord.LabBook;

import waba.ui.*;
import waba.util.*;
import waba.fx.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.util.*;

public class LObjMinimizedView extends LabObjectView
{
public int rColor = 0;
public int gColor = 0;
public int bColor = 255;

	LabObjectPtr minObjPtr;
    public LObjMinimizedView(LabObjectPtr ptr)
    {
		super(null, null, null);
		minObjPtr = ptr;
    }
    
	public void layout(boolean sDone)
    {
	}

	public void onPaint(Graphics g)
	{
		if(minObjPtr != null && minObjPtr.toString() != null){
			g.setColor(rColor,gColor,bColor);
			g.drawText(minObjPtr.toString(), 0, 0);
			FontMetrics fm = getFontMetrics(MainWindow.defaultFont);
			int lineY = fm.getHeight();
			g.setColor(rColor,gColor,bColor);
			g.drawLine(0,lineY, fm.getTextWidth(minObjPtr.toString()), lineY);
		}
	}

	public void close()
	{
	}

	public int getPreferredWidth()
	{
		if(minObjPtr != null && minObjPtr.toString() != null){
			FontMetrics fm = getFontMetrics(MainWindow.defaultFont);
			return fm.getTextWidth(minObjPtr.toString());
		}

		return -1;
	}

	public int getPreferredHeight()
	{
		if(minObjPtr != null && minObjPtr.toString() != null){
			FontMetrics fm = getFontMetrics(MainWindow.defaultFont);
			return fm.getHeight()+2;
		}

		return -1;
	}

	public LabObjectPtr getPtr(){ return minObjPtr; }
}
