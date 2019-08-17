package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.SignerOutputStream;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.io.IOException;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public final class XMLSignature extends SignatureElementProxy {
  public static final String ALGO_ID_MAC_HMAC_SHA1 = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
  
  public static final String ALGO_ID_SIGNATURE_DSA = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
  
  public static final String ALGO_ID_SIGNATURE_DSA_SHA256 = "http://www.w3.org/2009/xmldsig11#dsa-sha256";
  
  public static final String ALGO_ID_SIGNATURE_RSA = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
  
  public static final String ALGO_ID_SIGNATURE_RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
  
  public static final String ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5 = "http://www.w3.org/2001/04/xmldsig-more#rsa-md5";
  
  public static final String ALGO_ID_SIGNATURE_RSA_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
  
  public static final String ALGO_ID_SIGNATURE_RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
  
  public static final String ALGO_ID_SIGNATURE_RSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
  
  public static final String ALGO_ID_SIGNATURE_RSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
  
  public static final String ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5 = "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
  
  public static final String ALGO_ID_MAC_HMAC_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
  
  public static final String ALGO_ID_MAC_HMAC_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
  
  public static final String ALGO_ID_MAC_HMAC_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
  
  public static final String ALGO_ID_MAC_HMAC_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
  
  public static final String ALGO_ID_SIGNATURE_ECDSA_SHA1 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
  
  public static final String ALGO_ID_SIGNATURE_ECDSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
  
  public static final String ALGO_ID_SIGNATURE_ECDSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
  
  public static final String ALGO_ID_SIGNATURE_ECDSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
  
  private static Logger log = Logger.getLogger(XMLSignature.class.getName());
  
  private SignedInfo signedInfo;
  
  private KeyInfo keyInfo;
  
  private boolean followManifestsDuringValidation = false;
  
  private Element signatureValueElement;
  
  private static final int MODE_SIGN = 0;
  
  private static final int MODE_VERIFY = 1;
  
  private int state = 0;
  
  public XMLSignature(Document paramDocument, String paramString1, String paramString2) throws XMLSecurityException { this(paramDocument, paramString1, paramString2, 0, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"); }
  
  public XMLSignature(Document paramDocument, String paramString1, String paramString2, int paramInt) throws XMLSecurityException { this(paramDocument, paramString1, paramString2, paramInt, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"); }
  
  public XMLSignature(Document paramDocument, String paramString1, String paramString2, String paramString3) throws XMLSecurityException { this(paramDocument, paramString1, paramString2, 0, paramString3); }
  
  public XMLSignature(Document paramDocument, String paramString1, String paramString2, int paramInt, String paramString3) throws XMLSecurityException {
    super(paramDocument);
    String str = getDefaultPrefix("http://www.w3.org/2000/09/xmldsig#");
    if (str == null || str.length() == 0) {
      this.constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
    } else {
      this.constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str, "http://www.w3.org/2000/09/xmldsig#");
    } 
    XMLUtils.addReturnToElement(this.constructionElement);
    this.baseURI = paramString1;
    this.signedInfo = new SignedInfo(this.doc, paramString2, paramInt, paramString3);
    this.constructionElement.appendChild(this.signedInfo.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
    this.signatureValueElement = XMLUtils.createElementInSignatureSpace(this.doc, "SignatureValue");
    this.constructionElement.appendChild(this.signatureValueElement);
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public XMLSignature(Document paramDocument, String paramString, Element paramElement1, Element paramElement2) throws XMLSecurityException {
    super(paramDocument);
    String str = getDefaultPrefix("http://www.w3.org/2000/09/xmldsig#");
    if (str == null || str.length() == 0) {
      this.constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
    } else {
      this.constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str, "http://www.w3.org/2000/09/xmldsig#");
    } 
    XMLUtils.addReturnToElement(this.constructionElement);
    this.baseURI = paramString;
    this.signedInfo = new SignedInfo(this.doc, paramElement1, paramElement2);
    this.constructionElement.appendChild(this.signedInfo.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
    this.signatureValueElement = XMLUtils.createElementInSignatureSpace(this.doc, "SignatureValue");
    this.constructionElement.appendChild(this.signatureValueElement);
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public XMLSignature(Element paramElement, String paramString) throws XMLSignatureException, XMLSecurityException { this(paramElement, paramString, false); }
  
  public XMLSignature(Element paramElement, String paramString, boolean paramBoolean) throws XMLSignatureException, XMLSecurityException {
    super(paramElement, paramString);
    Element element1 = XMLUtils.getNextElement(paramElement.getFirstChild());
    if (element1 == null) {
      Object[] arrayOfObject = { "SignedInfo", "Signature" };
      throw new XMLSignatureException("xml.WrongContent", arrayOfObject);
    } 
    this.signedInfo = new SignedInfo(element1, paramString, paramBoolean);
    element1 = XMLUtils.getNextElement(paramElement.getFirstChild());
    this.signatureValueElement = XMLUtils.getNextElement(element1.getNextSibling());
    if (this.signatureValueElement == null) {
      Object[] arrayOfObject = { "SignatureValue", "Signature" };
      throw new XMLSignatureException("xml.WrongContent", arrayOfObject);
    } 
    Attr attr = this.signatureValueElement.getAttributeNodeNS(null, "Id");
    if (attr != null)
      this.signatureValueElement.setIdAttributeNode(attr, true); 
    Element element2 = XMLUtils.getNextElement(this.signatureValueElement.getNextSibling());
    if (element2 != null && element2.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") && element2.getLocalName().equals("KeyInfo")) {
      this.keyInfo = new KeyInfo(element2, paramString);
      this.keyInfo.setSecureValidation(paramBoolean);
    } 
    for (Element element3 = XMLUtils.getNextElement(this.signatureValueElement.getNextSibling()); element3 != null; element3 = XMLUtils.getNextElement(element3.getNextSibling())) {
      Attr attr1 = element3.getAttributeNodeNS(null, "Id");
      if (attr1 != null)
        element3.setIdAttributeNode(attr1, true); 
      NodeList nodeList = element3.getChildNodes();
      int i = nodeList.getLength();
      for (byte b = 0; b < i; b++) {
        Node node = nodeList.item(b);
        if (node.getNodeType() == 1) {
          Element element = (Element)node;
          String str = element.getLocalName();
          if (str.equals("Manifest")) {
            new Manifest(element, paramString);
          } else if (str.equals("SignatureProperties")) {
            new SignatureProperties(element, paramString);
          } 
        } 
      } 
    } 
    this.state = 1;
  }
  
  public void setId(String paramString) {
    if (paramString != null) {
      this.constructionElement.setAttributeNS(null, "Id", paramString);
      this.constructionElement.setIdAttributeNS(null, "Id", true);
    } 
  }
  
  public String getId() { return this.constructionElement.getAttributeNS(null, "Id"); }
  
  public SignedInfo getSignedInfo() { return this.signedInfo; }
  
  public byte[] getSignatureValue() throws XMLSignatureException {
    try {
      return Base64.decode(this.signatureValueElement);
    } catch (Base64DecodingException base64DecodingException) {
      throw new XMLSignatureException("empty", base64DecodingException);
    } 
  }
  
  private void setSignatureValueElement(byte[] paramArrayOfByte) {
    while (this.signatureValueElement.hasChildNodes())
      this.signatureValueElement.removeChild(this.signatureValueElement.getFirstChild()); 
    String str = Base64.encode(paramArrayOfByte);
    if (str.length() > 76 && !XMLUtils.ignoreLineBreaks())
      str = "\n" + str + "\n"; 
    Text text = this.doc.createTextNode(str);
    this.signatureValueElement.appendChild(text);
  }
  
  public KeyInfo getKeyInfo() {
    if (this.state == 0 && this.keyInfo == null) {
      this.keyInfo = new KeyInfo(this.doc);
      Element element1 = this.keyInfo.getElement();
      Element element2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "Object", 0);
      if (element2 != null) {
        this.constructionElement.insertBefore(element1, element2);
        XMLUtils.addReturnBeforeChild(this.constructionElement, element2);
      } else {
        this.constructionElement.appendChild(element1);
        XMLUtils.addReturnToElement(this.constructionElement);
      } 
    } 
    return this.keyInfo;
  }
  
  public void appendObject(ObjectContainer paramObjectContainer) throws XMLSignatureException {
    this.constructionElement.appendChild(paramObjectContainer.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public ObjectContainer getObjectItem(int paramInt) {
    Element element = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "Object", paramInt);
    try {
      return new ObjectContainer(element, this.baseURI);
    } catch (XMLSecurityException xMLSecurityException) {
      return null;
    } 
  }
  
  public int getObjectLength() { return length("http://www.w3.org/2000/09/xmldsig#", "Object"); }
  
  public void sign(Key paramKey) throws XMLSignatureException {
    if (paramKey instanceof PublicKey)
      throw new IllegalArgumentException(I18n.translate("algorithms.operationOnlyVerification")); 
    try {
      SignedInfo signedInfo1 = getSignedInfo();
      SignatureAlgorithm signatureAlgorithm = signedInfo1.getSignatureAlgorithm();
      unsyncBufferedOutputStream = null;
      try {
        signatureAlgorithm.initSign(paramKey);
        signedInfo1.generateDigestValues();
        unsyncBufferedOutputStream = new UnsyncBufferedOutputStream(new SignerOutputStream(signatureAlgorithm));
        signedInfo1.signInOctetStream(unsyncBufferedOutputStream);
      } catch (XMLSecurityException xMLSecurityException) {
        throw xMLSecurityException;
      } finally {
        if (unsyncBufferedOutputStream != null)
          try {
            unsyncBufferedOutputStream.close();
          } catch (IOException iOException) {
            if (log.isLoggable(Level.FINE))
              log.log(Level.FINE, iOException.getMessage(), iOException); 
          }  
      } 
      setSignatureValueElement(signatureAlgorithm.sign());
    } catch (XMLSignatureException xMLSignatureException) {
      throw xMLSignatureException;
    } catch (CanonicalizationException canonicalizationException) {
      throw new XMLSignatureException("empty", canonicalizationException);
    } catch (InvalidCanonicalizerException invalidCanonicalizerException) {
      throw new XMLSignatureException("empty", invalidCanonicalizerException);
    } catch (XMLSecurityException xMLSecurityException) {
      throw new XMLSignatureException("empty", xMLSecurityException);
    } 
  }
  
  public void addResourceResolver(ResourceResolver paramResourceResolver) { getSignedInfo().addResourceResolver(paramResourceResolver); }
  
  public void addResourceResolver(ResourceResolverSpi paramResourceResolverSpi) { getSignedInfo().addResourceResolver(paramResourceResolverSpi); }
  
  public boolean checkSignatureValue(X509Certificate paramX509Certificate) throws XMLSignatureException {
    if (paramX509Certificate != null)
      return checkSignatureValue(paramX509Certificate.getPublicKey()); 
    Object[] arrayOfObject = { "Didn't get a certificate" };
    throw new XMLSignatureException("empty", arrayOfObject);
  }
  
  public boolean checkSignatureValue(Key paramKey) throws XMLSignatureException {
    if (paramKey == null) {
      Object[] arrayOfObject = { "Didn't get a key" };
      throw new XMLSignatureException("empty", arrayOfObject);
    } 
    try {
      SignedInfo signedInfo1 = getSignedInfo();
      SignatureAlgorithm signatureAlgorithm = signedInfo1.getSignatureAlgorithm();
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "signatureMethodURI = " + signatureAlgorithm.getAlgorithmURI());
        log.log(Level.FINE, "jceSigAlgorithm    = " + signatureAlgorithm.getJCEAlgorithmString());
        log.log(Level.FINE, "jceSigProvider     = " + signatureAlgorithm.getJCEProviderName());
        log.log(Level.FINE, "PublicKey = " + paramKey);
      } 
      byte[] arrayOfByte = null;
      try {
        signatureAlgorithm.initVerify(paramKey);
        SignerOutputStream signerOutputStream = new SignerOutputStream(signatureAlgorithm);
        UnsyncBufferedOutputStream unsyncBufferedOutputStream = new UnsyncBufferedOutputStream(signerOutputStream);
        signedInfo1.signInOctetStream(unsyncBufferedOutputStream);
        unsyncBufferedOutputStream.close();
        arrayOfByte = getSignatureValue();
      } catch (IOException iOException) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, iOException.getMessage(), iOException); 
      } catch (XMLSecurityException xMLSecurityException) {
        throw xMLSecurityException;
      } 
      if (!signatureAlgorithm.verify(arrayOfByte)) {
        log.log(Level.WARNING, "Signature verification failed.");
        return false;
      } 
      return signedInfo1.verify(this.followManifestsDuringValidation);
    } catch (XMLSignatureException xMLSignatureException) {
      throw xMLSignatureException;
    } catch (XMLSecurityException xMLSecurityException) {
      throw new XMLSignatureException("empty", xMLSecurityException);
    } 
  }
  
  public void addDocument(String paramString1, Transforms paramTransforms, String paramString2, String paramString3, String paramString4) throws XMLSignatureException { this.signedInfo.addDocument(this.baseURI, paramString1, paramTransforms, paramString2, paramString3, paramString4); }
  
  public void addDocument(String paramString1, Transforms paramTransforms, String paramString2) throws XMLSignatureException { this.signedInfo.addDocument(this.baseURI, paramString1, paramTransforms, paramString2, null, null); }
  
  public void addDocument(String paramString, Transforms paramTransforms) throws XMLSignatureException { this.signedInfo.addDocument(this.baseURI, paramString, paramTransforms, "http://www.w3.org/2000/09/xmldsig#sha1", null, null); }
  
  public void addDocument(String paramString) { this.signedInfo.addDocument(this.baseURI, paramString, null, "http://www.w3.org/2000/09/xmldsig#sha1", null, null); }
  
  public void addKeyInfo(X509Certificate paramX509Certificate) throws XMLSecurityException {
    X509Data x509Data = new X509Data(this.doc);
    x509Data.addCertificate(paramX509Certificate);
    getKeyInfo().add(x509Data);
  }
  
  public void addKeyInfo(PublicKey paramPublicKey) { getKeyInfo().add(paramPublicKey); }
  
  public SecretKey createSecretKey(byte[] paramArrayOfByte) { return getSignedInfo().createSecretKey(paramArrayOfByte); }
  
  public void setFollowNestedManifests(boolean paramBoolean) { this.followManifestsDuringValidation = paramBoolean; }
  
  public String getBaseLocalName() { return "Signature"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\signature\XMLSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */