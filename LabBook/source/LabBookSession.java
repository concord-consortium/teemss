package org.concord.LabBook;

import waba.io.*;
import waba.util.*;
import extra.io.*;

public class LabBookSession
{
	LabBook labBook;

	Vector labObjects = new Vector();

	public LabBookSession(LabBook lBook)
	{
		labBook = lBook;
	}

	/*
	public void printObjects()
	{
		System.out.println("LBS: objects:");
		for(int i=0; i<labObjects.getCount(); i++){
			System.out.println(" obj: " + labObjects.get(i));
		}
	}	
	*/

	public boolean contains(LabObject lObj)
	{
		if(lObj == null) return false;

		int index = labObjects.find(lObj);
		return index >= 0;
	}

	// This gets the real object
	// it should only be used interally by 
	// dictionaries and things.
	public LabObject load(LabObjectPtr lObjPtr)
	{
		LabObject newObj = labBook.load(lObjPtr);
		int index = labObjects.find(newObj);
		if(index >= 0) {
			// this object has already been loaded. 
			// we just accidentally incremented the 
			// ref count 
			newObj.release();
		} else if(newObj != null){
			labObjects.add(newObj);
		}
		// if(newObj == null) (new RuntimeException("null loaded obj")).printStackTrace();
		return newObj;
	}

	// This gets the pseudo object 
	// if the ptr points to a dictionary with sub objects
	// it will return the main object.
	public LabObject getObj(LabObjectPtr lObjPtr)
	{
		LabObject newObj = load(lObjPtr);

		if(newObj instanceof LObjDictionary){
			LObjDictionary newDict = (LObjDictionary)newObj;
			if(newDict.hasMainObject){
				LObjSubDict mainObj = newDict.getMainObj(this);
				return mainObj;
			}
			return newDict;
		} else {
			/*
			if(hasMainObject &&
			   obj instanceof LObjSubDict &&
			   ((LabObjectPtr)(objects.get(0))).equals(node)){
				((LObjSubDict)obj).setDict(this);
			}
			*/
			return newObj;
		}
	}

	public void checkPoint()
	{
		for(int i=0; i<labObjects.getCount(); i++){
			LabObject obj = (LabObject)labObjects.get(i);
			obj.storeNow();
		}		
	}

	public int release(LabObject obj)
	{
		int index = labObjects.find(obj);
		if(index < 0) return -1;

		int refCount = obj.release();
		if(refCount == 0){
			// this object is no longer needed remove it from our list
			labObjects.del(index);
		}
		//		System.out.println("LBS: " + this + " release() " + 
		// 				   " obj: " + obj + " refC: " + refCount);
		return refCount;
	}

	/*
	 * release all the objects loaded in this session
	 */
	public void release()
	{
		for(int i=0; i<labObjects.getCount(); i++){
			LabObject obj = (LabObject)labObjects.get(i);
			int refCount = obj.release();
			//			System.out.println("LBS: " + this + " release() " + 
			//				   " obj: " + obj + " refC: " + refCount);
		}
		labObjects = new Vector();
	}
}
