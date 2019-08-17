package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.SymbolHash;

public class SchemaDVFactoryImpl extends BaseSchemaDVFactory {
  static final SymbolHash fBuiltInTypes = new SymbolHash();
  
  static void createBuiltInTypes() { createBuiltInTypes(fBuiltInTypes, XSSimpleTypeDecl.fAnySimpleType); }
  
  public XSSimpleType getBuiltInType(String paramString) { return (XSSimpleType)fBuiltInTypes.get(paramString); }
  
  public SymbolHash getBuiltInTypes() { return fBuiltInTypes.makeClone(); }
  
  static  {
    createBuiltInTypes();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\SchemaDVFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */