import waba.ui.*;
import graph.*;

public class DigitalDisplay extends Transform
{
    LabelBuf tLabel, vLabel;
    TextLine convertor = new TextLine("0");
    String units = "";
    float time, value;

    public DigitalDisplay(LabelBuf time, LabelBuf value)
    {
	tLabel = time;
	vLabel = value;
	convertor.maxDigits = 2;
    }

    public boolean transform(int num, int size, float data []){
	int offset = (num-1)*size;
	value = data[offset + 1];
	time = data[offset];

	//	System.out.println("v:" + data[1] + ", t:" + data[0]);

	return next.transform(num, size, data);
    }

    public void update()
    {
	vLabel.setText(convertor.fToString(value) + units);
	tLabel.setText(convertor.fToString(time) + "s");
	if(next != null)
	    next.update();
    }
}
