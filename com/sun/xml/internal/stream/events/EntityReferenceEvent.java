package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;

public class EntityReferenceEvent extends DummyEvent implements EntityReference {
  private EntityDeclaration fEntityDeclaration;
  
  private String fEntityName;
  
  public EntityReferenceEvent() { init(); }
  
  public EntityReferenceEvent(String paramString, EntityDeclaration paramEntityDeclaration) {
    init();
    this.fEntityName = paramString;
    this.fEntityDeclaration = paramEntityDeclaration;
  }
  
  public String getName() { return this.fEntityName; }
  
  public String toString() {
    String str = this.fEntityDeclaration.getReplacementText();
    if (str == null)
      str = ""; 
    return "&" + getName() + ";='" + str + "'";
  }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter) throws IOException {
    paramWriter.write(38);
    paramWriter.write(getName());
    paramWriter.write(59);
  }
  
  public EntityDeclaration getDeclaration() { return this.fEntityDeclaration; }
  
  protected void init() { setEventType(9); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\EntityReferenceEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */