import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import extra.ui.*;

public class LObjQuestionEditView extends LabObjectView
{
    LObjQuestion quest = null;

    Edit nameEdit;
    RelativeContainer edit = new RelativeContainer();
 
    Button doneButton;

    LabObjectView doc;

    String [] questionTypes = {"True/False", "Multiple Choice", "Essay"};
    Choice typeChoice = new Choice(questionTypes);
    Label typeLabel = new Label("Type");

    LabObjectView options = null;
    int optionsY = 0;
    int optionsH = 10;

    public LObjQuestionEditView(LObjViewContainer vc, LObjQuestion lq)
    {
	super(vc);
	quest = lq;
	lObj = (LabObject)lObj;
	typeChoice.setSelectedIndex(quest.questionType);
	if(quest.options != null){
	    options = quest.options.getView(null, true);
	}
    }

    public void onEvent(Event e)
    {
	if(e.type == ControlEvent.PRESSED &&
	   e.target == typeChoice){
	    if(typeChoice.getSelected().equals("Multiple Choice")){
		if(options == null){
		    LObjDictionary optionsDict = new LObjDictionary();
		    optionsDict.viewType = LObjDictionary.PAGING_VIEW;
		    options = optionsDict.getView(null, true);
		    options.layout(false);
		    options.setRect(1, optionsY, 140, optionsH);		    
		    add(options);
		}
	    } else {
		if(options != null){
		    remove(options);
		    options = null;
		}
	    }
	} else if(e.target == doneButton &&
		  e.type == ControlEvent.PRESSED){
	    if(container != null){
		container.done(this);
	    }	    
	}
    }
    
    public boolean showName = true;

    public void layout(boolean sDone)
    {
	if(didLayout) return;
	didLayout = true;

	showDone = sDone;

	if(showName){
	    nameEdit = new Edit();
	    nameEdit.setText(quest.name==null?"question":quest.name);
	    Label nameLabel = new Label("Name");
	    nameLabel.setRect(1, 1, 30, 15);
	    add(nameLabel);
	    nameEdit.setRect(30, 1, 50, 15);
	    add(nameEdit);
	} 
	doc = quest.getQuestionText().getView(null, true);
	if(doc instanceof LObjDocumentView) ((LObjDocumentView)doc).showName = false;
	doc.layout(false);
	add(doc);

	add(typeLabel);
	add(typeChoice);

	if(quest.questionType == quest.MULTIPLE_CHOICE){
	    if(options == null){
		LObjDictionary optionsDict = new LObjDictionary();
		optionsDict.viewType = LObjDictionary.PAGING_VIEW;
		options = optionsDict.getView(null, true);
	    }
	    add(options);
	}

	if(showDone){
	    doneButton = new Button("Done");
	    add(doneButton);
	}

    }

    public void setRect(int x, int y, int width, int height)
    {
	int curY = 1;
	super.setRect(x,y,width,height);
	if(!didLayout) layout(false);

	if(showDone){
	    doneButton.setRect(width-30,1,30,15);
	}

	if(showDone || showName){
	    height -=16;
	    curY = 16;
	}
	

	doc.setRect(1, curY, (width > 400)?400:width, (height - 16)/2);
	curY += (height - 16)/2;
	height -= (height - 16)/2;

	typeLabel.setRect(1, curY, 40, 15);
	typeChoice.setRect(41, curY, 100, 15);
	curY += 16;
	height -= 16;

	optionsY = curY;
	optionsH = ((height - 1) > 100)?100:(height - 1);
	if(options != null){
	    options.setRect(1, curY, 150, optionsH);
	}

    }

    public void close()
    {
	Debug.println("Got close in document");
	quest.questionType = typeChoice.getSelectedIndex();
	if(quest.questionType == quest.MULTIPLE_CHOICE || 
	   options != null){	    
	    quest.setOptions((LObjDictionary)options.getLabObject());
	    options.close();
	}
	if(showName){
	    quest.dict.name = nameEdit.getText();
	}
	doc.close();
    }

}
