import extra.io.*;

public class LObjGraph extends LabObject
{
    float xmin = 0f, xmax = 100f;
    float ymin = -20f, ymax = 50f;

    String title = null;
    String xLabel = null;
    String yLabel = null;

    LObjDataSet dataSet;

    public LObjGraph()
    {
	objectType = GRAPH;
    }
    
    public LabObjectView getView(boolean edit)
    {
	return (LabObjectView)(new LObjGraphView(this));
    }

    public void readExternal(DataStream ds)
    {

    }
    
    public void writeExternal(DataStream ds)
    {

    }

}
