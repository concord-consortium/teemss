package graph;

import waba.fx.*;

public abstract class Graph2D
{
    boolean redraw = true;

    public abstract void resize(int w, int h);

    public abstract void draw(Graphics g);

    public abstract int plot(Graphics g);

    public abstract void reset();

}
