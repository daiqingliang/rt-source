package com.sun.xml.internal.stream.events;

import javax.xml.stream.Location;

public class LocationImpl implements Location {
  String systemId;
  
  String publicId;
  
  int colNo;
  
  int lineNo;
  
  int charOffset;
  
  LocationImpl(Location paramLocation) {
    this.systemId = paramLocation.getSystemId();
    this.publicId = paramLocation.getPublicId();
    this.lineNo = paramLocation.getLineNumber();
    this.colNo = paramLocation.getColumnNumber();
    this.charOffset = paramLocation.getCharacterOffset();
  }
  
  public int getCharacterOffset() { return this.charOffset; }
  
  public int getColumnNumber() { return this.colNo; }
  
  public int getLineNumber() { return this.lineNo; }
  
  public String getPublicId() { return this.publicId; }
  
  public String getSystemId() { return this.systemId; }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Line number = " + getLineNumber());
    stringBuffer.append("\n");
    stringBuffer.append("Column number = " + getColumnNumber());
    stringBuffer.append("\n");
    stringBuffer.append("System Id = " + getSystemId());
    stringBuffer.append("\n");
    stringBuffer.append("Public Id = " + getPublicId());
    stringBuffer.append("\n");
    stringBuffer.append("CharacterOffset = " + getCharacterOffset());
    stringBuffer.append("\n");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\LocationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */