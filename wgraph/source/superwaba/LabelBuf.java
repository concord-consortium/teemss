package graph;

import waba.fx.*;
import waba.ui.*;

public class LabelBuf extends Label
{
    Image buffer = null;
    Graphics bufG;

    String text;
    Font font = MainWindow.defaultFont;
    int align = LEFT;

    public LabelBuf(String t){
	super(t);
	text = t;
    }

    public void draw()
    {
	onPaint(createGraphics());
    }

    public void setText(String s)
    {
	text = s;
	draw();
    }
	
    public void setFont(Font f)
    {
	font = f;
	super.setFont(f);
    }

    public void onPaint(Graphics g)
    {
	if(buffer == null){
	    Rect r = getRect();
	    buffer = new Image(r.width, r.height);
	    bufG = new Graphics(buffer);
	}

	bufG.setColor(255,255,255);
	bufG.fillRect(0,0,width,height);
	bufG.setColor(0,0,0);

	bufG.setFont(font);
	FontMetrics fm = getFontMetrics(font);
	int x = 0;
	int y = (this.height - fm.getHeight()) / 2;
	if (align == CENTER)
		x = (this.width - fm.getTextWidth(text)) / 2;
	else if (align == RIGHT)
		x = this.width - fm.getTextWidth(text);
	bufG.drawText(text, x, y);

	if(g != null){
	    g.copyRect(buffer, 0, 0, width, height, 0, 0); 	    
	}
    }

}
