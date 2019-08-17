package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class SignedInfo extends Manifest {
  private SignatureAlgorithm signatureAlgorithm = null;
  
  private byte[] c14nizedBytes = null;
  
  private Element c14nMethod = XMLUtils.createElementInSignatureSpace(this.doc, "CanonicalizationMethod");
  
  private Element signatureMethod;
  
  public SignedInfo(Document paramDocument) throws XMLSecurityException { this(paramDocument, "http://www.w3.org/2000/09/xmldsig#dsa-sha1", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"); }
  
  public SignedInfo(Document paramDocument, String paramString1, String paramString2) throws XMLSecurityException { this(paramDocument, paramString1, 0, paramString2); }
  
  public SignedInfo(Document paramDocument, String paramString1, int paramInt, String paramString2) throws XMLSecurityException {
    super(paramDocument);
    this.c14nMethod.setAttributeNS(null, "Algorithm", paramString2);
    this.constructionElement.appendChild(this.c14nMethod);
    XMLUtils.addReturnToElement(this.constructionElement);
    if (paramInt > 0) {
      this.signatureAlgorithm = new SignatureAlgorithm(this.doc, paramString1, paramInt);
    } else {
      this.signatureAlgorithm = new SignatureAlgorithm(this.doc, paramString1);
    } 
    this.signatureMethod = this.signatureAlgorithm.getElement();
    this.constructionElement.appendChild(this.signatureMethod);
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public SignedInfo(Document paramDocument, Element paramElement1, Element paramElement2) throws XMLSecurityException {
    super(paramDocument);
    this.constructionElement.appendChild(this.c14nMethod);
    XMLUtils.addReturnToElement(this.constructionElement);
    this.signatureAlgorithm = new SignatureAlgorithm(paramElement1, null);
    this.signatureMethod = this.signatureAlgorithm.getElement();
    this.constructionElement.appendChild(this.signatureMethod);
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public SignedInfo(Element paramElement, String paramString) throws XMLSecurityException { this(paramElement, paramString, false); }
  
  public SignedInfo(Element paramElement, String paramString, boolean paramBoolean) throws XMLSecurityException {
    super(reparseSignedInfoElem(paramElement), paramString, paramBoolean);
    this.signatureMethod = XMLUtils.getNextElement(this.c14nMethod.getNextSibling());
    this.signatureAlgorithm = new SignatureAlgorithm(this.signatureMethod, getBaseURI(), paramBoolean);
  }
  
  private static Element reparseSignedInfoElem(Element paramElement) throws XMLSecurityException {
    Element element = XMLUtils.getNextElement(paramElement.getFirstChild());
    String str = element.getAttributeNS(null, "Algorithm");
    if (!str.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315") && !str.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments") && !str.equals("http://www.w3.org/2001/10/xml-exc-c14n#") && !str.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments") && !str.equals("http://www.w3.org/2006/12/xml-c14n11") && !str.equals("http://www.w3.org/2006/12/xml-c14n11#WithComments"))
      try {
        Canonicalizer canonicalizer = Canonicalizer.getInstance(str);
        byte[] arrayOfByte = canonicalizer.canonicalizeSubtree(paramElement);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new ByteArrayInputStream(arrayOfByte));
        Node node = paramElement.getOwnerDocument().importNode(document.getDocumentElement(), true);
        paramElement.getParentNode().replaceChild(node, paramElement);
        return (Element)node;
      } catch (ParserConfigurationException parserConfigurationException) {
        throw new XMLSecurityException("empty", parserConfigurationException);
      } catch (IOException iOException) {
        throw new XMLSecurityException("empty", iOException);
      } catch (SAXException sAXException) {
        throw new XMLSecurityException("empty", sAXException);
      }  
    return paramElement;
  }
  
  public boolean verify() throws MissingResourceFailureException, XMLSecurityException { return verifyReferences(false); }
  
  public boolean verify(boolean paramBoolean) throws MissingResourceFailureException, XMLSecurityException { return verifyReferences(paramBoolean); }
  
  public byte[] getCanonicalizedOctetStream() throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException {
    if (this.c14nizedBytes == null) {
      Canonicalizer canonicalizer = Canonicalizer.getInstance(getCanonicalizationMethodURI());
      this.c14nizedBytes = canonicalizer.canonicalizeSubtree(this.constructionElement);
    } 
    return (byte[])this.c14nizedBytes.clone();
  }
  
  public void signInOctetStream(OutputStream paramOutputStream) throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException {
    if (this.c14nizedBytes == null) {
      Canonicalizer canonicalizer = Canonicalizer.getInstance(getCanonicalizationMethodURI());
      canonicalizer.setWriter(paramOutputStream);
      String str = getInclusiveNamespaces();
      if (str == null) {
        canonicalizer.canonicalizeSubtree(this.constructionElement);
      } else {
        canonicalizer.canonicalizeSubtree(this.constructionElement, str);
      } 
    } else {
      try {
        paramOutputStream.write(this.c14nizedBytes);
      } catch (IOException iOException) {
        throw new RuntimeException(iOException);
      } 
    } 
  }
  
  public String getCanonicalizationMethodURI() { return this.c14nMethod.getAttributeNS(null, "Algorithm"); }
  
  public String getSignatureMethodURI() {
    Element element = getSignatureMethodElement();
    return (element != null) ? element.getAttributeNS(null, "Algorithm") : null;
  }
  
  public Element getSignatureMethodElement() { return this.signatureMethod; }
  
  public SecretKey createSecretKey(byte[] paramArrayOfByte) { return new SecretKeySpec(paramArrayOfByte, this.signatureAlgorithm.getJCEAlgorithmString()); }
  
  protected SignatureAlgorithm getSignatureAlgorithm() { return this.signatureAlgorithm; }
  
  public String getBaseLocalName() { return "SignedInfo"; }
  
  public String getInclusiveNamespaces() {
    String str = this.c14nMethod.getAttributeNS(null, "Algorithm");
    if (!str.equals("http://www.w3.org/2001/10/xml-exc-c14n#") && !str.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments"))
      return null; 
    Element element = XMLUtils.getNextElement(this.c14nMethod.getFirstChild());
    if (element != null)
      try {
        return (new InclusiveNamespaces(element, "http://www.w3.org/2001/10/xml-exc-c14n#")).getInclusiveNamespaces();
      } catch (XMLSecurityException xMLSecurityException) {
        return null;
      }  
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\signature\SignedInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */