package org.concord.waba.extra.probware.transformers;

import org.concord.waba.extra.probware.*;
import org.concord.waba.extra.event.*;
import extra.util.PropObject;

public abstract class CCTransformer implements Transform{
String		name = null;
PropObject		[]properties = null;

	public CCTransformer(){
		this("unknown");
	}
	public CCTransformer(String name){
		this.name = name;
	}
	public void setName(String name){this.name = name;}
	public String getName(){return name;}

}