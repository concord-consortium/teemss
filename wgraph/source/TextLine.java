package graph;

import waba.fx.*;
import waba.ui.*;
import extra.util.Maths;


public class TextLine extends Object {
    // Constant
    public final static int RIGHT = 0;
    public final static int LEFT = 1;
    public final static int UP = 2;
    public final static int DOWN = 3;


    public final static int RIGHT_EDGE = 0;
    public final static int LEFT_EDGE = 1;
    public final static int TOP_EDGE = 2;
    public final static int BOTTOM_EDGE = 3;

    public int minDigits = 1;
    public int maxDigits = 4;

  /**
   * Font to use for text
   */
    public Font font     = null;
    public FontMetrics fontMet = null;
  /**
   * Text color
   */
    public Color color   = null;
  /**
   * Background Color
   */
    public Color background   = null;
    
    

  /**
   * The text to display
   */
    public  String text   = null;

    /**
     * the text direction
     */
    public int direction = 0;

    protected boolean cleared = false;
    protected int x,y;
    protected int width = 0;
    protected int height = 0;
    protected int textWidth = 0;
    protected int textHeight = 0;

    public TextLine(String s) {
	this.text = s;
	font = MainWindow.defaultFont;
	fontMet = MainWindow.getMainWindow().getFontMetrics(font);	
    }

    public TextLine(String s, Font f, Color c) {
	this(s);
	font = f;
	fontMet = MainWindow.getMainWindow().getFontMetrics(font);
	color = c;
    }

    public TextLine(String s, int d){
	this(s);
	direction = d;
    }

    

      /**
   * Convert float with correct digits
   */
  public String fToString(float val)
  {
    // We don't want any exponents printed so we need to do this ourselves.
        int j;
	char intChars[];
	char fltChars[];
	int len, nLen;
	int start=0, end;
	int exp=0;
	int multExp;
	int count;
	String absLabel;

	for(j=0; j<maxDigits; j++) val *= 10;

	if(((int)val) == 0){
	    return new String("0.0");
	}

	intChars = String.valueOf((int)Maths.abs(val)).toCharArray();
	len = intChars.length;

	if(len <= maxDigits){
	    fltChars = new char[maxDigits + 2];
	    fltChars[0] = '0';
	    fltChars[1] = '.';
	    for(j=0; j < maxDigits - len; j++){
		fltChars[2+j] = '0';
	    }
	    start = 2+j;
	    for(j=0; j < len; j++)
		fltChars[start + j] = intChars[j];
	} else {
	    fltChars = new char [len + 1];
	    for(j=0; j < len - maxDigits; j++){
		fltChars[j] = intChars[j];
	    }
	    fltChars[j] = '.';
	    for(; j < len; j++)
		fltChars[j + 1] = intChars[j];
	}

	end = fltChars.length - 1;
	for(j=0; j < maxDigits - minDigits; j++){
	    if(fltChars[end - j] != '0') break;
	}
	

	absLabel = new String(fltChars, 0, fltChars.length - j);

	if(val < 0)
	    return new String("-" + absLabel);
	else 
	    return absLabel;

  }    



    public boolean  parseText()
    {
	textWidth = fontMet.getTextWidth(text);
	textHeight = fontMet.getHeight();
	if(direction < 2){
	    width = textWidth;
	    height = textHeight;
	} else {
	    height = textWidth;
	    width = textHeight;
	}

	return true;
    }


    public int getWidth()
    {
	if(!parseText())
	    return 0;

	return width;
    }
    
    public int getHeight()
    {
	if(!parseText())
	    return 0;

	return height;
    }

    /* Draw starting at the upper right hand corner
     */

    public void drawRight(JGraphics g, int x, int y)
    {
	JGraphics lg;

	if(!parseText()){
	    return;
	}

	lg = g.create();
	
	if(background != null && !cleared) {
	    lg.setColor(background);
	    lg.fillRect(x, y, width, height);
	    lg.setColor(g.getColor());
	}

	if(font != null) lg.setFont(font);
	if(color != null) lg.setColor(color);

	lg.drawString(text, x, y);

	lg.dispose();

	lg = null;
	this.x = x;
	this.y = y;
	cleared = false;
	return;
    }

    public void draw(JGraphics g, int x, int y)
    {
	Image offsI = null;
	Image rotImage = null;
	JGraphics offsG = null;
	Graphics rotG = null;

	if(!parseText()){
	    return;
	}

	if(direction == RIGHT){
	    drawRight(g, x, y);
	    return;
	}

	offsI = new Image(textWidth, textHeight);
	offsG = new JGraphics(offsI);

	if(background == null) {
	    // HACK we should copy the current background to this back
	    // ground
	    background = new Color(255,255,255);
	}

	offsG.setFont(g.getFont());
	offsG.setColor(g.getColor());

	drawRight(offsG, 0, 0);

	rotImage = new Image(width, height);
	rotG =  new Graphics(rotImage);
	rotateImage(offsI, rotG);
	rotG.free();
	offsG.dispose();
	offsI.free();

	g.drawImage(rotImage, x, y);
	rotImage.free();
	this.x = x;
	this.y = y;
    }

    public void clear(JGraphics g)
    {
	if(cleared == false){
	    g.setColor(background);
	    g.fillRect(x,y,width,height);
	    cleared = true;
	}
    }

    public void drawCenter(JGraphics g, int x, int y, int edge)
    {
	int x0, y0;

	if(!parseText()){
	    return;
	}

	switch(edge){
	case RIGHT_EDGE:
	    x0 = x - width - 1;
	    y0 = y - height/2;
	    break;
	case LEFT_EDGE:
	    x0 = x;
	    y0 = y - height/2;
	    break;
	case TOP_EDGE:
	    x0 = x - width/2;
	    y0 = y;
	    break;
	case BOTTOM_EDGE:
	default :
	    x0 = x - width/2;
	    y0 = y - height - 1;
	    break;
	}

	draw(g, x0, y0);
    }

    
    public void rotateImage(Image srcImg, Graphics destG) 
    {
	int x, y;
	int tmpOffset;

	switch(direction){
	case UP:
	    tmpOffset = height - 1;
	    for(y = 0 ; y < textHeight; y++) {
		for(x = 0; x < textWidth; x++) {
		    destG.copyRect(srcImg, x, y, 1, 1, y, tmpOffset - x);
		}
	    }
	    break;
	case DOWN:
	    tmpOffset = width - 1;
	    for(y =0; y < textHeight; y++) {
		for(x = 0; x < textWidth; x++){
		    destG.copyRect(srcImg, x, y, 1, 1, tmpOffset - y, x);
		}
	    }
	    break;
	case LEFT:
	    for(y = 0; y < textHeight; y++) {
		for( x=0; x< textWidth; x++){
		    destG.copyRect(srcImg, x, y, 1, 1, width - x - 1, height - y - 1); 
		}
	    }
	    break;
	default:
	}
	
	return;

    }

}













