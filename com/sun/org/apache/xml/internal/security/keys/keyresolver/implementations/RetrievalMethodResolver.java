package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.RetrievalMethod;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class RetrievalMethodResolver extends KeyResolverSpi {
  private static Logger log = Logger.getLogger(RetrievalMethodResolver.class.getName());
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) {
    if (!XMLUtils.elementIsInSignatureSpace(paramElement, "RetrievalMethod"))
      return null; 
    try {
      RetrievalMethod retrievalMethod = new RetrievalMethod(paramElement, paramString);
      String str = retrievalMethod.getType();
      XMLSignatureInput xMLSignatureInput = resolveInput(retrievalMethod, paramString, this.secureValidation);
      if ("http://www.w3.org/2000/09/xmldsig#rawX509Certificate".equals(str)) {
        X509Certificate x509Certificate = getRawCertificate(xMLSignatureInput);
        return (x509Certificate != null) ? x509Certificate.getPublicKey() : null;
      } 
      Element element = obtainReferenceElement(xMLSignatureInput);
      if (XMLUtils.elementIsInSignatureSpace(element, "RetrievalMethod")) {
        if (this.secureValidation) {
          String str1 = "Error: It is forbidden to have one RetrievalMethod point to another with secure validation";
          if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, str1); 
          return null;
        } 
        RetrievalMethod retrievalMethod1 = new RetrievalMethod(element, paramString);
        XMLSignatureInput xMLSignatureInput1 = resolveInput(retrievalMethod1, paramString, this.secureValidation);
        Element element1 = obtainReferenceElement(xMLSignatureInput1);
        if (element1 == paramElement) {
          if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, "Error: Can't have RetrievalMethods pointing to each other"); 
          return null;
        } 
      } 
      return resolveKey(element, paramString, paramStorageResolver);
    } catch (XMLSecurityException xMLSecurityException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "XMLSecurityException", xMLSecurityException); 
    } catch (CertificateException certificateException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "CertificateException", certificateException); 
    } catch (IOException iOException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "IOException", iOException); 
    } catch (ParserConfigurationException parserConfigurationException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "ParserConfigurationException", parserConfigurationException); 
    } catch (SAXException sAXException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "SAXException", sAXException); 
    } 
    return null;
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) {
    if (!XMLUtils.elementIsInSignatureSpace(paramElement, "RetrievalMethod"))
      return null; 
    try {
      RetrievalMethod retrievalMethod = new RetrievalMethod(paramElement, paramString);
      String str = retrievalMethod.getType();
      XMLSignatureInput xMLSignatureInput = resolveInput(retrievalMethod, paramString, this.secureValidation);
      if ("http://www.w3.org/2000/09/xmldsig#rawX509Certificate".equals(str))
        return getRawCertificate(xMLSignatureInput); 
      Element element = obtainReferenceElement(xMLSignatureInput);
      if (XMLUtils.elementIsInSignatureSpace(element, "RetrievalMethod")) {
        if (this.secureValidation) {
          String str1 = "Error: It is forbidden to have one RetrievalMethod point to another with secure validation";
          if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, str1); 
          return null;
        } 
        RetrievalMethod retrievalMethod1 = new RetrievalMethod(element, paramString);
        XMLSignatureInput xMLSignatureInput1 = resolveInput(retrievalMethod1, paramString, this.secureValidation);
        Element element1 = obtainReferenceElement(xMLSignatureInput1);
        if (element1 == paramElement) {
          if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, "Error: Can't have RetrievalMethods pointing to each other"); 
          return null;
        } 
      } 
      return resolveCertificate(element, paramString, paramStorageResolver);
    } catch (XMLSecurityException xMLSecurityException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "XMLSecurityException", xMLSecurityException); 
    } catch (CertificateException certificateException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "CertificateException", certificateException); 
    } catch (IOException iOException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "IOException", iOException); 
    } catch (ParserConfigurationException parserConfigurationException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "ParserConfigurationException", parserConfigurationException); 
    } catch (SAXException sAXException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "SAXException", sAXException); 
    } 
    return null;
  }
  
  private static X509Certificate resolveCertificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Now we have a {" + paramElement.getNamespaceURI() + "}" + paramElement.getLocalName() + " Element"); 
    return (paramElement != null) ? KeyResolver.getX509Certificate(paramElement, paramString, paramStorageResolver) : null;
  }
  
  private static PublicKey resolveKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Now we have a {" + paramElement.getNamespaceURI() + "}" + paramElement.getLocalName() + " Element"); 
    return (paramElement != null) ? KeyResolver.getPublicKey(paramElement, paramString, paramStorageResolver) : null;
  }
  
  private static Element obtainReferenceElement(XMLSignatureInput paramXMLSignatureInput) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException, KeyResolverException {
    Element element;
    if (paramXMLSignatureInput.isElement()) {
      element = (Element)paramXMLSignatureInput.getSubNode();
    } else if (paramXMLSignatureInput.isNodeSet()) {
      element = getDocumentElement(paramXMLSignatureInput.getNodeSet());
    } else {
      byte[] arrayOfByte = paramXMLSignatureInput.getBytes();
      element = getDocFromBytes(arrayOfByte);
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "we have to parse " + arrayOfByte.length + " bytes"); 
    } 
    return element;
  }
  
  private static X509Certificate getRawCertificate(XMLSignatureInput paramXMLSignatureInput) throws CanonicalizationException, IOException, CertificateException {
    byte[] arrayOfByte = paramXMLSignatureInput.getBytes();
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    return (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(arrayOfByte));
  }
  
  private static XMLSignatureInput resolveInput(RetrievalMethod paramRetrievalMethod, String paramString, boolean paramBoolean) throws XMLSecurityException {
    Attr attr = paramRetrievalMethod.getURIAttr();
    Transforms transforms = paramRetrievalMethod.getTransforms();
    ResourceResolver resourceResolver = ResourceResolver.getInstance(attr, paramString, paramBoolean);
    XMLSignatureInput xMLSignatureInput = resourceResolver.resolve(attr, paramString, paramBoolean);
    if (transforms != null) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "We have Transforms"); 
      xMLSignatureInput = transforms.performTransforms(xMLSignatureInput);
    } 
    return xMLSignatureInput;
  }
  
  private static Element getDocFromBytes(byte[] paramArrayOfByte) throws KeyResolverException {
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setNamespaceAware(true);
      documentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      Document document = documentBuilder.parse(new ByteArrayInputStream(paramArrayOfByte));
      return document.getDocumentElement();
    } catch (SAXException sAXException) {
      throw new KeyResolverException("empty", sAXException);
    } catch (IOException iOException) {
      throw new KeyResolverException("empty", iOException);
    } catch (ParserConfigurationException parserConfigurationException) {
      throw new KeyResolverException("empty", parserConfigurationException);
    } 
  }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) { return null; }
  
  private static Element getDocumentElement(Set<Node> paramSet) {
    Iterator iterator = paramSet.iterator();
    Element element1 = null;
    while (iterator.hasNext()) {
      Node node = (Node)iterator.next();
      if (node != null && 1 == node.getNodeType()) {
        element1 = (Element)node;
        break;
      } 
    } 
    ArrayList arrayList = new ArrayList();
    while (element1 != null) {
      arrayList.add(element1);
      Node node = element1.getParentNode();
      if (node == null || 1 != node.getNodeType())
        break; 
      element1 = (Element)node;
    } 
    ListIterator listIterator = arrayList.listIterator(arrayList.size() - 1);
    Element element2 = null;
    while (listIterator.hasPrevious()) {
      element2 = (Element)listIterator.previous();
      if (paramSet.contains(element2))
        return element2; 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\RetrievalMethodResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */