package org.concord.LabBook;

import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.util.*;
import org.concord.waba.extra.event.*;
import waba.ui.*;
import waba.fx.*;


public class LabBookChooser extends Dialog{
LObjDictionary 		dict;
Button				choiceButton;
Button				cancelButton;
LObjDictionaryView	view;
ViewContainer		viewContainer;
ExtraMainWindow 	owner = null;
	LabBookSession      session = null;

	public LabBookChooser(ExtraMainWindow owner,LObjDictionary dict,
						  ViewContainer viewContainer, LabBookSession session){
		this(owner,dict,viewContainer,null, session);
	}
	
	public LabBookChooser(ExtraMainWindow owner,LObjDictionary dict,
						  ViewContainer viewContainer,DialogListener l,
						  LabBookSession session){
		super();
		this.dict = dict;
		this.owner = owner;
		this.viewContainer = viewContainer;
		this.session = session;
		addDialogListener(l);
		owner.setDialog(this);
	}
	
	public void setContent(){
		boolean firstTime = (view == null);
		super.setContent();
		if(view == null && viewContainer != null && dict != null){
			int oldViewType = dict.viewType;
			dict.viewType = LObjDictionary.TREE_VIEW;
			LabObjectView objView = dict.getView(viewContainer, true, session);
			if(objView instanceof LObjDictionaryView){
				view = (LObjDictionaryView)objView;
			}
			dict.viewType = oldViewType;
		}
		if(view != null){
			if(firstTime){
				view.viewFromExternal = true;
				view.layout(false);
				getContentPane().add(view);
			}
			
		}
		if(choiceButton == null){
			choiceButton = new Button("Choose");
			getContentPane().add(choiceButton);
		}
		if(cancelButton == null){
			cancelButton = new Button("Cancel");
			getContentPane().add(cancelButton);
		}
		Rect r = getContentPane().getRect();
		if(view != null) view.setRect(0,0,r.width,r.height - 20);
		cancelButton.setRect(1,r.height - 18, 40, 16);
		choiceButton.setRect(r.width - 55,r.height - 18, 40, 16);
	}

	
    public void onEvent(Event e){
		if(e.type == TreeControl.DOUBLE_CLICK){
			if(e.target instanceof TreeControl){
				TreeControl tc = (TreeControl)e.target;
			    TreeNode curNode = tc.getSelected();
				LabObject obj = ((DictTreeNode)tc.getRootNode()).getObj(curNode);	
				if(listener != null){
					listener.dialogClosed(new DialogEvent(this,null,null,obj,DialogEvent.OBJECT));
				}		
				hide();	
			}

		}else if(e.type == ControlEvent.PRESSED && e.target == cancelButton){
			hide();
		}else if(e.type == ControlEvent.PRESSED && e.target == choiceButton){
			if(view != null){
				TreeNode curNode = view.treeControl.getSelected();
				if(curNode != null){
					LabObject obj = ((DictTreeNode)view.treeControl.getRootNode()).getObj(curNode);	
					if(obj != null && listener != null){
						listener.dialogClosed(new DialogEvent(this,null,null,obj,DialogEvent.OBJECT));
					}
				}		
			}
			hide();
		}
	}

	public void hide(){
		if(owner != null) owner.setDialog(null);
		super.hide();
	}
	public void show(){
		if(owner != null) owner.setDialog(this);
		super.show();
	}

}

