package graph;

import waba.fx.*;
import waba.ui.*;
import waba.sys.*;
import extra.util.Maths;


public class BarSet extends Object
{

    public Color linecolor = null;
    public Axis axis = null;

    // DataSet stuff
    protected int length;
    protected int nBars;
    protected int oldBarLen[];
    protected int barWidth;
    protected float lenScale;
    protected int maxHeight = 0;

    // Axis stuff
    public int start;
    protected TextLine label    = new TextLine("0");
    protected TextLine [] digitals;
    public TextLine [] labels;
    protected boolean interleaveLabels = false;
    
    protected int orientation;
    protected int barPos[];
    protected int barDir = 1;
    public Color upColor = null;
    public Color downColor = null;

    static final int MINDIGITS = 1;
    static final int MAXDIGITS = 4;
    static final int BAR_SPACE = 2;

    public BarSet(Axis a, int num, int orien)
    {
	axis = a;
	nBars = num;
	oldBarLen = new int[num];
	barPos = new int[num];
	orientation = orien;
	length = 0;
	barDir = -1;
	upColor = new Color(0, 0, 0);
	downColor = new Color(255,255,255);
	label.maxDigits = 1;
	label.minDigits = 1;
	labels = new TextLine[num];
	digitals = new TextLine[num];
	for(int i=0; i<num; i++){
	    labels[i] = new TextLine("");
	    digitals[i] = new TextLine("00.0");
	    digitals[i].maxDigits = 1;
	    digitals[i].minDigits = 1;
	    digitals[i].background = downColor;
	    digitals[i].color = upColor;
	    labels[i].background = downColor;
	    labels[i].color = upColor;
	}
    }

    public void reset()
    {
	// Length scale
	lenScale = axis.yScale;
	maxHeight = axis.axisLen - 1;

	length = 0;
    }

    void setBarColor(JGraphics graphics, int percent)
    {
	int r, g, b;
	r = 0;
	b = 0;
	g = 0;

	if(percent < 25){
	    b = 255;
	    g = (255 * percent) / 25;
	} else if(percent < 50){
	    g = 255;
	    b = (255 * (50 - percent)) / 25;
	} else if(percent < 75){
	    g = 255;
	    r = (255 * (percent - 50)) / 25;
	} else {
	    r = 255;
	    g = (255 * (100 - percent)) / 25;
	} 
	
	graphics.setColor(r, g, b);

	return;
    }

    public void drawLabel(JGraphics g, float val, int index)
    {
	digitals[index].clear(g);

	digitals[index].text = label.fToString(val);
	
	int labelY = start+1;
	if(interleaveLabels && (index % 2) == 1){
	    labelY += label.getHeight();
	}
	// clear old label
	
	digitals[index].drawCenter(g, barPos[index]+barWidth/2,
			 labelY, label.TOP_EDGE);

    }

    /*
     *  The JGraphics should be translated 0,0 is the top left
     *  corner of the DataWindow
     */
    public void addColorPoint(JGraphics g, float x, float val[])
    {
	int barPercent;
	int i;
	int barLen;
	
	if(length > 0){
	    for(i=0; i<nBars; i++){
		barLen = barDir*(int)((val[i] - axis.minimum) * lenScale);
		if(barLen < 0){
		    barLen = 0;
		} else if(barLen > (barDir*axis.axisLen)){
		    barLen = (barDir*axis.axisLen);
		}
		if(oldBarLen[i] > barLen){
		    g.setColor(downColor);
		    // Hack for bottom only
		    g.fillRect(barPos[i], start - oldBarLen[i], 
			       barWidth,(oldBarLen[i]) - barLen);
		} 
	    }
	} 

	for(i=0; i<nBars; i++){
	    barLen = barDir* (int)((val[i] - axis.minimum) * lenScale);
	    if(barLen < 0){
		barLen = 0;
	    } else if(barLen > (barDir*axis.axisLen)){
		barLen = barDir*axis.axisLen;
	    }
	    if(barDir*axis.axisLen == 0){
		barPercent = 0;
	    } else {
		barPercent = (int)((barLen * 100) / (barDir*axis.axisLen));
	    }
	    
	    setBarColor(g, barPercent);
	    // Hack for bottom only bar graphs
	    g.fillRect(barPos[i], start - barLen, 
		       barWidth, barLen);
	    oldBarLen[i] = barLen;
	    
	    // also need to put label on bottom
	    drawLabel(g, val[i], i);
	    
	}

	length++;
    }

