package com.sun.xml.internal.stream.events;

import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.Characters;

public class CharacterEvent extends DummyEvent implements Characters {
  private String fData;
  
  private boolean fIsCData;
  
  private boolean fIsIgnorableWhitespace;
  
  private boolean fIsSpace = false;
  
  private boolean fCheckIfSpaceNeeded = true;
  
  public CharacterEvent() {
    this.fIsCData = false;
    init();
  }
  
  public CharacterEvent(String paramString) {
    this.fIsCData = false;
    init();
    this.fData = paramString;
  }
  
  public CharacterEvent(String paramString, boolean paramBoolean) {
    init();
    this.fData = paramString;
    this.fIsCData = paramBoolean;
  }
  
  public CharacterEvent(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
    init();
    this.fData = paramString;
    this.fIsCData = paramBoolean1;
    this.fIsIgnorableWhitespace = paramBoolean2;
  }
  
  protected void init() { setEventType(4); }
  
  public String getData() { return this.fData; }
  
  public void setData(String paramString) {
    this.fData = paramString;
    this.fCheckIfSpaceNeeded = true;
  }
  
  public boolean isCData() { return this.fIsCData; }
  
  public String toString() { return this.fIsCData ? ("<![CDATA[" + getData() + "]]>") : this.fData; }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter) throws IOException {
    if (this.fIsCData) {
      paramWriter.write("<![CDATA[" + getData() + "]]>");
    } else {
      charEncode(paramWriter, this.fData);
    } 
  }
  
  public boolean isIgnorableWhiteSpace() { return this.fIsIgnorableWhitespace; }
  
  public boolean isWhiteSpace() {
    if (this.fCheckIfSpaceNeeded) {
      checkWhiteSpace();
      this.fCheckIfSpaceNeeded = false;
    } 
    return this.fIsSpace;
  }
  
  private void checkWhiteSpace() {
    if (this.fData != null && this.fData.length() > 0) {
      this.fIsSpace = true;
      for (byte b = 0; b < this.fData.length(); b++) {
        if (!XMLChar.isSpace(this.fData.charAt(b))) {
          this.fIsSpace = false;
          break;
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\CharacterEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */