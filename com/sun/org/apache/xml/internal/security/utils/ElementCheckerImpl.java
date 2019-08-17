package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@Deprecated
public abstract class ElementCheckerImpl implements ElementChecker {
  public boolean isNamespaceElement(Node paramNode, String paramString1, String paramString2) { return !(paramNode == null || paramString2 != paramNode.getNamespaceURI() || !paramNode.getLocalName().equals(paramString1)); }
  
  public static class EmptyChecker extends ElementCheckerImpl {
    public void guaranteeThatElementInCorrectSpace(ElementProxy param1ElementProxy, Element param1Element) throws XMLSecurityException {}
  }
  
  public static class FullChecker extends ElementCheckerImpl {
    public void guaranteeThatElementInCorrectSpace(ElementProxy param1ElementProxy, Element param1Element) throws XMLSecurityException {
      String str1 = param1ElementProxy.getBaseLocalName();
      String str2 = param1ElementProxy.getBaseNamespace();
      String str3 = param1Element.getLocalName();
      String str4 = param1Element.getNamespaceURI();
      if (!str2.equals(str4) || !str1.equals(str3)) {
        Object[] arrayOfObject = { str4 + ":" + str3, str2 + ":" + str1 };
        throw new XMLSecurityException("xml.WrongElement", arrayOfObject);
      } 
    }
  }
  
  public static class InternedNsChecker extends ElementCheckerImpl {
    public void guaranteeThatElementInCorrectSpace(ElementProxy param1ElementProxy, Element param1Element) throws XMLSecurityException {
      String str1 = param1ElementProxy.getBaseLocalName();
      String str2 = param1ElementProxy.getBaseNamespace();
      String str3 = param1Element.getLocalName();
      String str4 = param1Element.getNamespaceURI();
      if (str2 != str4 || !str1.equals(str3)) {
        Object[] arrayOfObject = { str4 + ":" + str3, str2 + ":" + str1 };
        throw new XMLSecurityException("xml.WrongElement", arrayOfObject);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\ElementCheckerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */