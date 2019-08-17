package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.org.apache.xerces.util.XMLChar;
import javax.xml.stream.events.Characters;

public class CharactersEvent extends EventBase implements Characters {
  private String _text;
  
  private boolean isCData = false;
  
  private boolean isSpace = false;
  
  private boolean isIgnorable = false;
  
  private boolean needtoCheck = true;
  
  public CharactersEvent() { super(4); }
  
  public CharactersEvent(String paramString) {
    super(4);
    this._text = paramString;
  }
  
  public CharactersEvent(String paramString, boolean paramBoolean) {
    super(4);
    this._text = paramString;
    this.isCData = paramBoolean;
  }
  
  public String getData() { return this._text; }
  
  public void setData(String paramString) { this._text = paramString; }
  
  public boolean isCData() { return this.isCData; }
  
  public String toString() { return this.isCData ? ("<![CDATA[" + this._text + "]]>") : this._text; }
  
  public boolean isIgnorableWhiteSpace() { return this.isIgnorable; }
  
  public boolean isWhiteSpace() {
    if (this.needtoCheck) {
      checkWhiteSpace();
      this.needtoCheck = false;
    } 
    return this.isSpace;
  }
  
  public void setSpace(boolean paramBoolean) {
    this.isSpace = paramBoolean;
    this.needtoCheck = false;
  }
  
  public void setIgnorable(boolean paramBoolean) {
    this.isIgnorable = paramBoolean;
    setEventType(6);
  }
  
  private void checkWhiteSpace() {
    if (!Util.isEmptyString(this._text)) {
      this.isSpace = true;
      for (byte b = 0; b < this._text.length(); b++) {
        if (!XMLChar.isSpace(this._text.charAt(b))) {
          this.isSpace = false;
          break;
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\CharactersEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */