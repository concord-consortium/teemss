import waba.ui.*;
import waba.fx.*;

public class About extends Container
{
    Button done = new Button("Done");
    MainWindow mw;

    int xStart;
    int yStart;

    Font curFont;
    FontMetrics fm;

    public About(MainWindow mw, int w, int h)
    {
	this.mw = mw;
	setRect(0,0,w,h);
	done.setRect((w-40)/2,h-20, 40, 20);
	add(done);
	xStart = 0;
	curYpos = yStart = 20;
	curFont = mw.defaultFont;
	fm = getFontMetrics(curFont);
    }

    public void onEvent(Event e)
    {
	if(e.target == done && e.type == ControlEvent.PRESSED){
	    mw.remove(this);
	    mw.exit(0);
	}
    }

    int curYpos;

    public void printLine(String text, Graphics g)
    {
	int tWidth = fm.getTextWidth(text);
	g.drawText(text, (width - tWidth)/2, curYpos);
	curYpos += 11;
    }

    public void onPaint(Graphics g)
    {
	g.setColor(0,0,0);
	printLine("ProbeWare Tool", g);
	printLine("© Concord Consortium", g);
	printLine("", g);
	printLine("Development Team: ", g);
	printLine(" Stephen Bannasch", g);
	printLine(" Scott Cytacki", g);
	printLine(" Dmitry Markman", g);
    }
}
