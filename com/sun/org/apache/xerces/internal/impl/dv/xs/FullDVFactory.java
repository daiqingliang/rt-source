package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.SymbolHash;

public class FullDVFactory extends BaseDVFactory {
  static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
  
  static SymbolHash fFullTypes = new SymbolHash(89);
  
  public XSSimpleType getBuiltInType(String paramString) { return (XSSimpleType)fFullTypes.get(paramString); }
  
  public SymbolHash getBuiltInTypes() { return fFullTypes.makeClone(); }
  
  static void createBuiltInTypes(SymbolHash paramSymbolHash) {
    BaseDVFactory.createBuiltInTypes(paramSymbolHash);
    String str1 = "double";
    String str2 = "duration";
    String str3 = "ENTITY";
    String str4 = "ENTITIES";
    String str5 = "float";
    String str6 = "hexBinary";
    String str7 = "ID";
    String str8 = "IDREF";
    String str9 = "IDREFS";
    String str10 = "Name";
    String str11 = "NCName";
    String str12 = "NMTOKEN";
    String str13 = "NMTOKENS";
    String str14 = "language";
    String str15 = "normalizedString";
    String str16 = "NOTATION";
    String str17 = "QName";
    String str18 = "string";
    String str19 = "token";
    XSFacets xSFacets = new XSFacets();
    XSSimpleTypeDecl xSSimpleTypeDecl1 = XSSimpleTypeDecl.fAnySimpleType;
    XSSimpleTypeDecl xSSimpleTypeDecl2 = (XSSimpleTypeDecl)paramSymbolHash.get("string");
    paramSymbolHash.put("float", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "float", (short)4, (short)1, true, true, true, true, (short)5));
    paramSymbolHash.put("double", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "double", (short)5, (short)1, true, true, true, true, (short)6));
    paramSymbolHash.put("duration", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "duration", (short)6, (short)1, false, false, false, true, (short)7));
    paramSymbolHash.put("hexBinary", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "hexBinary", (short)15, (short)0, false, false, false, true, (short)16));
    paramSymbolHash.put("QName", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "QName", (short)18, (short)0, false, false, false, true, (short)19));
    paramSymbolHash.put("NOTATION", new XSSimpleTypeDecl(xSSimpleTypeDecl1, "NOTATION", (short)20, (short)0, false, false, false, true, (short)20));
    xSFacets.whiteSpace = 1;
    XSSimpleTypeDecl xSSimpleTypeDecl3 = new XSSimpleTypeDecl(xSSimpleTypeDecl2, "normalizedString", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)21);
    xSSimpleTypeDecl3.applyFacets1(xSFacets, (short)16, (short)0);
    paramSymbolHash.put("normalizedString", xSSimpleTypeDecl3);
    xSFacets.whiteSpace = 2;
    XSSimpleTypeDecl xSSimpleTypeDecl4 = new XSSimpleTypeDecl(xSSimpleTypeDecl3, "token", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)22);
    xSSimpleTypeDecl4.applyFacets1(xSFacets, (short)16, (short)0);
    paramSymbolHash.put("token", xSSimpleTypeDecl4);
    xSFacets.whiteSpace = 2;
    xSFacets.pattern = "([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*";
    XSSimpleTypeDecl xSSimpleTypeDecl5 = new XSSimpleTypeDecl(xSSimpleTypeDecl4, "language", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)23);
    xSSimpleTypeDecl5.applyFacets1(xSFacets, (short)24, (short)0);
    paramSymbolHash.put("language", xSSimpleTypeDecl5);
    xSFacets.whiteSpace = 2;
    XSSimpleTypeDecl xSSimpleTypeDecl6 = new XSSimpleTypeDecl(xSSimpleTypeDecl4, "Name", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)25);
    xSSimpleTypeDecl6.applyFacets1(xSFacets, (short)16, (short)0, (short)2);
    paramSymbolHash.put("Name", xSSimpleTypeDecl6);
    xSFacets.whiteSpace = 2;
    XSSimpleTypeDecl xSSimpleTypeDecl7 = new XSSimpleTypeDecl(xSSimpleTypeDecl6, "NCName", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)26);
    xSSimpleTypeDecl7.applyFacets1(xSFacets, (short)16, (short)0, (short)3);
    paramSymbolHash.put("NCName", xSSimpleTypeDecl7);
    paramSymbolHash.put("ID", new XSSimpleTypeDecl(xSSimpleTypeDecl7, "ID", (short)21, (short)0, false, false, false, true, (short)27));
    XSSimpleTypeDecl xSSimpleTypeDecl8 = new XSSimpleTypeDecl(xSSimpleTypeDecl7, "IDREF", (short)22, (short)0, false, false, false, true, (short)28);
    paramSymbolHash.put("IDREF", xSSimpleTypeDecl8);
    xSFacets.minLength = 1;
    XSSimpleTypeDecl xSSimpleTypeDecl9 = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, xSSimpleTypeDecl8, true, null);
    XSSimpleTypeDecl xSSimpleTypeDecl10 = new XSSimpleTypeDecl(xSSimpleTypeDecl9, "IDREFS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
    xSSimpleTypeDecl10.applyFacets1(xSFacets, (short)2, (short)0);
    paramSymbolHash.put("IDREFS", xSSimpleTypeDecl10);
    XSSimpleTypeDecl xSSimpleTypeDecl11 = new XSSimpleTypeDecl(xSSimpleTypeDecl7, "ENTITY", (short)23, (short)0, false, false, false, true, (short)29);
    paramSymbolHash.put("ENTITY", xSSimpleTypeDecl11);
    xSFacets.minLength = 1;
    xSSimpleTypeDecl9 = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, xSSimpleTypeDecl11, true, null);
    XSSimpleTypeDecl xSSimpleTypeDecl12 = new XSSimpleTypeDecl(xSSimpleTypeDecl9, "ENTITIES", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
    xSSimpleTypeDecl12.applyFacets1(xSFacets, (short)2, (short)0);
    paramSymbolHash.put("ENTITIES", xSSimpleTypeDecl12);
    xSFacets.whiteSpace = 2;
    XSSimpleTypeDecl xSSimpleTypeDecl13 = new XSSimpleTypeDecl(xSSimpleTypeDecl4, "NMTOKEN", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)24);
    xSSimpleTypeDecl13.applyFacets1(xSFacets, (short)16, (short)0, (short)1);
    paramSymbolHash.put("NMTOKEN", xSSimpleTypeDecl13);
    xSFacets.minLength = 1;
    xSSimpleTypeDecl9 = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, xSSimpleTypeDecl13, true, null);
    XSSimpleTypeDecl xSSimpleTypeDecl14 = new XSSimpleTypeDecl(xSSimpleTypeDecl9, "NMTOKENS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
    xSSimpleTypeDecl14.applyFacets1(xSFacets, (short)2, (short)0);
    paramSymbolHash.put("NMTOKENS", xSSimpleTypeDecl14);
  }
  
  static  {
    createBuiltInTypes(fFullTypes);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\FullDVFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */