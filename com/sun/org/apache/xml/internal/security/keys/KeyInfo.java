package com.sun.org.apache.xml.internal.security.keys;

import com.sun.org.apache.xml.internal.security.encryption.EncryptedKey;
import com.sun.org.apache.xml.internal.security.encryption.XMLCipher;
import com.sun.org.apache.xml.internal.security.encryption.XMLEncryptionException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.DEREncodedKeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.KeyInfoReference;
import com.sun.org.apache.xml.internal.security.keys.content.KeyName;
import com.sun.org.apache.xml.internal.security.keys.content.KeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.MgmtData;
import com.sun.org.apache.xml.internal.security.keys.content.PGPData;
import com.sun.org.apache.xml.internal.security.keys.content.RetrievalMethod;
import com.sun.org.apache.xml.internal.security.keys.content.SPKIData;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.DSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.RSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class KeyInfo extends SignatureElementProxy {
  private static Logger log = Logger.getLogger(KeyInfo.class.getName());
  
  private List<X509Data> x509Datas = null;
  
  private List<EncryptedKey> encryptedKeys = null;
  
  private static final List<StorageResolver> nullList;
  
  private List<StorageResolver> storageResolvers = nullList;
  
  private List<KeyResolverSpi> internalKeyResolvers = new ArrayList();
  
  private boolean secureValidation;
  
  public KeyInfo(Document paramDocument) {
    super(paramDocument);
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public KeyInfo(Element paramElement, String paramString) throws XMLSecurityException {
    super(paramElement, paramString);
    Attr attr = paramElement.getAttributeNodeNS(null, "Id");
    if (attr != null)
      paramElement.setIdAttributeNode(attr, true); 
  }
  
  public void setSecureValidation(boolean paramBoolean) { this.secureValidation = paramBoolean; }
  
  public void setId(String paramString) {
    if (paramString != null) {
      this.constructionElement.setAttributeNS(null, "Id", paramString);
      this.constructionElement.setIdAttributeNS(null, "Id", true);
    } 
  }
  
  public String getId() { return this.constructionElement.getAttributeNS(null, "Id"); }
  
  public void addKeyName(String paramString) { add(new KeyName(this.doc, paramString)); }
  
  public void add(KeyName paramKeyName) {
    this.constructionElement.appendChild(paramKeyName.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void addKeyValue(PublicKey paramPublicKey) { add(new KeyValue(this.doc, paramPublicKey)); }
  
  public void addKeyValue(Element paramElement) { add(new KeyValue(this.doc, paramElement)); }
  
  public void add(DSAKeyValue paramDSAKeyValue) { add(new KeyValue(this.doc, paramDSAKeyValue)); }
  
  public void add(RSAKeyValue paramRSAKeyValue) { add(new KeyValue(this.doc, paramRSAKeyValue)); }
  
  public void add(PublicKey paramPublicKey) { add(new KeyValue(this.doc, paramPublicKey)); }
  
  public void add(KeyValue paramKeyValue) {
    this.constructionElement.appendChild(paramKeyValue.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void addMgmtData(String paramString) { add(new MgmtData(this.doc, paramString)); }
  
  public void add(MgmtData paramMgmtData) {
    this.constructionElement.appendChild(paramMgmtData.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void add(PGPData paramPGPData) {
    this.constructionElement.appendChild(paramPGPData.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void addRetrievalMethod(String paramString1, Transforms paramTransforms, String paramString2) { add(new RetrievalMethod(this.doc, paramString1, paramTransforms, paramString2)); }
  
  public void add(RetrievalMethod paramRetrievalMethod) {
    this.constructionElement.appendChild(paramRetrievalMethod.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void add(SPKIData paramSPKIData) {
    this.constructionElement.appendChild(paramSPKIData.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void add(X509Data paramX509Data) {
    if (this.x509Datas == null)
      this.x509Datas = new ArrayList(); 
    this.x509Datas.add(paramX509Data);
    this.constructionElement.appendChild(paramX509Data.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void add(EncryptedKey paramEncryptedKey) throws XMLEncryptionException {
    if (this.encryptedKeys == null)
      this.encryptedKeys = new ArrayList(); 
    this.encryptedKeys.add(paramEncryptedKey);
    XMLCipher xMLCipher = XMLCipher.getInstance();
    this.constructionElement.appendChild(xMLCipher.martial(paramEncryptedKey));
  }
  
  public void addDEREncodedKeyValue(PublicKey paramPublicKey) { add(new DEREncodedKeyValue(this.doc, paramPublicKey)); }
  
  public void add(DEREncodedKeyValue paramDEREncodedKeyValue) {
    this.constructionElement.appendChild(paramDEREncodedKeyValue.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void addKeyInfoReference(String paramString) { add(new KeyInfoReference(this.doc, paramString)); }
  
  public void add(KeyInfoReference paramKeyInfoReference) {
    this.constructionElement.appendChild(paramKeyInfoReference.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void addUnknownElement(Element paramElement) {
    this.constructionElement.appendChild(paramElement);
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public int lengthKeyName() { return length("http://www.w3.org/2000/09/xmldsig#", "KeyName"); }
  
  public int lengthKeyValue() { return length("http://www.w3.org/2000/09/xmldsig#", "KeyValue"); }
  
  public int lengthMgmtData() { return length("http://www.w3.org/2000/09/xmldsig#", "MgmtData"); }
  
  public int lengthPGPData() { return length("http://www.w3.org/2000/09/xmldsig#", "PGPData"); }
  
  public int lengthRetrievalMethod() { return length("http://www.w3.org/2000/09/xmldsig#", "RetrievalMethod"); }
  
  public int lengthSPKIData() { return length("http://www.w3.org/2000/09/xmldsig#", "SPKIData"); }
  
  public int lengthX509Data() { return (this.x509Datas != null) ? this.x509Datas.size() : length("http://www.w3.org/2000/09/xmldsig#", "X509Data"); }
  
  public int lengthDEREncodedKeyValue() { return length("http://www.w3.org/2009/xmldsig11#", "DEREncodedKeyValue"); }
  
  public int lengthKeyInfoReference() { return length("http://www.w3.org/2009/xmldsig11#", "KeyInfoReference"); }
  
  public int lengthUnknownElement() {
    byte b1 = 0;
    NodeList nodeList = this.constructionElement.getChildNodes();
    for (byte b2 = 0; b2 < nodeList.getLength(); b2++) {
      Node node = nodeList.item(b2);
      if (node.getNodeType() == 1 && node.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#"))
        b1++; 
    } 
    return b1;
  }
  
  public KeyName itemKeyName(int paramInt) throws XMLSecurityException {
    Element element = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "KeyName", paramInt);
    return (element != null) ? new KeyName(element, this.baseURI) : null;
  }
  
  public KeyValue itemKeyValue(int paramInt) throws XMLSecurityException {
    Element element = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "KeyValue", paramInt);
    return (element != null) ? new KeyValue(element, this.baseURI) : null;
  }
  
  public MgmtData itemMgmtData(int paramInt) throws XMLSecurityException {
    Element element = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "MgmtData", paramInt);
    return (element != null) ? new MgmtData(element, this.baseURI) : null;
  }
  
  public PGPData itemPGPData(int paramInt) throws XMLSecurityException {
    Element element = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "PGPData", paramInt);
    return (element != null) ? new PGPData(element, this.baseURI) : null;
  }
  
  public RetrievalMethod itemRetrievalMethod(int paramInt) throws XMLSecurityException {
    Element element = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "RetrievalMethod", paramInt);
    return (element != null) ? new RetrievalMethod(element, this.baseURI) : null;
  }
  
  public SPKIData itemSPKIData(int paramInt) throws XMLSecurityException {
    Element element = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "SPKIData", paramInt);
    return (element != null) ? new SPKIData(element, this.baseURI) : null;
  }
  
  public X509Data itemX509Data(int paramInt) throws XMLSecurityException {
    if (this.x509Datas != null)
      return (X509Data)this.x509Datas.get(paramInt); 
    Element element = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "X509Data", paramInt);
    return (element != null) ? new X509Data(element, this.baseURI) : null;
  }
  
  public EncryptedKey itemEncryptedKey(int paramInt) throws XMLSecurityException {
    if (this.encryptedKeys != null)
      return (EncryptedKey)this.encryptedKeys.get(paramInt); 
    Element element = XMLUtils.selectXencNode(this.constructionElement.getFirstChild(), "EncryptedKey", paramInt);
    if (element != null) {
      XMLCipher xMLCipher = XMLCipher.getInstance();
      xMLCipher.init(4, null);
      return xMLCipher.loadEncryptedKey(element);
    } 
    return null;
  }
  
  public DEREncodedKeyValue itemDEREncodedKeyValue(int paramInt) throws XMLSecurityException {
    Element element = XMLUtils.selectDs11Node(this.constructionElement.getFirstChild(), "DEREncodedKeyValue", paramInt);
    return (element != null) ? new DEREncodedKeyValue(element, this.baseURI) : null;
  }
  
  public KeyInfoReference itemKeyInfoReference(int paramInt) throws XMLSecurityException {
    Element element = XMLUtils.selectDs11Node(this.constructionElement.getFirstChild(), "KeyInfoReference", paramInt);
    return (element != null) ? new KeyInfoReference(element, this.baseURI) : null;
  }
  
  public Element itemUnknownElement(int paramInt) {
    NodeList nodeList = this.constructionElement.getChildNodes();
    byte b1 = 0;
    for (byte b2 = 0; b2 < nodeList.getLength(); b2++) {
      Node node = nodeList.item(b2);
      if (node.getNodeType() == 1 && node.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") && ++b1 == paramInt)
        return (Element)node; 
    } 
    return null;
  }
  
  public boolean isEmpty() { return (this.constructionElement.getFirstChild() == null); }
  
  public boolean containsKeyName() { return (lengthKeyName() > 0); }
  
  public boolean containsKeyValue() { return (lengthKeyValue() > 0); }
  
  public boolean containsMgmtData() { return (lengthMgmtData() > 0); }
  
  public boolean containsPGPData() { return (lengthPGPData() > 0); }
  
  public boolean containsRetrievalMethod() { return (lengthRetrievalMethod() > 0); }
  
  public boolean containsSPKIData() { return (lengthSPKIData() > 0); }
  
  public boolean containsUnknownElement() { return (lengthUnknownElement() > 0); }
  
  public boolean containsX509Data() { return (lengthX509Data() > 0); }
  
  public boolean containsDEREncodedKeyValue() { return (lengthDEREncodedKeyValue() > 0); }
  
  public boolean containsKeyInfoReference() { return (lengthKeyInfoReference() > 0); }
  
  public PublicKey getPublicKey() throws KeyResolverException {
    PublicKey publicKey = getPublicKeyFromInternalResolvers();
    if (publicKey != null) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "I could find a key using the per-KeyInfo key resolvers"); 
      return publicKey;
    } 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "I couldn't find a key using the per-KeyInfo key resolvers"); 
    publicKey = getPublicKeyFromStaticResolvers();
    if (publicKey != null) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "I could find a key using the system-wide key resolvers"); 
      return publicKey;
    } 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "I couldn't find a key using the system-wide key resolvers"); 
    return null;
  }
  
  PublicKey getPublicKeyFromStaticResolvers() throws KeyResolverException {
    for (KeyResolverSpi keyResolverSpi : KeyResolver) {
      keyResolverSpi.setSecureValidation(this.secureValidation);
      Node node = this.constructionElement.getFirstChild();
      String str = getBaseURI();
      while (node != null) {
        if (node.getNodeType() == 1)
          for (StorageResolver storageResolver : this.storageResolvers) {
            PublicKey publicKey = keyResolverSpi.engineLookupAndResolvePublicKey((Element)node, str, storageResolver);
            if (publicKey != null)
              return publicKey; 
          }  
        node = node.getNextSibling();
      } 
    } 
    return null;
  }
  
  PublicKey getPublicKeyFromInternalResolvers() throws KeyResolverException {
    for (KeyResolverSpi keyResolverSpi : this.internalKeyResolvers) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Try " + keyResolverSpi.getClass().getName()); 
      keyResolverSpi.setSecureValidation(this.secureValidation);
      Node node = this.constructionElement.getFirstChild();
      String str = getBaseURI();
      while (node != null) {
        if (node.getNodeType() == 1)
          for (StorageResolver storageResolver : this.storageResolvers) {
            PublicKey publicKey = keyResolverSpi.engineLookupAndResolvePublicKey((Element)node, str, storageResolver);
            if (publicKey != null)
              return publicKey; 
          }  
        node = node.getNextSibling();
      } 
    } 
    return null;
  }
  
  public X509Certificate getX509Certificate() throws KeyResolverException {
    X509Certificate x509Certificate = getX509CertificateFromInternalResolvers();
    if (x509Certificate != null) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "I could find a X509Certificate using the per-KeyInfo key resolvers"); 
      return x509Certificate;
    } 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "I couldn't find a X509Certificate using the per-KeyInfo key resolvers"); 
    x509Certificate = getX509CertificateFromStaticResolvers();
    if (x509Certificate != null) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "I could find a X509Certificate using the system-wide key resolvers"); 
      return x509Certificate;
    } 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "I couldn't find a X509Certificate using the system-wide key resolvers"); 
    return null;
  }
  
  X509Certificate getX509CertificateFromStaticResolvers() throws KeyResolverException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Start getX509CertificateFromStaticResolvers() with " + KeyResolver.length() + " resolvers"); 
    String str = getBaseURI();
    for (KeyResolverSpi keyResolverSpi : KeyResolver) {
      keyResolverSpi.setSecureValidation(this.secureValidation);
      X509Certificate x509Certificate = applyCurrentResolver(str, keyResolverSpi);
      if (x509Certificate != null)
        return x509Certificate; 
    } 
    return null;
  }
  
  private X509Certificate applyCurrentResolver(String paramString, KeyResolverSpi paramKeyResolverSpi) throws KeyResolverException {
    for (Node node = this.constructionElement.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node.getNodeType() == 1)
        for (StorageResolver storageResolver : this.storageResolvers) {
          X509Certificate x509Certificate = paramKeyResolverSpi.engineLookupResolveX509Certificate((Element)node, paramString, storageResolver);
          if (x509Certificate != null)
            return x509Certificate; 
        }  
    } 
    return null;
  }
  
  X509Certificate getX509CertificateFromInternalResolvers() throws KeyResolverException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Start getX509CertificateFromInternalResolvers() with " + lengthInternalKeyResolver() + " resolvers"); 
    String str = getBaseURI();
    for (KeyResolverSpi keyResolverSpi : this.internalKeyResolvers) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Try " + keyResolverSpi.getClass().getName()); 
      keyResolverSpi.setSecureValidation(this.secureValidation);
      X509Certificate x509Certificate = applyCurrentResolver(str, keyResolverSpi);
      if (x509Certificate != null)
        return x509Certificate; 
    } 
    return null;
  }
  
  public SecretKey getSecretKey() throws KeyResolverException {
    SecretKey secretKey = getSecretKeyFromInternalResolvers();
    if (secretKey != null) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "I could find a secret key using the per-KeyInfo key resolvers"); 
      return secretKey;
    } 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "I couldn't find a secret key using the per-KeyInfo key resolvers"); 
    secretKey = getSecretKeyFromStaticResolvers();
    if (secretKey != null) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "I could find a secret key using the system-wide key resolvers"); 
      return secretKey;
    } 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "I couldn't find a secret key using the system-wide key resolvers"); 
    return null;
  }
  
  SecretKey getSecretKeyFromStaticResolvers() throws KeyResolverException {
    for (KeyResolverSpi keyResolverSpi : KeyResolver) {
      keyResolverSpi.setSecureValidation(this.secureValidation);
      Node node = this.constructionElement.getFirstChild();
      String str = getBaseURI();
      while (node != null) {
        if (node.getNodeType() == 1)
          for (StorageResolver storageResolver : this.storageResolvers) {
            SecretKey secretKey = keyResolverSpi.engineLookupAndResolveSecretKey((Element)node, str, storageResolver);
            if (secretKey != null)
              return secretKey; 
          }  
        node = node.getNextSibling();
      } 
    } 
    return null;
  }
  
  SecretKey getSecretKeyFromInternalResolvers() throws KeyResolverException {
    for (KeyResolverSpi keyResolverSpi : this.internalKeyResolvers) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Try " + keyResolverSpi.getClass().getName()); 
      keyResolverSpi.setSecureValidation(this.secureValidation);
      Node node = this.constructionElement.getFirstChild();
      String str = getBaseURI();
      while (node != null) {
        if (node.getNodeType() == 1)
          for (StorageResolver storageResolver : this.storageResolvers) {
            SecretKey secretKey = keyResolverSpi.engineLookupAndResolveSecretKey((Element)node, str, storageResolver);
            if (secretKey != null)
              return secretKey; 
          }  
        node = node.getNextSibling();
      } 
    } 
    return null;
  }
  
  public PrivateKey getPrivateKey() throws KeyResolverException {
    PrivateKey privateKey = getPrivateKeyFromInternalResolvers();
    if (privateKey != null) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "I could find a private key using the per-KeyInfo key resolvers"); 
      return privateKey;
    } 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "I couldn't find a secret key using the per-KeyInfo key resolvers"); 
    privateKey = getPrivateKeyFromStaticResolvers();
    if (privateKey != null) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "I could find a private key using the system-wide key resolvers"); 
      return privateKey;
    } 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "I couldn't find a private key using the system-wide key resolvers"); 
    return null;
  }
  
  PrivateKey getPrivateKeyFromStaticResolvers() throws KeyResolverException {
    for (KeyResolverSpi keyResolverSpi : KeyResolver) {
      keyResolverSpi.setSecureValidation(this.secureValidation);
      Node node = this.constructionElement.getFirstChild();
      String str = getBaseURI();
      while (node != null) {
        if (node.getNodeType() == 1) {
          PrivateKey privateKey = keyResolverSpi.engineLookupAndResolvePrivateKey((Element)node, str, null);
          if (privateKey != null)
            return privateKey; 
        } 
        node = node.getNextSibling();
      } 
    } 
    return null;
  }
  
  PrivateKey getPrivateKeyFromInternalResolvers() throws KeyResolverException {
    for (KeyResolverSpi keyResolverSpi : this.internalKeyResolvers) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Try " + keyResolverSpi.getClass().getName()); 
      keyResolverSpi.setSecureValidation(this.secureValidation);
      Node node = this.constructionElement.getFirstChild();
      String str = getBaseURI();
      while (node != null) {
        if (node.getNodeType() == 1) {
          PrivateKey privateKey = keyResolverSpi.engineLookupAndResolvePrivateKey((Element)node, str, null);
          if (privateKey != null)
            return privateKey; 
        } 
        node = node.getNextSibling();
      } 
    } 
    return null;
  }
  
  public void registerInternalKeyResolver(KeyResolverSpi paramKeyResolverSpi) { this.internalKeyResolvers.add(paramKeyResolverSpi); }
  
  int lengthInternalKeyResolver() { return this.internalKeyResolvers.size(); }
  
  KeyResolverSpi itemInternalKeyResolver(int paramInt) { return (KeyResolverSpi)this.internalKeyResolvers.get(paramInt); }
  
  public void addStorageResolver(StorageResolver paramStorageResolver) {
    if (this.storageResolvers == nullList)
      this.storageResolvers = new ArrayList(); 
    this.storageResolvers.add(paramStorageResolver);
  }
  
  public String getBaseLocalName() { return "KeyInfo"; }
  
  static  {
    ArrayList arrayList = new ArrayList(1);
    arrayList.add(null);
    nullList = Collections.unmodifiableList(arrayList);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\KeyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */