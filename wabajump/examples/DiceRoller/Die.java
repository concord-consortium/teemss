import waba.ui.*;
import waba.fx.*;

// This file was written by Peter Carroll <kedge@se77en.com>

public class Die extends Control{
   private int iValue=1;

   Die()
   {
   }

   public void roll(Random oRand)
   {
      iValue=oRand.getRandom(6) + 1;
   }

   public void onPaint(Graphics g)
   {
	  g.clearClip();
	  g.setColor(255,255,255);
	  g.fillRect(this.x, this.y, this.width, this.height);

	  Image oDieImage=new Image(100+iValue, 20, 20);
	  g.drawImage(oDieImage, this.x, this.y);
   }
}