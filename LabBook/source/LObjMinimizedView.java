package org.concord.LabBook;

import waba.ui.*;
import waba.util.*;
import waba.fx.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.probs.*;
import org.concord.waba.extra.probware.*;
import extra.ui.*;
import extra.util.*;

public class LObjMinimizedView extends LabObjectView
{
public int rColor = 0;
public int gColor = 0;
public int bColor = 255;
    public LObjMinimizedView(LabObject obj)
    {
		super(null);

		lObj = obj;
    }
    
	public void layout(boolean sDone)
    {
	}

	public void onPaint(Graphics g)
	{
		if(lObj != null && lObj.name != null){
			g.setColor(rColor,gColor,bColor);
			g.drawText(lObj.name, 0, 0);
			FontMetrics fm = getFontMetrics(MainWindow.defaultFont);
			int lineY = fm.getHeight()+1;
			g.drawLine(0,lineY, width, lineY);
		}
	}

	public void close()
	{
	}

	public int getPreferredWidth()
	{
		if(lObj != null && lObj.name != null){
			FontMetrics fm = getFontMetrics(MainWindow.defaultFont);
			return fm.getTextWidth(lObj.name);
		}

		return -1;
	}

	public int getPreferredHeight()
	{
		if(lObj != null && lObj.name != null){
			FontMetrics fm = getFontMetrics(MainWindow.defaultFont);
			return fm.getHeight()+2;
		}

		return -1;
	}
}
