import waba.ui.*;
import waba.fx.*;
import waba.sys.*;

//This file was written by Peter Carroll <kedge@se77en.com>

public class DiceRoller extends MainWindow implements IDiceRollerUI
{
   private DiceRollerUI oUI=new DiceRollerUI(this.height, this.width);
   Random oRand=new Random();
   private int iNumDice=6;
   private Die oDice[]=new Die[iNumDice];

   public DiceRoller()
   {
      Title oTitle=new Title("DiceRoller");
  	   oTitle.setRect(0, 0, this.width, 15);
	   add(oTitle);
      oUI.addTo(this);
      for(int i=0;i<iNumDice;i++){
		 oDice[i]=new Die();
		 oDice[i].roll(oRand);
         oDice[i].setRect((22*i)+2,40,20,20);
         add(oDice[i]);
      }
   }

/*   public static int PilotMain(int cmd, int cmdBPB, int launchFlags)
   {
      if (cmd != 0)
         return 0;
      (new JumpApp(new DiceRoller())).start();
      return 0;
   }*/

   public void onEvent(Event event)
   {
      oUI.handleEvent(event, this);
   }

   public void onRoll()
   {
      //Roll Dice Button clicked
      for(int i=0;i<iNumDice;i++){
		 oDice[i].roll(oRand);
		 oDice[i].repaint();
      }
   }

   public void onNumDiceEdit()
   {
      //Number of Dice changed
      for(int i=0; i<iNumDice; i++){
		 remove(oDice[i]);
      }

      Graphics oGraphics=this.createGraphics();
      oGraphics.setClip(0,30,20,160);
      oGraphics.clearClip();
      oGraphics.setColor(255,255,255);
      oGraphics.fillRect(0,30,20,160);
      this.repaint();

      iNumDice=Convert.toInt(oUI.txtNumDice.getText());
      if(iNumDice<1) iNumDice=1;
      if(iNumDice>6) iNumDice=6;

	  oUI.txtNumDice.setText(String.valueOf(iNumDice));

      oDice=new Die[iNumDice];

   	  for(int i=0;i<iNumDice;i++){
		 oDice[i]=new Die();
		 oDice[i].roll(oRand);
		 oDice[i].setRect((22*i)+2,40,20,20);
		 add(oDice[i]);
      }
   }
}
