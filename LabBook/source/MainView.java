package org.concord.LabBook;

import waba.ui.*;
import waba.util.*;

import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.io.*;

class PtrWindow
{
	PtrWindow(LabObjectPtr ptr, LabObjectPtr dictPtr, boolean edit)
	{
		this.ptr = ptr;
		this.dictPtr = dictPtr;
		this.edit = edit;
		savedState = null;
	}

	LabObjectPtr ptr;
	LabObjectPtr dictPtr;

	boolean edit;

	byte [] savedState;
}

/*
		BufferStream bsIn = new BufferStream();
		DataStream dsIn = new DataStream(bsIn);

		// We didn't find it so we need to parse it from the file
		byte [] buffer = db.readObjectBytes(lObj.ptr.devId, lObj.ptr.objId);
		if(buffer == null) return false;

		// set bufferStream buffer
		// read buffer by
		bsIn.setBuffer(buffer);
*/

/*
    BufferStream bsOut = new BufferStream();
    DataStream dsOut = new DataStream(bsOut);

	curObjPtr.obj.writeExternal(dsOut);
	outBuf = bsOut.getBuffer();

	bsOut.setBuffer(null);
*/

public abstract class MainView extends ExtraMainWindow
    implements ViewContainer
{
    protected LabBook labBook;
    protected Container me = new Container();
    protected LabObjectView lObjView = null;
    protected MenuBar menuBar;
    protected int myHeight;

	protected LabObjectView curFullView = null;
	protected Vector fullViews = new Vector();
	protected Timer winChangeTimer = null;
	protected LabBookSession curWinSession = null;

    protected Menu file;
	Vector fileMenuStrings = new Vector();
	Vector fileListeners = new Vector();

	public abstract String [] getCreateNames();

	public abstract void createObj(String name, LObjDictionaryView dView);

    public void addMenu(LabObjectView source, Menu menu)
    {
		if(menu != null) menuBar.add(menu);
    }

    public void delMenu(LabObjectView source, Menu menu)
    {
		if(menu != null) menuBar.remove(menu);
    }

	void updateFileMenu()
	{		
		int i;
		file.removeAll();
		for(i=0; i < fileMenuStrings.getCount(); i++){
			String [] items = (String [])fileMenuStrings.get(i);
			for(int j=0; j < items.length; j++){
				file.add(items[j]);
			}
			if(i < (fileMenuStrings.getCount() - 1)) file.add("-");
		}		
	}

	public void addFileMenuItems(String [] items, ActionListener source)
	{
		fileMenuStrings.insert(0, items);
		updateFileMenu();		
		if(source != null) fileListeners.add(source);
	}


	public void removeFileMenuItems(String [] items, ActionListener source)
	{
		int index = fileMenuStrings.find(items);
		if(index < 0) return;
		fileMenuStrings.del(index);
		updateFileMenu();
		if(source != null){
			index = fileListeners.find(source);
			if(index < 0) return;
			fileListeners.del(index);
		}
	}

    public void onStart()
    {
		LabBook.init();

		menuBar = new MenuBar();

		// Notice the width and height will change here
		setMenuBar(menuBar);
		waba.fx.Rect myRect = content.getRect();
		myHeight = myRect.height;

		me.setRect(0,0,width,myHeight);

		add(me);

		labBook = new LabBook();
		LabObject.lBook = labBook;

		file = new Menu("File");
		file.addActionListener(this);

		menuBar.add(file);
	}


    public void actionPerformed(ActionEvent e)
    {
		if(e.getSource() == file){			
			for(int i=0; i<fileListeners.getCount(); i++){
				((ActionListener)fileListeners.get(i)).actionPerformed(e);
			}
		}
	}

	public void onEvent(Event ev)
	{
		if(ev.target == this && ev.type == ControlEvent.TIMER){
			removeTimer(winChangeTimer);
			winChangeTimer = null;
			showLastWindow();
		}
	}

	public void closeTopWindowView()
	{
		// The order is important here because closeTopWin
		// calls setShowMenus which checks lObjView to decide which 
		// menus to show.

		if(fullViews != null &&
		   fullViews.getCount() > 0){

			closeCurFullView();

			fullViews.del(fullViews.getCount()-1);			

			if(winChangeTimer == null){
				winChangeTimer = addTimer(50);		
			}
		}
	}

	void closeCurFullView()
	{
		if(curFullView == null) return;

		curFullView.setShowMenus(false);
		if(curFullView.getContainer() != this) return; //throw new RuntimeException("error")

		// close the view
		curFullView.close();

		remove(curFullView);
		curFullView = null;
	    
		// release it's session
		if(curWinSession != null) curWinSession.release();
		
		setFocus(null);
	}

	void showLastWindow()
	{
		if(fullViews.getCount() > 0){
			Object newTopWin = fullViews.get(fullViews.getCount()-1);
			if(!(newTopWin instanceof PtrWindow)) return;

			PtrWindow pWin = (PtrWindow)newTopWin;
			LabObjectPtr ptr = pWin.ptr;
					
			curWinSession = ptr.getSession();

			LabObject lObj = curWinSession.load(pWin.ptr);					
			LObjDictionary dict = (LObjDictionary)curWinSession.load(pWin.dictPtr);

			curFullView = lObj.getView(this, pWin.edit, dict, curWinSession);

			if(pWin.savedState != null){
				BufferStream bsIn = new BufferStream();
				DataStream dsIn = new DataStream(bsIn);

				// set bufferStream buffer
				bsIn.setBuffer(pWin.savedState);
				curFullView.restoreState(dsIn);
			}
			curFullView.layout(true);
			curFullView.setRect(0,0,width,myHeight);

			curFullView.setShowMenus(true);
			add(curFullView);
			setFocus(curFullView);
		} else {			
			lObjView.setShowMenus(true);
			add(me);
			if(lObjView instanceof LObjDictionaryView){
				((LObjDictionaryView)lObjView).updateWindow();
			}
		}
	}

	/*
	 *  This function requires a special LabObject
	 *  if the labObject has been loaded by the caller
	 *  the caller needs to not release the objects session
	 *
	 *  However once the the View of this object is closed the
	 *  the object will be in a weird state, because it might have
	 *  loaded objects in the View's session.  So the object should
	 *  probably just be released before this is called.  But it is
	 *  trick releasing the object because it might have references
	 *  in the callers session.  Ugh..
	 *
	 *  if the caller comes from a previous showFullWindowObj 
	 *  this will be taken care of automatically
	 */
	public void showFullWindowObj(boolean edit, LObjDictionary dict,  LabObjectPtr ptr)
	{
		LabObject obj;

		LabObjectPtr dictPtr = null;
		if(dict != null) dictPtr = dict.getVisiblePtr();

		if(curFullView == null){
			// This was called by a window or timer that isn't managed
			// by us 
			lObjView.setShowMenus(false);
			remove(me);

			setFocus(null);
		} else {

			// Save the state of this window
			BufferStream bsOut = new BufferStream();
			DataStream dsOut = new DataStream(bsOut);
			curFullView.saveState(dsOut);

			closeCurFullView();

			if(fullViews != null &&
			   fullViews.getCount() > 0){
				((PtrWindow)fullViews.get(fullViews.getCount() - 1)).savedState = bsOut.getBuffer();
				
			}
		}

		fullViews.add(new PtrWindow(ptr, dictPtr, edit));		
		
		if(winChangeTimer == null){
			winChangeTimer = addTimer(50);
		}
	}

	public MainView getMainView()
	{
		return this;
	}

    public void done(LabObjectView source)
	{
		if(source == curFullView){
			closeTopWindowView();
		}
	}

}
