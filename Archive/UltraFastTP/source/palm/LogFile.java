import waba.io.*;
import waba.ui.*;

public class LogFile extends Transform
{

    public LogFile(String name)
    {
    }

    public boolean transform(int num, int size, float [] data)
    {
	return next.transform(num, size, data);
    }

}
