package com.sun.org.apache.xml.internal.security.utils;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Deprecated
public class IdResolver {
  public static void registerElementById(Element paramElement, Attr paramAttr) { paramElement.setIdAttributeNode(paramAttr, true); }
  
  public static Element getElementById(Document paramDocument, String paramString) { return paramDocument.getElementById(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\IdResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */