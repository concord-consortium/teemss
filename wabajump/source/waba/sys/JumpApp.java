package waba.sys;
import palmos.*;
import waba.ui.MainWindow;
import waba.ui.PenEvent;
import waba.ui.KeyEvent;
import waba.ui.IKeys;

public class JumpApp
{
  private static final int pageUpChr = 0x000B;
  private static final int pageDownChr = 0x000C;
  private static final int returnChr = 0x000D;
  private static final int escapeChr = 0x001B;
  private static final int backspaceChr = 0x0008;
  private static final int tabChr = 0x0009;
  private static final int menuChr = 0x0105;
  private static final int commandChr = 0x0106;
  private static final int leftArrowChr = 0x001C;
  private static final int rightArrowChr = 0x001D;
  private static final int upArrowChr = 0x001E;
  private static final int downArrowChr = 0x001F;
  private static final int keyboardAlphaChr = 0x0110;
  private static final int keyboardNumericChr = 0x0111;
  private static final int keyboardChr = 0x0109;
  private static final int hard1Chr = 0x0204;
  private static final int hard2Chr = 0x0205;
  private static final int hard3Chr = 0x0206;
  private static final int hard4Chr = 0x0207;
  private static final int calcChr = 0x010B;
  private static final int findChr = 0x010A;
  private static final int hardContrastChr = 0x020B;

  private MainWindow mainWindow;

  public JumpApp(MainWindow mainWindow)
  {
    this.mainWindow=mainWindow;
  }

/*
	private Graphics g;

  public Graphics getGraphics()
  {
		if (g==null)
		  g=new Graphics(Palm.WinGetDisplayWindow());
		else
		  g.reset();
		return g;
	}
*/
  public void handleMainWinEvent(Event e)
  {
    switch (e.eType)
    {
      case Event.penUpEvent:
      case Event.penDownEvent:
      case Event.penMoveEvent:
        {
          int type,x,y;

          if (e.eType == Event.penDownEvent)
            type = 200; // PenEvent.PEN_DOWN
          else if (e.eType == Event.penUpEvent)
            type = 202; // PenEvent.PEN_UP
          else
            type = 201; // PenEvent.PEN_MOVE
          x = e.screenX;
          y = e.screenY;
          mainWindow._postEvent(type,0,x,y,0,0);
          break;
        }
    case Event.keyDownEvent:
        {
          int type = 100; // KeyEvent.KEY_PRESS
          short chr = e.data1;
          int key = 0;
          switch (chr)
          {
            // NOTE: these should go somewhere:
            //    nextFieldChr
            //    prevFieldChr
            //    linefeedChr
            case pageUpChr:    key = 75000; break; // PAGE_UP
            case pageDownChr:  key = 75001; break; // PAGE_DOWN
    //        case :             key = 75002; break; // HOME
    //        case :             key = 75003; break; // END
            case upArrowChr:   key = 75004; break; // UP
            case downArrowChr: key = 75005; break; // DOWN
            case leftArrowChr: key = 75006; break; // LEFT
            case rightArrowChr:key = 75007; break; // RIGHT
    //        case :             key = 75008; break; // INSERT
            case returnChr:    key = 75009; break; // ENTER
            case tabChr:       key = 75010; break; // TAB
            case backspaceChr: key = 75011; break; // BACKSPACE
            case escapeChr:    key = 75012; break; // ESCAPE
    //        case :             key = 75013; break; // DELETE
            case menuChr:      key = 75014; break; // MENU
            case commandChr:   key = 75015; break; // COMMAND
            //the following keycodes borrowed from SuperWaba
            case keyboardAlphaChr: key = 76000; break;
            case keyboardNumericChr: key = 76001; break;
            case keyboardChr: key = 76002; break;
            case hard1Chr: key = 76003; break;
            case hard2Chr: key = 76004; break;
            case hard3Chr: key = 76005; break;
            case hard4Chr: key = 76006; break;
            case calcChr: key = 76007; break;
            case findChr: key = 76008; break;
            case hardContrastChr: key = 76009; break; // guich@121: take care of the contrast button bug
          }
          if (key==0)
          {
            if (chr > 255)
              break;
            key = chr;
          }

          mainWindow._postEvent(type,key,0,0,0,0);
          break;
        }
    }
  }

  public void timerCheck()
  {
    int now = Vm.getTimeStamp();
    int diff = now - mainWindow._timerStart;
    if (diff < 0)
      diff += (1L << 30); // max stamp is (1 << 30)
    if (diff < mainWindow._timerInterval)
      return;
    mainWindow._onTimerTick();
  }

  public int calcEventTimeout()
  {
    int timeout;

    if (mainWindow._timerInterval <= 0)
      timeout = -1;
    else
    {
      int now=Vm.getTimeStamp();
      int diff=now-mainWindow._timerStart;

      if (diff < 0)
        diff += (1L << 30); // max stamp is (1 << 30)
      timeout = mainWindow._timerInterval - diff;
      if (timeout <= 0)
        timeout = 0;
      timeout = Vm.millisToTicks(timeout);
      if (timeout <= 0)
        timeout = 1;
    }
    return timeout;
  }

  private void handleEvent(Event e)
  {
    int type=-1;
    switch(e.eType)
    {
      case Event.penDownEvent:
        type=PenEvent.PEN_DOWN;
        break;
      case Event.penMoveEvent:
        type=PenEvent.PEN_MOVE;
        break;
      case Event.penUpEvent:
        type=PenEvent.PEN_UP;
        break;
      case Event.keyDownEvent:
      case Event.menuEvent:
        type=KeyEvent.KEY_PRESS;
        break;
    }
    if (type!=-1)
    {
      mainWindow._postEvent(type, keyValue(e.data1,0), e.screenX, e.screenY, 0,0);
    }
  }

  public static int keyValue(int key, int mod)
  {
  switch (key)
    {
    case 8:
      key = IKeys.BACKSPACE;
      break;
    case 10:
      key = IKeys.ENTER;
      break;
    case 127:
      key = IKeys.DELETE;
      break;
    case menuChr:      
	key = 75014; 
	break; // MENU
    }
  return key;
  }

  public void start()
  {
    mainWindow.onStart();
    mainWindow._doPaint(0,0,160,160);
    Event e = new Event();
    ShortHolder err = new ShortHolder((short) 0);
    while (e.eType != e.appStopEvent) {
      Palm.EvtGetEvent(e, calcEventTimeout());

      //stop keyboard event
      //to prevent a fatal exception
      boolean flgDoSysHandleEvent=true;
      if(e.eType==Event.keyDownEvent){
         int iChr=e.data1;
         if(iChr>=keyboardChr && iChr<=keyboardNumericChr){
            flgDoSysHandleEvent=false;
         }
      }

      if(flgDoSysHandleEvent){
         if (Palm.SysHandleEvent(e))
           continue;
         if (Palm.MenuHandleEvent(0, e, err))
           continue;
      }
      handleEvent(e);
      timerCheck();
    }
    mainWindow.onExit();
    if (err.value==0)
      return;
  }
}
