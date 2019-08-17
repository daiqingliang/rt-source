package org.w3c.dom.ls;

import org.w3c.dom.Document;
import org.w3c.dom.events.Event;

public interface LSLoadEvent extends Event {
  Document getNewDocument();
  
  LSInput getInput();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\ls\LSLoadEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */