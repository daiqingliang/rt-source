package com.sun.org.apache.xerces.internal.xs;

public interface XSSimpleTypeDefinition extends XSTypeDefinition {
  public static final short VARIETY_ABSENT = 0;
  
  public static final short VARIETY_ATOMIC = 1;
  
  public static final short VARIETY_LIST = 2;
  
  public static final short VARIETY_UNION = 3;
  
  public static final short FACET_NONE = 0;
  
  public static final short FACET_LENGTH = 1;
  
  public static final short FACET_MINLENGTH = 2;
  
  public static final short FACET_MAXLENGTH = 4;
  
  public static final short FACET_PATTERN = 8;
  
  public static final short FACET_WHITESPACE = 16;
  
  public static final short FACET_MAXINCLUSIVE = 32;
  
  public static final short FACET_MAXEXCLUSIVE = 64;
  
  public static final short FACET_MINEXCLUSIVE = 128;
  
  public static final short FACET_MININCLUSIVE = 256;
  
  public static final short FACET_TOTALDIGITS = 512;
  
  public static final short FACET_FRACTIONDIGITS = 1024;
  
  public static final short FACET_ENUMERATION = 2048;
  
  public static final short ORDERED_FALSE = 0;
  
  public static final short ORDERED_PARTIAL = 1;
  
  public static final short ORDERED_TOTAL = 2;
  
  short getVariety();
  
  XSSimpleTypeDefinition getPrimitiveType();
  
  short getBuiltInKind();
  
  XSSimpleTypeDefinition getItemType();
  
  XSObjectList getMemberTypes();
  
  short getDefinedFacets();
  
  boolean isDefinedFacet(short paramShort);
  
  short getFixedFacets();
  
  boolean isFixedFacet(short paramShort);
  
  String getLexicalFacetValue(short paramShort);
  
  StringList getLexicalEnumeration();
  
  StringList getLexicalPattern();
  
  short getOrdered();
  
  boolean getFinite();
  
  boolean getBounded();
  
  boolean getNumeric();
  
  XSObjectList getFacets();
  
  XSObjectList getMultiValueFacets();
  
  XSObjectList getAnnotations();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSSimpleTypeDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */