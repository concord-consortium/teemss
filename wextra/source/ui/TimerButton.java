package org.concord.waba.extra.ui;
import waba.ui.*;
import waba.fx.*;

public class TimerButton extends Button{
	Timer timer;
	boolean	firstPress = true;
	int		counter = 0;
	public TimerButton(String text){
		super(text);
	}
	public void onEvent(Event event){
		super.onEvent(event);
		if (event.type == PenEvent.PEN_DOWN){
			if(timer == null) timer = addTimer(2000);

		}else if (event.type == PenEvent.PEN_UP){
			if(timer != null) {
				removeTimer(timer);
				timer = null;
				firstPress = true;
				counter = 0;
			}						
		}
		if(event instanceof ControlEvent && event.type == ControlEvent.TIMER){
			if(timer == null) return;
			if(firstPress){
				removeTimer(timer);
				timer = addTimer(1000);
				firstPress = false;
			}else{
				if(counter < 8){
					counter++;
				}else if(counter == 8){
					removeTimer(timer);
					timer = addTimer(500);
				}
			}
		}
	}

}
