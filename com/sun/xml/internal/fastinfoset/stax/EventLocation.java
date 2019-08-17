package com.sun.xml.internal.fastinfoset.stax;

import javax.xml.stream.Location;

public class EventLocation implements Location {
  String _systemId = null;
  
  String _publicId = null;
  
  int _column = -1;
  
  int _line = -1;
  
  int _charOffset = -1;
  
  public static Location getNilLocation() { return new EventLocation(); }
  
  public int getLineNumber() { return this._line; }
  
  public int getColumnNumber() { return this._column; }
  
  public int getCharacterOffset() { return this._charOffset; }
  
  public String getPublicId() { return this._publicId; }
  
  public String getSystemId() { return this._systemId; }
  
  public void setLineNumber(int paramInt) { this._line = paramInt; }
  
  public void setColumnNumber(int paramInt) { this._column = paramInt; }
  
  public void setCharacterOffset(int paramInt) { this._charOffset = paramInt; }
  
  public void setPublicId(String paramString) { this._publicId = paramString; }
  
  public void setSystemId(String paramString) { this._systemId = paramString; }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Line number = " + this._line);
    stringBuffer.append("\n");
    stringBuffer.append("Column number = " + this._column);
    stringBuffer.append("\n");
    stringBuffer.append("System Id = " + this._systemId);
    stringBuffer.append("\n");
    stringBuffer.append("Public Id = " + this._publicId);
    stringBuffer.append("\n");
    stringBuffer.append("CharacterOffset = " + this._charOffset);
    stringBuffer.append("\n");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\EventLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */