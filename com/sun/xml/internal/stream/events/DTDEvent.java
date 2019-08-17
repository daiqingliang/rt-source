package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import javax.xml.stream.events.DTD;

public class DTDEvent extends DummyEvent implements DTD {
  private String fDoctypeDeclaration;
  
  private List fNotations;
  
  private List fEntities;
  
  public DTDEvent() { init(); }
  
  public DTDEvent(String paramString) {
    init();
    this.fDoctypeDeclaration = paramString;
  }
  
  public void setDocumentTypeDeclaration(String paramString) { this.fDoctypeDeclaration = paramString; }
  
  public String getDocumentTypeDeclaration() { return this.fDoctypeDeclaration; }
  
  public void setEntities(List paramList) { this.fEntities = paramList; }
  
  public List getEntities() { return this.fEntities; }
  
  public void setNotations(List paramList) { this.fNotations = paramList; }
  
  public List getNotations() { return this.fNotations; }
  
  public Object getProcessedDTD() { return null; }
  
  protected void init() { setEventType(11); }
  
  public String toString() { return this.fDoctypeDeclaration; }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter) throws IOException { paramWriter.write(this.fDoctypeDeclaration); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\DTDEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */