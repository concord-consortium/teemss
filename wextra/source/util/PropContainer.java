package org.concord.waba.extra.util;

import org.concord.waba.extra.io.*;
import waba.util.Vector;

public class PropContainer
{
	Vector properties 			= null;
	Vector owners = null;
	String name;

	public PropContainer(String name)
	{
		setName(name);
	}
	
	public void setName(String n){name = n;}
	public String getName(){return name;}

	public void addProperty(PropObject obj)
	{
		addProperty(obj, null);
	}

	public void addProperty(PropObject obj, PropOwner owner)
	{
		if(properties == null){
			properties = new Vector();
			owners = new Vector();
		}
		properties.add(obj);
		owners.add(owner);
	}

	public void removeProperty(PropObject obj)
	{
		int index = properties.find(obj);
		if(index >= 0){
			properties.del(index);
			owners.del(index);
		}
	}

	public Vector getProperties(){return properties;}
	
	public PropObject getProperty(String name){
		if(name == null) return null;
		for(int i = 0; i < properties.getCount(); i++){
			PropObject po = (PropObject)properties.get(i);
			if(po == null) continue;
			if(name.equals(po.getName())){
				return po;
			}
		}
		return null;
	}

	public PropObject findProperty(int id)
	{
		for(int i = 0; i < properties.getCount(); i++){
			PropObject po = (PropObject)properties.get(i);
			if(po == null) continue;
			if(id == po.getId()){
				return po;
			}
		}
		return null;
	}

	public PropObject findPropObjWithCookie(Object cookie)
	{
		for(int i = 0; i < properties.getCount(); i++){
			PropObject po = (PropObject)properties.get(i);
			if(po == null) continue;
			if(cookie == po.getCookie()){
				return po;
			}
		}
		return null;
	}

	public int countProperties(){
		return properties.getCount();
	}
	
	public String getPropertyValue(String nameProperty){
		PropObject p = getProperty(nameProperty);
		if(p == null) return null;
		return p.getValue();
	}

	public float getPropertyValueAsFloat(String nameProperty){
		PropObject p = getProperty(nameProperty);
		if(p == null) return 0.0f;
		return p.getFValue();
	}
	
	public PropObject[]	getPropArray()
	{
		if(properties.getCount() < 1) return null;

		PropObject [] props = new PropObject[properties.getCount()];
		for(int i=0; i<props.length; i++){
			props[i]= (PropObject)properties.get(i);
		}
		return props;
	}
	
	public void apply()
	{
		int nProperties = properties.getCount();
		for(int j = 0; j < nProperties; j++){
			// technically all the vis values should be updated if
			// the focus stuff is working correctly
			// .. so lets see :)
			PropObject po = (PropObject)properties.get(j);
			if(po != null){
				po.apply();
			}
		}
	}

	public void close()
	{
		int nProperties = properties.getCount();
		for(int j = 0; j < nProperties; j++){
			PropObject po = (PropObject)properties.get(j);
			if(po != null){
				po.setCookie(null);
				po.close();
			}
		}
	}

	public boolean visValueChanged(PropObject po)
	{		
		int index = properties.find(po);
		if(index < 0) return false;

		PropOwner pOwner = (PropOwner) owners.get(index);
		if(pOwner != null) return pOwner.visValueChanged(po);

		return false;
	}

	public void writeExternal(DataStream out)
	{
		out.writeInt(countProperties());
		for(int i = 0; i < countProperties(); i++){
			PropObject p = (PropObject)properties.get(i);
			if(p != null) p.writeExternal(out);
		}
	}

	public void readExternal(DataStream in)
	{
		int temp = in.readInt();
		if(temp < 1) return;
		
		//		properties = new Vector(temp);
		for(int i = 0; i < temp; i++){
			int propId = PropObject.readExternalId(in);
			PropObject p = findProperty(propId);
			if(p != null){
				p.readExternalUpdate(in);
			} else {
				p = new PropObject(in);
				properties.add(p);
				//				setPropertyValue(i,p.getValue());
			} 
		}
	}
}
