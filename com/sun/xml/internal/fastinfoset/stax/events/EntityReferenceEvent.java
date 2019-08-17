package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;

public class EntityReferenceEvent extends EventBase implements EntityReference {
  private EntityDeclaration _entityDeclaration;
  
  private String _entityName;
  
  public EntityReferenceEvent() { init(); }
  
  public EntityReferenceEvent(String paramString, EntityDeclaration paramEntityDeclaration) {
    init();
    this._entityName = paramString;
    this._entityDeclaration = paramEntityDeclaration;
  }
  
  public String getName() { return this._entityName; }
  
  public EntityDeclaration getDeclaration() { return this._entityDeclaration; }
  
  public void setName(String paramString) { this._entityName = paramString; }
  
  public void setDeclaration(EntityDeclaration paramEntityDeclaration) { this._entityDeclaration = paramEntityDeclaration; }
  
  public String toString() {
    String str = this._entityDeclaration.getReplacementText();
    if (str == null)
      str = ""; 
    return "&" + getName() + ";='" + str + "'";
  }
  
  protected void init() { setEventType(9); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\EntityReferenceEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */