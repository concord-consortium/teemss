package org.concord.LabBook;

import waba.ui.*;
import waba.util.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import extra.ui.*;

public class LObjDictPagingView extends LabObjectView 
    implements ViewContainer
{
    LabObjectView lObjView = null;

    int newIndex = 0;

    LObjDictionary dict;
 
    Button doneButton = new Button("Done");
    Button backButton = new Button("Prev");
    Choice objectChoice = null;
    Button nextButton = new Button("Next");
	
//    Button delButton = new Button("Del");

    TreeNode [] childArray;

    int index;

    LabObject curObj;
    boolean editStatus = true;
    int defaultNewObjectType = -1;

    public LObjDictPagingView(ViewContainer vc, LObjDictionary d, boolean edit)
    {
		super(vc);

		dict = d;
		lObj = dict;
		childArray = dict.childArray();
		index = 0;
		editStatus = edit;

	
    }

	public void setShowMenus(boolean state)
	{
		if(!showMenus && state){
			// our container wants us to show our menus
			showMenus = true;
			if(lObjView != null) lObjView.setShowMenus(state);
		} else if(showMenus && !state){
			// out container wants us to remove our menus
			showMenus = false;
			if(lObjView != null) lObjView.setShowMenus(state);
		}
	}

	Vector objList = new Vector();

    public void layout(boolean sDone)
    {
		if(didLayout){
			if(showDone != sDone){
				showDone = sDone;
				if(showDone) add(doneButton);
				else remove(doneButton);
			}
			return;
		}
		didLayout = true;

		showDone = sDone;

		if(showDone){
			add(doneButton);
		} 

		add(nextButton);
		add(backButton);

		if(childArray != null){
			for(int i=0; i < childArray.length; i++){
				objList.add(childArray[i].toString());
				
			}
		}

		objectChoice = new Choice(objList);
		add(objectChoice);

//		if(editStatus) add(delButton);
	
		showObject();
    }

    public void setRect(int x, int y, int width, int height)
    {
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);
	
		int curX = 2;
		backButton.setRect(curX, 0, 25, 15);
		curX += 27;
		nextButton.setRect(curX, 0, 25, 15);
		curX+=27;
		int doneWidth = 0;
		if(showDone){
			doneButton.setRect(width-25, 0, 25, 15);
			doneWidth = 25;
		}
		int choiceWidth = width - curX - doneWidth - 2;
		if(choiceWidth > 120) choiceWidth = 120;
		objectChoice.setRect(curX, 0, choiceWidth, 15);
		curX += choiceWidth;
//		if(editStatus) delButton.setRect(width-50, 0, 25, 15);
		if(lObjView != null){
			lObjView.setRect(0,15,width, height-15);
			Debug.println("Adding lObjView at: " + x + ", " + y +
						  ", " + width + ", " + height);
		}
    }

    public void onEvent(Event e)
    {
		if(e.type == ControlEvent.PRESSED){
			TreeNode curNode;
			TreeNode parent;

			LabObject newObj;
			if(e.target == nextButton){
				index++;
				if(!editStatus && 
				   (index >= childArray.length)){
					// If edit is turned off make sure they can't add objects
					index = childArray.length - 1;
				} else if(index < childArray.length){
					repaint();
				} else {
					if(dict.hasObjTemplate){
						newObj = dict.getObjTemplate().copy();
					} else if(defaultNewObjectType != -1){
						newObj = LabBook.makeNewObj(defaultNewObjectType);
					} else {
						return;
					}
					newObj.setName("New" + newIndex);
					newIndex++;
					dict.insert(newObj, index);
					childArray = dict.childArray();
					if(index >= childArray.length) index = childArray.length - 1;
				}
				showObject();
		
			}  else if(e.target == backButton){
				index--;
				if(index < 0) index = 0;
				showObject();
			} else if(e.target == doneButton){
				if(container != null){
					container.done(this);
				}
			} else if(e.target == objectChoice){
				int newIndex = objectChoice.getSelectedIndex();
				if(newIndex == index) return;
				index = newIndex;
				if(index < 0) index = 0;
				if(index >= childArray.length) index = childArray.length;
				showObject();
			}
		}
    }

	public MainView getMainView()
	{
		if(container != null) return container.getMainView();
		else return null;
	}

    public void done(LabObjectView source) {}

    public void reload(LabObjectView source)
    {
		if(source == lObjView){
			LabObject obj = source.getLabObject();
			lObjView.close();
			remove(lObjView);
	    
			lObjView = obj.getView(this, editStatus);
			lObjView.layout(false);
			lObjView.setRect(0,15,width,height-15);
			add(lObjView);
		}
    }

    public void showObject()
    {
		if(childArray == null) return;
		if(index < 0 || index >= childArray.length) return;

		TreeNode curNode = childArray[index];
		LabObject obj = null;
		if(index == 0 &&
		   curNode.toString().equals("..empty..")){
			if(dict.hasObjTemplate){
				obj = dict.getObjTemplate().copy();
			} else if(defaultNewObjectType != -1){
				obj = LabBook.makeNewObj(defaultNewObjectType);
			} else {
				return;
			}
	    
			obj.setName("New" + newIndex);
			newIndex++;
			dict.insert(obj, 0);
			childArray = dict.childArray();
		} else {
			obj = dict.getObj(curNode);
			if(obj == null) Debug.println("showPage: object not in database: " +
										  ((LabObjectPtr)curNode).debug());
	    
		} 

		if(obj == null) return;
       	if(obj == curObj) return;

		if(lObjView != null){
			lObjView.close();
			remove(lObjView);
		}
		if(curObj != null) { 
			int objRefCount = curObj.release();
			//			System.out.println("LODP: showObject: released old object new refC: " +
			//				   objRefCount);
		}
		
		objectChoice.setSelectedIndex(index);

		curObj = obj;

		lObjView = obj.getView(this, editStatus,dict);//dima add dict

		lObjView.layout(false);
		if(width > 0 || height > 15){
			lObjView.setRect(0,15,width,height-15);
			Debug.println("Adding lObjView at: " + x + ", " + y +
						  ", " + width + ", " + height);
		}
		lObjView.setShowMenus(showMenus);
		add(lObjView);

		// do I need this
		// repaint();
    }

    public void close()
    {
		if(lObjView != null){
			lObjView.close();
		}

		super.close();
		// Commit ???
		// Store ??
    }
}
