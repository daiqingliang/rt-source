package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public class BaseDVFactory extends SchemaDVFactory {
  static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
  
  static SymbolHash fBaseTypes = new SymbolHash(53);
  
  public XSSimpleType getBuiltInType(String paramString) { return (XSSimpleType)fBaseTypes.get(paramString); }
  
  public SymbolHash getBuiltInTypes() { return fBaseTypes.makeClone(); }
  
  public XSSimpleType createTypeRestriction(String paramString1, String paramString2, short paramShort, XSSimpleType paramXSSimpleType, XSObjectList paramXSObjectList) { return new XSSimpleTypeDecl((XSSimpleTypeDecl)paramXSSimpleType, paramString1, paramString2, paramShort, false, paramXSObjectList); }
  
  public XSSimpleType createTypeList(String paramString1, String paramString2, short paramShort, XSSimpleType paramXSSimpleType, XSObjectList paramXSObjectList) { return new XSSimpleTypeDecl(paramString1, paramString2, paramShort, (XSSimpleTypeDecl)paramXSSimpleType, false, paramXSObjectList); }
  
  public XSSimpleType createTypeUnion(String paramString1, String paramString2, short paramShort, XSSimpleType[] paramArrayOfXSSimpleType, XSObjectList paramXSObjectList) {
    int i = paramArrayOfXSSimpleType.length;
    XSSimpleTypeDecl[] arrayOfXSSimpleTypeDecl = new XSSimpleTypeDecl[i];
    System.arraycopy(paramArrayOfXSSimpleType, 0, arrayOfXSSimpleTypeDecl, 0, i);
    return new XSSimpleTypeDecl(paramString1, paramString2, paramShort, arrayOfXSSimpleTypeDecl, paramXSObjectList);
  }
  
  static void createBuiltInTypes(SymbolHash paramSymbolHash) {
    String str1 = "anySimpleType";
    String str2 = "anyURI";
    String str3 = "base64Binary";
    String str4 = "boolean";
    String str5 = "byte";
    String str6 = "date";
    String str7 = "dateTime";
    String str8 = "gDay";
    String str9 = "decimal";
    String str10 = "int";
    String str11 = "integer";
    String str12 = "long";
    String str13 = "negativeInteger";
    String str14 = "gMonth";
    String str15 = "gMonthDay";
    String str16 = "nonNegativeInteger";
    String str17 = "nonPositiveInteger";
    String str18 = "positiveInteger";
    String str19 = "short";
    String str20 = "string";
    String str21 = "time";
    String str22 = "unsignedByte";
    String str23 = "unsignedInt";
    String str24 = "unsignedLong";
    String str25 = "unsignedShort";
    String str26 = "gYear";
    String str27 = "gYearMonth";
    XSFacets xSFacets = new XSFacets();
    XSSimpleTypeDecl xSSimpleTypeDecl1 = XSSimpleTypeDecl.fAnySimpleType;
    paramSymbolHash.put("anySimpleType", xSSimpleTypeDecl1);
    XSSimpleTypeDecl xSSimpleTypeDecl2 = new XSSimpleTypeDecl(xSSimpleTypeDecl1, "string", (short)1, (short)0, false, false, false, true, (short)2);
    paramSymbolHash.put("string", xSSimpleTypeDecl2);
    paramSymbolHash.put("boolean", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "boolean", (short)2, (short)0, false, true, false, true, (short)3));
    XSSimpleTypeDecl xSSimpleTypeDecl3 = new XSSimpleTypeDecl(xSSimpleTypeDecl1, "decimal", (short)3, (short)2, false, false, true, true, (short)4);
    paramSymbolHash.put("decimal", xSSimpleTypeDecl3);
    paramSymbolHash.put("anyURI", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "anyURI", (short)17, (short)0, false, false, false, true, (short)18));
    paramSymbolHash.put("base64Binary", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "base64Binary", (short)16, (short)0, false, false, false, true, (short)17));
    paramSymbolHash.put("dateTime", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "dateTime", (short)7, (short)1, false, false, false, true, (short)8));
    paramSymbolHash.put("time", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "time", (short)8, (short)1, false, false, false, true, (short)9));
    paramSymbolHash.put("date", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "date", (short)9, (short)1, false, false, false, true, (short)10));
    paramSymbolHash.put("gYearMonth", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "gYearMonth", (short)10, (short)1, false, false, false, true, (short)11));
    paramSymbolHash.put("gYear", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "gYear", (short)11, (short)1, false, false, false, true, (short)12));
    paramSymbolHash.put("gMonthDay", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "gMonthDay", (short)12, (short)1, false, false, false, true, (short)13));
    paramSymbolHash.put("gDay", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "gDay", (short)13, (short)1, false, false, false, true, (short)14));
    paramSymbolHash.put("gMonth", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "gMonth", (short)14, (short)1, false, false, false, true, (short)15));
    XSSimpleTypeDecl xSSimpleTypeDecl4 = new XSSimpleTypeDecl(xSSimpleTypeDecl3, "integer", (short)24, (short)2, false, false, true, true, (short)30);
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
  }
  
  static  {
    createBuiltInTypes(fBaseTypes);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\BaseDVFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */