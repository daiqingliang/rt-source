package javax.xml.soap;

import java.util.Iterator;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public interface SOAPElement extends Node, Element {
  SOAPElement addChildElement(Name paramName) throws SOAPException;
  
  SOAPElement addChildElement(QName paramQName) throws SOAPException;
  
  SOAPElement addChildElement(String paramString) throws SOAPException;
  
  SOAPElement addChildElement(String paramString1, String paramString2) throws SOAPException;
  
  SOAPElement addChildElement(String paramString1, String paramString2, String paramString3) throws SOAPException;
  
  SOAPElement addChildElement(SOAPElement paramSOAPElement) throws SOAPException;
  
  void removeContents();
  
  SOAPElement addTextNode(String paramString) throws SOAPException;
  
  SOAPElement addAttribute(Name paramName, String paramString) throws SOAPException;
  
  SOAPElement addAttribute(QName paramQName, String paramString) throws SOAPException;
  
  SOAPElement addNamespaceDeclaration(String paramString1, String paramString2) throws SOAPException;
  
  String getAttributeValue(Name paramName);
  
  String getAttributeValue(QName paramQName);
  
  Iterator getAllAttributes();
  
  Iterator getAllAttributesAsQNames();
  
  String getNamespaceURI(String paramString);
  
  Iterator getNamespacePrefixes();
  
  Iterator getVisibleNamespacePrefixes();
  
  QName createQName(String paramString1, String paramString2) throws SOAPException;
  
  Name getElementName();
  
  QName getElementQName();
  
  SOAPElement setElementQName(QName paramQName) throws SOAPException;
  
  boolean removeAttribute(Name paramName);
  
  boolean removeAttribute(QName paramQName);
  
  boolean removeNamespaceDeclaration(String paramString);
  
  Iterator getChildElements();
  
  Iterator getChildElements(Name paramName);
  
  Iterator getChildElements(QName paramQName);
  
  void setEncodingStyle(String paramString) throws SOAPException;
  
  String getEncodingStyle();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\soap\SOAPElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */