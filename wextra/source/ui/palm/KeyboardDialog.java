package org.concord.waba.extra.ui;

import waba.ui.*;
import waba.fx.*;
import waba.sys.*;

import org.concord.waba.extra.io.*;

/** a popup keyboard to be used with the edit class. guich@102 */

public final class KeyboardDialog extends Dialog
{
	private Edit targetEdit = null;
	private TextControl targetTC = null;
	private KBEdit destEdit;
	public boolean isActive;
	private PushButtonGroup pbs[] = new PushButtonGroup[6];
	private KeyEvent ke = new KeyEvent();
	private int inside=-1;

	private boolean isCaps, isShift;
	private final int NUMERIC_PAD = 0;
	private final int SYMBOLS_PAD = 1;
	private final int ACCENT_PAD = 2; 
	private final int TEXT_PAD = 3;
	private final int CAPS_PAD = 4;
	private final int SPECIAL_PAD = 5;
	/** when the user press the edit's abc keyboard, this windows is poped up. then, it adds the edit 
		so its new parent is this. since the event (abc_keypressed) is propagating up, this class will receive 
		it, closing the just opened keyboard. so this flag makes this window ignore that event. */
	private boolean ignoreNextEvent; 

	ExtraMainWindow extMW = null;

	private String names[][] = 
	{
		/* in the future, put all in one string; it consumes less 1k...
		   {"123456789.0,"},
		   {"+$&()-#@[]*%|{}/=\\<>"},
		   {"багавйкинуфхцъзс"}, 
		   {"qwertyuiop\"_asdfghjkl:~^zxcvbnm!?;\"`"},
		   {"Shift","Caps","          ","«",},
		*/
		{"1","2","3","4","5","6","7","8","9",".","0",","},
		{"+","$","&","(",")","-","#","@","[","]","*","%","|","{","}","/","=","\\","<",">"},
		{"б","а","г","а","в","й","к","и","н","у","ф","х","ц","ъ","з","с"}, 
		{"q","w","e","r","t","y","u","i","o","p","'","_","a","s","d","f","g","h","j","k","l",":","~","^","z","x","c","v","b","n","m","!","?",";","\"","`"},
		{"Caps","Shift"},
		{"                   ","«","Done"},
	};

	public KeyboardDialog()
	{
		super("Popup Keyboard");
		setRect(0,10,160,150);
		ke.type = KeyEvent.KEY_PRESS;
	}
	public KeyboardDialog(Edit targetEdit, ExtraMainWindow extMW)
	{
		this();
		this.targetEdit = targetEdit;
		this.extMW = extMW;
	}

	public KeyboardDialog(TextControl targetTC, ExtraMainWindow extMW)
	{
		this();
		this.targetTC = targetTC;
		if(targetTC != null){
			targetTC.saveLineIndex();
		}
		this.extMW = extMW;
	}
   
	public void setContent()
	{
		Container contentPane = getContentPane();
		if(contentPane == null) return;

		Rect cRect = contentPane.getRect();
		int cWidth = cRect.width;
		int cHeight = cRect.height;
			
		ignoreNextEvent = true;
		isCaps = false;      
		isShift = false;
      
		if (pbs[0] == null){
			int []rows = new int[]{4,4,4,3,1,1};
			int []x = new int[]{3 ,41,101,       3, 3,       51};
			int []w = new int[]{37,65,50,cWidth-4,48,cWidth-52};
			int []y = new int[]{32,32,32,75,112,112};
			int []h = new int[]{40,40,40,37, 13, 13};
			int []ig = new int[]{6,2,6,4,4,5};
			for (int i =0; i < pbs.length; i++) {
				contentPane.add(pbs[i] = new PushButtonGroup(names[i], false, -1, i!=SPECIAL_PAD?-1:2, ig[i], rows[i], i < 4, (i != 4) ? PushButtonGroup.BUTTON : PushButtonGroup.CHECK)); 
				pbs[i].setRect(x[i],y[i],w[i],h[i]);
				pbs[i].id = i;
			}      
		}

		destEdit = new KBEdit();
		destEdit.setRect(0,0,cWidth,10);      
		contentPane.add(destEdit);

		ke.target = destEdit;      
	}
   
	public void show()
	{
		super.show();
		if(targetEdit != null){
			destEdit.setText(targetEdit.getText());
		} else if(targetTC != null){
			destEdit.setText(targetTC.getLineAtSavedIndex());
		}
		destEdit.setFocus(true);
	}

	public void hide()
	{
		destEdit.setFocus(false);
		super.hide();
		if(targetEdit != null){
			targetEdit.setText(destEdit.getText());
		} else if(targetTC != null){
			targetTC.setLineAtSavedIndex(destEdit.getText());
		}
		if(extMW != null) extMW.kbDialogClosed();
	}
	
	public void setCharCase(boolean upper)
	{
		String text[] = names[TEXT_PAD];
		if(upper){
			for(int i=0; i<text.length; i++){
				text[i] = DataStream.toUpperCase(text[i]);
			}
		} else {
			for(int i=0; i<text.length; i++){
				text[i] = DataStream.toLowerCase(text[i]);
			}
		}
		pbs[TEXT_PAD].repaint();
	}

	public void onEvent(Event event)
	{
		if (event.type == ControlEvent.PRESSED && event.target instanceof PushButtonGroup){
			PushButtonGroup pb = (PushButtonGroup)event.target;
			if(pb.getSelected() == -1) return;
			String st = names[pb.id][pb.getSelected()];
			if (pb.id == SPECIAL_PAD || pb.id == CAPS_PAD){
				// special char?
				int key = -1;
				switch (st.charAt(0)){
				case 'D': pb.setSelected(-1); hide(); break;
				case '«': key = IKeys.BACKSPACE; break;
				case ' ': key = ' '; break;
				case 'S': 
					isShift = !isShift; 
					isCaps = false;
					setCharCase(isShift);
					break;
				case 'C': 
					isCaps =  !isCaps; 
					isShift = false; 
					setCharCase(isCaps);
					break;
				}


				if (key != -1) insertKey(key);
			} else {
				if (isShift || isCaps) st = DataStream.toUpperCase(st);
				char c = st.charAt(0);
				insertKey(c); 
				cancelShift();
			}
		}
	}
	private void cancelShift()
	{
		if (isShift){
			isShift = false;
			pbs[CAPS_PAD].setSelected(-1);
			setCharCase(false);
		}
	}




	/**
	 *  This requires a modified Edit control.  The standard 
	 *  waba control doens't like recieving key events when
	 * it doesn't have focus
	 */
	private void insertKey(int key)
	{
		ke.key = key;
		getWabaWindow().setFocus(destEdit);
		destEdit.postEvent(ke);
		//Vm.sleep(100);
	}
}
