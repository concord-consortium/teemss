package org.concord.waba.extra.ui;
public class Icon extends ImagePane
{
    java.io.InputStream stream = null;

    public Icon(String path)
    {
		imagePath = path;
    }

    protected waba.fx.Image getImage()
    {
		try{
			stream = this.getClass().getResourceAsStream("../../../../../icons/" + imagePath);
			//system icons should be in extra/sys/icons
			//regular - from root
		}catch(Exception e){
			return null;
		}

		return new waba.fx.Image(stream);
    }

}
