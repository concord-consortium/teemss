package extra.util;
import waba.util.Vector;

public class PropContainer
{
	Vector properties 			= null;
	String name;

	public PropContainer(String name){
		setName(name);
	}
	
	public void setName(String n){name = n;}
	public String getName(){return name;}

	public void addProperty(PropObject obj){
		if(properties == null){
			properties = new Vector();
		}
		properties.add(obj);
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

	public PropObject getProperty(int index){
		if(index < 0 || index >= properties.getCount()) return null;
		return (PropObject)properties.get(index);
	}

	public int countProperties(){
		return properties.getCount();
	}
	
	public String getPropertyValue(String nameProperty){
		PropObject p = getProperty(nameProperty);
		if(p == null) return null;
		return p.getValue();
	}
	public String getPropertyValue(int index){
		PropObject p = getProperty(index);
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

	protected boolean setPValue(PropObject p,String value){
		if(p == null || value == null) return false;
		p.setValue(value);
		return true;
	}
	
	public boolean setPropertyValue(String nameProperty,String value){
		return setPValue(getProperty(nameProperty),value);
	}
	public boolean setPropertyValue(int index,String value){
		return setPValue(getProperty(index),value);
	}

	public boolean visValueChanged(PropObject po)
	{
		return false;
	}

	public void writeExternal(extra.io.DataStream out)
	{
		if(properties.getCount() > 0){
			out.writeBoolean(true);

			out.writeInt(countProperties());
			for(int i = 0; i < countProperties(); i++){
				PropObject p = getProperty(i);
				out.writeBoolean(p != null);
				if(p == null){
					continue;
				}
				p.writeExternal(out);
			}
		} else {
			out.writeBoolean(false);
		}
	}

	public void readExternal(extra.io.DataStream in)
	{
		if(in.readBoolean()){
			int temp = in.readInt();
			if(temp < 1) return;
			properties = new Vector(temp);
			for(int i = 0; i < temp; i++){
				if(in.readBoolean()){
					PropObject p = new PropObject(in);
					properties.add(p);
					setPropertyValue(i,p.getValue());
				} else {
					properties.add(null);
				}
			}
		}
	}
}