    /*
     *  The JGraphics should be translated 0,0 is the top left
     *  corner of the DataWindow
     */
    public void addPoint(JGraphics g, float x, float val[])
    {
	int i;
	int barLen;

	// Should check the length of val

	if(length > 0){
	    for(i=0; i<nBars; i++){
		barLen = (int)((val[i] - axis.minimum) * lenScale);
		if(oldBarLen[i] > barLen){
		    g.setColor(upColor);
		    g.fillRect(barPos[i], start + barLen, barWidth, 
			       (oldBarLen[i] - barLen));
		} else {
		    g.setColor(downColor);
		    g.fillRect(barPos[i], start + oldBarLen[i], 
			       barWidth,(barLen - oldBarLen[i]));
		}
		oldBarLen[i] = barLen;

		// also need to put label on bottom
	    }
	} else {
	    // Watch out for color changes
	    for(i=0; i<nBars; i++){
		g.setColor(upColor);
		barLen = (int)((val[i] - axis.minimum) * lenScale);
		// Hack for bottom only bar graphs
		g.fillRect(barPos[i], start + barLen, 
			   barWidth, barDir*barLen);
		oldBarLen[i] = barLen;

		// also need to put label on bottom
	    }
	}

	length++;

    }

	/* This is a hack
	 * it should take into account the orientation
	 */
    public void draw(JGraphics g, int x, int y, int aLen, int gLen)
    {
	// Figure out the postions and widths of the bars and labels
	int maxSpacing;
	float barScale;
	int i;

	barScale = (float)((float)(aLen - BAR_SPACE)/ (float)nBars);
	maxSpacing = (int)barScale;
	// Keep at least a 2:1 ratio of height:width of the bars 
	if(maxSpacing > barDir*axis.axisLen / 2){
	    maxSpacing = barDir*axis.axisLen / 2;
	}

	barWidth = maxSpacing - BAR_SPACE;
	
	for(i=0; i<nBars; i++){
	    barPos[i] = (int)(barScale * i) + BAR_SPACE + x;
	}

	// Draw the axis line
	start = y-1;

	// Length scale
	lenScale = axis.yScale;
	maxHeight = axis.axisLen - 1;

	// When this funct is called the background has been cleared
	// so tell all our labels so they don't draw in their old pos
	for(i=0; i<nBars; i++){
	    digitals[i].cleared = true;
	    labels[i].cleared = true;
	}

	configLabels();

	// also need to put text label on bottom
	int labelY = start+1+label.getHeight();
	if(interleaveLabels){
	    labelY += label.getHeight();
	}
	for(i=0; i<nBars; i++){
	    labels[i].drawCenter(g, barPos[i]+barWidth/2,
				     labelY, label.TOP_EDGE);
	}

    }

    public void configLabels()
    {

	// Compute how to print the labels
	label.text = label.fToString(axis.minimum);
	int max_label_width = label.getWidth();
	label.text = label.fToString(axis.maximum);
	max_label_width = Maths.max(max_label_width, label.getWidth()); 
	interleaveLabels = false;
	if(max_label_width + 2 > barWidth){
	    interleaveLabels = true;
	}

    }

    public int getWidth(int estHeight)
    {
	return 0;
    }

    public int getHeight(int estWidth)
    {
	// should compute height
	barWidth = (estWidth - BAR_SPACE)/nBars - BAR_SPACE; 

	configLabels();
	if(interleaveLabels){
	    return 3*label.getHeight();
	} else {
	    return 2*label.getHeight();
	}
    }

}













