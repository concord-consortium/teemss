/*



Scribble.java



Copyright (c) 1998, 1999 Wabasoft 



Wabasoft grants you a non-exclusive license to use, modify and re-distribute

this program provided that this copyright notice and license appear on all

copies of the software.



Software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR

IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF

MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE

HEREBY EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE IS ASSUMED

BY THE LICENSEE. 



WABASOFT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY

LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING SOFTWARE.

IN NO EVENT WILL WABASOFT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE,

PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL

OR PUNITIVE DAMAGES, HOWEVER CAUSED AN REGARDLESS OF THE THEORY OF LIABILITY,

ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF WABASOFT HAS

BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 



*/



import waba.ui.*;

import waba.fx.*;

import extra.ui.*;

import org.concord.waba.extra.ui.Menu;

import org.concord.waba.extra.ui.MenuBar;

import org.concord.waba.extra.ui.ExtraMainWindow;

import org.concord.waba.extra.ui.Dialog;

import org.concord.waba.extra.ui.PopupMenu;

import java.awt.event.ActionEvent;

import extra.util.*;





/**

 * A program that lets a user scribble.

 */



public class mytestJava extends ExtraMainWindow implements org.concord.waba.extra.event.DialogListener

{

DrawArea drawArea;

Button 	closeButton, clearButton;

Menu	myEMenu;

Menu	myEMenu2;





List	myList;

PopupMenu pMenu;

public mytestJava()

	{

//		MyTitle title = new MyTitle("mytest");

//		title.setRect(0, 0, this.width/2, 15);

	//	add(title);

	//	myMenu = new WMenu(items);

	//	myMenu.setRect(this.width/2, 0, this.width/4, 15);

	//	add(myMenu);



		

		drawArea = new DrawArea();

		drawArea.setRect(0, 20, this.width, this.height - 40);

		add(drawArea);



		MenuBar menubar = new MenuBar();

		myEMenu = new Menu("w1");

		myEMenu.add("Clear");

		myEMenu.add("3");

		myEMenu.addActionListener(this);

		menubar.add(myEMenu);

		myEMenu2 = new Menu("namenamename");

		myEMenu2.add("Clear");

		myEMenu2.add("2");

		myEMenu2.add("3");

		myEMenu2.addActionListener(this);

		menubar.add(myEMenu2);

		setMenuBar(menubar);

		

		pMenu = new PopupMenu("popup");

		pMenu.add("pop1");

		pMenu.add("pop2");

		pMenu.add("pop3");

		add(pMenu); 

		pMenu.addActionListener(this);

		

		String []listStrings = {"list1","list2","list3"};

		myList = new List(listStrings); 

		myList.setRect(0, 0, this.width/2, 15);

		add(myList);

//		String ImageName = "dima";

//		java.io.InputStream stream = getClass().getResourceAsStream("icons/windows/"+ImageName+".bmp");

//		System.out.println("BEGIN  "+stream);



		

	

	}

	

	

    public void dialogClosed(org.concord.waba.extra.event.DialogEvent e){

    	System.out.println("Command "+e.getActionCommand()+" InfoType "+e.getInfoType()+" Info "+e.getInfo());

    	if((e.getInfoType() == org.concord.waba.extra.event.DialogEvent.PROPERTIES) && (e.getInfo() instanceof PropContainer)){

    		System.out.println("PROPERTIES");

    		PropContainer pc = (PropContainer)e.getInfo();

    		int nContainers = pc.getNumbPropContainers();

		for(int i = 0; i < nContainers; i++){

    			System.out.println("Name "+pc.getPropertiesContainerName(i));

			waba.util.Vector prop = pc.getProperties(i);

			if(prop == null) continue;

			int nProperties = prop.getCount();

			for(int j = 0; j < nProperties; j++){

				PropObject po = (PropObject)prop.get(j);

    				System.out.println(po.getName()+" = "+po.getValue());

			}

			System.out.println();

		}

    	}

    }

    

    

    public void actionPerformed(java.awt.event.ActionEvent e){

		if(e.getSource() == myEMenu2 || e.getSource() == myEMenu){

			if(e.getSource() == myEMenu){

				System.out.println("myEMenu");

			}else if(e.getSource() == myEMenu2){

				System.out.println("myEMenu2");

			}

			if(e.getActionCommand().equals("Clear")){

				drawArea.clear();

				_doPaint(x,y,width,height);//we need to do it for awt

			}

		}

		if(e.getSource() == pMenu){

			System.out.println(e.getActionCommand());

		}

    }



// Event.java

    public void onEvent(waba.ui.Event event)

   {	

	if (event.type == PenEvent.PEN_DOWN){

		try{

			PenEvent penEvent = (PenEvent)event;

			System.out.println("x "+penEvent.x+" y "+penEvent.y+" this.width "+this.width);

			if(((penEvent.y >= 0 && penEvent.y <= 20) || (penEvent.y > this.height - 40)) && (penEvent.x >= this.width/2 && penEvent.x <= this.width)){

//				pMenu.show(penEvent.x,penEvent.y);

				

//				Dialog.showMessageDialog(this,"Title Title Title Title","Message Message","Button",Dialog.WARN_DIALOG);

				String []confirmButtons = {"Yes","No","Cancel"};

//				Dialog.showConfirmDialog(this,"Title Title Title Title","Message Message",confirmButtons,Dialog.WARN_DIALOG);



				String []choices = {"item1","item2","item3"};

//				Dialog.showInputDialog(this,"Title Title Title Title","Message Message",confirmButtons,Dialog.CHOICE_INP_DIALOG,choices);

				Dialog.showInputDialog(this,"Title Title Title Title","Message Message",confirmButtons,Dialog.EDIT_INP_DIALOG);





//				showPropDialog();

				



			}

		}catch(Exception e){}

	}

	if (event.type == ControlEvent.PRESSED){

		if (event.target == closeButton){

			exit(0);

		}else if (event.target == clearButton){

			drawArea.clear();

		}else if (event.target == myList){

			System.out.println(myList.getSelected() + " index " + myList.getSelectedIndex());

		}

	}

}



	public void showPropDialog(){

		PropContainer pc = new PropContainer();

		pc.createSubContainer("main");

		pc.createSubContainer("test");

		String ch1[] = {"mc1","mc2","mc3","mc4"};

		String ch2[] = {"tc1","tc2","tc3"};

		pc.addProperty(new PropObject("mp1","mv1"),"main");

		pc.addProperty(new PropObject("mp2",ch1),"main");

		pc.addProperty(new PropObject("tp1","tv1"),"test");

		pc.addProperty(new PropObject("tp2","tv2"),"test");

		pc.addProperty(new PropObject("tp3",ch2),"test");

		org.concord.waba.extra.ui.PropertyDialog d = new org.concord.waba.extra.ui.PropertyDialog(this,this,"Properties",pc);

		d.setRect(50,50,160,160);

		d.setContent();

		d.show();

		System.out.println("showPropDialog");

	}





}





