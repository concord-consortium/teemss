/* This is an addition to WabaJump that does not exist in Waba yet.
   It is the almighty, InfraRed class!!!
   Now I just hope it works!

   Peter Carroll <kedge@se77en.com>

*/

package waba.io;

import palmos.*;

public class InfraRed extends Stream{

//Commented out for the time being.

   private int serCtlIrDAEnable=9;
   private int serCtlIrDADisable=10;
   private SerialPort oSerial;

   //I've sources that say use 9600, I've seen some that say 19200
   //I wish I had two Palms to test this with.
   public InfraRed(int iBaudRate){
      oSerial=new SerialPort(0, iBaudRate);
/*      int iRefNum=oSerial.getRefNum();
      if(iRefNum>-1){
         Palm.SerControl(iRefNum, serCtlIrDAEnable,null,(ShortHolder)null);
      }*/
   }

   public boolean close()
   {
/*      int iRefNum=oSerial.getRefNum();
      if(iRefNum>-1){
         Palm.SerControl(iRefNum, serCtlIrDADisable,null,(ShortHolder)null);
      }                      */
      return oSerial.close();
   }

   public boolean isOpen()
   {
      return oSerial.isOpen();
   }

   public boolean setFlowControl(boolean on)
   {
      return oSerial.setFlowControl(on);
   }

   public boolean setReadTimeout(int millis)
   {
      return oSerial.setReadTimeout(millis);
   }

   public int readBytes(byte buf[], int start, int count)
   {
      return oSerial.readBytes(buf, start, count);
   }

   public int writeBytes(byte buf[], int start, int count)
   {
      return oSerial.writeBytes(buf,start,count);
   }

   public int readCheck()
   {
      return oSerial.readCheck();
   }
}
