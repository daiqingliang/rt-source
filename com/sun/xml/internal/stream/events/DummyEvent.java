package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public abstract class DummyEvent implements XMLEvent {
  private static DummyLocation nowhere = new DummyLocation();
  
  private int fEventType;
  
  protected Location fLocation = nowhere;
  
  public DummyEvent() {}
  
  public DummyEvent(int paramInt) { this.fEventType = paramInt; }
  
  public int getEventType() { return this.fEventType; }
  
  protected void setEventType(int paramInt) { this.fEventType = paramInt; }
  
  public boolean isStartElement() { return (this.fEventType == 1); }
  
  public boolean isEndElement() { return (this.fEventType == 2); }
  
  public boolean isEntityReference() { return (this.fEventType == 9); }
  
  public boolean isProcessingInstruction() { return (this.fEventType == 3); }
  
  public boolean isCharacterData() { return (this.fEventType == 4); }
  
  public boolean isStartDocument() { return (this.fEventType == 7); }
  
  public boolean isEndDocument() { return (this.fEventType == 8); }
  
  public Location getLocation() { return this.fLocation; }
  
  void setLocation(Location paramLocation) {
    if (paramLocation == null) {
      this.fLocation = nowhere;
    } else {
      this.fLocation = paramLocation;
    } 
  }
  
  public Characters asCharacters() { return (Characters)this; }
  
  public EndElement asEndElement() { return (EndElement)this; }
  
  public StartElement asStartElement() { return (StartElement)this; }
  
  public QName getSchemaType() { return null; }
  
  public boolean isAttribute() { return (this.fEventType == 10); }
  
  public boolean isCharacters() { return (this.fEventType == 4); }
  
  public boolean isNamespace() { return (this.fEventType == 13); }
  
  public void writeAsEncodedUnicode(Writer paramWriter) throws XMLStreamException {
    try {
      writeAsEncodedUnicodeEx(paramWriter);
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  protected abstract void writeAsEncodedUnicodeEx(Writer paramWriter) throws XMLStreamException;
  
  protected void charEncode(Writer paramWriter, String paramString) throws IOException {
    if (paramString == null || paramString == "")
      return; 
    byte b = 0;
    int i = 0;
    int j = paramString.length();
    while (b < j) {
      switch (paramString.charAt(b)) {
        case '<':
          paramWriter.write(paramString, i, b - i);
          paramWriter.write("&lt;");
          i = b + 1;
          break;
        case '&':
          paramWriter.write(paramString, i, b - i);
          paramWriter.write("&amp;");
          i = b + 1;
          break;
        case '>':
          paramWriter.write(paramString, i, b - i);
          paramWriter.write("&gt;");
          i = b + 1;
          break;
        case '"':
          paramWriter.write(paramString, i, b - i);
          paramWriter.write("&quot;");
          i = b + 1;
          break;
      } 
      b++;
    } 
    paramWriter.write(paramString, i, j - i);
  }
  
  static class DummyLocation implements Location {
    public int getCharacterOffset() { return -1; }
    
    public int getColumnNumber() { return -1; }
    
    public int getLineNumber() { return -1; }
    
    public String getPublicId() { return null; }
    
    public String getSystemId() { return null; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\DummyEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */