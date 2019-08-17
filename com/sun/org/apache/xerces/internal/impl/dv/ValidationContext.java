package com.sun.org.apache.xerces.internal.impl.dv;

import java.util.Locale;

public interface ValidationContext {
  boolean needFacetChecking();
  
  boolean needExtraChecking();
  
  boolean needToNormalize();
  
  boolean useNamespaces();
  
  boolean isEntityDeclared(String paramString);
  
  boolean isEntityUnparsed(String paramString);
  
  boolean isIdDeclared(String paramString);
  
  void addId(String paramString);
  
  void addIdRef(String paramString);
  
  String getSymbol(String paramString);
  
  String getURI(String paramString);
  
  Locale getLocale();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\ValidationContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */