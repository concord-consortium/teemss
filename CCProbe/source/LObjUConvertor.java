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
		currChoiceFrom.setRect(5, 25, 40, 15);
		currChoiceTo.setRect(width - 45, 25, 40, 15);
		numberLeft.setText("");
		numberRight.setText("");
	}

	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		if(!didLayout) layout(true);

		int curY = 1;
		int dHeight = height;
		if(clearButton != null){
			clearButton.setRect(width/2 - 20, 65, 40, 15);
		}
		if(dirButton != null){
			dirButton.setRect(width/2 - 10, 25, 20, 15);
		}
		if(convertButton != null){
			convertButton.setRect(width/2 - 5, 45, 10, 15);
		}
		if(doneButton != null){
			doneButton.setRect(width/2 - 20, height - 15, 40, 15);
		}
		if(catChoice != null){
			catChoice.setRect(width/2 - 50, 5, 100, 15);
		}
		if(currChoiceFrom != null){
			currChoiceFrom.setRect(5, 25, 40, 15);
		}
		if(currChoiceTo != null){
			currChoiceTo.setRect(width - 45, 25, 40, 15);
		}
		if(numberLeft != null){
			numberLeft.setRect(5, 45, 60, 15);
		}
		if(numberRight != null){
			numberRight.setRect(width - 65, 45, 60, 15);
		}

	}

    public void close(){
    	super.close();
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
