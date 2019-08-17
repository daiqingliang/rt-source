package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.Comment;

public class CommentEvent extends DummyEvent implements Comment {
  private String fText;
  
  public CommentEvent() { init(); }
  
  public CommentEvent(String paramString) {
    init();
    this.fText = paramString;
  }
  
  protected void init() { setEventType(5); }
  
  public String toString() { return "<!--" + getText() + "-->"; }
  
  public String getText() { return this.fText; }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter) throws IOException { paramWriter.write("<!--" + getText() + "-->"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\CommentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */