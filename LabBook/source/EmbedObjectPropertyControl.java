package org.concord.LabBook;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import waba.ui.*;
import waba.fx.*;
import waba.sys.*;


public class EmbedObjectPropertyControl extends LabObjectView
{
public Choice	alignmentChoice;
public Check	wrapCheck,linkCheck;
public Edit	widthEdit,heightEdit;
public Label	alignmentLabel,wrapLabel,widthLabel,heightLabel;

public Button	prefSizeButton;

public static boolean lastAlighnLeft 	= true;
public static boolean lastWrap 		= true;
public static boolean lastLink 		= false;
public static int		lastW			= 10;
public static int		lastH			= 10;
waba.fx.Font 	font;
Button			doneButton;
private String	name;
private Label	nameLabel;
	public EmbedObjectPropertyControl(ViewContainer vc){
		super(vc, null, null);
		font = new Font("Helvetica",Font.PLAIN,12);
	}
	public EmbedObjectPropertyControl(ViewContainer vc,String name){
		super(vc, null, null);
		font = new Font("Helvetica",Font.PLAIN,12);
		this.name = name;
	}
	
	public void setName(String name){
		if(this.name == null) return;
		if(name == null) return;
		this.name = name;
		if(nameLabel == null){
			nameLabel = new Label(name);
			add(nameLabel);
		}else{
			nameLabel.setText(name);
		}
	}
	
	public void layout(boolean sDone){
		if(didLayout) return;
		didLayout = true;
		if(name != null && nameLabel == null){
			nameLabel = new Label(name);
			add(nameLabel);
		}
		
		if(alignmentLabel == null){
			alignmentLabel = new Label("Align");
			add(alignmentLabel);
		}
		if(alignmentChoice == null){
			String choices[] = {"Left","Right"};
			alignmentChoice = new Choice(choices);
			add(alignmentChoice);
		}
		if(wrapCheck == null){
			wrapCheck = new Check("Wrap");
			add(wrapCheck);
		}
		if(linkCheck == null){
			linkCheck = new Check("Link ");
			add(linkCheck);
			
		}
		if(widthLabel == null){
			widthLabel = new Label("W");
			add(widthLabel);
		}
		if(widthEdit == null){
			widthEdit = new Edit();
			add(widthEdit);
		}
		if(heightLabel == null){
			heightLabel = new Label("H");
			add(heightLabel);
		}
		if(heightEdit == null){
			heightEdit = new Edit();
			add(heightEdit);
		}
		if(prefSizeButton == null){
			prefSizeButton = new Button("Def. Size");
			add(prefSizeButton);
		}
		
		showDone = sDone;
		if(showDone){
			doneButton = new Button("Done");
			add(doneButton);
		} 
	}
	
	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);
		int yStart = 5;

  		waba.fx.FontMetrics fm = getFontMetrics(font); 
		int xStart = 4;
		
		if(nameLabel != null){
			nameLabel.setRect(xStart,yStart, width - 2*xStart, 15);
			yStart = 20;
		}
		
		
		int cLength = (fm == null)?40:(2 + fm.getTextWidth("Align"));
		alignmentLabel.setRect(xStart,yStart, cLength, 15);
		if(lastAlighnLeft){
			alignmentChoice.setSelectedIndex("Left");
		}else{
			alignmentChoice.setSelectedIndex("Right");
		}
		xStart += (cLength);
		cLength = 40;
		alignmentChoice.setRect(xStart,yStart, cLength, 15);
		
		
		wrapCheck.setChecked(lastWrap);
		xStart += (3+cLength);
		cLength = 37;
		wrapCheck.setRect(xStart,yStart, cLength, 15);


		linkCheck.setChecked(lastLink);
		xStart += (5+cLength);
		linkCheck.setRect(xStart,yStart, cLength, 15);

		
		xStart = 4;
		yStart += 15;
		cLength = (fm == null)?30:(2 + fm.getTextWidth("W"));
		widthLabel.setRect(xStart,yStart, cLength, 15);

		widthEdit.setText(""+lastW);
		xStart += (cLength);
		cLength = 30;
		widthEdit.setRect(xStart,yStart, cLength, 15);
		
		
		xStart += (cLength);
		cLength = (fm == null)?30:(2 + fm.getTextWidth("H"));
		heightLabel.setRect(xStart,yStart, cLength, 15);

		heightEdit.setText(""+lastH);
		xStart += (cLength);
		cLength = 30;
		heightEdit.setRect(xStart,yStart, cLength, 15);
		
		if(prefSizeButton != null){
			xStart += (cLength);
			cLength = 50;
			prefSizeButton.setRect(width - cLength - 5,yStart, cLength, 15);
		}
		
		if(showDone){
			doneButton.setRect(width-31,height-15,30,15);
		}
	}
	public final static int NEED_DEFAULT_SIZE = 2010;
	public void onEvent(Event e){
		if(prefSizeButton != null && e.target == prefSizeButton && e.type == ControlEvent.PRESSED){
			postEvent(new ControlEvent(NEED_DEFAULT_SIZE, this));
		}else if(e.target == doneButton && e.type == ControlEvent.PRESSED){
			if(container != null){
				container.done(this);
			}	 
		}
	}
	public int getPreferredWidth(waba.fx.FontMetrics fm){
		return 100;
	}

	public int getPreferredHeight(waba.fx.FontMetrics fm){
		return 40;
	}

	private Dimension preferrDimension;
	public Dimension getPreferredSize(){
		if(preferrDimension == null){
			preferrDimension = new Dimension(getPreferredWidth(null),getPreferredHeight(null));
		}else{
			preferrDimension.width = getPreferredWidth(null);
			preferrDimension.height = getPreferredHeight(null);
		}
		return preferrDimension;
	}
	

}
