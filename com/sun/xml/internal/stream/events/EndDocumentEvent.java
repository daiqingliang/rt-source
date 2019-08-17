package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.EndDocument;

public class EndDocumentEvent extends DummyEvent implements EndDocument {
  public EndDocumentEvent() { init(); }
  
  protected void init() { setEventType(8); }
  
  public String toString() { return "ENDDOCUMENT"; }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter) throws IOException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\EndDocumentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */