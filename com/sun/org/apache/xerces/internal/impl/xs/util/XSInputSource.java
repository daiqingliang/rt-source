package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xs.XSObject;

public final class XSInputSource extends XMLInputSource {
  private SchemaGrammar[] fGrammars;
  
  private XSObject[] fComponents;
  
  public XSInputSource(SchemaGrammar[] paramArrayOfSchemaGrammar) {
    super(null, null, null);
    this.fGrammars = paramArrayOfSchemaGrammar;
    this.fComponents = null;
  }
  
  public XSInputSource(XSObject[] paramArrayOfXSObject) {
    super(null, null, null);
    this.fGrammars = null;
    this.fComponents = paramArrayOfXSObject;
  }
  
  public SchemaGrammar[] getGrammars() { return this.fGrammars; }
  
  public void setGrammars(SchemaGrammar[] paramArrayOfSchemaGrammar) { this.fGrammars = paramArrayOfSchemaGrammar; }
  
  public XSObject[] getComponents() { return this.fComponents; }
  
  public void setComponents(XSObject[] paramArrayOfXSObject) { this.fComponents = paramArrayOfXSObject; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\x\\util\XSInputSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */