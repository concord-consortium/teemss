/*
Copyright (C) 2001 Concord Consortium

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package graph;

import waba.fx.*;
import waba.ui.*;
import waba.sys.*;

public class BarSet extends Object
{

    public Color linecolor = null;
    public ColorAxis axis = null;

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
    public boolean barSel[];
    protected int barDir = 1;
    int [] upCol = {0,0,0};
    int [] downCol = {255,255,255};


    static final int MINDIGITS = 1;
    static final int MAXDIGITS = 4;
    static final int BAR_SPACE = 2;

    static final int BOTTOM = 0;

    public BarSet(ColorAxis a, int num, int orien)
    {
		axis = a;
		nBars = num;
		oldBarLen = new int[num];
		barPos = new int[num];
		barSel = new boolean[num];
		orientation = orien;
		length = 0;
		barDir = -1;
	
		label.maxDigits = 1;
		label.minDigits = 1;

		labels = new TextLine[num];
		digitals = new TextLine[num];
		for(int i=0; i<num; i++){
			labels[i] = new TextLine();
			digitals[i] = new TextLine("00.0");
			digitals[i].maxDigits = 1;
			digitals[i].minDigits = 1;
			barSel[i] = false;
		}
    }

    public void free()
    {
		int i;

		if(label != null)label.free();
		if(labels != null){
			for(i=0; i<labels.length; i++){
				if(labels[i] != null){
					labels[i].free();
				}
			}
		}
		if(digitals != null){
			for(i=0; i<digitals.length; i++){
				if(digitals[i] != null){
					digitals[i].free();
				}
			}
		}

    }

    public void reset()
    {
		// Length scale
		lenScale = axis.scale;
		maxHeight = axis.length - 1;

		length = 0;

    }

    public void drawLabel(Graphics g, float val, int index)
    {
		digitals[index].setText(label.fToString(val));
	
		int labelY = start+1;
		if(interleaveLabels && (index % 2) == 1){
			labelY += label.height;
		}
		// clear old label
	
		digitals[index].drawCenter(g, barPos[index]+barWidth/2,
								   labelY, label.TOP_EDGE);

    }

    /*
     *  The Graphics should be translated 0,0 is the top left
     *  corner of the DataWindow
     */
    public void addColorPoint(Graphics g, float x, float val[])
    {
		int barPercent;
		int i;
		int barLen;
	
		if(length > 0){
			for(i=0; i<nBars; i++){
				barLen = barDir*(int)((val[i] - axis.dispMin) * lenScale);
				if(barLen < 0){
					barLen = 0;
				} else if(barLen > (barDir*axis.dispLen)){
					barLen = (barDir*axis.dispLen);
				}
				if(oldBarLen[i] > barLen){
					g.setColor(downCol[0],downCol[1],downCol[2]);
					// Hack for bottom only
					g.fillRect(barPos[i], start - oldBarLen[i], 
							   barWidth,(oldBarLen[i]) - barLen);
				} 
			}
		} 

		g.setColor(255,255,255);
		if(interleaveLabels)
			g.fillRect(drawnX, start+1, axisLen, label.height*2+1);
		else
			g.fillRect(drawnX, start+1, axisLen, label.height+1);

		for(i=0; i<nBars; i++){
			barLen = barDir* (int)((val[i] - axis.dispMin) * lenScale);
			if(barLen < 0){
				barLen = 0;
			} else if(barLen > (barDir*axis.dispLen)){
				barLen = barDir*axis.dispLen;
			}
			if(barDir*axis.length == 0){
				barPercent = 0;
			} else {
				barPercent = (int)((barLen * 1000) / (barDir*axis.dispLen));
			}
	    
			axis.setBarColor(g, barPercent);
			// Hack for bottom only bar graphs
			g.fillRect(barPos[i], start - barLen, 
					   barWidth, barLen);
			oldBarLen[i] = barLen;
			if(barSel[i]){
				g.setColor(0,0,0);
				g.drawRect(barPos[i], start - barLen,
						   barWidth, barLen);
			}
			// also need to put label on bottom
			drawLabel(g, val[i], i);
	    
		}

		length++;
    }

    /*
     *  The Graphics should be translated 0,0 is the top left
     *  corner of the DataWindow
     */
    public void addPoint(Graphics g, float x, float val[])
    {
		int i;
		int barLen;

		// Should check the length of val

		if(length > 0){
			for(i=0; i<nBars; i++){
				barLen = (int)((val[i] - axis.dispMin) * lenScale);
				if(oldBarLen[i] > barLen){
					g.setColor(upCol[0],upCol[1],upCol[2]);
					g.fillRect(barPos[i], start + barLen, barWidth, 
							   (oldBarLen[i] - barLen));
				} else {
					g.setColor(downCol[0],downCol[1],downCol[2]);
					g.fillRect(barPos[i], start + oldBarLen[i], 
							   barWidth,(barLen - oldBarLen[i]));
				}
				oldBarLen[i] = barLen;

				// also need to put label on bottom
			}
		} else {
			// Watch out for color changes
			for(i=0; i<nBars; i++){
				g.setColor(upCol[0],upCol[1],upCol[2]);
				barLen = (int)((val[i] - axis.dispMin) * lenScale);
				// Hack for bottom only bar graphs
				g.fillRect(barPos[i], start + barLen, 
						   barWidth, barDir*barLen);
				oldBarLen[i] = barLen;

				// also need to put label on bottom
			}
		}

		length++;

    }

    int drawnX = 0;
    int axisLen = 0;

	/* This is a hack
	 * it should take into account the orientation
	 */
    public void draw(Graphics g, int x, int y, int aLen, int gLen)
    {
		// Figure out the postions and widths of the bars and labels
		int maxSpacing;
		int i;

		drawnX = x;
		axisLen = aLen;

		maxSpacing = (aLen - BAR_SPACE)/ nBars;

		// Keep at least a 3:1 ratio of height:width of the bars 
		if(maxSpacing > barDir*axis.dispLen / 3){
			maxSpacing = barDir*axis.dispLen / 3;
		}

		barWidth = maxSpacing - BAR_SPACE;
	
		int curPos = BAR_SPACE + x;
		for(i=0; i<nBars; i++){
			barPos[i] = curPos;
			curPos += barWidth + BAR_SPACE;
		}

		// Draw the axis line
		start = y-1;

		// Length scale
		lenScale = axis.scale;
		maxHeight = axis.dispLen - 1;

		// When this funct is called the background has been cleared
		// so tell all our labels so they don't draw in their old pos

		configLabels();

		// also need to put text label on bottom
		int labelY = start+1+label.height;
		if(interleaveLabels){
			labelY += label.height;
		}
		for(i=0; i<nBars; i++){
			labels[i].drawCenter(g, barPos[i]+barWidth/2, labelY, label.TOP_EDGE);
		}

    }

    public void configLabels()
    {

		// Compute how to print the labels
		label.setText(label.fToString(axis.min));
		int max_label_width = label.width;
		label.setText(label.fToString(axis.dispMin + (axis.dispLen / axis.scale)));
		if(max_label_width < label.width){
			max_label_width = label.width;
		}
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
			return 3*label.height;
		} else {
			return 2*label.height;
		}
    }

}













