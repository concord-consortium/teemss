package org.concord.LabBook;

import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import waba.ui.*;
import waba.fx.*;
import waba.sys.*;
import waba.util.Vector;
import extra.io.*;

public class TextObjPropertyView extends LabObjectView{
	CCStringWrapper stringWrapper;
	public Edit		strEdit;
	public Button	doneButton;
	public Button	cancelButton;
	public Edit		colorEdit;
	public Label	colorLabel;
	public Check	linkCheck;
	ExtraMainWindow owner;
	LObjDictionary dict;

	LObjDictionaryView	view;

	LabBookSession session;


	public TextObjPropertyView(ExtraMainWindow owner,LObjDictionary dict,
							   ViewContainer vc, CCStringWrapper stringWrapper, 
							   LabBookSession session){
		super(vc, (LabObject)dict, session);
		this.stringWrapper 	= stringWrapper;
		this.dict 	= dict;
		this.owner 	= owner;
		this.session = session;
	}
	public void layout(boolean sDone){
		if(didLayout) return;
		didLayout = true;
		if(strEdit == null){
			strEdit = new Edit();
			if(stringWrapper != null){
				strEdit.setText(stringWrapper.getStr());
			}
			add(strEdit);
		}
		if(colorEdit == null){
			colorEdit = new Edit();
			add(colorEdit);
			if(stringWrapper != null){
				colorEdit.setText(hexaFromColor(stringWrapper.rColor,stringWrapper.gColor,stringWrapper.bColor));
			}
		}
		if(colorLabel == null){
			colorLabel = new Label("Color 0x");
			add(colorLabel);
		}
		if(linkCheck == null){
			linkCheck = new Check("Link");
			add(linkCheck);
			if(stringWrapper != null){
				linkCheck.setChecked(stringWrapper.link);
			}
		}
		if(view == null && container != null && dict != null){
			view = (LObjDictionaryView)dict.getView(container, true, session);
			view.viewFromExternal = true;
			view.layout(false);
			add(view);
		}
		if(cancelButton == null){
			cancelButton = new Button("Cancel");
			add(cancelButton);
		}
		if(doneButton == null){
			doneButton = new Button("Done");
			add(doneButton);
		}
	}
	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);
		if(doneButton != null)	doneButton.setRect(width-31,height-15,30,15);
		if(cancelButton != null) cancelButton.setRect(2,height-15,40,15);

		if(strEdit != null){
			strEdit.setRect(2,2, width - 4, 15);
		}
		
		if(colorLabel != null){
			colorLabel.setRect(2,20,36,15);
		}
		if(colorEdit != null){
			colorEdit.setRect(40,20,40,15);
		}
		if(linkCheck != null){
			linkCheck.setRect(2,40,50,15);
		}
		if(view != null){
			view.setRect(2,60,width - 4,height - 77);
		}
		
	}
	
	static char hexaFromDigit(int d){
		if(d >=0 && d <= 9){
			return (char)(d + '0');
		}else if(d >=10 && d <= 15){
			return (char)(d - 10 + 'A');
		}
		return '0';
	}
	static String hexaFromColor(int r, int g, int b){
		if(r < 0) 	r = 0;
		if(r > 255) r = 255;
		if(g < 0) 	g = 0;
		if(g > 255) g = 255;
		if(b < 0) 	b = 0;
		if(b > 255) b = 255;
		String str = "";
		
		str += hexaFromDigit(r >>> 4);
		str += hexaFromDigit(r & 0xF);
		str += hexaFromDigit(g >>> 4);
		str += hexaFromDigit(g & 0xF);
		str += hexaFromDigit(b >>> 4);
		str += hexaFromDigit(b & 0xF);
		
		return str;		
	}
	
	static int byteFromHexa(String str){
		int retValue = 0;
		if(str == null) return retValue;
		int base = 1;
		int curCharInd = str.length() - 1;
		while(curCharInd >= 0){
			char c = str.charAt(curCharInd);
			if(c >= '0' && c <= '9'){
				retValue += base*(int)(c - '0');
			}else if(c >= 'A' && c <= 'F'){
				retValue += base*(int)(c - 'A' + 10);
			}else if(c >= 'a' && c <= 'f'){
				retValue += base*(int)(c - 'a' + 10);
			}
			curCharInd--;
			base <<= 4;
		}
		
		return retValue;
		
	}

	public void onEvent(Event e){
		if(e.target == cancelButton && e.type == ControlEvent.PRESSED){
			if(container != null) container.done(this);
		}else if(e.target == doneButton && e.type == ControlEvent.PRESSED){
			if(container != null){
				container.done(this);
				if(stringWrapper == null) return;
				if(linkCheck != null){
					stringWrapper.link = linkCheck.getChecked();
				}
				if(colorEdit != null){
					String strColor = colorEdit.getText();
					int n = strColor.length();
					if(n < 6){
						for(int i = 0; i < 6-n; i++) strColor += "0";
					}else{
						strColor = strColor.substring(0,6);
					}
					stringWrapper.rColor = byteFromHexa(strColor.substring(0,2));
					stringWrapper.gColor = byteFromHexa(strColor.substring(2,4));
					stringWrapper.bColor = byteFromHexa(strColor.substring(4,6));
					
				}
				int oldIndex = stringWrapper.indexInDict;
				stringWrapper.indexInDict = -1;
				if(stringWrapper.link && (view != null) && (stringWrapper.owner != null) && (stringWrapper.owner.objDictionary != null)){
					TreeNode curNode = view.treeControl.getSelected();
					if(curNode == null){
						stringWrapper.indexInDict = oldIndex;
					}else{
						LabObject obj = ((DictTreeNode)(view.treeControl.getRootNode())).getObj(curNode);
						if(obj != null){
							int dIndex = stringWrapper.owner.objDictionary.getIndex(obj);
							if(dIndex >= 0){
								stringWrapper.indexInDict = dIndex;
							}else{
								stringWrapper.owner.objDictionary.add(obj);
								stringWrapper.indexInDict = stringWrapper.owner.objDictionary.getChildCount() - 1;
							}
						}
					}
				}		


				if(strEdit != null && stringWrapper.owner != null){
					stringWrapper.owner.changeLineContent(strEdit.getText(),stringWrapper);
				}
				stringWrapper.owner.repaint();
			}	 
		}
	}
}
