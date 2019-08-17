package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.StartDocument;

public class StartDocumentEvent extends DummyEvent implements StartDocument {
  protected String fSystemId;
  
  protected String fEncodingScheam;
  
  protected boolean fStandalone;
  
  protected String fVersion;
  
  private boolean fEncodingSchemeSet = false;
  
  private boolean fStandaloneSet = false;
  
  private boolean nestedCall = false;
  
  public StartDocumentEvent() { init("UTF-8", "1.0", true, null); }
  
  public StartDocumentEvent(String paramString) { init(paramString, "1.0", true, null); }
  
  public StartDocumentEvent(String paramString1, String paramString2) { init(paramString1, paramString2, true, null); }
  
  public StartDocumentEvent(String paramString1, String paramString2, boolean paramBoolean) {
    this.fStandaloneSet = true;
    init(paramString1, paramString2, paramBoolean, null);
  }
  
  public StartDocumentEvent(String paramString1, String paramString2, boolean paramBoolean, Location paramLocation) {
    this.fStandaloneSet = true;
    init(paramString1, paramString2, paramBoolean, paramLocation);
  }
  
  protected void init(String paramString1, String paramString2, boolean paramBoolean, Location paramLocation) {
    setEventType(7);
    this.fEncodingScheam = paramString1;
    this.fVersion = paramString2;
    this.fStandalone = paramBoolean;
    if (paramString1 != null && !paramString1.equals("")) {
      this.fEncodingSchemeSet = true;
    } else {
      this.fEncodingSchemeSet = false;
      this.fEncodingScheam = "UTF-8";
    } 
    this.fLocation = paramLocation;
  }
  
  public String getSystemId() { return (this.fLocation == null) ? "" : this.fLocation.getSystemId(); }
  
  public String getCharacterEncodingScheme() { return this.fEncodingScheam; }
  
  public boolean isStandalone() { return this.fStandalone; }
  
  public String getVersion() { return this.fVersion; }
  
  public void setStandalone(boolean paramBoolean) {
    this.fStandaloneSet = true;
    this.fStandalone = paramBoolean;
  }
  
  public void setStandalone(String paramString) {
    this.fStandaloneSet = true;
    if (paramString == null) {
      this.fStandalone = true;
      return;
    } 
    if (paramString.equals("yes")) {
      this.fStandalone = true;
    } else {
      this.fStandalone = false;
    } 
  }
  
  public boolean encodingSet() { return this.fEncodingSchemeSet; }
  
  public boolean standaloneSet() { return this.fStandaloneSet; }
  
  public void setEncoding(String paramString) { this.fEncodingScheam = paramString; }
  
  void setDeclaredEncoding(boolean paramBoolean) { this.fEncodingSchemeSet = paramBoolean; }
  
  public void setVersion(String paramString) { this.fVersion = paramString; }
  
  void clear() {
    this.fEncodingScheam = "UTF-8";
    this.fStandalone = true;
    this.fVersion = "1.0";
    this.fEncodingSchemeSet = false;
    this.fStandaloneSet = false;
  }
  
  public String toString() {
    String str = "<?xml version=\"" + this.fVersion + "\"";
    str = str + " encoding='" + this.fEncodingScheam + "'";
    if (this.fStandaloneSet) {
      if (this.fStandalone) {
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
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter) throws IOException { paramWriter.write(toString()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\StartDocumentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */