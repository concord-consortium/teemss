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

    public LabObjectView getView(boolean edit)
    {
	if(mainObject != null){
	    return mainObject.getView(edit);
	}

	if(viewType == TREE_VIEW)
	    return new LObjDictionaryView(this);
	if(viewType == PAGING_VIEW)
	    return new LObjDictPagingView(this, edit);

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
	System.out.println("Removing node");
	int index = getIndex(node);
	objects.del(index);
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
	viewType = ds.readInt();

	int size = ds.readInt();
	
	for(i=0; i<size; i++){
	    LabObjectPtr ptr = LabObjectPtr.readExternal(ds);
	    objects.add(ptr);
	    System.out.println(" Reading: " + ptr.devId + ", " + ptr.objId);
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
	ds.writeInt(viewType);

	ds.writeInt(size);
	for(i=0; i<size; i++){
	    LabObjectPtr ptr = (LabObjectPtr)objects.get(i);
	    ptr.writeExternal(ds);
	    System.out.println(" Writing: " + ptr.devId + ", " + ptr.objId);
	}

	lBook.store(mainObject).writeExternal(ds);
	lBook.store(newObjectTemplate).writeExternal(ds);

    }

    public TreeNode getChildAt(int index)
    {
	if(index < 0 || index >= objects.getCount()) return null;

	return (TreeNode)(lBook.load((LabObjectPtr)(objects.get(index))));
    }

    public boolean showChildren = true;

    public boolean isLeaf()
    {
	return !showChildren;
    }

    public TreeNode [] childArray()
    {
	TreeNode [] children;
	int numObjs = objects.getCount();

	if(numObjs == 0){
	    children = new TreeNode [1];
	    children[0] = new LObjDictionary();
	    ((LabObject)(children[0])).name = "..empty..";
	    ((LObjDictionary)(children[0])).showChildren = false;
	    return children;
	}
	
	children = new TreeNode [numObjs];
	for(int i=0; i<numObjs; i++){
	    children[i] = (TreeNode)lBook.load((LabObjectPtr)objects.get(i));
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
	
	System.out.println("getIndex searching " + numObjs + " objects");
	for(int i=0; i<numObjs; i++){
	    System.out.println(" Checking node: " + lBook.load((LabObjectPtr)objects.get(i)));
	    if(node  == (TreeNode)lBook.load((LabObjectPtr)objects.get(i))){
		return i;
	    } 
	}
	
	return -1;
    }

    public LabObject copy()
    {
	System.out.println("Copying a dictionary");
	LObjDictionary me = new LObjDictionary();
	if(mainObject != null){
	    System.out.println("  adding mainObject");
	    LObjSubDict newMO = (LObjSubDict)mainObject.copy();
	    newMO.setDict(me);
	    me.mainObject = newMO;
	    
	}
	me.viewType = viewType;
	return (LabObject)me;
    }
}
