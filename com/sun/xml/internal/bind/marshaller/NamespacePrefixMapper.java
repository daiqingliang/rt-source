package com.sun.xml.internal.bind.marshaller;

public abstract class NamespacePrefixMapper {
  private static final String[] EMPTY_STRING = new String[0];
  
  public abstract String getPreferredPrefix(String paramString1, String paramString2, boolean paramBoolean);
  
  public String[] getPreDeclaredNamespaceUris() { return EMPTY_STRING; }
  
  public String[] getPreDeclaredNamespaceUris2() { return EMPTY_STRING; }
  
  public String[] getContextualNamespaceDecls() { return EMPTY_STRING; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\marshaller\NamespacePrefixMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */