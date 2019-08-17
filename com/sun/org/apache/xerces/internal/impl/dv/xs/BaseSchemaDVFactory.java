package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.XSDeclarationPool;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public abstract class BaseSchemaDVFactory extends SchemaDVFactory {
  static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
  
  protected XSDeclarationPool fDeclPool = null;
  
  protected static void createBuiltInTypes(SymbolHash paramSymbolHash, XSSimpleTypeDecl paramXSSimpleTypeDecl) {
    String str1 = "anySimpleType";
    String str2 = "anyURI";
    String str3 = "base64Binary";
    String str4 = "boolean";
    String str5 = "byte";
    String str6 = "date";
    String str7 = "dateTime";
    String str8 = "gDay";
    String str9 = "decimal";
    String str10 = "double";
    String str11 = "duration";
    String str12 = "ENTITY";
    String str13 = "ENTITIES";
    String str14 = "float";
    String str15 = "hexBinary";
    String str16 = "ID";
    String str17 = "IDREF";
    String str18 = "IDREFS";
    String str19 = "int";
    String str20 = "integer";
    String str21 = "long";
    String str22 = "Name";
    String str23 = "negativeInteger";
    String str24 = "gMonth";
    String str25 = "gMonthDay";
    String str26 = "NCName";
    String str27 = "NMTOKEN";
    String str28 = "NMTOKENS";
    String str29 = "language";
    String str30 = "nonNegativeInteger";
    String str31 = "nonPositiveInteger";
    String str32 = "normalizedString";
    String str33 = "NOTATION";
    String str34 = "positiveInteger";
    String str35 = "QName";
    String str36 = "short";
    String str37 = "string";
    String str38 = "time";
    String str39 = "token";
    String str40 = "unsignedByte";
    String str41 = "unsignedInt";
    String str42 = "unsignedLong";
    String str43 = "unsignedShort";
    String str44 = "gYear";
    String str45 = "gYearMonth";
    XSFacets xSFacets = new XSFacets();
    paramSymbolHash.put("anySimpleType", XSSimpleTypeDecl.fAnySimpleType);
    XSSimpleTypeDecl xSSimpleTypeDecl1 = new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "string", (short)1, (short)0, false, false, false, true, (short)2);
    paramSymbolHash.put("string", xSSimpleTypeDecl1);
    paramSymbolHash.put("boolean", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "boolean", (short)2, (short)0, false, true, false, true, (short)3));
    XSSimpleTypeDecl xSSimpleTypeDecl2 = new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "decimal", (short)3, (short)2, false, false, true, true, (short)4);
    paramSymbolHash.put("decimal", xSSimpleTypeDecl2);
    paramSymbolHash.put("anyURI", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "anyURI", (short)17, (short)0, false, false, false, true, (short)18));
    paramSymbolHash.put("base64Binary", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "base64Binary", (short)16, (short)0, false, false, false, true, (short)17));
    XSSimpleTypeDecl xSSimpleTypeDecl3 = new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "duration", (short)6, (short)1, false, false, false, true, (short)7);
    paramSymbolHash.put("duration", xSSimpleTypeDecl3);
    paramSymbolHash.put("dateTime", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "dateTime", (short)7, (short)1, false, false, false, true, (short)8));
    paramSymbolHash.put("time", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "time", (short)8, (short)1, false, false, false, true, (short)9));
    paramSymbolHash.put("date", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "date", (short)9, (short)1, false, false, false, true, (short)10));
    paramSymbolHash.put("gYearMonth", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "gYearMonth", (short)10, (short)1, false, false, false, true, (short)11));
    paramSymbolHash.put("gYear", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "gYear", (short)11, (short)1, false, false, false, true, (short)12));
    paramSymbolHash.put("gMonthDay", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "gMonthDay", (short)12, (short)1, false, false, false, true, (short)13));
    paramSymbolHash.put("gDay", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "gDay", (short)13, (short)1, false, false, false, true, (short)14));
    paramSymbolHash.put("gMonth", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "gMonth", (short)14, (short)1, false, false, false, true, (short)15));
    XSSimpleTypeDecl xSSimpleTypeDecl4 = new XSSimpleTypeDecl(xSSimpleTypeDecl2, "integer", (short)24, (short)2, false, false, true, true, (short)30);
    paramSymbolHash.put("integer", xSSimpleTypeDecl4);
    xSFacets.maxInclusive = "0";
    XSSimpleTypeDecl xSSimpleTypeDecl5 = new XSSimpleTypeDecl(xSSimpleTypeDecl4, "nonPositiveInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)31);
    xSSimpleTypeDecl5.applyFacets1(xSFacets, (short)32, (short)0);
    paramSymbolHash.put("nonPositiveInteger", xSSimpleTypeDecl5);
    xSFacets.maxInclusive = "-1";
    XSSimpleTypeDecl xSSimpleTypeDecl6 = new XSSimpleTypeDecl(xSSimpleTypeDecl5, "negativeInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)32);
    xSSimpleTypeDecl6.applyFacets1(xSFacets, (short)32, (short)0);
    paramSymbolHash.put("negativeInteger", xSSimpleTypeDecl6);
    xSFacets.maxInclusive = "9223372036854775807";
    xSFacets.minInclusive = "-9223372036854775808";
    XSSimpleTypeDecl xSSimpleTypeDecl7 = new XSSimpleTypeDecl(xSSimpleTypeDecl4, "long", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)33);
    xSSimpleTypeDecl7.applyFacets1(xSFacets, (short)288, (short)0);
    paramSymbolHash.put("long", xSSimpleTypeDecl7);
    xSFacets.maxInclusive = "2147483647";
    xSFacets.minInclusive = "-2147483648";
    XSSimpleTypeDecl xSSimpleTypeDecl8 = new XSSimpleTypeDecl(xSSimpleTypeDecl7, "int", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)34);
    xSSimpleTypeDecl8.applyFacets1(xSFacets, (short)288, (short)0);
    paramSymbolHash.put("int", xSSimpleTypeDecl8);
    xSFacets.maxInclusive = "32767";
    xSFacets.minInclusive = "-32768";
    XSSimpleTypeDecl xSSimpleTypeDecl9 = new XSSimpleTypeDecl(xSSimpleTypeDecl8, "short", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)35);
    xSSimpleTypeDecl9.applyFacets1(xSFacets, (short)288, (short)0);
    paramSymbolHash.put("short", xSSimpleTypeDecl9);
    xSFacets.maxInclusive = "127";
    xSFacets.minInclusive = "-128";
    XSSimpleTypeDecl xSSimpleTypeDecl10 = new XSSimpleTypeDecl(xSSimpleTypeDecl9, "byte", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)36);
    xSSimpleTypeDecl10.applyFacets1(xSFacets, (short)288, (short)0);
    paramSymbolHash.put("byte", xSSimpleTypeDecl10);
    xSFacets.minInclusive = "0";
    XSSimpleTypeDecl xSSimpleTypeDecl11 = new XSSimpleTypeDecl(xSSimpleTypeDecl4, "nonNegativeInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)37);
    xSSimpleTypeDecl11.applyFacets1(xSFacets, (short)256, (short)0);
    paramSymbolHash.put("nonNegativeInteger", xSSimpleTypeDecl11);
    xSFacets.maxInclusive = "18446744073709551615";
    XSSimpleTypeDecl xSSimpleTypeDecl12 = new XSSimpleTypeDecl(xSSimpleTypeDecl11, "unsignedLong", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)38);
    xSSimpleTypeDecl12.applyFacets1(xSFacets, (short)32, (short)0);
    paramSymbolHash.put("unsignedLong", xSSimpleTypeDecl12);
    xSFacets.maxInclusive = "4294967295";
    XSSimpleTypeDecl xSSimpleTypeDecl13 = new XSSimpleTypeDecl(xSSimpleTypeDecl12, "unsignedInt", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)39);
    xSSimpleTypeDecl13.applyFacets1(xSFacets, (short)32, (short)0);
    paramSymbolHash.put("unsignedInt", xSSimpleTypeDecl13);
    xSFacets.maxInclusive = "65535";
    XSSimpleTypeDecl xSSimpleTypeDecl14 = new XSSimpleTypeDecl(xSSimpleTypeDecl13, "unsignedShort", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)40);
    xSSimpleTypeDecl14.applyFacets1(xSFacets, (short)32, (short)0);
    paramSymbolHash.put("unsignedShort", xSSimpleTypeDecl14);
    xSFacets.maxInclusive = "255";
    XSSimpleTypeDecl xSSimpleTypeDecl15 = new XSSimpleTypeDecl(xSSimpleTypeDecl14, "unsignedByte", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)41);
    xSSimpleTypeDecl15.applyFacets1(xSFacets, (short)32, (short)0);
    paramSymbolHash.put("unsignedByte", xSSimpleTypeDecl15);
    xSFacets.minInclusive = "1";
    XSSimpleTypeDecl xSSimpleTypeDecl16 = new XSSimpleTypeDecl(xSSimpleTypeDecl11, "positiveInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)42);
    xSSimpleTypeDecl16.applyFacets1(xSFacets, (short)256, (short)0);
    paramSymbolHash.put("positiveInteger", xSSimpleTypeDecl16);
    paramSymbolHash.put("float", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "float", (short)4, (short)1, true, true, true, true, (short)5));
    paramSymbolHash.put("double", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "double", (short)5, (short)1, true, true, true, true, (short)6));
    paramSymbolHash.put("hexBinary", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "hexBinary", (short)15, (short)0, false, false, false, true, (short)16));
    paramSymbolHash.put("NOTATION", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "NOTATION", (short)20, (short)0, false, false, false, true, (short)20));
    xSFacets.whiteSpace = 1;
    XSSimpleTypeDecl xSSimpleTypeDecl17 = new XSSimpleTypeDecl(xSSimpleTypeDecl1, "normalizedString", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)21);
    xSSimpleTypeDecl17.applyFacets1(xSFacets, (short)16, (short)0);
    paramSymbolHash.put("normalizedString", xSSimpleTypeDecl17);
    xSFacets.whiteSpace = 2;
    XSSimpleTypeDecl xSSimpleTypeDecl18 = new XSSimpleTypeDecl(xSSimpleTypeDecl17, "token", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)22);
    xSSimpleTypeDecl18.applyFacets1(xSFacets, (short)16, (short)0);
    paramSymbolHash.put("token", xSSimpleTypeDecl18);
    xSFacets.whiteSpace = 2;
    xSFacets.pattern = "([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*";
    XSSimpleTypeDecl xSSimpleTypeDecl19 = new XSSimpleTypeDecl(xSSimpleTypeDecl18, "language", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)23);
    xSSimpleTypeDecl19.applyFacets1(xSFacets, (short)24, (short)0);
    paramSymbolHash.put("language", xSSimpleTypeDecl19);
    xSFacets.whiteSpace = 2;
    XSSimpleTypeDecl xSSimpleTypeDecl20 = new XSSimpleTypeDecl(xSSimpleTypeDecl18, "Name", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)25);
    xSSimpleTypeDecl20.applyFacets1(xSFacets, (short)16, (short)0, (short)2);
    paramSymbolHash.put("Name", xSSimpleTypeDecl20);
    xSFacets.whiteSpace = 2;
    XSSimpleTypeDecl xSSimpleTypeDecl21 = new XSSimpleTypeDecl(xSSimpleTypeDecl20, "NCName", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)26);
    xSSimpleTypeDecl21.applyFacets1(xSFacets, (short)16, (short)0, (short)3);
    paramSymbolHash.put("NCName", xSSimpleTypeDecl21);
    paramSymbolHash.put("QName", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "QName", (short)18, (short)0, false, false, false, true, (short)19));
    paramSymbolHash.put("ID", new XSSimpleTypeDecl(xSSimpleTypeDecl21, "ID", (short)21, (short)0, false, false, false, true, (short)27));
    XSSimpleTypeDecl xSSimpleTypeDecl22 = new XSSimpleTypeDecl(xSSimpleTypeDecl21, "IDREF", (short)22, (short)0, false, false, false, true, (short)28);
    paramSymbolHash.put("IDREF", xSSimpleTypeDecl22);
    xSFacets.minLength = 1;
    XSSimpleTypeDecl xSSimpleTypeDecl23 = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, xSSimpleTypeDecl22, true, null);
    XSSimpleTypeDecl xSSimpleTypeDecl24 = new XSSimpleTypeDecl(xSSimpleTypeDecl23, "IDREFS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
    xSSimpleTypeDecl24.applyFacets1(xSFacets, (short)2, (short)0);
    paramSymbolHash.put("IDREFS", xSSimpleTypeDecl24);
    XSSimpleTypeDecl xSSimpleTypeDecl25 = new XSSimpleTypeDecl(xSSimpleTypeDecl21, "ENTITY", (short)23, (short)0, false, false, false, true, (short)29);
    paramSymbolHash.put("ENTITY", xSSimpleTypeDecl25);
    xSFacets.minLength = 1;
    xSSimpleTypeDecl23 = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, xSSimpleTypeDecl25, true, null);
    XSSimpleTypeDecl xSSimpleTypeDecl26 = new XSSimpleTypeDecl(xSSimpleTypeDecl23, "ENTITIES", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
    xSSimpleTypeDecl26.applyFacets1(xSFacets, (short)2, (short)0);
    paramSymbolHash.put("ENTITIES", xSSimpleTypeDecl26);
    xSFacets.whiteSpace = 2;
    XSSimpleTypeDecl xSSimpleTypeDecl27 = new XSSimpleTypeDecl(xSSimpleTypeDecl18, "NMTOKEN", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)24);
    xSSimpleTypeDecl27.applyFacets1(xSFacets, (short)16, (short)0, (short)1);
    paramSymbolHash.put("NMTOKEN", xSSimpleTypeDecl27);
    xSFacets.minLength = 1;
    xSSimpleTypeDecl23 = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, xSSimpleTypeDecl27, true, null);
    XSSimpleTypeDecl xSSimpleTypeDecl28 = new XSSimpleTypeDecl(xSSimpleTypeDecl23, "NMTOKENS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
    xSSimpleTypeDecl28.applyFacets1(xSFacets, (short)2, (short)0);
    paramSymbolHash.put("NMTOKENS", xSSimpleTypeDecl28);
  }
  
  public XSSimpleType createTypeRestriction(String paramString1, String paramString2, short paramShort, XSSimpleType paramXSSimpleType, XSObjectList paramXSObjectList) {
    if (this.fDeclPool != null) {
      XSSimpleTypeDecl xSSimpleTypeDecl = this.fDeclPool.getSimpleTypeDecl();
      return xSSimpleTypeDecl.setRestrictionValues((XSSimpleTypeDecl)paramXSSimpleType, paramString1, paramString2, paramShort, paramXSObjectList);
    } 
    return new XSSimpleTypeDecl((XSSimpleTypeDecl)paramXSSimpleType, paramString1, paramString2, paramShort, false, paramXSObjectList);
  }
  
  public XSSimpleType createTypeList(String paramString1, String paramString2, short paramShort, XSSimpleType paramXSSimpleType, XSObjectList paramXSObjectList) {
    if (this.fDeclPool != null) {
      XSSimpleTypeDecl xSSimpleTypeDecl = this.fDeclPool.getSimpleTypeDecl();
      return xSSimpleTypeDecl.setListValues(paramString1, paramString2, paramShort, (XSSimpleTypeDecl)paramXSSimpleType, paramXSObjectList);
    } 
    return new XSSimpleTypeDecl(paramString1, paramString2, paramShort, (XSSimpleTypeDecl)paramXSSimpleType, false, paramXSObjectList);
  }
  
  public XSSimpleType createTypeUnion(String paramString1, String paramString2, short paramShort, XSSimpleType[] paramArrayOfXSSimpleType, XSObjectList paramXSObjectList) {
    int i = paramArrayOfXSSimpleType.length;
    XSSimpleTypeDecl[] arrayOfXSSimpleTypeDecl = new XSSimpleTypeDecl[i];
    System.arraycopy(paramArrayOfXSSimpleType, 0, arrayOfXSSimpleTypeDecl, 0, i);
    if (this.fDeclPool != null) {
      XSSimpleTypeDecl xSSimpleTypeDecl = this.fDeclPool.getSimpleTypeDecl();
      return xSSimpleTypeDecl.setUnionValues(paramString1, paramString2, paramShort, arrayOfXSSimpleTypeDecl, paramXSObjectList);
    } 
    return new XSSimpleTypeDecl(paramString1, paramString2, paramShort, arrayOfXSSimpleTypeDecl, paramXSObjectList);
  }
  
  public void setDeclPool(XSDeclarationPool paramXSDeclarationPool) { this.fDeclPool = paramXSDeclarationPool; }
  
  public XSSimpleTypeDecl newXSSimpleTypeDecl() { return new XSSimpleTypeDecl(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\BaseSchemaDVFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */