package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.StartDocument;

public class StartDocumentEvent extends EventBase implements StartDocument {
  protected String _systemId;
  
  protected String _encoding = "UTF-8";
  
  protected boolean _standalone = true;
  
  protected String _version = "1.0";
  
  private boolean _encodingSet = false;
  
  private boolean _standaloneSet = false;
  
  public void reset() {
    this._encoding = "UTF-8";
    this._standalone = true;
    this._version = "1.0";
    this._encodingSet = false;
    this._standaloneSet = false;
  }
  
  public StartDocumentEvent() { this(null, null); }
  
  public StartDocumentEvent(String paramString) { this(paramString, null); }
  
  public StartDocumentEvent(String paramString1, String paramString2) {
    if (paramString1 != null) {
      this._encoding = paramString1;
      this._encodingSet = true;
    } 
    if (paramString2 != null)
      this._version = paramString2; 
    setEventType(7);
  }
  
  public String getSystemId() { return super.getSystemId(); }
  
  public String getCharacterEncodingScheme() { return this._encoding; }
  
  public boolean encodingSet() { return this._encodingSet; }
  
  public boolean isStandalone() { return this._standalone; }
  
  public boolean standaloneSet() { return this._standaloneSet; }
  
  public String getVersion() { return this._version; }
  
  public void setStandalone(boolean paramBoolean) {
    this._standaloneSet = true;
    this._standalone = paramBoolean;
  }
  
  public void setStandalone(String paramString) {
    this._standaloneSet = true;
    if (paramString == null) {
      this._standalone = true;
      return;
    } 
    if (paramString.equals("yes")) {
      this._standalone = true;
    } else {
      this._standalone = false;
    } 
  }
  
  public void setEncoding(String paramString) {
    this._encoding = paramString;
    this._encodingSet = true;
  }
  
  void setDeclaredEncoding(boolean paramBoolean) { this._encodingSet = paramBoolean; }
  
  public void setVersion(String paramString) { this._version = paramString; }
  
  void clear() {
    this._encoding = "UTF-8";
    this._standalone = true;
    this._version = "1.0";
    this._encodingSet = false;
    this._standaloneSet = false;
  }
  
  public String toString() {
    String str = "<?xml version=\"" + this._version + "\"";
    str = str + " encoding='" + this._encoding + "'";
    if (this._standaloneSet) {
      if (this._standalone) {
        str = str + " standalone='yes'?>";
      } else {
        str = str + " standalone='no'?>";
      } 
    } else {
      str = str + "?>";
    } 
    return str;
  }
  
  public boolean isStartDocument() { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\StartDocumentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */