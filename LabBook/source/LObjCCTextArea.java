package org.concord.LabBook;

import waba.util.*;
import waba.ui.*;
import org.concord.waba.extra.io.*;
import org.concord.waba.extra.ui.*;

public class LObjCCTextArea extends LObjSubDict{
	static public	boolean editMode = false;
	Vector lines = null;
	LBCompDesc [] components = null;

    public LObjCCTextArea(){
		super(DefaultFactory.CCTEXTAREA);
    }

	public static LabObject makeNewObj(boolean direct){
		return new LObjCCTextArea();
	}

    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict, 
								 LabBookSession session)
	{
		return new LObjCCTextAreaView(vc, this, edit, session, curDict, 
									  lines, components);
    }

    public LabObjectView getPropertyView(ViewContainer vc, LObjDictionary curDict, 
										 LabBookSession session){
    	LObjCCTextAreaPropView propView = new LObjCCTextAreaPropView(vc, this);
    	propView.setEditMode(editMode);
 		return propView;
    }

	public void firstStore(LabBookSession session)
	{
		super.firstStore(session);
		LObjDictionary dict = DefaultFactory.createDictionary();
		session.storeNew(dict);
		setObjDict(dict);
	}

	public void setup(Vector linesVector, Vector linkComponents, 
					  Vector embedComponents, LabBookSession session)
	{
		if(linesVector == null) return;

		int i;
		int linkObjectIndex = 0;
		LObjDictionary objDictionary = getObjDict(session);
		for(i = 0; i < linesVector.getCount(); i++){
			CCStringWrapper wrapper = (CCStringWrapper)linesVector.get(i);
			if(wrapper.link && linkComponents != null){
				LabObject lObject = (LabObject)linkComponents.get(linkObjectIndex);
				if(lObject != null){
					objDictionary.add(lObject);
					wrapper.indexInDict = (linkObjectIndex++);
				}
			}
		}
		lines = linesVector;

		if(embedComponents != null && embedComponents.getCount() > 0){
			for(i = 0; i < embedComponents.getCount(); i++){
				LBCompDesc objDesc = (LBCompDesc)embedComponents.get(i);
				Object o = objDesc.getObject();

				if(o == null || !(o instanceof LabObject)) continue;

				LabObject labObject = (LabObject)o;

				int nComponents = (components == null)?0:components.length;
				LBCompDesc []newComponents = new LBCompDesc[nComponents+1];
				if(components != null){
					waba.sys.Vm.copyArray(components,0,newComponents,0,nComponents);
				}
				components = newComponents;

				components[nComponents] = objDesc;

				setObj(labObject,nComponents + 1);
			}
		}
	}

	public void setLines(Vector lines){ this.lines = lines; }
	public void setComponents(LBCompDesc [] components){ this.components = components; }

	public LObjDictionary getObjDict(LabBookSession session)
	{
		return (LObjDictionary) getObj(0, session);
	}

	public void setObjDict(LObjDictionary dict)
	{
		setObj(dict, 0);
	}

    public void writeExternal(DataStream out)
	{
		if(lines == null){
			out.writeInt(0);
		} else {
			out.writeInt(lines.getCount());
		}
		if(components == null){
			out.writeInt(0);
		} else {
			out.writeInt(components.length);
		}
		if(lines != null){
			for(int i=0; i<lines.getCount(); i++){
				CCStringWrapper sWrap = (CCStringWrapper)lines.get(i);
				if(sWrap == null){
					out.writeBoolean(false);
				} else {
					out.writeBoolean(true);
					sWrap.writeExternal(out);
				}
			}
		}
		if(components != null){
			for(int i=0; i<components.length; i++){
	    		LBCompDesc d = components[i];
				if(d == null){
					out.writeBoolean(false);
				} else {
					out.writeBoolean(true);
					d.writeExternal(out);
				}
			}
		}
    }

    public void readExternal(DataStream in)
	{
		int nLines = in.readInt();
		int nComp = in.readInt();
		lines = new Vector();
		for(int i=0; i<nLines; i++){
			if(in.readBoolean()){
				lines.add(new CCStringWrapper(in));
			} else {
				lines.add(null);
			}
		}

		components = new LBCompDesc[nComp];
		for(int i = 0; i < nComp; i++){
			if(in.readBoolean()){
				components[i] = new LBCompDesc(in);
			} else {
				components[i] = null;
			}
		}
    }
}
