package graph;

public interface Graph2D
{
    public void resize(int w, int h);

    public void setRange(float min, float range);

    public Object addBin(int location, String label);

    public boolean removeBin(Object id);

    public void draw(JGraphics g, int x, int y);

    public int plot(JGraphics g);

    public void reset();

    public int getNextBin();

    public boolean addPoint(int confId, int x, float values[]);

    public boolean addPoint(int confId, int locId, int x, float value);

    public int transLocId(int confId, int locId);

}
