package org.concord.CCProbe;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;
import extra.util.CCUnit;
import org.concord.LabBook.*;
//LabObject implements Storable
public class LObjUConvertor extends LabObject
{

    public LObjUConvertor()
    {
		super(DataObjFactory.UCONVERTOR);
    }

    public LabObjectView getView(ViewContainer vc, 
								 boolean edit, LObjDictionary curDict)
    {    	
    	 return new LObjUConvertorView(vc, this);
    }

    public void writeExternal(DataStream out)
    {
		super.writeExternal(out);
    }

    public void readExternal(DataStream in)
    {
		super.readExternal(in);	
    }
}
class LObjUConvertorView extends LabObjectView
{
	Button clearButton,convertButton,doneButton,dirButton;
	Choice catChoice;
	Choice currChoiceFrom,currChoiceTo;
	Edit	numberLeft,numberRight;
	boolean leftToRight = true;

	Label	nameLabel;
	Edit 	nameEdit;
	boolean	nameEditWasAdded = false;
	boolean doneButtonWasAdded = false;

	public LObjUConvertorView(ViewContainer vc, LObjUConvertor d){
		super(vc);
		lObj = d;	
	}

    public void writeExternal(DataStream out){
    }

    public void readExternal(DataStream in){

    }
    public void layout(boolean sDone){
		if(didLayout) return;
		didLayout = true;

		showDone = sDone;

		if(numberLeft == null) numberLeft = new Edit();
		if(numberRight == null) numberRight = new Edit();
		if(clearButton == null) clearButton = new Button("Clear");
		add(clearButton);
		if(convertButton == null) convertButton = new Button("=");
		add(convertButton);
		if(dirButton == null) dirButton = new Button("->");
		add(dirButton);
		createCatChoice();
		createCurrCat(CCUnit.UNIT_CAT_LENGTH);
		add(numberLeft);
		add(numberRight);
		if(showDone){
			doneButton = new Button("Done");
			add(doneButton);
			doneButtonWasAdded = true;
		}
	}
	public void setEmbeddedState(boolean embeddedState){
		boolean oldState = getEmbeddedState();
		super.setEmbeddedState(embeddedState);
		if(oldState != getEmbeddedState()){
			if(doneButtonWasAdded && doneButton != null){
				remove(doneButton);
				doneButtonWasAdded = false;
			}
			if(getEmbeddedState()){
				if(nameEditWasAdded){
					if(nameEdit != null) remove(nameEdit);
					if(nameLabel != null) remove(nameLabel);
				}
				nameEditWasAdded = false;
			}else{
				if(!nameEditWasAdded){
					waba.fx.Rect r = getRect();
					if(nameEdit != null){
						add(nameEdit);
						int editW = (showDone)?r.width - 62:r.width - 32;
						nameEdit.setRect(30, 1, editW, 15);
					}
					if(nameLabel != null){
						add(nameLabel);
						nameLabel.setRect(1, 1, 30, 15);
					}
					nameEditWasAdded = true;
				}
			}
			if(showDone && !doneButtonWasAdded && doneButton != null){
				add(doneButton);
				doneButtonWasAdded = true;
			}
		}
	}


	public void createCatChoice(){
		catChoice = new Choice();
		for(int i = CCUnit.UNIT_CAT_LENGTH; i <= CCUnit.UNIT_CAT_ELECTRICITY; i++){
			String str = CCUnit.catNames[i];
			catChoice.add(str);
		}
		add(catChoice);
	}
	public void createCurrCat(int index){
		if(index < CCUnit.UNIT_CAT_LENGTH || index > CCUnit.UNIT_CAT_ELECTRICITY) return;
		if(currChoiceFrom != null){
			remove(currChoiceFrom);
		}
		if(currChoiceTo != null){
			remove(currChoiceTo);
		}
		currChoiceFrom = new Choice();
		currChoiceTo   = new Choice();
		for(int i = 0; i < CCUnit.unitTable.length; i++){
			CCUnit u = CCUnit.unitTable[i];
			if(u.unitCategory == index){
				currChoiceFrom.add(u.abbreviation);
				currChoiceTo.add(u.abbreviation);
			}
		}
		add(currChoiceFrom);
		add(currChoiceTo);
		numberLeft.setText("");
		numberRight.setText("");
		
		nameEdit = new Edit();
		nameEdit.setText(getLabObject().name);
		nameLabel = new Label("Name");
		if(getEmbeddedState()){
			nameEditWasAdded = false;
		}else{
			add(nameLabel);
			add(nameEdit);
			nameEditWasAdded = true;
		}
	}

	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		if(!didLayout) layout(true);
		int yStart = 15;
		if(doneButton != null && !getEmbeddedState()){
			doneButton.setRect(width-30,0,30,15);
		}
		if(!getEmbeddedState() && nameEdit != null && nameEditWasAdded){
			waba.fx.Rect r = getRect();
			nameLabel.setRect(1, 1, 30, 15);
			int editW = (showDone)?r.width - 62:r.width - 32;
			nameEdit.setRect(30, 1, editW, 15);
		}

		int curY = 1;
		int dHeight = height;
		if(clearButton != null){
			clearButton.setRect(width/2 - 20, yStart + 65, 40, 15);
		}
		if(dirButton != null){
			dirButton.setRect(width/2 - 10, yStart + 25, 20, 15);
		}
		if(convertButton != null){
			convertButton.setRect(width/2 - 5, yStart + 45, 10, 15);
		}
		if(catChoice != null){
			catChoice.setRect(width/2 - 50, yStart + 5, 100, 15);
		}
		if(currChoiceFrom != null){
			currChoiceFrom.setRect(5, yStart + 25, 40, 15);
		}
		if(currChoiceTo != null){
			currChoiceTo.setRect(width - 45, yStart + 25, 40, 15);
		}
		if(numberLeft != null){
			numberLeft.setRect(5, yStart + 45, 60, 15);
		}
		if(numberRight != null){
			numberRight.setRect(width - 65, yStart + 45, 60, 15);
		}
	}

    public void close(){
    	super.close();
    	if(nameEdit != null){
    		getLabObject().name = nameEdit.getText();
    	}
    }

	public void onEvent(Event e){
		if(e.target == doneButton &&
			e.type == ControlEvent.PRESSED){
			if(container != null){
				container.done(this);
			}	
		}else if(e.target == catChoice &&
			e.type == ControlEvent.PRESSED){
			int index = CCUnit.UNIT_CAT_LENGTH + catChoice.getSelectedIndex();
			createCurrCat(index);
		}else if(e.target == convertButton &&
			e.type == ControlEvent.PRESSED){
			convert();
		}else if(e.target == clearButton &&
			e.type == ControlEvent.PRESSED){
			numberLeft.setText("");
			numberRight.setText("");
		} else if(e.target == dirButton &&
			e.type == ControlEvent.PRESSED){
			leftToRight = !leftToRight;
			if(leftToRight){
				dirButton.setText("->");
			}else{
				dirButton.setText("<-");
			}
		}   
	}

	public void convert(){
		String	leftAbbr = currChoiceFrom.getSelected();
		String	rightAbbr = currChoiceTo.getSelected();
		CCUnit unitLeft = null;
		CCUnit unitRight = null;
		for(int i = 0; i < CCUnit.unitTable.length; i++){
			CCUnit u = CCUnit.unitTable[i];
			if(u.abbreviation.equals(leftAbbr) && unitLeft == null){
				unitLeft = u;
			}
			if(u.abbreviation.equals(rightAbbr) && unitRight == null){
				unitRight = u;
			}
			if(unitRight != null && unitLeft != null) break;
		}
		if(unitRight == null && unitLeft == null) return;
		Edit eFrom,eTo;
		CCUnit uFrom,uTo;
		if(leftToRight){
			eFrom = numberLeft;
			eTo = numberRight;
			uFrom = unitLeft;
			uTo = unitRight;
		}else{
			eFrom = numberRight;
			eTo = numberLeft;
			uFrom = unitRight;
			uTo = unitLeft;
		}
		
		
		String	valFromStr = eFrom.getText();
		float valFrom = (valFromStr == null || valFromStr.length() < 1)?0f:extra.util.ConvertExtra.toFloat(valFromStr);
		float valTo = CCUnit.unitConvert(uFrom, valFrom,uTo);
		if(CCUnit.errorConvertStatus != 0){
			eTo.setText("ERROR");
		}else{
			eTo.setText(""+valTo);
		}
	}

}
