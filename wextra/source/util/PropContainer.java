package extra.util;
import waba.util.Vector;

public class PropContainer
{
Vector propContainers 		= null;
Vector propContainersNames 	= null;
Vector properties 			= null;
	public PropContainer(){
	}
	
	public PropContainer createSubContainer(String name){
		if(propContainers == null){
			propContainersNames 	= new Vector();
			propContainers 		= new Vector();
		}
		PropContainer pc = new PropContainer();
		propContainersNames.add(name);
		propContainers.add(pc);
		return pc;
	}
	
	private void addProperty(PropObject obj){
		if(properties == null){
			properties = new Vector();
		}
		properties.add(obj);
	}
	public void addProperty(PropObject obj,String nameContainer){
		if(nameContainer == null){
			addProperty(obj);
		}
		PropContainer pc = findContainerByName(nameContainer);
		if(pc != null){
			pc.addProperty(obj);
		}
	}
	
	public Vector getProperties(){return properties;}
	
	public Vector getProperties(String nameContainer){
		if(nameContainer == null) return getProperties();
		PropContainer pc = findContainerByName(nameContainer);
		if(pc != null){
			return pc.getProperties();
		}
		return null;
	}
	public Vector getProperties(int index){
		PropContainer pc = getPropertiesContainer(index);
		if(pc != null){
			return pc.getProperties();
		}
		return null;
	}
	public Vector getPropertiesContainers(){
		return propContainers;
	}
	public PropContainer getPropertiesContainer(int index){
		if(index < 0 || index >= getNumbPropContainers()) return null;
		return (PropContainer)getPropertiesContainers().get(index);
	}
	public String getPropertiesContainerName(int index){
		if(index < 0 || index >= getNumbPropContainers()) return null;
		return (String)getPropertiesContainersNames().get(index);
	}
	public int getNumbPropContainers(){
		if(propContainers == null) return 0;
		return propContainers.getCount();
	}
	
	public Vector getPropertiesContainersNames(){
		return propContainersNames;
	}
	public String getContainerName(int index){
		if(index < 0 || index >= getNumbPropContainers()) return "";
		return (String)getPropertiesContainersNames().get(index);
	}
	
	public PropObject getProperty(String name){
		if(name == null) return null;
		for(int i = 0; i < properties.getCount(); i++){
			PropObject po = (PropObject)properties.get(i);
			if(name.equals(po.getName())){
				return po;
			}
		}
		return null;
	}
	public PropObject getProperty(String name,String nameContainer){
		if(nameContainer == null) return getProperty(name);
		PropContainer pc = findContainerByName(nameContainer);
		if(pc != null){
			return pc.getProperty(name);
		}
		return null;
	}
	
	public PropContainer findContainerByName(String name){
		PropContainer retValue = null;
		if(name == null || propContainersNames == null) return retValue;
		for(int i = 0; i < propContainersNames.getCount(); i++){
			String nm = (String)propContainersNames.get(i);
			if(name.equals(nm)){
				retValue = (PropContainer)propContainers.get(i);
				break;
			}
		}
		return retValue;
	}
}
