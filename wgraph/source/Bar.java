package graph;

import waba.util.*;
import waba.fx.*;
import waba.ui.*;

public class Bar 
{
    String label;
    int index;
    float curValue;
    Object ptr;

    BarGraph barGraph;

    public void setLabel(String l)
    {
	label = l;
	barGraph.barSet.labels[index].setText(l);
    }
}

