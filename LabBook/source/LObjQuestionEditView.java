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
		    options.setRect(1, (height - 16)/2 + 40, 140, 100);		    
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
	super.setRect(x,y,width,height);
	if(!didLayout) layout(false);

	doc.setRect(1, 16, width - 200, (height - 16)/2);

	typeLabel.setRect(1, (height - 16)/2 + 20, 40, 15);
	typeChoice.setRect(41, (height - 16)/2 + 20, 100, 15);

	if(options != null){
	    options.setRect(1, (height - 16)/2 + 40, 140, 100);
	}

	if(showDone){
	    doneButton.setRect(width-30,height-15,30,15);
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
