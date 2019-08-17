package org.w3c.dom.ls;

import org.w3c.dom.events.Event;

public interface LSProgressEvent extends Event {
  LSInput getInput();
  
  int getPosition();
  
  int getTotalSize();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\ls\LSProgressEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */