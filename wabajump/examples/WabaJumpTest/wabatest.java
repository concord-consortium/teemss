import waba.ui.*;
import waba.fx.*;
import waba.sys.*;

public class wabatest extends MainWindow{
   Button cmdRepaint=new Button("Repaint");
   Button cmdExit=new Button("Exit");

   public wabatest()
   {
      cmdRepaint.setRect(20,135,40,20);
      this.add(cmdRepaint);
      cmdExit.setRect(100,135,40,20);
      this.add(cmdExit);
   }

/*   public static int PilotMain(int cmd, int cmdBPB, int launchFlags)
   {
      if (cmd != 0)
         return 0;
      (new JumpApp(new wabatest())).start();
      return 0;
   }*/

   public void onPaint(Graphics oGraphics)
   {
      oGraphics.setColor(0,0,0);
      oGraphics.drawText("OS: " + Vm.getPlatform(),5,5);
      oGraphics.drawText("Color: " + Vm.isColor(),5,20);
      FontMetrics oFM=this.getFontMetrics(this.defaultFont);
      oGraphics.drawText("Font Height: " + oFM.getHeight(),5,35);
      oGraphics.drawText("Font Width (W): " + oFM.getTextWidth("W"),5,50);
      oGraphics.drawText("Window Height: " + this.height,5,65);
      oGraphics.drawText("Window Width: " + this.width,5,80);
   }

   public void onEvent(Event oEvent)
   {
      if(oEvent.type==ControlEvent.PRESSED){
         if(oEvent.target==cmdExit){
            this.exit(0);
         }
         else if(oEvent.target==cmdRepaint){
            this.repaint();
         }
      }
   }
}