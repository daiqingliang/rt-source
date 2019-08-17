package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.EndDocument;

public class EndDocumentEvent extends EventBase implements EndDocument {
  public EndDocumentEvent() { super(8); }
  
  public String toString() { return "<? EndDocument ?>"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\EndDocumentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */