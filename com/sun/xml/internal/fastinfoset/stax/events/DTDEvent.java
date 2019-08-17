package com.sun.xml.internal.fastinfoset.stax.events;

import java.util.List;
import javax.xml.stream.events.DTD;

public class DTDEvent extends EventBase implements DTD {
  private String _dtd;
  
  private List _notations;
  
  private List _entities;
  
  public DTDEvent() { setEventType(11); }
  
  public DTDEvent(String paramString) {
    setEventType(11);
    this._dtd = paramString;
  }
  
  public String getDocumentTypeDeclaration() { return this._dtd; }
  
  public void setDTD(String paramString) { this._dtd = paramString; }
  
  public List getEntities() { return this._entities; }
  
  public List getNotations() { return this._notations; }
  
  public Object getProcessedDTD() { return null; }
  
  public void setEntities(List paramList) { this._entities = paramList; }
  
  public void setNotations(List paramList) { this._notations = paramList; }
  
  public String toString() { return this._dtd; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\DTDEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */