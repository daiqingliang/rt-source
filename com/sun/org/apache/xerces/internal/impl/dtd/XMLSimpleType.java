package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;

public class XMLSimpleType {
  public static final short TYPE_CDATA = 0;
  
  public static final short TYPE_ENTITY = 1;
  
  public static final short TYPE_ENUMERATION = 2;
  
  public static final short TYPE_ID = 3;
  
  public static final short TYPE_IDREF = 4;
  
  public static final short TYPE_NMTOKEN = 5;
  
  public static final short TYPE_NOTATION = 6;
  
  public static final short TYPE_NAMED = 7;
  
  public static final short DEFAULT_TYPE_DEFAULT = 3;
  
  public static final short DEFAULT_TYPE_FIXED = 1;
  
  public static final short DEFAULT_TYPE_IMPLIED = 0;
  
  public static final short DEFAULT_TYPE_REQUIRED = 2;
  
  public short type;
  
  public String name;
  
  public String[] enumeration;
  
  public boolean list;
  
  public short defaultType;
  
  public String defaultValue;
  
  public String nonNormalizedDefaultValue;
  
  public DatatypeValidator datatypeValidator;
  
  public void setValues(short paramShort1, String paramString1, String[] paramArrayOfString, boolean paramBoolean, short paramShort2, String paramString2, String paramString3, DatatypeValidator paramDatatypeValidator) {
    this.type = paramShort1;
    this.name = paramString1;
    if (paramArrayOfString != null && paramArrayOfString.length > 0) {
      this.enumeration = new String[paramArrayOfString.length];
      System.arraycopy(paramArrayOfString, 0, this.enumeration, 0, this.enumeration.length);
    } else {
      this.enumeration = null;
    } 
    this.list = paramBoolean;
    this.defaultType = paramShort2;
    this.defaultValue = paramString2;
    this.nonNormalizedDefaultValue = paramString3;
    this.datatypeValidator = paramDatatypeValidator;
  }
  
  public void setValues(XMLSimpleType paramXMLSimpleType) {
    this.type = paramXMLSimpleType.type;
    this.name = paramXMLSimpleType.name;
    if (paramXMLSimpleType.enumeration != null && paramXMLSimpleType.enumeration.length > 0) {
      this.enumeration = new String[paramXMLSimpleType.enumeration.length];
      System.arraycopy(paramXMLSimpleType.enumeration, 0, this.enumeration, 0, this.enumeration.length);
    } else {
      this.enumeration = null;
    } 
    this.list = paramXMLSimpleType.list;
    this.defaultType = paramXMLSimpleType.defaultType;
    this.defaultValue = paramXMLSimpleType.defaultValue;
    this.nonNormalizedDefaultValue = paramXMLSimpleType.nonNormalizedDefaultValue;
    this.datatypeValidator = paramXMLSimpleType.datatypeValidator;
  }
  
  public void clear() {
    this.type = -1;
    this.name = null;
    this.enumeration = null;
    this.list = false;
    this.defaultType = -1;
    this.defaultValue = null;
    this.nonNormalizedDefaultValue = null;
    this.datatypeValidator = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\XMLSimpleType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */