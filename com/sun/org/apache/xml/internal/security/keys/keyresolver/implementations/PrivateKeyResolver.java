package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509IssuerSerial;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SubjectName;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class PrivateKeyResolver extends KeyResolverSpi {
  private static Logger log = Logger.getLogger(PrivateKeyResolver.class.getName());
  
  private KeyStore keyStore;
  
  private char[] password;
  
  public PrivateKeyResolver(KeyStore paramKeyStore, char[] paramArrayOfChar) {
    this.keyStore = paramKeyStore;
    this.password = paramArrayOfChar;
  }
  
  public boolean engineCanResolve(Element paramElement, String paramString, StorageResolver paramStorageResolver) { return (XMLUtils.elementIsInSignatureSpace(paramElement, "X509Data") || XMLUtils.elementIsInSignatureSpace(paramElement, "KeyName")); }
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException { return null; }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException { return null; }
  
  public SecretKey engineResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException { return null; }
  
  public PrivateKey engineLookupAndResolvePrivateKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName() + "?"); 
    if (XMLUtils.elementIsInSignatureSpace(paramElement, "X509Data")) {
      PrivateKey privateKey = resolveX509Data(paramElement, paramString);
      if (privateKey != null)
        return privateKey; 
    } else if (XMLUtils.elementIsInSignatureSpace(paramElement, "KeyName")) {
      log.log(Level.FINE, "Can I resolve KeyName?");
      String str = paramElement.getFirstChild().getNodeValue();
      try {
        Key key = this.keyStore.getKey(str, this.password);
        if (key instanceof PrivateKey)
          return (PrivateKey)key; 
      } catch (Exception exception) {
        log.log(Level.FINE, "Cannot recover the key", exception);
      } 
    } 
    log.log(Level.FINE, "I can't");
    return null;
  }
  
  private PrivateKey resolveX509Data(Element paramElement, String paramString) {
    log.log(Level.FINE, "Can I resolve X509Data?");
    try {
      X509Data x509Data = new X509Data(paramElement, paramString);
      int i = x509Data.lengthSKI();
      byte b;
      for (b = 0; b < i; b++) {
        XMLX509SKI xMLX509SKI = x509Data.itemSKI(b);
        PrivateKey privateKey = resolveX509SKI(xMLX509SKI);
        if (privateKey != null)
          return privateKey; 
      } 
      i = x509Data.lengthIssuerSerial();
      for (b = 0; b < i; b++) {
        XMLX509IssuerSerial xMLX509IssuerSerial = x509Data.itemIssuerSerial(b);
        PrivateKey privateKey = resolveX509IssuerSerial(xMLX509IssuerSerial);
        if (privateKey != null)
          return privateKey; 
      } 
      i = x509Data.lengthSubjectName();
      for (b = 0; b < i; b++) {
        XMLX509SubjectName xMLX509SubjectName = x509Data.itemSubjectName(b);
        PrivateKey privateKey = resolveX509SubjectName(xMLX509SubjectName);
        if (privateKey != null)
          return privateKey; 
      } 
      i = x509Data.lengthCertificate();
      for (b = 0; b < i; b++) {
        XMLX509Certificate xMLX509Certificate = x509Data.itemCertificate(b);
        PrivateKey privateKey = resolveX509Certificate(xMLX509Certificate);
        if (privateKey != null)
          return privateKey; 
      } 
    } catch (XMLSecurityException xMLSecurityException) {
      log.log(Level.FINE, "XMLSecurityException", xMLSecurityException);
    } catch (KeyStoreException keyStoreException) {
      log.log(Level.FINE, "KeyStoreException", keyStoreException);
    } 
    return null;
  }
  
  private PrivateKey resolveX509SKI(XMLX509SKI paramXMLX509SKI) throws XMLSecurityException, KeyStoreException {
    log.log(Level.FINE, "Can I resolve X509SKI?");
    Enumeration enumeration = this.keyStore.aliases();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      if (this.keyStore.isKeyEntry(str)) {
        Certificate certificate = this.keyStore.getCertificate(str);
        if (certificate instanceof X509Certificate) {
          XMLX509SKI xMLX509SKI = new XMLX509SKI(paramXMLX509SKI.getDocument(), (X509Certificate)certificate);
          if (xMLX509SKI.equals(paramXMLX509SKI)) {
            log.log(Level.FINE, "match !!! ");
            try {
              Key key = this.keyStore.getKey(str, this.password);
              if (key instanceof PrivateKey)
                return (PrivateKey)key; 
            } catch (Exception exception) {
              log.log(Level.FINE, "Cannot recover the key", exception);
            } 
          } 
        } 
      } 
    } 
    return null;
  }
  
  private PrivateKey resolveX509IssuerSerial(XMLX509IssuerSerial paramXMLX509IssuerSerial) throws KeyStoreException {
    log.log(Level.FINE, "Can I resolve X509IssuerSerial?");
    Enumeration enumeration = this.keyStore.aliases();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      if (this.keyStore.isKeyEntry(str)) {
        Certificate certificate = this.keyStore.getCertificate(str);
        if (certificate instanceof X509Certificate) {
          XMLX509IssuerSerial xMLX509IssuerSerial = new XMLX509IssuerSerial(paramXMLX509IssuerSerial.getDocument(), (X509Certificate)certificate);
          if (xMLX509IssuerSerial.equals(paramXMLX509IssuerSerial)) {
            log.log(Level.FINE, "match !!! ");
            try {
              Key key = this.keyStore.getKey(str, this.password);
              if (key instanceof PrivateKey)
                return (PrivateKey)key; 
            } catch (Exception exception) {
              log.log(Level.FINE, "Cannot recover the key", exception);
            } 
          } 
        } 
      } 
    } 
    return null;
  }
  
  private PrivateKey resolveX509SubjectName(XMLX509SubjectName paramXMLX509SubjectName) throws KeyStoreException {
    log.log(Level.FINE, "Can I resolve X509SubjectName?");
    Enumeration enumeration = this.keyStore.aliases();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      if (this.keyStore.isKeyEntry(str)) {
        Certificate certificate = this.keyStore.getCertificate(str);
        if (certificate instanceof X509Certificate) {
          XMLX509SubjectName xMLX509SubjectName = new XMLX509SubjectName(paramXMLX509SubjectName.getDocument(), (X509Certificate)certificate);
          if (xMLX509SubjectName.equals(paramXMLX509SubjectName)) {
            log.log(Level.FINE, "match !!! ");
            try {
              Key key = this.keyStore.getKey(str, this.password);
              if (key instanceof PrivateKey)
                return (PrivateKey)key; 
            } catch (Exception exception) {
              log.log(Level.FINE, "Cannot recover the key", exception);
            } 
          } 
        } 
      } 
    } 
    return null;
  }
  
  private PrivateKey resolveX509Certificate(XMLX509Certificate paramXMLX509Certificate) throws XMLSecurityException, KeyStoreException {
    log.log(Level.FINE, "Can I resolve X509Certificate?");
    byte[] arrayOfByte = paramXMLX509Certificate.getCertificateBytes();
    Enumeration enumeration = this.keyStore.aliases();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      if (this.keyStore.isKeyEntry(str)) {
        Certificate certificate = this.keyStore.getCertificate(str);
        if (certificate instanceof X509Certificate) {
          byte[] arrayOfByte1 = null;
          try {
            arrayOfByte1 = certificate.getEncoded();
          } catch (CertificateEncodingException certificateEncodingException) {}
          if (arrayOfByte1 != null && Arrays.equals(arrayOfByte1, arrayOfByte)) {
            log.log(Level.FINE, "match !!! ");
            try {
              Key key = this.keyStore.getKey(str, this.password);
              if (key instanceof PrivateKey)
                return (PrivateKey)key; 
            } catch (Exception exception) {
              log.log(Level.FINE, "Cannot recover the key", exception);
            } 
          } 
        } 
      } 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\PrivateKeyResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */