package org.concord.CCProbe;

import waba.util.*;
import waba.ui.*;

import org.concord.waba.extra.io.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.util.*;

import org.concord.LabBook.*;
import org.concord.ProbeLib.*;

//LabObject implements Storable
public class LObjUConvertor extends LabObject
{
	public float	lastLeftNumber = -1f;
	public float	lastRightNumber = -1f;
	public int		lastCatIndex = -1;
	public int		lastLeftIndex = -1;
	public int		lastRightIndex = -1;
	public int		lastDirection = 0;	

    public LObjUConvertor()
    {
		super(DataObjFactory.UCONVERTOR);
    }

    public LabObjectView getView(ViewContainer vc, 
								 boolean edit, LObjDictionary curDict,
								 LabBookSession session)
    {    
    	LObjUConvertorView newView = new LObjUConvertorView(vc, this);
    	if(newView != null){
    		newView.setPrevValues(lastLeftNumber,lastRightNumber,
								  lastCatIndex,lastLeftIndex,
								  lastRightIndex,lastDirection);
    	}
    	return newView;
    }

    public void writeExternal(DataStream out)
    {
		out.writeFloat(lastLeftNumber);
		out.writeFloat(lastRightNumber);
		out.writeInt(lastCatIndex);
		out.writeInt(lastLeftIndex);
		out.writeInt(lastRightIndex);
		out.writeInt(lastDirection);
    }

    public void readExternal(DataStream in)
    {
		lastLeftNumber = in.readFloat();
		lastRightNumber = in.readFloat();
		lastCatIndex = in.readInt();
		lastLeftIndex = in.readInt();
		lastRightIndex = in.readInt();
		lastDirection = in.readInt();
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
	
	LObjUConvertor	owner;
	
	public float	lastLeftNumber = -1f;
	public float	lastRightNumber = -1f;
	public int		lastCatIndex = -1;
	public int		lastLeftIndex = -1;
	public int		lastRightIndex = -1;
	public int		lastDirection = 0;	

	public LObjUConvertorView(ViewContainer vc, LObjUConvertor d){
		super(vc, (LabObject)d, null);
		owner = d;
	}

    public void writeExternal(DataStream out){
    }

    public void readExternal(DataStream in){

    }
    public void layout(boolean sDone){
		if(didLayout) return;
		didLayout = true;

		showDone = sDone;

		if(clearButton == null) clearButton = new Button("Clear");
		add(clearButton);
		if(convertButton == null) convertButton = new Button("=");
		add(convertButton);
		if(dirButton == null) dirButton = new Button("->");
		add(dirButton);
		createCatChoice();
		if(numberLeft == null) numberLeft = new Edit();
		if(numberRight == null) numberRight = new Edit();
		if(lastCatIndex >= 0){
			createCurrCat(CCUnit.UNIT_CAT_LENGTH+lastCatIndex);
		}else{
			createCurrCat(CCUnit.UNIT_CAT_LENGTH);
		}
		add(numberLeft);
		add(numberRight);
		if(showDone){
			doneButton = new Button("Done");
			add(doneButton);
			doneButtonWasAdded = true;
		}
		if(nameEdit == null) nameEdit = new Edit();
		nameEdit.setText(getLabObject().getName());
		if(nameLabel == null) nameLabel = new Label("Name");
		if(getEmbeddedState()){
			nameEditWasAdded = false;
		}else{
			add(nameLabel);
			add(nameEdit);
			nameEditWasAdded = true;
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
	
	boolean    needCreateCurrCat = true;
	
	public void createCurrCat(int index){
		if(index < CCUnit.UNIT_CAT_LENGTH || index > CCUnit.UNIT_CAT_ELECTRICITY) return;
		if(currChoiceFrom != null){
			remove(currChoiceFrom);
		}
		if(currChoiceTo != null){
			remove(currChoiceTo);
		}

		Vector catAbbrevs = CCUnit.getCatUnitAbbrev(index);
		currChoiceFrom = new Choice(catAbbrevs);
		currChoiceTo   = new Choice(catAbbrevs);

		add(currChoiceFrom);
		add(currChoiceTo);
		needCreateCurrCat = false;
		catChoice.setSelectedIndex(index - CCUnit.UNIT_CAT_LENGTH);
		needCreateCurrCat = true;
		
		if(lastLeftNumber >= 0f){
			numberLeft.setText(""+lastLeftNumber);
		}else{
			numberLeft.setText("");
		}
		if(lastRightNumber >= 0f){
			numberRight.setText(""+lastRightNumber);
		}else{
			numberRight.setText("");
		}
		if(lastLeftIndex >= 0 && (currChoiceFrom != null)){
			currChoiceFrom.setSelectedIndex(lastLeftIndex);
		}
		if(lastRightIndex >= 0 && (currChoiceTo != null)){
			currChoiceTo.setSelectedIndex(lastRightIndex);
		}
		
		if(lastDirection != 0 && dirButton != null){
			leftToRight = (lastDirection == 1);
			if(leftToRight){
				dirButton.setText("->");
			}else{
				dirButton.setText("<-");
			}
		}

		
		
	}
	public int getPreferredWidth(){
		return 100;
	}

	public int getPreferredHeight(){
		return 120;
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
	public void setPrevValues(float lastLeftNumber,float  lastRightNumber,int  lastCatIndex,
							  int lastLeftIndex,int lastRightIndex,int lastDirection){
		this.lastLeftNumber 	= lastLeftNumber;
		this.lastRightNumber 	= lastRightNumber;
		this.lastCatIndex 		= lastCatIndex;
		this.lastLeftIndex 		= lastLeftIndex;
		this.lastRightIndex 	= lastRightIndex;
		this.lastDirection 		= lastDirection;

/*
		System.out.println("lastCatIndex "+this.lastCatIndex+" lastDirection "+this.lastDirection);
		System.out.println("lastLeftNumber "+this.lastLeftNumber+" lastRightNumber "+this.lastRightNumber);
		System.out.println("lastLeftIndex "+this.lastLeftIndex+" lastRightIndex "+this.lastRightIndex);
*/
	}

    public void close(){
    	if(owner != null){
			owner.lastLeftNumber = -1f;
			if(numberLeft != null){
				owner.lastLeftNumber = ConvertExtra.toFloat(numberLeft.getText());
				if(owner.lastLeftNumber < 0f) owner.lastLeftNumber = 0f;
			}
			owner.lastRightNumber = -1f;
			if(numberRight != null){
				owner.lastRightNumber = ConvertExtra.toFloat(numberRight.getText());
				if(owner.lastRightNumber < 0f) owner.lastRightNumber = 0f;
			}
			owner.lastCatIndex = -1;
			if(catChoice != null){
				owner.lastCatIndex = catChoice.getSelectedIndex();
			}
			owner.lastLeftIndex = -1;
			if(currChoiceFrom != null){
				owner.lastLeftIndex = currChoiceFrom.getSelectedIndex();
			}
			owner.lastRightIndex = -1;
			if(currChoiceTo != null){
				owner.lastRightIndex = currChoiceTo.getSelectedIndex();
			}
			owner.lastDirection = (leftToRight)?1:-1;	
		}
		lastLeftNumber 	= owner.lastLeftNumber;
		lastRightNumber = owner.lastRightNumber;
		lastCatIndex 	= owner.lastCatIndex;
		lastLeftIndex 	= owner.lastLeftIndex;
		lastRightIndex 	= owner.lastRightIndex;
		lastDirection 	= owner.lastDirection;

/*
		System.out.println("lastCatIndex "+lastCatIndex+" lastDirection "+lastDirection);
		System.out.println("lastLeftNumber "+lastLeftNumber+" lastRightNumber "+lastRightNumber);
		System.out.println("lastLeftIndex "+lastLeftIndex+" lastRightIndex "+lastRightIndex);
*/
    	super.close();
    	if(nameEdit != null){
    		getLabObject().setName(nameEdit.getText());
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
			waba.fx.Rect oldRect = getRect();
			if(needCreateCurrCat){
				lastLeftNumber 	= owner.lastLeftNumber = -1f;
				lastRightNumber = owner.lastRightNumber = -1f;
				lastCatIndex = owner.lastCatIndex = index - CCUnit.UNIT_CAT_LENGTH;
				lastLeftIndex = owner.lastLeftIndex = 0;
				lastRightIndex = owner.lastRightIndex = 0;
				createCurrCat(index);
			}
			setRect(oldRect.x,oldRect.y,oldRect.width,oldRect.height);
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
			lastDirection = owner.lastDirection = (leftToRight)?1:-1;	
		} else if(e.target == currChoiceFrom){
			lastLeftIndex = owner.lastLeftIndex = currChoiceFrom.getSelectedIndex();
		} else if(e.target == currChoiceTo){
			lastRightIndex 	= owner.lastRightIndex = currChoiceTo.getSelectedIndex();
		}   
	}

	public void convert(){
		String	leftAbbr = currChoiceFrom.getSelected();
		String	rightAbbr = currChoiceTo.getSelected();
		CCUnit unitLeft = null;
		CCUnit unitRight = null;
		for(int i = 1; i < CCUnit.UNIT_TABLE_LENGTH; i++){
			CCUnit u = CCUnit.getUnit(i);
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
		float valFrom = (valFromStr == null || valFromStr.length() < 1)?0f:ConvertExtra.toFloat(valFromStr);
		float valTo = CCUnit.unitConvert(uFrom, valFrom,uTo);
		if(CCUnit.errorConvertStatus != 0){
			eTo.setText("ERROR");
		}else{
			eTo.setText(""+valTo);
		}
		owner.lastLeftNumber = -1f;
		if(numberLeft != null){
			owner.lastLeftNumber = ConvertExtra.toFloat(numberLeft.getText());
			if(owner.lastLeftNumber < 0f) owner.lastLeftNumber = 0f;
		}
		lastLeftNumber 	= owner.lastLeftNumber;
		owner.lastRightNumber = -1f;
		if(numberRight != null){
			owner.lastRightNumber = ConvertExtra.toFloat(numberRight.getText());
			if(owner.lastRightNumber < 0f) owner.lastRightNumber = 0f;
		}
		lastRightNumber = owner.lastRightNumber;
	}

}
