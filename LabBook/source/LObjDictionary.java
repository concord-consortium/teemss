import waba.util.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;

public class LObjDictionary extends LabObject
{
    public final static int TREE_VIEW = 0;
    public final static int PAGING_VIEW = 1;

    public Vector objects = new Vector();
    public int viewType = TREE_VIEW;

    public LObjSubDict mainObject = null;
    public LabObject newObjectTemplate = null;

    public LObjDictionary()
    {       
	objectType = DICTIONARY;

    }

    public LabObjectView getView(LObjViewContainer vc, boolean edit)
    {
	if(mainObject != null){
	    return mainObject.getView(vc, edit);
	}

	if(viewType == TREE_VIEW)
	    return new LObjDictionaryView(vc, this);
	if(viewType == PAGING_VIEW)
	    return new LObjDictPagingView(vc, this, edit);

	return null;
    }

    public void add(LabObject lObj)
    {
	objects.add(lBook.store(lObj));
    }

    public void insert(LabObjectPtr lObj, int index)
    {
	objects.insert(index, lObj);
    }

    public void insert(TreeNode node, int index)
    {
	if(node != null && 
	   !(node instanceof LabObject)) return;
	
	insert(lBook.store((LabObject)node), index);
    }

    public void remove(TreeNode node)
    {
	Debug.println("Removing node");
	int index = getIndex(node);
	objects.del(index);

	// Should tell the labbook we don't care about this obj any more
    }

    public void remove(int index)
    {
	if(index < 0 || index >= objects.getCount()) return;
	
	objects.del(index);
    }

    public int getChildCount()
    {
	return objects.getCount();
    }

    public void readExternal(DataStream ds)
    {
	objects = new Vector();
	int i;
	super.readExternal(ds);
	hideChildren = (ds.readByte() == 0?false:true);
	viewType = ds.readInt();

	int size = ds.readInt();
	
	for(i=0; i<size; i++){
	    LabObjectPtr ptr = LabObjectPtr.readExternal(ds);
	    objects.add(ptr);
	    Debug.println(" Reading: " + ptr.devId + ", " + ptr.objId);
	}

	LabObjectPtr mObjPtr = LabObjectPtr.readExternal(ds);
	mainObject = (LObjSubDict)lBook.load(mObjPtr);	 
	newObjectTemplate = lBook.load(LabObjectPtr.readExternal(ds));
    }

    public void writeExternal(DataStream ds)
    {
	int i;
	int size = objects.getCount();
	super.writeExternal(ds);
	ds.writeByte(hideChildren?1:0);
	ds.writeInt(viewType);

	ds.writeInt(size);
	for(i=0; i<size; i++){
	    LabObjectPtr ptr = (LabObjectPtr)objects.get(i);
	    ptr.writeExternal(ds);
	    Debug.println(" Writing: " + ptr.devId + ", " + ptr.objId);
	}

	lBook.store(mainObject).writeExternal(ds);
	lBook.store(newObjectTemplate).writeExternal(ds);

    }

    public TreeNode getChildAt(int index)
    {
	if(index < 0 || index >= objects.getCount()) return null;

	return (lBook.load((LabObjectPtr)(objects.get(index))));
    }

    public boolean hideChildren = false;

    public boolean isLeaf()
    {
	return hideChildren;
    }

    public TreeNode [] childArray()
    {
	TreeNode [] children;
	int numObjs = objects.getCount();

	if(numObjs == 0){
	    children = new TreeNode [1];
	    children[0] = new LObjDictionary();
	    ((LabObject)(children[0])).name = "..empty..";
	    ((LObjDictionary)(children[0])).hideChildren = true;
	    return children;
	}
	
	children = new TreeNode [numObjs];
	for(int i=0; i<numObjs; i++){
	    children[i] = lBook.load((LabObjectPtr)objects.get(i));
	}
	return children;
    }

    /*
     * Very inefficient right now
     */
    public int getIndex(TreeNode node)
    {
	if(!(node instanceof LabObject)) return -1;
	
	int numObjs = objects.getCount();
	
	Debug.println("getIndex searching " + numObjs + " objects");
	for(int i=0; i<numObjs; i++){
	    Debug.println(" Checking node: " + lBook.load((LabObjectPtr)objects.get(i)));
	    if(node  == lBook.load((LabObjectPtr)objects.get(i))){
		return i;
	    } 
	}
	
	return -1;
    }

    public LabObject copy()
    {
	Debug.println("Copying a dictionary");
	LObjDictionary me = new LObjDictionary();
	if(mainObject != null){
	    Debug.println("  adding mainObject");
	    LObjSubDict newMO = (LObjSubDict)mainObject.copy();
	    newMO.setDict(me);
	    me.mainObject = newMO;
	    
	}
	me.viewType = viewType;
	return me;
    }
}
