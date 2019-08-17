package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public abstract class ElementProxy {
  protected static final Logger log = Logger.getLogger(ElementProxy.class.getName());
  
  protected Element constructionElement = null;
  
  protected String baseURI = null;
  
  protected Document doc = null;
  
  private static Map<String, String> prefixMappings = new ConcurrentHashMap();
  
  public ElementProxy() {}
  
  public ElementProxy(Document paramDocument) {
    if (paramDocument == null)
      throw new RuntimeException("Document is null"); 
    this.doc = paramDocument;
    this.constructionElement = createElementForFamilyLocal(this.doc, getBaseNamespace(), getBaseLocalName());
  }
  
  public ElementProxy(Element paramElement, String paramString) throws XMLSecurityException {
    if (paramElement == null)
      throw new XMLSecurityException("ElementProxy.nullElement"); 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "setElement(\"" + paramElement.getTagName() + "\", \"" + paramString + "\")"); 
    this.doc = paramElement.getOwnerDocument();
    this.constructionElement = paramElement;
    this.baseURI = paramString;
    guaranteeThatElementInCorrectSpace();
  }
  
  public abstract String getBaseNamespace();
  
  public abstract String getBaseLocalName();
  
  protected Element createElementForFamilyLocal(Document paramDocument, String paramString1, String paramString2) {
    Element element = null;
    if (paramString1 == null) {
      element = paramDocument.createElementNS(null, paramString2);
    } else {
      String str1 = getBaseNamespace();
      String str2 = getDefaultPrefix(str1);
      if (str2 == null || str2.length() == 0) {
        element = paramDocument.createElementNS(paramString1, paramString2);
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", paramString1);
      } else {
        element = paramDocument.createElementNS(paramString1, str2 + ":" + paramString2);
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str2, paramString1);
      } 
    } 
    return element;
  }
  
  public static Element createElementForFamily(Document paramDocument, String paramString1, String paramString2) {
    Element element = null;
    String str = getDefaultPrefix(paramString1);
    if (paramString1 == null) {
      element = paramDocument.createElementNS(null, paramString2);
    } else if (str == null || str.length() == 0) {
      element = paramDocument.createElementNS(paramString1, paramString2);
      element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", paramString1);
    } else {
      element = paramDocument.createElementNS(paramString1, str + ":" + paramString2);
      element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str, paramString1);
    } 
    return element;
  }
  
  public void setElement(Element paramElement, String paramString) throws XMLSecurityException {
    if (paramElement == null)
      throw new XMLSecurityException("ElementProxy.nullElement"); 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "setElement(" + paramElement.getTagName() + ", \"" + paramString + "\""); 
    this.doc = paramElement.getOwnerDocument();
    this.constructionElement = paramElement;
    this.baseURI = paramString;
  }
  
  public final Element getElement() { return this.constructionElement; }
  
  public final NodeList getElementPlusReturns() {
    HelperNodeList helperNodeList = new HelperNodeList();
    helperNodeList.appendChild(this.doc.createTextNode("\n"));
    helperNodeList.appendChild(getElement());
    helperNodeList.appendChild(this.doc.createTextNode("\n"));
    return helperNodeList;
  }
  
  public Document getDocument() { return this.doc; }
  
  public String getBaseURI() { return this.baseURI; }
  
  void guaranteeThatElementInCorrectSpace() {
    String str1 = getBaseLocalName();
    String str2 = getBaseNamespace();
    String str3 = this.constructionElement.getLocalName();
    String str4 = this.constructionElement.getNamespaceURI();
    if (!str2.equals(str4) && !str1.equals(str3)) {
      Object[] arrayOfObject = { str4 + ":" + str3, str2 + ":" + str1 };
      throw new XMLSecurityException("xml.WrongElement", arrayOfObject);
    } 
  }
  
  public void addBigIntegerElement(BigInteger paramBigInteger, String paramString) {
    if (paramBigInteger != null) {
      Element element = XMLUtils.createElementInSignatureSpace(this.doc, paramString);
      Base64.fillElementWithBigInteger(element, paramBigInteger);
      this.constructionElement.appendChild(element);
      XMLUtils.addReturnToElement(this.constructionElement);
    } 
  }
  
  public void addBase64Element(byte[] paramArrayOfByte, String paramString) {
    if (paramArrayOfByte != null) {
      Element element = Base64.encodeToElement(this.doc, paramString, paramArrayOfByte);
      this.constructionElement.appendChild(element);
      if (!XMLUtils.ignoreLineBreaks())
        this.constructionElement.appendChild(this.doc.createTextNode("\n")); 
    } 
  }
  
  public void addTextElement(String paramString1, String paramString2) {
    Element element = XMLUtils.createElementInSignatureSpace(this.doc, paramString2);
    Text text = this.doc.createTextNode(paramString1);
    element.appendChild(text);
    this.constructionElement.appendChild(element);
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void addBase64Text(byte[] paramArrayOfByte) {
    if (paramArrayOfByte != null) {
      Text text = XMLUtils.ignoreLineBreaks() ? this.doc.createTextNode(Base64.encode(paramArrayOfByte)) : this.doc.createTextNode("\n" + Base64.encode(paramArrayOfByte) + "\n");
      this.constructionElement.appendChild(text);
    } 
  }
  
  public void addText(String paramString) {
    if (paramString != null) {
      Text text = this.doc.createTextNode(paramString);
      this.constructionElement.appendChild(text);
    } 
  }
  
  public BigInteger getBigIntegerFromChildElement(String paramString1, String paramString2) throws Base64DecodingException { return Base64.decodeBigIntegerFromText(XMLUtils.selectNodeText(this.constructionElement.getFirstChild(), paramString2, paramString1, 0)); }
  
  @Deprecated
  public byte[] getBytesFromChildElement(String paramString1, String paramString2) throws XMLSecurityException {
    Element element = XMLUtils.selectNode(this.constructionElement.getFirstChild(), paramString2, paramString1, 0);
    return Base64.decode(element);
  }
  
  public String getTextFromChildElement(String paramString1, String paramString2) { return XMLUtils.selectNode(this.constructionElement.getFirstChild(), paramString2, paramString1, 0).getTextContent(); }
  
  public byte[] getBytesFromTextChild() throws XMLSecurityException { return Base64.decode(XMLUtils.getFullTextChildrenFromElement(this.constructionElement)); }
  
  public String getTextFromTextChild() { return XMLUtils.getFullTextChildrenFromElement(this.constructionElement); }
  
  public int length(String paramString1, String paramString2) {
    byte b = 0;
    for (Node node = this.constructionElement.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (paramString2.equals(node.getLocalName()) && paramString1.equals(node.getNamespaceURI()))
        b++; 
    } 
    return b;
  }
  
  public void setXPathNamespaceContext(String paramString1, String paramString2) {
    String str;
    if (paramString1 == null || paramString1.length() == 0)
      throw new XMLSecurityException("defaultNamespaceCannotBeSetHere"); 
    if (paramString1.equals("xmlns"))
      throw new XMLSecurityException("defaultNamespaceCannotBeSetHere"); 
    if (paramString1.startsWith("xmlns:")) {
      str = paramString1;
    } else {
      str = "xmlns:" + paramString1;
    } 
    Attr attr = this.constructionElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", str);
    if (attr != null) {
      if (!attr.getNodeValue().equals(paramString2)) {
        Object[] arrayOfObject = { str, this.constructionElement.getAttributeNS(null, str) };
        throw new XMLSecurityException("namespacePrefixAlreadyUsedByOtherURI", arrayOfObject);
      } 
      return;
    } 
    this.constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", str, paramString2);
  }
  
  public static void setDefaultPrefix(String paramString1, String paramString2) {
    JavaUtils.checkRegisterPermission();
    if (prefixMappings.containsValue(paramString2)) {
      String str = (String)prefixMappings.get(paramString1);
      if (!str.equals(paramString2)) {
        Object[] arrayOfObject = { paramString2, paramString1, str };
        throw new XMLSecurityException("prefix.AlreadyAssigned", arrayOfObject);
      } 
    } 
    if ("http://www.w3.org/2000/09/xmldsig#".equals(paramString1))
      XMLUtils.setDsPrefix(paramString2); 
    if ("http://www.w3.org/2001/04/xmlenc#".equals(paramString1))
      XMLUtils.setXencPrefix(paramString2); 
    prefixMappings.put(paramString1, paramString2);
  }
  
  public static void registerDefaultPrefixes() {
    setDefaultPrefix("http://www.w3.org/2000/09/xmldsig#", "ds");
    setDefaultPrefix("http://www.w3.org/2001/04/xmlenc#", "xenc");
    setDefaultPrefix("http://www.w3.org/2009/xmlenc11#", "xenc11");
    setDefaultPrefix("http://www.xmlsecurity.org/experimental#", "experimental");
    setDefaultPrefix("http://www.w3.org/2002/04/xmldsig-filter2", "dsig-xpath-old");
    setDefaultPrefix("http://www.w3.org/2002/06/xmldsig-filter2", "dsig-xpath");
    setDefaultPrefix("http://www.w3.org/2001/10/xml-exc-c14n#", "ec");
    setDefaultPrefix("http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter", "xx");
  }
  
  public static String getDefaultPrefix(String paramString) { return (String)prefixMappings.get(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\ElementProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */