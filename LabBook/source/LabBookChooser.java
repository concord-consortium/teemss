package org.concord.LabBook;import org.concord.waba.extra.ui.*;import org.concord.waba.extra.event.*;import waba.ui.*;import waba.fx.*;public class LabBookChooser extends Dialog{LObjDictionary 		dict;Button				choiceButton;LObjDictionaryView	view;ViewContainer		viewContainer;	public LabBookChooser(LObjDictionary dict,ViewContainer viewContainer){		this(dict,viewContainer,null);	}		public LabBookChooser(LObjDictionary dict,ViewContainer viewContainer,DialogListener l){		super();		this.dict = dict;		this.viewContainer = viewContainer;		addDialogListener(l);	}		public void setContent(){		boolean firstTime = (view == null);		super.setContent();		if(view == null && viewContainer != null && dict != null){			view = (LObjDictionaryView)dict.getView(viewContainer, true);		}		Rect r = getContentPane().getRect();		if(view != null){			if(firstTime){				view.viewFromExternal = true;				view.layout(false);				getContentPane().add(view);			}			view.setRect(0,0,r.width,r.height);		}		if(choiceButton == null){			choiceButton = new Button("Choose LabObject");			getContentPane().add(choiceButton);		}		choiceButton.setRect(r.width/2 - 45,r.height - 18, 90, 16);	}	    public void onEvent(Event e){		if(e.type == TreeControl.DOUBLE_CLICK){			if(e.target instanceof TreeControl){				TreeControl tc = (TreeControl)e.target;			    TreeNode curNode = tc.getSelected();				LabObject obj = dict.getObj(curNode);					if(listener != null){					listener.dialogClosed(new DialogEvent(this,null,null,obj,DialogEvent.OBJECT));				}						hide();				}		}else if(e.type == ControlEvent.PRESSED && e.target == choiceButton){			if(view != null){				TreeNode curNode = view.treeControl.getSelected();				if(curNode != null){					LabObject obj = dict.getObj(curNode);						if(obj != null && listener != null){						listener.dialogClosed(new DialogEvent(this,null,null,obj,DialogEvent.OBJECT));					}				}					}			hide();		}	}}