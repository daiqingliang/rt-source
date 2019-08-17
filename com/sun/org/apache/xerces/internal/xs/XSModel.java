package com.sun.org.apache.xerces.internal.xs;

public interface XSModel {
  StringList getNamespaces();
  
  XSNamespaceItemList getNamespaceItems();
  
  XSNamedMap getComponents(short paramShort);
  
  XSNamedMap getComponentsByNamespace(short paramShort, String paramString);
  
  XSObjectList getAnnotations();
  
  XSElementDeclaration getElementDeclaration(String paramString1, String paramString2);
  
  XSAttributeDeclaration getAttributeDeclaration(String paramString1, String paramString2);
  
  XSTypeDefinition getTypeDefinition(String paramString1, String paramString2);
  
  XSAttributeGroupDefinition getAttributeGroup(String paramString1, String paramString2);
  
  XSModelGroupDefinition getModelGroupDefinition(String paramString1, String paramString2);
  
  XSNotationDeclaration getNotationDeclaration(String paramString1, String paramString2);
  
  XSObjectList getSubstitutionGroup(XSElementDeclaration paramXSElementDeclaration);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */