package graph;

import waba.ui.*;

public class PropWindow extends Container
{
    Button set;
    Button cancel;
    
    public static Container topContainer = null;
    public static int DEF_WIDTH = 150;
    public static int DEF_HEIGHT = 150;

    MainWindow mw; 

    public PropWindow()
    {
	setRect(0,0, DEF_WIDTH, DEF_HEIGHT);

	set=new Button("Set");
	set.setRect(5, DEF_HEIGHT-20, 30, 17);
	add(set);

	cancel=new Button("Cancel");
	cancel.setRect(DEF_WIDTH-45, DEF_HEIGHT-20, 40, 17);
	add(cancel);

	mw = MainWindow.getMainWindow();
    }

    PropObject po;
    PropPage pp;

    public void showProp(PropObject po, PropPage pp)
    {
	this.po = po;
	this.pp = pp;

	pp.setRect(3,15,width, height-30);
	po.updateProp(pp, PropPage.REFRESH);
	add(pp);
	
	if(topContainer != null){
	    mw.remove(topContainer);
	}
	mw.add(this);
	
    }

    public void onEvent(Event e)
    {
	if(e.type == ControlEvent.PRESSED){
	    if(e.target == cancel){
		po = null;
		mw.remove(this);
		if(topContainer != null) mw.add(topContainer);
	    } else {
		if(po != null){
		    po.updateProp(pp, pp.UPDATE);
		    mw.remove(this);
		    if(topContainer != null) mw.add(topContainer);
		}
	    }
	}
    }
}






