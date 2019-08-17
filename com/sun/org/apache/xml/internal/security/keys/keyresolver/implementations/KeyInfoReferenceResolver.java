package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import com.sun.org.apache.xml.internal.security.keys.content.KeyInfoReference;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class KeyInfoReferenceResolver extends KeyResolverSpi {
  private static Logger log = Logger.getLogger(KeyInfoReferenceResolver.class.getName());
  
  public boolean engineCanResolve(Element paramElement, String paramString, StorageResolver paramStorageResolver) { return XMLUtils.elementIsInSignature11Space(paramElement, "KeyInfoReference"); }
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName()); 
    if (!engineCanResolve(paramElement, paramString, paramStorageResolver))
      return null; 
    try {
      KeyInfo keyInfo = resolveReferentKeyInfo(paramElement, paramString, paramStorageResolver);
      if (keyInfo != null)
        return keyInfo.getPublicKey(); 
    } catch (XMLSecurityException xMLSecurityException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "XMLSecurityException", xMLSecurityException); 
    } 
    return null;
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName()); 
    if (!engineCanResolve(paramElement, paramString, paramStorageResolver))
      return null; 
    try {
      KeyInfo keyInfo = resolveReferentKeyInfo(paramElement, paramString, paramStorageResolver);
      if (keyInfo != null)
        return keyInfo.getX509Certificate(); 
    } catch (XMLSecurityException xMLSecurityException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "XMLSecurityException", xMLSecurityException); 
    } 
    return null;
  }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName()); 
    if (!engineCanResolve(paramElement, paramString, paramStorageResolver))
      return null; 
    try {
      KeyInfo keyInfo = resolveReferentKeyInfo(paramElement, paramString, paramStorageResolver);
      if (keyInfo != null)
        return keyInfo.getSecretKey(); 
    } catch (XMLSecurityException xMLSecurityException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "XMLSecurityException", xMLSecurityException); 
    } 
    return null;
  }
  
  public PrivateKey engineLookupAndResolvePrivateKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName()); 
    if (!engineCanResolve(paramElement, paramString, paramStorageResolver))
      return null; 
    try {
      KeyInfo keyInfo = resolveReferentKeyInfo(paramElement, paramString, paramStorageResolver);
      if (keyInfo != null)
        return keyInfo.getPrivateKey(); 
    } catch (XMLSecurityException xMLSecurityException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "XMLSecurityException", xMLSecurityException); 
    } 
    return null;
  }
  
  private KeyInfo resolveReferentKeyInfo(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws XMLSecurityException {
    KeyInfoReference keyInfoReference = new KeyInfoReference(paramElement, paramString);
    Attr attr = keyInfoReference.getURIAttr();
    XMLSignatureInput xMLSignatureInput = resolveInput(attr, paramString, this.secureValidation);
    Element element = null;
    try {
      element = obtainReferenceElement(xMLSignatureInput);
    } catch (Exception exception) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "XMLSecurityException", exception); 
      return null;
    } 
    if (element == null) {
      log.log(Level.FINE, "De-reference of KeyInfoReference URI returned null: " + attr.getValue());
      return null;
    } 
    validateReference(element);
    KeyInfo keyInfo = new KeyInfo(element, paramString);
    keyInfo.addStorageResolver(paramStorageResolver);
    return keyInfo;
  }
  
  private void validateReference(Element paramElement) throws XMLSecurityException {
    if (!XMLUtils.elementIsInSignatureSpace(paramElement, "KeyInfo")) {
      Object[] arrayOfObject = { new QName(paramElement.getNamespaceURI(), paramElement.getLocalName()) };
      throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.WrongType", arrayOfObject);
    } 
    KeyInfo keyInfo = new KeyInfo(paramElement, "");
    if (keyInfo.containsKeyInfoReference()) {
      if (this.secureValidation)
        throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.ReferenceWithSecure"); 
      throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.ReferenceWithoutSecure");
    } 
  }
  
  private XMLSignatureInput resolveInput(Attr paramAttr, String paramString, boolean paramBoolean) throws XMLSecurityException {
    ResourceResolver resourceResolver = ResourceResolver.getInstance(paramAttr, paramString, paramBoolean);
    return resourceResolver.resolve(paramAttr, paramString, paramBoolean);
  }
  
  private Element obtainReferenceElement(XMLSignatureInput paramXMLSignatureInput) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException, KeyResolverException {
    Element element;
    if (paramXMLSignatureInput.isElement()) {
      element = (Element)paramXMLSignatureInput.getSubNode();
    } else {
      if (paramXMLSignatureInput.isNodeSet()) {
        log.log(Level.FINE, "De-reference of KeyInfoReference returned an unsupported NodeSet");
        return null;
      } 
      byte[] arrayOfByte = paramXMLSignatureInput.getBytes();
      element = getDocFromBytes(arrayOfByte);
    } 
    return element;
  }
  
  private Element getDocFromBytes(byte[] paramArrayOfByte) throws KeyResolverException {
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\KeyInfoReferenceResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */