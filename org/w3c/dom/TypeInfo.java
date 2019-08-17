package org.w3c.dom;

public interface TypeInfo {
  public static final int DERIVATION_RESTRICTION = 1;
  
  public static final int DERIVATION_EXTENSION = 2;
  
  public static final int DERIVATION_UNION = 4;
  
  public static final int DERIVATION_LIST = 8;
  
  String getTypeName();
  
  String getTypeNamespace();
  
  boolean isDerivedFrom(String paramString1, String paramString2, int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\TypeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */