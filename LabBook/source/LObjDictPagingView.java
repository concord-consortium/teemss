package org.concord.LabBook;

import waba.ui.*;
import waba.fx.*;
import waba.util.*;

import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.io.*;

public class LObjDictPagingView extends LabObjectView 
    implements ViewContainer
{
    LabObjectView lObjView = null;

    int newIndex = 0;

    LObjDictionary dict;
	DictTreeNode dictNode;

    Button doneButton = new Button("Done");
    Button backButton = new Button("Prev");
    Choice objectChoice = null;
    Button nextButton = new Button("Next");
	
//    Button delButton = new Button("Del");

    int index;
	int curIndex = -1;
	Vector savedStates = new Vector();

    LabObject curObj;
    boolean editStatus = true;
    int defaultNewObjectType = -1;

	LabBookSession listSession = null;
	LabBookSession lObjSession = null;

    public LObjDictPagingView(ViewContainer vc, LObjDictionary d, boolean edit, 
							  LabBookSession session)
    {
		super(vc, (LabObject)d, session);
		dict = d;

		listSession = d.getVisiblePtr().getSession();
		dictNode = new DictTreeNode(dict.getVisiblePtr(), listSession, dict.lBook);

		index = 0;
		editStatus = edit;	
    }

	/*
	public void onPaint(Graphics g)
	{
		(new Exception("onPaint")).printStackTrace();
	}
	*/

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

	public void restoreState(DataStream ds)
	{
		index = ds.readInt();
		int numStates = ds.readInt();
		savedStates = new Vector(numStates);
		for(int i = 0; i < numStates; i++){
			short len = ds.readShort();
			if(len > 0){
				byte [] buf = new byte [len];
				ds.readBytes(buf, 0, len);
				savedStates.add(buf);
			} else {
				savedStates.add(null);
			}
		}
	}

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

		TreeNode [] childArray = dictNode.childArray();
		if(childArray != null){
			for(int i=0; i < childArray.length; i++){
				objList.add(childArray[i].toString());
				
			}
		}

		objectChoice = new Choice(objList);
		add(objectChoice);

		listSession.release();
		
		showObjectTimer = addTimer(50);
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
		if(e.type == ControlEvent.TIMER &&e.target == this){
			if(showObjectTimer != null){
				removeTimer(showObjectTimer);
				showObjectTimer = null;
				showObject();
			}
		}

		if(e.type == ControlEvent.PRESSED){
			TreeNode curNode;
			TreeNode parent;
			int childLength = dictNode.getChildCount();

			LabObject newObj;
			if(e.target == nextButton){
				index++;
				if(!editStatus && 
				   (index >= childLength)){
					// If edit is turned off make sure they can't add objects
					index = childLength - 1;
					return;
				} else if(index < childLength){
					// repaint();
				} else {
					if(dict.hasObjTemplate){
						newObj = dict.getObjTemplate(session).copy();
					} else if(defaultNewObjectType != -1){
						newObj = LabBook.makeNewObj(defaultNewObjectType);
					} else {
						return;
					}
					newObj.setName("New" + newIndex);
					newIndex++;
					dict.insert(newObj, index);
					childLength = dictNode.getChildCount();
					if(index >= childLength) index = childLength - 1;
				}
				//				dict.lBook.printCaches();
				// listSession.printObjects();
				// lObjSession.printObjects();
				showObject();
				// lObjSession.printObjects();
				// listSession.printObjects();
				// dict.lBook.printCaches();		
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
				if(index >= childLength) index = childLength;
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
			if(lObjSession != null) lObjSession.release();

			remove(lObjView);
	    
			lObjSession = dict.getVisiblePtr().getSession();
			lObjView = obj.getView(this, editStatus, lObjSession);
			lObjView.layout(false);
			lObjView.setRect(0,15,width,height-15);
			add(lObjView);
		}
    }

	Timer showObjectTimer = null;

    public void showObject()
    {
		if(dictNode == null) return;
		int childLength = dictNode.getChildCount();
		if(index < 0 || index >= childLength) return;


		if(lObjView != null && curIndex != -1){
			// Save the state of this view
			saveCurViewState();

			lObjView.close();
			if(lObjSession != null) lObjSession.release();
			remove(lObjView);
			lObjView = null;
			curObj = null;
			lObjSession = null;
			listSession.release();
			
			showObjectTimer = addTimer(60);
			return;
		}

		TreeNode curNode = dictNode.getChildAt(index);
		LabObject obj = null;
		if(index == 0 &&
		   curNode.toString().equals("..empty..")){
			if(dict.hasObjTemplate){
				obj = dict.getObjTemplate(session).copy();
			} else if(defaultNewObjectType != -1){
				obj = LabBook.makeNewObj(defaultNewObjectType);
			} else {
				return;
			}
	    
			obj.setName("New" + newIndex);
			newIndex++;
			dict.insert(obj, 0);
		} else {
			obj = dictNode.getObj(curNode);
			if(obj == null) Debug.println("showPage: object not in database: " +
										  ((LabObjectPtr)curNode).debug());
	    
		} 

		if(obj == null) return;
       	if(obj == curObj) return;

		objectChoice.setSelectedIndex(index);
		objectChoice.repaint();

		curObj = obj;

		lObjSession  = obj.getVisiblePtr().getSession();
		lObjView = obj.getView(this, editStatus, dict, lObjSession);//dima add dict

		if(index < savedStates.getCount() &&
		   savedStates.get(index) != null){
			BufferStream bsIn = new BufferStream();
			DataStream dsIn = new DataStream(bsIn);

			// set bufferStream buffer
			bsIn.setBuffer((byte [])savedStates.get(index));

			lObjView.restoreState(dsIn);
		}
		lObjView.layout(false);
		if(width > 0 || height > 15){
			lObjView.setRect(0,15,width,height-15);
			Debug.println("Adding lObjView at: " + x + ", " + y +
						  ", " + width + ", " + height);
		}
		lObjView.setShowMenus(showMenus);
		add(lObjView);

		MainWindow.getMainWindow().setFocus(lObjView);
		curIndex = index;
    }

	void saveCurViewState()
	{
		if(lObjView != null && curIndex != -1){
			// Save the state of this view
			BufferStream bsOut = new BufferStream();
			DataStream dsOut = new DataStream(bsOut);
			lObjView.saveState(dsOut);
			if(savedStates.getCount() <= curIndex){
				for(int i=savedStates.getCount(); i<= curIndex; i++){
					savedStates.add(null);
				}
			}
			savedStates.set(curIndex, bsOut.getBuffer());
		}
	}

	public void saveState(DataStream ds)
	{
		ds.writeInt(index);
		
		// need to save the state of the current object
		saveCurViewState();

		ds.writeInt(savedStates.getCount());

		for(int i=0; i< savedStates.getCount(); i++){
			if(savedStates.get(i) != null){
				byte [] state = (byte [])savedStates.get(i);
				ds.writeShort(state.length);
				ds.writeBytes(state, 0, state.length);
			} else {
				ds.writeShort(0);
			}
		}		   
	}

	public void close()
	{

		if(lObjView != null){
			lObjView.close();
			if(lObjSession != null) lObjSession.release();
		}

		super.close();

		if(listSession != null) listSession.release();
		// Commit ???
		// Store ??
    }
}
