package org.concord.waba.extra.ui;
import waba.ui.*;
import waba.fx.*;
import waba.util.*;
import org.concord.waba.extra.event.*;

class TreeLine{
    TreeLine(TreeNode node)
    {
	this.node = node;
    }

    int depth = 0;
    boolean expanded = false;
    boolean selected = false;
    TreeNode node;
    TreeNode parent = null;
}

public class TreeControl extends Control implements TreeModelListener
{
    TreeModel tm;
    Vector lines = new Vector();

    int indentSize = 10;
    int textHeight = 10;

    Font myFont = MainWindow.defaultFont;
    FontMetrics myFM;

    TreeLine selected = null;

    public TreeControl(TreeModel tm)
    {
	TreeNode curNode;
	this.tm = tm;
	tm.addTreeModelListener(this);
	curNode = tm.getRoot();
	lines.add(new TreeLine(curNode));	
	myFM = getFontMetrics(myFont);
    }

    boolean sRoot = true;
    public void showRoot(boolean sRoot)
    {
	this.sRoot = sRoot;

    }

    public void onPaint(Graphics g)
    {
	int numLines = lines.getCount();
	TreeLine line;
	int curY = 0;

	if(numLines == 0) return;

	if(!sRoot && numLines < 2) return;

	int i = 0;
	int depthOffset = 0;
	if(!sRoot){
	    i = 1;
	    depthOffset = -1;
	}

	g.setColor(0,0,0);
	for(; i<numLines; i++){
	    line = (TreeLine)lines.get(i);
	    if(!line.node.isLeaf()){
		drawTwist(g, (line.depth - depthOffset)* indentSize, curY, line.expanded);
	    }
	    if(!line.selected){
		g.drawText(line.node.toString(), (line.depth+depthOffset+1)*indentSize, curY);
	    } else {
		int tWidth = myFM.getTextWidth(line.node.toString());
		g.setColor(0,0,0);
		g.fillRect((line.depth+depthOffset+1)*indentSize, curY, tWidth, textHeight);
		g.setColor(255,255,255);
		g.drawText(line.node.toString(), (line.depth+depthOffset+1)*indentSize, curY);
		g.setColor(0,0,0);
	    }
	    curY += textHeight;
	}
    }

    public TreeNode getSelected()
    {
	if(selected == null) return null;

	return selected.node;
    }

    public TreeNode getSelectedParent()
    {
	if(selected == null) return null;

	return selected.parent;
    }
    

    public void onEvent(Event e)
    {
	if(e instanceof PenEvent && e.type == PenEvent.PEN_DOWN){
	    PenEvent pe = (PenEvent)e;
	    int lineIndex = pe.y / textHeight;
	    if(lineIndex >= lines.getCount()) return;
	    TreeLine line = (TreeLine)lines.get(lineIndex);
	    if(line == null) return;
	    int lineOff = line.depth*indentSize;
	    if(pe.x >= lineOff && pe.x < lineOff + indentSize){
		if(line.expanded) collapse(lineIndex, lines);
		else expand(lineIndex, lines);
		repaint();
	    } else if(pe.x >= lineOff + indentSize){
		if(selected != null && line != selected){
		    selected.selected = false;
		}
		line.selected = !line.selected;
		selected = line;
		// Send event;
		repaint();
	    }
	}
    }

    int [] coord1 = {1,8,5};
    int [] coord2 = {1,1,8};
    void drawTwist(Graphics g, int x ,int y, boolean expanded)
    {
	g.translate(x,y);
	if(expanded){
	    g.fillPolygon(coord1, coord2, 3);
	} else {
	    g.fillPolygon(coord2, coord1, 3);
	}
	g.translate(-x,-y);
    }

    void expand(int index, Vector lines)
    {
	TreeLine line = (TreeLine)lines.get(index);
	TreeLine child;
	TreeNode [] children;

	// send event

	if(!line.node.isLeaf() && !line.expanded){
	    line.expanded = true;
	    children = line.node.childArray();
	    for(int i=0; i<children.length; i++){
		child = new TreeLine(children[i]);
		child.depth = line.depth+1;
		child.parent = line.node;
		lines.insert(index+i+1, child);		
	    }
	}
	
    }

    void collapse(int index, Vector lines)
    {
	TreeLine line = (TreeLine)lines.get(index);
	int curIndex = index+1;
	TreeLine next;

	// send event

	if(!line.node.isLeaf() && line.expanded){
	    line.expanded = false;
	    while(curIndex < lines.getCount()){
		next = (TreeLine)lines.get(curIndex);
		if(next.depth == line.depth){
		    break;
		}
		lines.del(curIndex);
	    }
	}
	
    }

    public void treeNodeInserted(TreeNode node, TreeNode parent)
    {
	TreeLine line;
	int i;

	for(i=0; i < lines.getCount(); i++){
	    line = (TreeLine)lines.get(i);
	    if(line.node == parent){
		if(line.expanded){
		    reparse();
		    //collapse(i,lines);
		    //expand(i,lines);
		} else {
		    expand(i,lines);
		}
		break;
	    }
	}

	if(selected != null) selected.selected  = false;
	for(i=0; i < lines.getCount(); i++){
	    line = (TreeLine)lines.get(i);
	    if(line.node == node){
		line.selected = true;
		selected = line;
		break;
	    }
	}

	repaint();
    }

    void reparse()
    {
	Vector newLines = new Vector();
	int i, j;
	int numLines = lines.getCount();
	TreeLine curLine;
	TreeLine newLine;
	TreeLine newSelected = null;

	TreeNode curNode = tm.getRoot();
	newLine = new TreeLine(curNode);
	newLines.add(newLine);
	
	while(true){
	    
	    for(i=0; i < newLines.getCount(); i++){
		newLine = (TreeLine)(newLines.get(i));
		for(j=0; j < numLines; j++){
		    curLine = (TreeLine)(lines.get(j));
		    if(newLine.node == curLine.node){
			if(curLine.selected == true) {
			    newLine.selected = true;
			    newSelected = newLine;
			}
			if(curLine.expanded == true &&
			   newLine.expanded != true){
			    expand(i, newLines);
			    break;
			}
		    }
		}
		if(j != numLines) break;
	    }
	    if(i == newLines.getCount()) break;
	}

	lines = newLines;	    
	selected = newSelected;
    }

    public void treeModelChanged()
    {
	System.out.println("TreeModelChanged");
	reparse();
	repaint();
    }
}
