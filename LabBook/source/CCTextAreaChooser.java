package org.concord.LabBook;

import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import waba.ui.*;
import waba.fx.*;
import waba.sys.*;
import waba.util.Vector;
import extra.io.*;

public class CCTextAreaChooser extends LabBookChooser
{
	EmbedObjectPropertyControl		objProperty;

	public CCTextAreaChooser(ExtraMainWindow owner,LObjDictionary dict,
							 ViewContainer viewContainer,DialogListener l,
							 LabBookSession session)
	{
		super(owner,dict,viewContainer,l, session);
	}

	public void setContent()
	{
		Rect r = getContentPane().getRect();
		super.setContent();
		if(view != null) view.setRect(0,0,r.width,r.height - 52);
		if(objProperty == null){
			objProperty = new EmbedObjectPropertyControl(null);
			objProperty.layout(false);
			getContentPane().add(objProperty);
			objProperty.setRect(0,r.height - 55, r.width, 37);
		}				
	}

    public void onEvent(Event e){
    	LabObject  obj = null;
    	boolean	   doNotify = false;
    	if(e.type == EmbedObjectPropertyControl.NEED_DEFAULT_SIZE){
			if(view == null) return;
			TreeNode curNode = view.treeControl.getSelected();
			obj = ((DictTreeNode)view.treeControl.getRootNode()).getObj(curNode);	
			if(obj == null) return;
			boolean isLink = objProperty.linkCheck.getChecked();
			LabObjectView objView = (isLink)?obj.getMinimizedView():obj.getView(null,false,session);
			if(objView == null) return;
			extra.ui.Dimension d = objView.getPreferredSize();
			if(d == null) return;
			if(d.width > 0){
				objProperty.widthEdit.setText(""+d.width);
				objProperty.lastW = d.width;
			}
			if(d.height > 0){
				objProperty.heightEdit.setText(""+d.height);
				objProperty.lastH = d.height;
			}
    		return;
    	}
    	
		if(e.type == TreeControl.DOUBLE_CLICK){
			if(e.target instanceof TreeControl){
				TreeControl tc = (TreeControl)e.target;
			    TreeNode curNode = tc.getSelected();
				obj = ((DictTreeNode)tc.getRootNode()).getObj(curNode);	
				doNotify = true;
			}

		}else if(e.type == ControlEvent.PRESSED && e.target == cancelButton){
			hide();
		}else if(e.type == ControlEvent.PRESSED && e.target == choiceButton){
			if(view != null){
				TreeNode curNode = view.treeControl.getSelected();
				if(curNode != null){
					obj = ((DictTreeNode)view.treeControl.getRootNode()).getObj(curNode);	
				}		
			}
			doNotify = true;
		}
		if(doNotify){
			if(obj != null && listener != null && objProperty != null){
				boolean wrap = objProperty.wrapCheck.getChecked();
				objProperty.lastWrap = wrap;
				int alighn = LBCompDesc.ALIGNMENT_LEFT;
				if(objProperty.alignmentChoice != null){
					objProperty.lastAlighnLeft = true;
					if(objProperty.alignmentChoice.getSelected().equals("Right")){
						objProperty.lastAlighnLeft = false;
						alighn = LBCompDesc.ALIGNMENT_RIGHT;
					}
				}
				int wc = 10;
				if(objProperty.widthEdit != null){
					wc = Convert.toInt(objProperty.widthEdit.getText());
				}
				objProperty.lastW = wc;
				int hc = 10;
				if(objProperty.heightEdit != null){
					hc = Convert.toInt(objProperty.heightEdit.getText());
				}
				objProperty.lastH = hc;
	 	  		LBCompDesc cdesc = new LBCompDesc(0,wc,hc,alighn,wrap,objProperty.linkCheck.getChecked());
	 	  		objProperty.lastLink = objProperty.linkCheck.getChecked();
	 	  		cdesc.setObject(obj);
				if(listener != null) listener.dialogClosed(new DialogEvent(this,null,null,cdesc,DialogEvent.OBJECT));
			}
			hide();
		}
	}
}
