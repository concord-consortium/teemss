import waba.fx.*;
import waba.ui.*;
import waba.io.*;

class ModelObject extends CanvasObject
  implements ThermalPatch
{
    float initTemp, specHeat, conduct;
    ThermalPlane tp = null;
    Edit tempEdit, specHeatEdit, conducEdit;    
    Label tmpLabel;
    Image im;
    byte [] imArray;
    int imWidth, imHeight;
    int [] colorMap = new int [256];
    int [] greyMap = new int [256];
    int grey = 0x00A0A0A0;
    int tempGridNum = 1;
    int tempGridDenom = 1;
    float temps[] = null;

    ModelObject()
    {
	super();
	initColorMap();

	tempEdit = new Edit();
	specHeatEdit = new Edit();
	conducEdit = new Edit();
    }

    ModelObject(ThermalPlane tp, int w, int h)
    {
	this();
	
	this.tp = tp;
	width = w;
	height = h;
	imWidth = w-2;
	imHeight = h-2;
	im = new Image(imWidth, imHeight);
	imArray = new byte [imWidth * imHeight];
    }

    public void readExt(DataStream ds)
    {
	int i, numTemps;
	boolean validTempArray;
	super.readExt(ds);
	
	initTemp = ds.readFloat();
	specHeat = ds.readFloat();
	conduct = ds.readFloat();
	imWidth = width-2;
	imHeight = height-2;
	im = new Image(imWidth, imHeight);
	imArray = new byte [imWidth * imHeight];
	validTempArray = ds.readBoolean();
	if(!validTempArray){
	    temps = null;
	} else {
	    tempGridNum = ds.readInt();
	    tempGridDenom = ds.readInt();
	    int tempsLength = ds.readInt();
	    temps = new float[tempsLength];
	    for(i=0; i<tempsLength; i++){
		temps[i] = ds.readFloat();
	    }
	}
    }	

    public void writeExt(DataStream ds)
    {
	ds.writeString("ModelObject");
	super.writeExt(ds);
	ds.writeFloat(initTemp);
	ds.writeFloat(specHeat);
	ds.writeFloat(conduct);
	if(tp == null){
	    ds.writeBoolean(false);
	} else {
	    ds.writeBoolean(true);
	    tp.getAllTemps(this);
	    ds.writeInt(tempGridNum);
	    ds.writeInt(tempGridDenom);
	    ds.writeInt(temps.length);
	    for(int i=0; i < temps.length; i++){
		ds.writeFloat(temps[i]);
	    }
	}

    }

    void initColorMap()
    {
	int percent;
	int i;
	int r, g, b;

	for(i=0; i<256; i++){
	    r = 0;
	    b = 0;
	    g = 0;
	    percent = (int)(i*1000/ 255);

	    if(percent < 0){
		b = 255;
	    } else if(percent < 250){
		b = 255;
		g = (255 * percent) / 250;
	    } else if(percent < 500){
		g = 255;
		b = (255 * (500 - percent)) / 250;
	    } else if(percent < 750){
		g = 255;
		r = (255 * (percent - 500)) / 250;
	    } else if(percent < 1000){
		r = 255;
		g = (255 * (1000 - percent)) / 250;
	    } else {
		r = 255;
	    }

	    colorMap[i] = 0;
	    colorMap[i] = (r << 16) | (g << 8) | b;
	}
	for(i=0; i<256; i++){
	    greyMap[i] = grey;
	}
    }

    public float getTemp(int x, int y)
    {

	if(x < this.x ||
	   y < this.y ||
	   x >= this.x + width ||
	   y >= this.y + height){
	    // out of bounds ????
	    return (float)0.0;
	}

	if(tp != null){
	    return tp.getTemp(x, y);
	} else {
	    return initTemp;
	}
    }

    public Container setupProp(int w, int h)
    {
	super.setupProp(w,h);

	// Initial Temperature
	tmpLabel = new Label("Initial Temp");
	tmpLabel.setRect(2,2,65,17);
	properties.add(tmpLabel);
	tempEdit.setRect(70,2,50,17);
	properties.add(tempEdit);

	// Specific heat
	tmpLabel = new Label("Specific Heat");
	tmpLabel.setRect(2,20,65,17);
	properties.add(tmpLabel);
	specHeatEdit.setRect(70,20,50,17);
	properties.add(specHeatEdit);

	// "Conductivity"
	tmpLabel = new Label("Conductivity");
	tmpLabel.setRect(2,40,65,17);
	properties.add(tmpLabel);
	conducEdit.setRect(70,40,50,17);
	properties.add(conducEdit);
	
	return properties;
    }

    public void refreshProp()
    {
	tempEdit.setText(initTemp + "");
	specHeatEdit.setText(specHeat + "");
	conducEdit.setText(conduct + "");
    }

    public void updateProp()
    {
	if(tp != null) tp.removePatch(this, x, y);
	temps = null;
	initTemp = floatValue(tempEdit.getText());
	specHeat = floatValue(specHeatEdit.getText());
	conduct = floatValue(conducEdit.getText());
	if(tp != null) tp.addPatch(this, x, y);
	updateTemp();
    }

    public void move(int x, int y)
    {
	if(tp != null){
	    tp.getAllTemps(this);
	    tp.removePatch(this, this.x, this.y);
	}
	super.move(x, y);
	if(tp != null){
	    tp.addPatch(this, x, y);
	}
	updateTemp();
    }

    public void rotate(int rot)
    {
	if(tp != null){
	    tp.removePatch(this, x, y);	
	    
	    // for now clear our temps
	    temps = null;
	}
	super.rotate(rot);
	if(tp != null){
	    // need to rotate temps here
	    tp.addPatch(this, x, y);
	}
	im.free();
	imWidth = width-2;
	imHeight = height-2;
	im = new Image(imWidth, imHeight);
	updateTemp();
	
    }

    public void move(int rot, int x, int y)
    {
	if(tp != null){
	    if(rot == 0) tp.getAllTemps(this);
	    else temps = null;
	    tp.removePatch(this, this.x, this.y);
	
	    // for now clear our temps
	}	
	super.rotate(rot);
	super.move(x, y);
	if(tp != null){
	    // need to rotate temps here
	    tp.addPatch(this, x, y);
	}
	im.free();
	imWidth = width-2;
	imHeight = height-2;
	im = new Image(imWidth, imHeight);
	updateTemp();
    }

    public void add()
    {
	super.add();
	if(canvas.live && canvas.tp != null){
	    tp = canvas.tp;
	    tp.addPatch(this, x, y);
	    // remove our temps array
	    temps = null;
	}
	updateTemp();
    }
    
    public void remove()
    {
	if(tp != null) {
	    tp.removePatch(this, x, y);
	    tp = null;
	}
	super.remove();
	im.free();
    }

    public void copyTo(CanvasObject co)
    {
	super.copyTo(co);
	if(co instanceof ModelObject){
	    ModelObject me = (ModelObject)co;
	    me.initTemp = initTemp;
	    me.specHeat = specHeat;
	    me.conduct = conduct;
	}
    }

    public CanvasObject copy()
    {
	ModelObject me = new ModelObject(null,width,height);
	copyTo(me);
	return me;
    }

    public void updateTemp()
    {
	float temp;
	int pixVal;
	
	if(canvas == null){
	    return;
	}

       
	int i,j;
	if(canvas.gv != null){
	    float minTemp = canvas.gv.minValue;
	    float tempRng = canvas.gv.range;
	    if(tp != null){ 
		for(j=0; j < imHeight; j++){
		    for(i=0; i < imWidth; i++){
			temp = tp.getTemp(i+x+1,j+y+1);
			pixVal = (int)((temp - minTemp)*255/ tempRng);
			if(pixVal > 255){
			    pixVal = 255;
			} else if(pixVal < 0){
			    pixVal = 0;
			}
			imArray[i+j*imWidth] = (byte)pixVal;
		    }
		}
	    } else {
		for(j=0; j < imHeight; j++){
		    for(i=0; i < imWidth; i++){
			temp = initTemp;
			pixVal = (int)((temp - minTemp)*255/ tempRng);
			if(pixVal > 255){
			    pixVal = 255;
			} else if(pixVal < 0){
			    pixVal = 0;
			}
			imArray[i+j*imWidth] = (byte)pixVal;
		    }
		}
	    } 
	    im.setPixels(8, colorMap, imWidth, imHeight, 0, imArray);
	} else {
	    im.setPixels(8, greyMap, imWidth, imHeight, 0, imArray);
	}
	
	canvas.interactAll(this, CanvasObject.UPDATE);

    }

    public void draw(Graphics g)
    {

	if(g != null){
	    int i,j;
	    g.drawImage(im, x+1, y+1);
	    g.setColor(100,100,100);
	    g.drawRect(x,y,width,height);

	    if(selected){
		// draw black line 
		g.setColor(0,0,0);
		g.drawRect(x,y,width,height);
		g.drawRect(x+1,y+1,width-2,height-2);
	    }
	}
    }

}









