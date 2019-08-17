package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.EntityDeclaration;

public class EntityDeclarationImpl extends EventBase implements EntityDeclaration {
  private String _publicId;
  
  private String _systemId;
  
  private String _baseURI;
  
  private String _entityName;
  
  private String _replacement;
  
  private String _notationName;
  
  public EntityDeclarationImpl() { init(); }
  
  public EntityDeclarationImpl(String paramString1, String paramString2) {
    init();
    this._entityName = paramString1;
    this._replacement = paramString2;
  }
  
  public String getPublicId() { return this._publicId; }
  
  public String getSystemId() { return this._systemId; }
  
  public String getName() { return this._entityName; }
  
  public String getNotationName() { return this._notationName; }
  
  public String getReplacementText() { return this._replacement; }
  
  public String getBaseURI() { return this._baseURI; }
  
  public void setPublicId(String paramString) { this._publicId = paramString; }
  
  public void setSystemId(String paramString) { this._systemId = paramString; }
  
  public void setBaseURI(String paramString) { this._baseURI = paramString; }
  
  public void setName(String paramString) { this._entityName = paramString; }
  
  public void setReplacementText(String paramString) { this._replacement = paramString; }
  
  public void setNotationName(String paramString) { this._notationName = paramString; }
  
  protected void init() { setEventType(15); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\EntityDeclarationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */