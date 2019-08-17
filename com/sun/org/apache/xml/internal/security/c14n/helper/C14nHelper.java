package com.sun.org.apache.xml.internal.security.c14n.helper;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class C14nHelper {
  public static boolean namespaceIsRelative(Attr paramAttr) { return !namespaceIsAbsolute(paramAttr); }
  
  public static boolean namespaceIsRelative(String paramString) { return !namespaceIsAbsolute(paramString); }
  
  public static boolean namespaceIsAbsolute(Attr paramAttr) { return namespaceIsAbsolute(paramAttr.getValue()); }
  
  public static boolean namespaceIsAbsolute(String paramString) { return (paramString.length() == 0) ? true : ((paramString.indexOf(':') > 0)); }
  
  public static void assertNotRelativeNS(Attr paramAttr) throws CanonicalizationException {
    if (paramAttr == null)
      return; 
    String str = paramAttr.getNodeName();
    boolean bool1 = str.equals("xmlns");
    boolean bool2 = str.startsWith("xmlns:");
    if ((bool1 || bool2) && namespaceIsRelative(paramAttr)) {
      String str1 = paramAttr.getOwnerElement().getTagName();
      String str2 = paramAttr.getValue();
      Object[] arrayOfObject = { str1, str, str2 };
      throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", arrayOfObject);
    } 
  }
  
  public static void checkTraversability(Document paramDocument) throws CanonicalizationException {
    if (!paramDocument.isSupported("Traversal", "2.0")) {
      Object[] arrayOfObject = { paramDocument.getImplementation().getClass().getName() };
      throw new CanonicalizationException("c14n.Canonicalizer.TraversalNotSupported", arrayOfObject);
    } 
  }
  
  public static void checkForRelativeNamespace(Element paramElement) throws CanonicalizationException {
    if (paramElement != null) {
      NamedNodeMap namedNodeMap = paramElement.getAttributes();
      for (byte b = 0; b < namedNodeMap.getLength(); b++)
        assertNotRelativeNS((Attr)namedNodeMap.item(b)); 
    } else {
      throw new CanonicalizationException("Called checkForRelativeNamespace() on null");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\helper\C14nHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */