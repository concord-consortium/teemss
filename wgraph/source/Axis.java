package graph;

import waba.ui.*;
import waba.fx.*;
import waba.util.*;
import extra.util.Maths;


public class Axis extends Object {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int BOTTOM = 2; 
    public static final int TOP = 3;


    static final int NUMBER_OF_TICS = 4;
    static final boolean MAJOR = true;
    static final boolean MINOR = false;

    public boolean ticsInside = true;
    public int labelDirection = TextLine.RIGHT;
    public boolean drawgrid = false;
    public boolean drawzero = false;
    public Color gridcolor = null;
    public Color zerocolor = null;
  /**
   * Rescale the axis so that labels fall at the end of the Axis. Default
   * value <i>false</i>.
   */
    public boolean force_end_labels = false;
  /**
   * Size in pixels of the major tick marks
   */
    public int     major_tic_size = 5;
  /**
   * Size in pixels of the minor tick marks
   */
    public int     minor_tic_size  = 3;
  /**
   * Number of minor tick marks between major tick marks
   */
    public int     minor_tic_count = 1;
  /**
   * Color of the Axis.
   */
    public Color   axiscolor;
  /**
   * Minimum data value of the axis. This is the value used to scale
   * data into the data window. This is the value to alter to force
   * a rescaling of the data window.
   */
    public float minimum;
  /**
   * Maximum data value of the axis. This is the value used to scale
   * data into the data window. This is the value to alter to force
   * a rescaling of the data window.
   */
    public float maximum;
    public float scale;
    public int offset;
    public String exponentString = null;
    public int label_spacing = 4;
    public boolean use_exponent = true;
/*
***********************
** Protected Variables      
**********************/
  /**
   * The orientation of the axis. Either Axis.HORIZONTAL or
   * Axis.VERTICAL
   */
      protected int orientation;

  /**
   * The position of the axis. Either Axis.LEFT, Axis.RIGHT, Axis.TOP, or
   * Axis.BOTTOM
   */
      protected int position;

  /**
   * The width of the Axis. Where width for a horizontal axis is really 
   * the height
   */
    protected int width = 0;
    protected int height = 0;

  /**
   * Textline class to hold the labels before printing.
   */
      protected TextLine label    = new TextLine("0");
  /**
   * The width of the maximum label. Used to position a Vertical Axis.
   */
    protected int max_label_width     = 0;
    protected int max_label_height = 0;

  /**
   * String to contain the labels.
   */  
      protected String label_string[]     = null;
  /**
   * The actual values of the axis labels
   */
      protected float  label_value[]      = null;
  /**
   * The starting value of the labels
   */
      protected float label_start        = (float)0.0;
  /**
   * The increment between labels
   */
      protected float label_step         = (float)0.0;
  /**
   * The label exponent
   */
      protected int    label_exponent     = 0;
  /**
   * The number of labels required
   */
      protected int    label_count        = 0;
  /**
   * Initial guess for the number of labels required
   */
      protected int    guess_label_number = 4;


    public Axis(float min, float max, int p) {
	minimum = min;
	maximum = max;
	position = p;
	axiscolor = new Color(0,0,0);
    }

    /**
     * SERC: this comes from the SpecialFunction class
     */
     final static float log10(float x) {
         if( x <= (float)0.0 ) return 0;
         return Maths.log(x)/(float)2.30258509299;
     }

    final static float floor(float x) {
	if(x >= 0)
	    return (float)((int)x);
	else 
	    return (float)((int)(x- (float)1.0));
    }


    protected boolean configAxis()
    {
	int tmpMult;
	int i;

	calculateGridLabels();

	if(label_exponent != 0) {
	    if((label_exponent < 4) && (label_exponent > 0)){
		tmpMult = 1;
		for(i=0;i<label_exponent;i++){tmpMult *= 10;}
		exponentString = "x" + (String.valueOf(tmpMult));
	    } else
		exponentString = "x10^"+String.valueOf(label_exponent);
	}
	
	max_label_width = 0;
	max_label_height = 0;

	for(i=0; i<label_string.length; i++){
	    label.text = " " + label_string[i];
	    max_label_width = Maths.max(label.getWidth(), max_label_width);
	    max_label_height = Maths.max(label.getHeight(), max_label_height);
	}
	
	width = 0;

	// If the ticks are out side this will take up space
	if(!ticsInside) width += major_tic_size + 1;

	width += label_spacing + max_label_width;	

	height = 0;
	// If the ticks are out side this will take up space
	if(!ticsInside) height += major_tic_size + 1;
	
	height += label_spacing + max_label_height;

	return true;
    }

    /*
     *  Return how much the axis extends past the data window width
     */
    public int getWidth(int estHeight)
    {
	if(position > 1){
	    return 0;
	}

	configAxis();
	return width;
    }

    public int getHeight(int estWidth)
    {
	if(position < 2){
	    return 0;
	}

	configAxis();
	return height;
    }

    int xStart;
    int yStart;
    public int axisLen;
    int gridLen;
    int label_edge;
    int xGridEndOff;
    int yGridEndOff;
    int xLabelOff;
    int yLabelOff;
    int xMaTicOff;
    int yMaTicOff;
    int xMiTicOff;
    int yMiTicOff;
    float xScale;
    float yScale;

    public void setSize(int aLen, int gLen)
    {
	axisLen = aLen;
	gridLen = gLen;

	setup();
    }

    public void setRange(float min, float max)
    {
 	minimum = min;
	maximum = max;

	setup();
    }

    /*
     * aLength is a vector 
     * down for vertical and right for horzontal.
     * the direction can be changed by negating the length.
     * gLength is the length of grid lines to be drawn.
     * it should be positive and it's direction is determined
     * by the position of the axis.
     */
    public void setup()
    {
	int inDir;
	int ticDir;
	int aLen = axisLen;
	int gLen = gridLen;
	
	configAxis();

	if(ticsInside) ticDir = 1;
	else ticDir = -1;

	if(position > 1){
	    xScale = (aLen-1)/(maximum - minimum);
	    yScale = (float)0.0;

	    if(position == TOP) {
		inDir = 1;
		label_edge = label.BOTTOM_EDGE;
	    } else {
		inDir = -1;
		label_edge = label.TOP_EDGE;
	    }

	    xGridEndOff = 0;
	    yGridEndOff = inDir*gLen;

	    xLabelOff = 0;
	    yLabelOff = label_spacing*(-inDir);

	    ticDir = inDir*ticDir;
	    xMaTicOff = 0;
	    yMaTicOff = major_tic_size*ticDir;
	    xMiTicOff = 0;
	    yMiTicOff = minor_tic_size*ticDir;

	} else {
	    yScale = (aLen-1)/(maximum - minimum);
	    xScale = (float)0.0;

	    if(position == LEFT ) {
		inDir = 1;
		label_edge = label.RIGHT_EDGE;
	    } else { 
		inDir = -1;
		label_edge = label.LEFT_EDGE;
	    }

	    yGridEndOff = 0;
	    xGridEndOff = inDir*gLen;

	    yLabelOff = 0;
	    xLabelOff = label_spacing*(-inDir);

	    ticDir = inDir*ticDir;
	    xMaTicOff = major_tic_size*ticDir;
	    yMaTicOff = 0;
	    xMiTicOff = minor_tic_size*ticDir;
	    yMiTicOff = 0;
	}
    }



    /*
     * The (x,y) is the start point, 
     * You must call setRangeAndSize first
     */
    public void draw(JGraphics g, int x, int y)
    {
	xStart = x;
	yStart = y;

	if( axiscolor != null) g.setColor(axiscolor);

	if(position > 1){
	    g.drawLine(x,y,x+axisLen-1,y);
	} else {
	    g.drawLine(x,y,x,y+axisLen-1);
	}

        drawAxis(g);
    }

/*
*********************
** Protected Methods
********************/
    protected void drawGridLine(JGraphics g, float val, Color gridC)
    {
	Color c;
	int x0, y0;

        x0 = xStart + (int)( ( val - minimum ) * xScale);
	y0 = yStart + (int)( ( val - minimum ) * yScale);


	c = g.getColor();
	if(gridC != null) g.setColor(gridC);
	g.drawLine(x0,y0,x0+xGridEndOff,y0+yGridEndOff);
	g.setColor(c);
                      
    }
	
    protected void drawTic(JGraphics g, float val, boolean major)
    {
	int x0, y0;

        x0 = xStart + (int)( ( val - minimum ) * xScale);
	y0 = yStart + (int)( ( val - minimum ) * yScale);

	if(major)
	    g.drawLine(x0,y0,x0+xMaTicOff,y0+yMaTicOff);
	else
	    g.drawLine(x0,y0,x0+xMiTicOff,y0+yMiTicOff);

    }


    protected void drawLabel(JGraphics g, float val, String text)
    {
	int x0, y0;

        x0 = xStart + xLabelOff + (int)( ( val - minimum ) * xScale);
	y0 = yStart + yLabelOff + (int)( ( val - minimum ) * yScale);

	label.text = text;
	label.drawCenter(g, x0, y0, label_edge);
	
    }


  /**
   * Draw the Axis.
   * @param g Graphics context.
   */
     protected void drawAxis(JGraphics g) {
          int i;
          int j;
          float minor_step;

          float vmin = (float)(minimum*(float)1.001);
          float vmax = (float)(maximum*(float)1.001);

          float val;
          float minor;

          minor_step = label_step/(minor_tic_count+1);
          val = label_start;
          for(i=0; i<label_count; i++) {
              if( val >= vmin && val <= vmax ) {
                 if( Maths.abs(label_value[i]) <= (float)0.0001 && drawzero ) {
                     drawGridLine(g, val,  zerocolor);
                 } else if( drawgrid ) {
		     drawGridLine(g, val, gridcolor);
                 }
		 drawTic(g, val, MAJOR); 
              }

              minor = val + minor_step;
              for(j=0; j<minor_tic_count; j++) {
                 if( minor >= vmin && minor <= vmax ) {
                    if( drawgrid ) {
			drawGridLine(g, minor, gridcolor);
		    }
		    drawTic(g, minor, MINOR);
                 }
                minor += minor_step;
              }

              val += label_step;
          }

          val = label_start;
          for(i=0; i<label_count; i++) {
              if( val >= vmin && val <= vmax ) {
		  drawLabel(g, val, label_string[i]);
	      }
              val += label_step;
          }

     }




  /**
   * calculate the labels
   */
      protected void calculateGridLabels() {
        float val;
        int i;
        int j;
        
	if(use_exponent){
	    if (Maths.abs(minimum) > Maths.abs(maximum) ) 
		label_exponent = (int)log10(Maths.abs(minimum));
	    else
		label_exponent = (int)log10(Maths.abs(maximum));
	} else {
	    label_exponent = 0;
	}

        label_step = RoundUp( (maximum-minimum)/(float)guess_label_number );
        label_start = floor( minimum/label_step )*label_step;

        val = label_start;
        label_count = 1;
        while(val < maximum) { 
	    val += label_step; 
	    label_count++; 
	}

        label_string = new String[label_count];
        label_value  = new float[label_count];

	float tmpVal;
	val = label_start;
        for(i=0; i<label_count; i++) {
	    tmpVal = val;
            if( label_exponent< 0 ) {
                  for(j=label_exponent; j<0;j++) { tmpVal *= (float)10.0; }
            } else {
                  for(j=0; j<label_exponent;j++) { tmpVal /= (float)10.0; }
            }

	    label_string[i] = label.fToString(tmpVal);
            label_value[i] = (float)tmpVal;

	    val += label_step;
        }

      }

/*
*******************
** Private Methods
******************/

  /**
   * Round up the passed value to a NICE value.
   */

      private float RoundUp( float val ) {
          int exponent;
          int i;

          exponent = (int)log10(val);

          if( exponent < 0 ) {
             for(i=exponent; i<0; i++) { val *= (float)10.0; }
          } else {
             for(i=0; i<exponent; i++) { val /= (float)10.0; }
          }

          if( val > (float)5.0 )     val = (float)10.0;
          else
          if( val > (float)2.0 )     val = (float)5.0;
          else
          if( val > (float)1.0 )     val = (float)2.0;
          else
                              val = (float)1.0;

          if( exponent < 0 ) {
             for(i=exponent; i<0; i++) { val /= (float)10.0; }
          } else {
             for(i=0; i<exponent; i++) { val *= (float)10.0; }
          }
          
          return val;

      }                  

}










