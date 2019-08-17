package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.ProcessingInstruction;

public class ProcessingInstructionEvent extends EventBase implements ProcessingInstruction {
  private String targetName;
  
  private String _data;
  
  public ProcessingInstructionEvent() { init(); }
  
  public ProcessingInstructionEvent(String paramString1, String paramString2) {
    this.targetName = paramString1;
    this._data = paramString2;
    init();
  }
  
  protected void init() { setEventType(3); }
  
  public String getTarget() { return this.targetName; }
  
  public void setTarget(String paramString) { this.targetName = paramString; }
  
  public void setData(String paramString) { this._data = paramString; }
  
  public String getData() { return this._data; }
  
  public String toString() { return (this._data != null && this.targetName != null) ? ("<?" + this.targetName + " " + this._data + "?>") : ((this.targetName != null) ? ("<?" + this.targetName + "?>") : ((this._data != null) ? ("<?" + this._data + "?>") : "<??>")); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\ProcessingInstructionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */