import extra.io.*;

public abstract class LObjSubDict extends LabObject
{
    LObjDictionary dict;

    public void setDict(LObjDictionary d){
	dict = d;
    }

    public void writeExternal(DataStream out)
    {
	super.writeExternal(out);
	lBook.store(dict).writeExternal(out);
    }

    public void readExternal(DataStream in)
    {
	super.readExternal(in);
	dict = ((LObjDictionary)lBook.load(LabObjectPtr.readExternal(in)));
    }

    public void setObj(LabObject obj, int id)
    {
	if(dict.getChildCount() <= id){
	    for(int i=dict.getChildCount(); i<id; i++){
		dict.add(null);
	    }
	    dict.insert(obj, id);
	} else {
	    Debug.println("Removing #" + id + " from dict: " + dict.name);
	    dict.remove(id);
	    dict.insert(obj, id);
	}

    }

    public LabObject getObj(int id)
    {
	LabObject obj = (LabObject)dict.getChildAt(id);
	if(obj == null) Debug.println("Got null obj from subDict");

	return obj;
    }

}