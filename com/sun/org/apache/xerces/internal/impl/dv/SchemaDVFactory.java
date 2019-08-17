package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public abstract class SchemaDVFactory {
  private static final String DEFAULT_FACTORY_CLASS = "com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl";
  
  public static final SchemaDVFactory getInstance() throws DVFactoryException { return getInstance("com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl"); }
  
  public static final SchemaDVFactory getInstance(String paramString) throws DVFactoryException {
    try {
      return (SchemaDVFactory)ObjectFactory.newInstance(paramString, true);
    } catch (ClassCastException classCastException) {
      throw new DVFactoryException("Schema factory class " + paramString + " does not extend from SchemaDVFactory.");
    } 
  }
  
  public abstract XSSimpleType getBuiltInType(String paramString);
  
  public abstract SymbolHash getBuiltInTypes();
  
  public abstract XSSimpleType createTypeRestriction(String paramString1, String paramString2, short paramShort, XSSimpleType paramXSSimpleType, XSObjectList paramXSObjectList);
  
  public abstract XSSimpleType createTypeList(String paramString1, String paramString2, short paramShort, XSSimpleType paramXSSimpleType, XSObjectList paramXSObjectList);
  
  public abstract XSSimpleType createTypeUnion(String paramString1, String paramString2, short paramShort, XSSimpleType[] paramArrayOfXSSimpleType, XSObjectList paramXSObjectList);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\SchemaDVFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */