package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.ProcessingInstruction;

public class ProcessingInstructionEvent extends DummyEvent implements ProcessingInstruction {
  private String fName;
  
  private String fContent;
  
  public ProcessingInstructionEvent() { init(); }
  
  public ProcessingInstructionEvent(String paramString1, String paramString2) { this(paramString1, paramString2, null); }
  
  public ProcessingInstructionEvent(String paramString1, String paramString2, Location paramLocation) {
    init();
    this.fName = paramString1;
    this.fContent = paramString2;
    setLocation(paramLocation);
  }
  
  protected void init() { setEventType(3); }
  
  public String getTarget() { return this.fName; }
  
  public void setTarget(String paramString) { this.fName = paramString; }
  
  public void setData(String paramString) { this.fContent = paramString; }
  
  public String getData() { return this.fContent; }
  
  public String toString() { return (this.fContent != null && this.fName != null) ? ("<?" + this.fName + " " + this.fContent + "?>") : ((this.fName != null) ? ("<?" + this.fName + "?>") : ((this.fContent != null) ? ("<?" + this.fContent + "?>") : "<??>")); }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter) throws IOException { paramWriter.write(toString()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\ProcessingInstructionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */