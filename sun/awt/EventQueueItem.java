package sun.awt;

import java.awt.AWTEvent;

public class EventQueueItem {
  public AWTEvent event;
  
  public EventQueueItem next;
  
  public EventQueueItem(AWTEvent paramAWTEvent) { this.event = paramAWTEvent; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\EventQueueItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */