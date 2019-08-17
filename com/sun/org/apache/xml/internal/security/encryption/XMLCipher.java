package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.EncryptedKeyResolver;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.transforms.InvalidTransformException;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XMLCipher {
  private static Logger log = Logger.getLogger(XMLCipher.class.getName());
  
  public static final String TRIPLEDES = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
  
  public static final String AES_128 = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
  
  public static final String AES_256 = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
  
  public static final String AES_192 = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";
  
  public static final String AES_128_GCM = "http://www.w3.org/2009/xmlenc11#aes128-gcm";
  
  public static final String AES_192_GCM = "http://www.w3.org/2009/xmlenc11#aes192-gcm";
  
  public static final String AES_256_GCM = "http://www.w3.org/2009/xmlenc11#aes256-gcm";
  
  public static final String RSA_v1dot5 = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
  
  public static final String RSA_OAEP = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";
  
  public static final String RSA_OAEP_11 = "http://www.w3.org/2009/xmlenc11#rsa-oaep";
  
  public static final String DIFFIE_HELLMAN = "http://www.w3.org/2001/04/xmlenc#dh";
  
  public static final String TRIPLEDES_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-tripledes";
  
  public static final String AES_128_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes128";
  
  public static final String AES_256_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes256";
  
  public static final String AES_192_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes192";
  
  public static final String SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
  
  public static final String SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
  
  public static final String SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
  
  public static final String RIPEMD_160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
  
  public static final String XML_DSIG = "http://www.w3.org/2000/09/xmldsig#";
  
  public static final String N14C_XML = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
  
  public static final String N14C_XML_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
  
  public static final String EXCL_XML_N14C = "http://www.w3.org/2001/10/xml-exc-c14n#";
  
  public static final String EXCL_XML_N14C_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
  
  public static final String PHYSICAL_XML_N14C = "http://santuario.apache.org/c14n/physical";
  
  public static final String BASE64_ENCODING = "http://www.w3.org/2000/09/xmldsig#base64";
  
  public static final int ENCRYPT_MODE = 1;
  
  public static final int DECRYPT_MODE = 2;
  
  public static final int UNWRAP_MODE = 4;
  
  public static final int WRAP_MODE = 3;
  
  private static final String ENC_ALGORITHMS = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2009/xmlenc11#rsa-oaep\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\nhttp://www.w3.org/2009/xmlenc11#aes128-gcm\nhttp://www.w3.org/2009/xmlenc11#aes192-gcm\nhttp://www.w3.org/2009/xmlenc11#aes256-gcm\n";
  
  private Cipher contextCipher;
  
  private int cipherMode = Integer.MIN_VALUE;
  
  private String algorithm = null;
  
  private String requestedJCEProvider = null;
  
  private Canonicalizer canon;
  
  private Document contextDocument;
  
  private Factory factory;
  
  private Serializer serializer;
  
  private Key key;
  
  private Key kek;
  
  private EncryptedKey ek;
  
  private EncryptedData ed;
  
  private SecureRandom random;
  
  private boolean secureValidation;
  
  private String digestAlg;
  
  private List<KeyResolverSpi> internalKeyResolvers;
  
  public void setSerializer(Serializer paramSerializer) {
    this.serializer = paramSerializer;
    paramSerializer.setCanonicalizer(this.canon);
  }
  
  public Serializer getSerializer() { return this.serializer; }
  
  private XMLCipher(String paramString1, String paramString2, String paramString3, String paramString4) throws XMLEncryptionException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Constructing XMLCipher..."); 
    this.factory = new Factory(null);
    this.algorithm = paramString1;
    this.requestedJCEProvider = paramString2;
    this.digestAlg = paramString4;
    try {
      if (paramString3 == null) {
        this.canon = Canonicalizer.getInstance("http://santuario.apache.org/c14n/physical");
      } else {
        this.canon = Canonicalizer.getInstance(paramString3);
      } 
    } catch (InvalidCanonicalizerException invalidCanonicalizerException) {
      throw new XMLEncryptionException("empty", invalidCanonicalizerException);
    } 
    if (this.serializer == null)
      this.serializer = new DocumentSerializer(); 
    this.serializer.setCanonicalizer(this.canon);
    if (paramString1 != null)
      this.contextCipher = constructCipher(paramString1, paramString4); 
  }
  
  private static boolean isValidEncryptionAlgorithm(String paramString) { return (paramString.equals("http://www.w3.org/2001/04/xmlenc#tripledes-cbc") || paramString.equals("http://www.w3.org/2001/04/xmlenc#aes128-cbc") || paramString.equals("http://www.w3.org/2001/04/xmlenc#aes256-cbc") || paramString.equals("http://www.w3.org/2001/04/xmlenc#aes192-cbc") || paramString.equals("http://www.w3.org/2009/xmlenc11#aes128-gcm") || paramString.equals("http://www.w3.org/2009/xmlenc11#aes192-gcm") || paramString.equals("http://www.w3.org/2009/xmlenc11#aes256-gcm") || paramString.equals("http://www.w3.org/2001/04/xmlenc#rsa-1_5") || paramString.equals("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p") || paramString.equals("http://www.w3.org/2009/xmlenc11#rsa-oaep") || paramString.equals("http://www.w3.org/2001/04/xmlenc#kw-tripledes") || paramString.equals("http://www.w3.org/2001/04/xmlenc#kw-aes128") || paramString.equals("http://www.w3.org/2001/04/xmlenc#kw-aes256") || paramString.equals("http://www.w3.org/2001/04/xmlenc#kw-aes192")); }
  
  private static void validateTransformation(String paramString) {
    if (null == paramString)
      throw new NullPointerException("Transformation unexpectedly null..."); 
    if (!isValidEncryptionAlgorithm(paramString))
      log.log(Level.WARNING, "Algorithm non-standard, expected one of http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2009/xmlenc11#rsa-oaep\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\nhttp://www.w3.org/2009/xmlenc11#aes128-gcm\nhttp://www.w3.org/2009/xmlenc11#aes192-gcm\nhttp://www.w3.org/2009/xmlenc11#aes256-gcm\n"); 
  }
  
  public static XMLCipher getInstance(String paramString) throws XMLEncryptionException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Getting XMLCipher with transformation"); 
    validateTransformation(paramString);
    return new XMLCipher(paramString, null, null, null);
  }
  
  public static XMLCipher getInstance(String paramString1, String paramString2) throws XMLEncryptionException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Getting XMLCipher with transformation and c14n algorithm"); 
    validateTransformation(paramString1);
    return new XMLCipher(paramString1, null, paramString2, null);
  }
  
  public static XMLCipher getInstance(String paramString1, String paramString2, String paramString3) throws XMLEncryptionException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Getting XMLCipher with transformation and c14n algorithm"); 
    validateTransformation(paramString1);
    return new XMLCipher(paramString1, null, paramString2, paramString3);
  }
  
  public static XMLCipher getProviderInstance(String paramString1, String paramString2) throws XMLEncryptionException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Getting XMLCipher with transformation and provider"); 
    if (null == paramString2)
      throw new NullPointerException("Provider unexpectedly null.."); 
    validateTransformation(paramString1);
    return new XMLCipher(paramString1, paramString2, null, null);
  }
  
  public static XMLCipher getProviderInstance(String paramString1, String paramString2, String paramString3) throws XMLEncryptionException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Getting XMLCipher with transformation, provider and c14n algorithm"); 
    if (null == paramString2)
      throw new NullPointerException("Provider unexpectedly null.."); 
    validateTransformation(paramString1);
    return new XMLCipher(paramString1, paramString2, paramString3, null);
  }
  
  public static XMLCipher getProviderInstance(String paramString1, String paramString2, String paramString3, String paramString4) throws XMLEncryptionException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Getting XMLCipher with transformation, provider and c14n algorithm"); 
    if (null == paramString2)
      throw new NullPointerException("Provider unexpectedly null.."); 
    validateTransformation(paramString1);
    return new XMLCipher(paramString1, paramString2, paramString3, paramString4);
  }
  
  public static XMLCipher getInstance() throws XMLEncryptionException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Getting XMLCipher with no arguments"); 
    return new XMLCipher(null, null, null, null);
  }
  
  public static XMLCipher getProviderInstance(String paramString) throws XMLEncryptionException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Getting XMLCipher with provider"); 
    return new XMLCipher(null, paramString, null, null);
  }
  
  public void init(int paramInt, Key paramKey) throws XMLEncryptionException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Initializing XMLCipher..."); 
    this.ek = null;
    this.ed = null;
    switch (paramInt) {
      case 1:
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "opmode = ENCRYPT_MODE"); 
        this.ed = createEncryptedData(1, "NO VALUE YET");
        break;
      case 2:
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "opmode = DECRYPT_MODE"); 
        break;
      case 3:
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "opmode = WRAP_MODE"); 
        this.ek = createEncryptedKey(1, "NO VALUE YET");
        break;
      case 4:
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "opmode = UNWRAP_MODE"); 
        break;
      default:
        log.log(Level.SEVERE, "Mode unexpectedly invalid");
        throw new XMLEncryptionException("Invalid mode in init");
    } 
    this.cipherMode = paramInt;
    this.key = paramKey;
  }
  
  public void setSecureValidation(boolean paramBoolean) { this.secureValidation = paramBoolean; }
  
  public void registerInternalKeyResolver(KeyResolverSpi paramKeyResolverSpi) {
    if (this.internalKeyResolvers == null)
      this.internalKeyResolvers = new ArrayList(); 
    this.internalKeyResolvers.add(paramKeyResolverSpi);
  }
  
  public EncryptedData getEncryptedData() {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Returning EncryptedData"); 
    return this.ed;
  }
  
  public EncryptedKey getEncryptedKey() {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Returning EncryptedKey"); 
    return this.ek;
  }
  
  public void setKEK(Key paramKey) { this.kek = paramKey; }
  
  public Element martial(EncryptedData paramEncryptedData) { return this.factory.toElement(paramEncryptedData); }
  
  public Element martial(Document paramDocument, EncryptedData paramEncryptedData) {
    this.contextDocument = paramDocument;
    return this.factory.toElement(paramEncryptedData);
  }
  
  public Element martial(EncryptedKey paramEncryptedKey) { return this.factory.toElement(paramEncryptedKey); }
  
  public Element martial(Document paramDocument, EncryptedKey paramEncryptedKey) {
    this.contextDocument = paramDocument;
    return this.factory.toElement(paramEncryptedKey);
  }
  
  public Element martial(ReferenceList paramReferenceList) { return this.factory.toElement(paramReferenceList); }
  
  public Element martial(Document paramDocument, ReferenceList paramReferenceList) {
    this.contextDocument = paramDocument;
    return this.factory.toElement(paramReferenceList);
  }
  
  private Document encryptElement(Element paramElement) throws Exception {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Encrypting element..."); 
    if (null == paramElement)
      log.log(Level.SEVERE, "Element unexpectedly null..."); 
    if (this.cipherMode != 1 && log.isLoggable(Level.FINE))
      log.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE..."); 
    if (this.algorithm == null)
      throw new XMLEncryptionException("XMLCipher instance without transformation specified"); 
    encryptData(this.contextDocument, paramElement, false);
    Element element = this.factory.toElement(this.ed);
    Node node = paramElement.getParentNode();
    node.replaceChild(element, paramElement);
    return this.contextDocument;
  }
  
  private Document encryptElementContent(Element paramElement) throws Exception {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Encrypting element content..."); 
    if (null == paramElement)
      log.log(Level.SEVERE, "Element unexpectedly null..."); 
    if (this.cipherMode != 1 && log.isLoggable(Level.FINE))
      log.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE..."); 
    if (this.algorithm == null)
      throw new XMLEncryptionException("XMLCipher instance without transformation specified"); 
    encryptData(this.contextDocument, paramElement, true);
    Element element = this.factory.toElement(this.ed);
    removeContent(paramElement);
    paramElement.appendChild(element);
    return this.contextDocument;
  }
  
  public Document doFinal(Document paramDocument1, Document paramDocument2) throws Exception {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Processing source document..."); 
    if (null == paramDocument1)
      log.log(Level.SEVERE, "Context document unexpectedly null..."); 
    if (null == paramDocument2)
      log.log(Level.SEVERE, "Source document unexpectedly null..."); 
    this.contextDocument = paramDocument1;
    Document document = null;
    switch (this.cipherMode) {
      case 2:
        document = decryptElement(paramDocument2.getDocumentElement());
      case 1:
        document = encryptElement(paramDocument2.getDocumentElement());
      case 3:
      case 4:
        return document;
    } 
    throw new XMLEncryptionException("empty", new IllegalStateException());
  }
  
  public Document doFinal(Document paramDocument, Element paramElement) throws Exception {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Processing source element..."); 
    if (null == paramDocument)
      log.log(Level.SEVERE, "Context document unexpectedly null..."); 
    if (null == paramElement)
      log.log(Level.SEVERE, "Source element unexpectedly null..."); 
    this.contextDocument = paramDocument;
    Document document = null;
    switch (this.cipherMode) {
      case 2:
        document = decryptElement(paramElement);
      case 1:
        document = encryptElement(paramElement);
      case 3:
      case 4:
        return document;
    } 
    throw new XMLEncryptionException("empty", new IllegalStateException());
  }
  
  public Document doFinal(Document paramDocument, Element paramElement, boolean paramBoolean) throws Exception {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Processing source element..."); 
    if (null == paramDocument)
      log.log(Level.SEVERE, "Context document unexpectedly null..."); 
    if (null == paramElement)
      log.log(Level.SEVERE, "Source element unexpectedly null..."); 
    this.contextDocument = paramDocument;
    Document document = null;
    switch (this.cipherMode) {
      case 2:
        if (paramBoolean) {
          document = decryptElementContent(paramElement);
        } else {
          document = decryptElement(paramElement);
        } 
      case 1:
        if (paramBoolean) {
          document = encryptElementContent(paramElement);
        } else {
          document = encryptElement(paramElement);
        } 
      case 3:
      case 4:
        return document;
    } 
    throw new XMLEncryptionException("empty", new IllegalStateException());
  }
  
  public EncryptedData encryptData(Document paramDocument, Element paramElement) throws Exception { return encryptData(paramDocument, paramElement, false); }
  
  public EncryptedData encryptData(Document paramDocument, String paramString, InputStream paramInputStream) throws Exception {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Encrypting element..."); 
    if (null == paramDocument)
      log.log(Level.SEVERE, "Context document unexpectedly null..."); 
    if (null == paramInputStream)
      log.log(Level.SEVERE, "Serialized data unexpectedly null..."); 
    if (this.cipherMode != 1 && log.isLoggable(Level.FINE))
      log.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE..."); 
    return encryptData(paramDocument, null, paramString, paramInputStream);
  }
  
  public EncryptedData encryptData(Document paramDocument, Element paramElement, boolean paramBoolean) throws Exception {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Encrypting element..."); 
    if (null == paramDocument)
      log.log(Level.SEVERE, "Context document unexpectedly null..."); 
    if (null == paramElement)
      log.log(Level.SEVERE, "Element unexpectedly null..."); 
    if (this.cipherMode != 1 && log.isLoggable(Level.FINE))
      log.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE..."); 
    return paramBoolean ? encryptData(paramDocument, paramElement, "http://www.w3.org/2001/04/xmlenc#Content", null) : encryptData(paramDocument, paramElement, "http://www.w3.org/2001/04/xmlenc#Element", null);
  }
  
  private EncryptedData encryptData(Document paramDocument, Element paramElement, String paramString, InputStream paramInputStream) throws Exception {
    Cipher cipher;
    this.contextDocument = paramDocument;
    if (this.algorithm == null)
      throw new XMLEncryptionException("XMLCipher instance without transformation specified"); 
    byte[] arrayOfByte1 = null;
    if (paramInputStream == null) {
      if (paramString.equals("http://www.w3.org/2001/04/xmlenc#Content")) {
        NodeList nodeList = paramElement.getChildNodes();
        if (null != nodeList) {
          arrayOfByte1 = this.serializer.serializeToByteArray(nodeList);
        } else {
          cipher = new Object[] { "Element has no content." };
          throw new XMLEncryptionException("empty", cipher);
        } 
      } else {
        arrayOfByte1 = this.serializer.serializeToByteArray(paramElement);
      } 
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Serialized octets:\n" + new String(arrayOfByte1, "UTF-8")); 
    } 
    byte[] arrayOfByte2 = null;
    if (this.contextCipher == null) {
      cipher = constructCipher(this.algorithm, null);
    } else {
      cipher = this.contextCipher;
    } 
    try {
      if ("http://www.w3.org/2009/xmlenc11#aes128-gcm".equals(this.algorithm) || "http://www.w3.org/2009/xmlenc11#aes192-gcm".equals(this.algorithm) || "http://www.w3.org/2009/xmlenc11#aes256-gcm".equals(this.algorithm)) {
        if (this.random == null)
          this.random = SecureRandom.getInstance("SHA1PRNG"); 
        byte[] arrayOfByte = new byte[12];
        this.random.nextBytes(arrayOfByte);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(arrayOfByte);
        cipher.init(this.cipherMode, this.key, ivParameterSpec);
      } else {
        cipher.init(this.cipherMode, this.key);
      } 
    } catch (InvalidKeyException invalidKeyException) {
      throw new XMLEncryptionException("empty", invalidKeyException);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new XMLEncryptionException("empty", noSuchAlgorithmException);
    } 
    try {
      if (paramInputStream != null) {
        byte[] arrayOfByte = new byte[8192];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        while ((i = paramInputStream.read(arrayOfByte)) != -1) {
          byte[] arrayOfByte5 = cipher.update(arrayOfByte, 0, i);
          byteArrayOutputStream.write(arrayOfByte5);
        } 
        byteArrayOutputStream.write(cipher.doFinal());
        arrayOfByte2 = byteArrayOutputStream.toByteArray();
      } else {
        arrayOfByte2 = cipher.doFinal(arrayOfByte1);
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "Expected cipher.outputSize = " + Integer.toString(cipher.getOutputSize(arrayOfByte1.length))); 
      } 
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Actual cipher.outputSize = " + Integer.toString(arrayOfByte2.length)); 
    } catch (IllegalStateException illegalStateException) {
      throw new XMLEncryptionException("empty", illegalStateException);
    } catch (IllegalBlockSizeException illegalBlockSizeException) {
      throw new XMLEncryptionException("empty", illegalBlockSizeException);
    } catch (BadPaddingException badPaddingException) {
      throw new XMLEncryptionException("empty", badPaddingException);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new XMLEncryptionException("empty", unsupportedEncodingException);
    } 
    byte[] arrayOfByte3 = cipher.getIV();
    byte[] arrayOfByte4 = new byte[arrayOfByte3.length + arrayOfByte2.length];
    System.arraycopy(arrayOfByte3, 0, arrayOfByte4, 0, arrayOfByte3.length);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte4, arrayOfByte3.length, arrayOfByte2.length);
    String str = Base64.encode(arrayOfByte4);
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Encrypted octets:\n" + str);
      log.log(Level.FINE, "Encrypted octets length = " + str.length());
    } 
    try {
      CipherData cipherData = this.ed.getCipherData();
      CipherValue cipherValue = cipherData.getCipherValue();
      cipherValue.setValue(str);
      if (paramString != null)
        this.ed.setType((new URI(paramString)).toString()); 
      EncryptionMethod encryptionMethod = this.factory.newEncryptionMethod((new URI(this.algorithm)).toString());
      encryptionMethod.setDigestAlgorithm(this.digestAlg);
      this.ed.setEncryptionMethod(encryptionMethod);
    } catch (URISyntaxException uRISyntaxException) {
      throw new XMLEncryptionException("empty", uRISyntaxException);
    } 
    return this.ed;
  }
  
  public EncryptedData loadEncryptedData(Document paramDocument, Element paramElement) throws Exception {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Loading encrypted element..."); 
    if (null == paramDocument)
      throw new NullPointerException("Context document unexpectedly null..."); 
    if (null == paramElement)
      throw new NullPointerException("Element unexpectedly null..."); 
    if (this.cipherMode != 2)
      throw new XMLEncryptionException("XMLCipher unexpectedly not in DECRYPT_MODE..."); 
    this.contextDocument = paramDocument;
    this.ed = this.factory.newEncryptedData(paramElement);
    return this.ed;
  }
  
  public EncryptedKey loadEncryptedKey(Document paramDocument, Element paramElement) throws XMLEncryptionException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Loading encrypted key..."); 
    if (null == paramDocument)
      throw new NullPointerException("Context document unexpectedly null..."); 
    if (null == paramElement)
      throw new NullPointerException("Element unexpectedly null..."); 
    if (this.cipherMode != 4 && this.cipherMode != 2)
      throw new XMLEncryptionException("XMLCipher unexpectedly not in UNWRAP_MODE or DECRYPT_MODE..."); 
    this.contextDocument = paramDocument;
    this.ek = this.factory.newEncryptedKey(paramElement);
    return this.ek;
  }
  
  public EncryptedKey loadEncryptedKey(Element paramElement) throws XMLEncryptionException { return loadEncryptedKey(paramElement.getOwnerDocument(), paramElement); }
  
  public EncryptedKey encryptKey(Document paramDocument, Key paramKey) throws XMLEncryptionException { return encryptKey(paramDocument, paramKey, null, null); }
  
  public EncryptedKey encryptKey(Document paramDocument, Key paramKey, String paramString, byte[] paramArrayOfByte) throws XMLEncryptionException {
    Cipher cipher;
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Encrypting key ..."); 
    if (null == paramKey)
      log.log(Level.SEVERE, "Key unexpectedly null..."); 
    if (this.cipherMode != 3)
      log.log(Level.FINE, "XMLCipher unexpectedly not in WRAP_MODE..."); 
    if (this.algorithm == null)
      throw new XMLEncryptionException("XMLCipher instance without transformation specified"); 
    this.contextDocument = paramDocument;
    byte[] arrayOfByte = null;
    if (this.contextCipher == null) {
      cipher = constructCipher(this.algorithm, null);
    } else {
      cipher = this.contextCipher;
    } 
    try {
      OAEPParameterSpec oAEPParameterSpec = constructOAEPParameters(this.algorithm, this.digestAlg, paramString, paramArrayOfByte);
      if (oAEPParameterSpec == null) {
        cipher.init(3, this.key);
      } else {
        cipher.init(3, this.key, oAEPParameterSpec);
      } 
      arrayOfByte = cipher.wrap(paramKey);
    } catch (InvalidKeyException invalidKeyException) {
      throw new XMLEncryptionException("empty", invalidKeyException);
    } catch (IllegalBlockSizeException illegalBlockSizeException) {
      throw new XMLEncryptionException("empty", illegalBlockSizeException);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new XMLEncryptionException("empty", invalidAlgorithmParameterException);
    } 
    String str = Base64.encode(arrayOfByte);
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Encrypted key octets:\n" + str);
      log.log(Level.FINE, "Encrypted key octets length = " + str.length());
    } 
    CipherValue cipherValue = this.ek.getCipherData().getCipherValue();
    cipherValue.setValue(str);
    try {
      EncryptionMethod encryptionMethod = this.factory.newEncryptionMethod((new URI(this.algorithm)).toString());
      encryptionMethod.setDigestAlgorithm(this.digestAlg);
      encryptionMethod.setMGFAlgorithm(paramString);
      encryptionMethod.setOAEPparams(paramArrayOfByte);
      this.ek.setEncryptionMethod(encryptionMethod);
    } catch (URISyntaxException uRISyntaxException) {
      throw new XMLEncryptionException("empty", uRISyntaxException);
    } 
    return this.ek;
  }
  
  public Key decryptKey(EncryptedKey paramEncryptedKey, String paramString) throws XMLEncryptionException {
    Key key1;
    Cipher cipher;
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Decrypting key from previously loaded EncryptedKey..."); 
    if (this.cipherMode != 4 && log.isLoggable(Level.FINE))
      log.log(Level.FINE, "XMLCipher unexpectedly not in UNWRAP_MODE..."); 
    if (paramString == null)
      throw new XMLEncryptionException("Cannot decrypt a key without knowing the algorithm"); 
    if (this.key == null) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Trying to find a KEK via key resolvers"); 
      KeyInfo keyInfo = paramEncryptedKey.getKeyInfo();
      if (keyInfo != null) {
        keyInfo.setSecureValidation(this.secureValidation);
        try {
          String str1 = paramEncryptedKey.getEncryptionMethod().getAlgorithm();
          String str2 = JCEMapper.getJCEKeyAlgorithmFromURI(str1);
          if ("RSA".equals(str2)) {
            this.key = keyInfo.getPrivateKey();
          } else {
            this.key = keyInfo.getSecretKey();
          } 
        } catch (Exception exception) {
          if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, exception.getMessage(), exception); 
        } 
      } 
      if (this.key == null) {
        log.log(Level.SEVERE, "XMLCipher::decryptKey called without a KEK and cannot resolve");
        throw new XMLEncryptionException("Unable to decrypt without a KEK");
      } 
    } 
    XMLCipherInput xMLCipherInput = new XMLCipherInput(paramEncryptedKey);
    xMLCipherInput.setSecureValidation(this.secureValidation);
    byte[] arrayOfByte = xMLCipherInput.getBytes();
    String str = JCEMapper.getJCEKeyAlgorithmFromURI(paramString);
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "JCE Key Algorithm: " + str); 
    if (this.contextCipher == null) {
      cipher = constructCipher(paramEncryptedKey.getEncryptionMethod().getAlgorithm(), paramEncryptedKey.getEncryptionMethod().getDigestAlgorithm());
    } else {
      cipher = this.contextCipher;
    } 
    try {
      EncryptionMethod encryptionMethod = paramEncryptedKey.getEncryptionMethod();
      OAEPParameterSpec oAEPParameterSpec = constructOAEPParameters(encryptionMethod.getAlgorithm(), encryptionMethod.getDigestAlgorithm(), encryptionMethod.getMGFAlgorithm(), encryptionMethod.getOAEPparams());
      if (oAEPParameterSpec == null) {
        cipher.init(4, this.key);
      } else {
        cipher.init(4, this.key, oAEPParameterSpec);
      } 
      key1 = cipher.unwrap(arrayOfByte, str, 3);
    } catch (InvalidKeyException invalidKeyException) {
      throw new XMLEncryptionException("empty", invalidKeyException);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new XMLEncryptionException("empty", noSuchAlgorithmException);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new XMLEncryptionException("empty", invalidAlgorithmParameterException);
    } 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Decryption of key type " + paramString + " OK"); 
    return key1;
  }
  
  private OAEPParameterSpec constructOAEPParameters(String paramString1, String paramString2, String paramString3, byte[] paramArrayOfByte) {
    if ("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(paramString1) || "http://www.w3.org/2009/xmlenc11#rsa-oaep".equals(paramString1)) {
      String str = "SHA-1";
      if (paramString2 != null)
        str = JCEMapper.translateURItoJCEID(paramString2); 
      PSource.PSpecified pSpecified = PSource.PSpecified.DEFAULT;
      if (paramArrayOfByte != null)
        pSpecified = new PSource.PSpecified(paramArrayOfByte); 
      MGF1ParameterSpec mGF1ParameterSpec = new MGF1ParameterSpec("SHA-1");
      if ("http://www.w3.org/2009/xmlenc11#rsa-oaep".equals(paramString1))
        if ("http://www.w3.org/2009/xmlenc11#mgf1sha256".equals(paramString3)) {
          mGF1ParameterSpec = new MGF1ParameterSpec("SHA-256");
        } else if ("http://www.w3.org/2009/xmlenc11#mgf1sha384".equals(paramString3)) {
          mGF1ParameterSpec = new MGF1ParameterSpec("SHA-384");
        } else if ("http://www.w3.org/2009/xmlenc11#mgf1sha512".equals(paramString3)) {
          mGF1ParameterSpec = new MGF1ParameterSpec("SHA-512");
        }  
      return new OAEPParameterSpec(str, "MGF1", mGF1ParameterSpec, pSpecified);
    } 
    return null;
  }
  
  private Cipher constructCipher(String paramString1, String paramString2) throws XMLEncryptionException {
    Cipher cipher;
    String str = JCEMapper.translateURItoJCEID(paramString1);
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "JCE Algorithm = " + str); 
    try {
      if (this.requestedJCEProvider == null) {
        cipher = Cipher.getInstance(str);
      } else {
        cipher = Cipher.getInstance(str, this.requestedJCEProvider);
      } 
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      if ("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(paramString1) && (paramString2 == null || "http://www.w3.org/2000/09/xmldsig#sha1".equals(paramString2))) {
        try {
          if (this.requestedJCEProvider == null) {
            cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
          } else {
            cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding", this.requestedJCEProvider);
          } 
        } catch (Exception exception) {
          throw new XMLEncryptionException("empty", exception);
        } 
      } else {
        throw new XMLEncryptionException("empty", noSuchAlgorithmException);
      } 
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new XMLEncryptionException("empty", noSuchProviderException);
    } catch (NoSuchPaddingException noSuchPaddingException) {
      throw new XMLEncryptionException("empty", noSuchPaddingException);
    } 
    return cipher;
  }
  
  public Key decryptKey(EncryptedKey paramEncryptedKey) throws XMLEncryptionException { return decryptKey(paramEncryptedKey, this.ed.getEncryptionMethod().getAlgorithm()); }
  
  private static void removeContent(Node paramNode) {
    while (paramNode.hasChildNodes())
      paramNode.removeChild(paramNode.getFirstChild()); 
  }
  
  private Document decryptElement(Element paramElement) throws Exception {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Decrypting element..."); 
    if (this.cipherMode != 2)
      log.log(Level.SEVERE, "XMLCipher unexpectedly not in DECRYPT_MODE..."); 
    byte[] arrayOfByte = decryptToByteArray(paramElement);
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Decrypted octets:\n" + new String(arrayOfByte)); 
    Node node1 = paramElement.getParentNode();
    Node node2 = this.serializer.deserialize(arrayOfByte, node1);
    if (node1 != null && 9 == node1.getNodeType()) {
      this.contextDocument.removeChild(this.contextDocument.getDocumentElement());
      this.contextDocument.appendChild(node2);
    } else if (node1 != null) {
      node1.replaceChild(node2, paramElement);
    } 
    return this.contextDocument;
  }
  
  private Document decryptElementContent(Element paramElement) throws Exception {
    Element element = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedData").item(0);
    if (null == element)
      throw new XMLEncryptionException("No EncryptedData child element."); 
    return decryptElement(element);
  }
  
  public byte[] decryptToByteArray(Element paramElement) throws XMLEncryptionException {
    Cipher cipher;
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Decrypting to ByteArray..."); 
    if (this.cipherMode != 2)
      log.log(Level.SEVERE, "XMLCipher unexpectedly not in DECRYPT_MODE..."); 
    EncryptedData encryptedData = this.factory.newEncryptedData(paramElement);
    if (this.key == null) {
      KeyInfo keyInfo = encryptedData.getKeyInfo();
      if (keyInfo != null)
        try {
          String str = encryptedData.getEncryptionMethod().getAlgorithm();
          EncryptedKeyResolver encryptedKeyResolver = new EncryptedKeyResolver(str, this.kek);
          if (this.internalKeyResolvers != null) {
            int j = this.internalKeyResolvers.size();
            for (byte b = 0; b < j; b++)
              encryptedKeyResolver.registerInternalKeyResolver((KeyResolverSpi)this.internalKeyResolvers.get(b)); 
          } 
          keyInfo.registerInternalKeyResolver(encryptedKeyResolver);
          keyInfo.setSecureValidation(this.secureValidation);
          this.key = keyInfo.getSecretKey();
        } catch (KeyResolverException keyResolverException) {
          if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, keyResolverException.getMessage(), keyResolverException); 
        }  
      if (this.key == null) {
        log.log(Level.SEVERE, "XMLCipher::decryptElement called without a key and unable to resolve");
        throw new XMLEncryptionException("encryption.nokey");
      } 
    } 
    XMLCipherInput xMLCipherInput = new XMLCipherInput(encryptedData);
    xMLCipherInput.setSecureValidation(this.secureValidation);
    byte[] arrayOfByte1 = xMLCipherInput.getBytes();
    String str1 = JCEMapper.translateURItoJCEID(encryptedData.getEncryptionMethod().getAlgorithm());
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "JCE Algorithm = " + str1); 
    try {
      if (this.requestedJCEProvider == null) {
        cipher = Cipher.getInstance(str1);
      } else {
        cipher = Cipher.getInstance(str1, this.requestedJCEProvider);
      } 
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new XMLEncryptionException("empty", noSuchAlgorithmException);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new XMLEncryptionException("empty", noSuchProviderException);
    } catch (NoSuchPaddingException noSuchPaddingException) {
      throw new XMLEncryptionException("empty", noSuchPaddingException);
    } 
    int i = cipher.getBlockSize();
    String str2 = encryptedData.getEncryptionMethod().getAlgorithm();
    if ("http://www.w3.org/2009/xmlenc11#aes128-gcm".equals(str2) || "http://www.w3.org/2009/xmlenc11#aes192-gcm".equals(str2) || "http://www.w3.org/2009/xmlenc11#aes256-gcm".equals(str2))
      i = 12; 
    byte[] arrayOfByte2 = new byte[i];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(arrayOfByte2);
    try {
      cipher.init(this.cipherMode, this.key, ivParameterSpec);
    } catch (InvalidKeyException invalidKeyException) {
      throw new XMLEncryptionException("empty", invalidKeyException);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new XMLEncryptionException("empty", invalidAlgorithmParameterException);
    } 
    try {
      return cipher.doFinal(arrayOfByte1, i, arrayOfByte1.length - i);
    } catch (IllegalBlockSizeException illegalBlockSizeException) {
      throw new XMLEncryptionException("empty", illegalBlockSizeException);
    } catch (BadPaddingException badPaddingException) {
      throw new XMLEncryptionException("empty", badPaddingException);
    } 
  }
  
  public EncryptedData createEncryptedData(int paramInt, String paramString) throws XMLEncryptionException {
    CipherValue cipherValue;
    CipherReference cipherReference;
    EncryptedData encryptedData = null;
    CipherData cipherData = null;
    switch (paramInt) {
      case 2:
        cipherReference = this.factory.newCipherReference(paramString);
        cipherData = this.factory.newCipherData(paramInt);
        cipherData.setCipherReference(cipherReference);
        encryptedData = this.factory.newEncryptedData(cipherData);
        break;
      case 1:
        cipherValue = this.factory.newCipherValue(paramString);
        cipherData = this.factory.newCipherData(paramInt);
        cipherData.setCipherValue(cipherValue);
        encryptedData = this.factory.newEncryptedData(cipherData);
        break;
    } 
    return encryptedData;
  }
  
  public EncryptedKey createEncryptedKey(int paramInt, String paramString) throws XMLEncryptionException {
    CipherValue cipherValue;
    CipherReference cipherReference;
    EncryptedKey encryptedKey = null;
    CipherData cipherData = null;
    switch (paramInt) {
      case 2:
        cipherReference = this.factory.newCipherReference(paramString);
        cipherData = this.factory.newCipherData(paramInt);
        cipherData.setCipherReference(cipherReference);
        encryptedKey = this.factory.newEncryptedKey(cipherData);
        break;
      case 1:
        cipherValue = this.factory.newCipherValue(paramString);
        cipherData = this.factory.newCipherData(paramInt);
        cipherData.setCipherValue(cipherValue);
        encryptedKey = this.factory.newEncryptedKey(cipherData);
        break;
    } 
    return encryptedKey;
  }
  
  public AgreementMethod createAgreementMethod(String paramString) { return this.factory.newAgreementMethod(paramString); }
  
  public CipherData createCipherData(int paramInt) { return this.factory.newCipherData(paramInt); }
  
  public CipherReference createCipherReference(String paramString) { return this.factory.newCipherReference(paramString); }
  
  public CipherValue createCipherValue(String paramString) { return this.factory.newCipherValue(paramString); }
  
  public EncryptionMethod createEncryptionMethod(String paramString) { return this.factory.newEncryptionMethod(paramString); }
  
  public EncryptionProperties createEncryptionProperties() { return this.factory.newEncryptionProperties(); }
  
  public EncryptionProperty createEncryptionProperty() { return this.factory.newEncryptionProperty(); }
  
  public ReferenceList createReferenceList(int paramInt) { return this.factory.newReferenceList(paramInt); }
  
  public Transforms createTransforms() { return this.factory.newTransforms(); }
  
  public Transforms createTransforms(Document paramDocument) { return this.factory.newTransforms(paramDocument); }
  
  private class Factory {
    private Factory() {}
    
    AgreementMethod newAgreementMethod(String param1String) { return new AgreementMethodImpl(param1String); }
    
    CipherData newCipherData(int param1Int) { return new CipherDataImpl(param1Int); }
    
    CipherReference newCipherReference(String param1String) { return new CipherReferenceImpl(param1String); }
    
    CipherValue newCipherValue(String param1String) { return new CipherValueImpl(param1String); }
    
    EncryptedData newEncryptedData(CipherData param1CipherData) { return new EncryptedDataImpl(param1CipherData); }
    
    EncryptedKey newEncryptedKey(CipherData param1CipherData) { return new EncryptedKeyImpl(param1CipherData); }
    
    EncryptionMethod newEncryptionMethod(String param1String) { return new EncryptionMethodImpl(param1String); }
    
    EncryptionProperties newEncryptionProperties() { return new EncryptionPropertiesImpl(); }
    
    EncryptionProperty newEncryptionProperty() { return new EncryptionPropertyImpl(); }
    
    ReferenceList newReferenceList(int param1Int) { return new ReferenceListImpl(param1Int); }
    
    Transforms newTransforms() { return new TransformsImpl(); }
    
    Transforms newTransforms(Document param1Document) { return new TransformsImpl(param1Document); }
    
    CipherData newCipherData(Element param1Element) throws XMLEncryptionException {
      if (null == param1Element)
        throw new NullPointerException("element is null"); 
      byte b = 0;
      Element element = null;
      if (param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherValue").getLength() > 0) {
        b = 1;
        element = (Element)param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherValue").item(0);
      } else if (param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherReference").getLength() > 0) {
        b = 2;
        element = (Element)param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherReference").item(0);
      } 
      CipherData cipherData = newCipherData(b);
      if (b == 1) {
        cipherData.setCipherValue(newCipherValue(element));
      } else if (b == 2) {
        cipherData.setCipherReference(newCipherReference(element));
      } 
      return cipherData;
    }
    
    CipherReference newCipherReference(Element param1Element) throws XMLEncryptionException {
      Attr attr = param1Element.getAttributeNodeNS(null, "URI");
      CipherReferenceImpl cipherReferenceImpl = new CipherReferenceImpl(attr);
      NodeList nodeList = param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "Transforms");
      Element element = (Element)nodeList.item(0);
      if (element != null) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "Creating a DSIG based Transforms element"); 
        try {
          cipherReferenceImpl.setTransforms(new TransformsImpl(element));
        } catch (XMLSignatureException xMLSignatureException) {
          throw new XMLEncryptionException("empty", xMLSignatureException);
        } catch (InvalidTransformException invalidTransformException) {
          throw new XMLEncryptionException("empty", invalidTransformException);
        } catch (XMLSecurityException xMLSecurityException) {
          throw new XMLEncryptionException("empty", xMLSecurityException);
        } 
      } 
      return cipherReferenceImpl;
    }
    
    CipherValue newCipherValue(Element param1Element) {
      String str = XMLUtils.getFullTextChildrenFromElement(param1Element);
      return newCipherValue(str);
    }
    
    EncryptedData newEncryptedData(Element param1Element) throws XMLEncryptionException {
      EncryptedData encryptedData = null;
      NodeList nodeList = param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherData");
      Element element1 = (Element)nodeList.item(nodeList.getLength() - 1);
      CipherData cipherData = newCipherData(element1);
      encryptedData = newEncryptedData(cipherData);
      encryptedData.setId(param1Element.getAttributeNS(null, "Id"));
      encryptedData.setType(param1Element.getAttributeNS(null, "Type"));
      encryptedData.setMimeType(param1Element.getAttributeNS(null, "MimeType"));
      encryptedData.setEncoding(param1Element.getAttributeNS(null, "Encoding"));
      Element element2 = (Element)param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod").item(0);
      if (null != element2)
        encryptedData.setEncryptionMethod(newEncryptionMethod(element2)); 
      Element element3 = (Element)param1Element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo").item(0);
      if (null != element3) {
        KeyInfo keyInfo = newKeyInfo(element3);
        encryptedData.setKeyInfo(keyInfo);
      } 
      Element element4 = (Element)param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperties").item(0);
      if (null != element4)
        encryptedData.setEncryptionProperties(newEncryptionProperties(element4)); 
      return encryptedData;
    }
    
    EncryptedKey newEncryptedKey(Element param1Element) throws XMLEncryptionException {
      EncryptedKey encryptedKey = null;
      NodeList nodeList = param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherData");
      Element element1 = (Element)nodeList.item(nodeList.getLength() - 1);
      CipherData cipherData = newCipherData(element1);
      encryptedKey = newEncryptedKey(cipherData);
      encryptedKey.setId(param1Element.getAttributeNS(null, "Id"));
      encryptedKey.setType(param1Element.getAttributeNS(null, "Type"));
      encryptedKey.setMimeType(param1Element.getAttributeNS(null, "MimeType"));
      encryptedKey.setEncoding(param1Element.getAttributeNS(null, "Encoding"));
      encryptedKey.setRecipient(param1Element.getAttributeNS(null, "Recipient"));
      Element element2 = (Element)param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod").item(0);
      if (null != element2)
        encryptedKey.setEncryptionMethod(newEncryptionMethod(element2)); 
      Element element3 = (Element)param1Element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo").item(0);
      if (null != element3) {
        KeyInfo keyInfo = newKeyInfo(element3);
        encryptedKey.setKeyInfo(keyInfo);
      } 
      Element element4 = (Element)param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperties").item(0);
      if (null != element4)
        encryptedKey.setEncryptionProperties(newEncryptionProperties(element4)); 
      Element element5 = (Element)param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "ReferenceList").item(0);
      if (null != element5)
        encryptedKey.setReferenceList(newReferenceList(element5)); 
      Element element6 = (Element)param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CarriedKeyName").item(0);
      if (null != element6)
        encryptedKey.setCarriedName(element6.getFirstChild().getNodeValue()); 
      return encryptedKey;
    }
    
    KeyInfo newKeyInfo(Element param1Element) throws XMLEncryptionException {
      try {
        KeyInfo keyInfo = new KeyInfo(param1Element, null);
        keyInfo.setSecureValidation(XMLCipher.this.secureValidation);
        if (XMLCipher.this.internalKeyResolvers != null) {
          int i = XMLCipher.this.internalKeyResolvers.size();
          for (byte b = 0; b < i; b++)
            keyInfo.registerInternalKeyResolver((KeyResolverSpi)XMLCipher.this.internalKeyResolvers.get(b)); 
        } 
        return keyInfo;
      } catch (XMLSecurityException xMLSecurityException) {
        throw new XMLEncryptionException("Error loading Key Info", xMLSecurityException);
      } 
    }
    
    EncryptionMethod newEncryptionMethod(Element param1Element) {
      String str = param1Element.getAttributeNS(null, "Algorithm");
      EncryptionMethod encryptionMethod = newEncryptionMethod(str);
      Element element1 = (Element)param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeySize").item(0);
      if (null != element1)
        encryptionMethod.setKeySize(Integer.valueOf(element1.getFirstChild().getNodeValue()).intValue()); 
      Element element2 = (Element)param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "OAEPparams").item(0);
      if (null != element2)
        try {
          String str1 = element2.getFirstChild().getNodeValue();
          encryptionMethod.setOAEPparams(Base64.decode(str1.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
          throw new RuntimeException("UTF-8 not supported", unsupportedEncodingException);
        } catch (Base64DecodingException base64DecodingException) {
          throw new RuntimeException("BASE-64 decoding error", base64DecodingException);
        }  
      Element element3 = (Element)param1Element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "DigestMethod").item(0);
      if (element3 != null) {
        String str1 = element3.getAttributeNS(null, "Algorithm");
        encryptionMethod.setDigestAlgorithm(str1);
      } 
      Element element4 = (Element)param1Element.getElementsByTagNameNS("http://www.w3.org/2009/xmlenc11#", "MGF").item(0);
      if (element4 != null && !"http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(XMLCipher.this.algorithm)) {
        String str1 = element4.getAttributeNS(null, "Algorithm");
        encryptionMethod.setMGFAlgorithm(str1);
      } 
      return encryptionMethod;
    }
    
    EncryptionProperties newEncryptionProperties(Element param1Element) {
      EncryptionProperties encryptionProperties = newEncryptionProperties();
      encryptionProperties.setId(param1Element.getAttributeNS(null, "Id"));
      NodeList nodeList = param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperty");
      for (byte b = 0; b < nodeList.getLength(); b++) {
        Node node = nodeList.item(b);
        if (null != node)
          encryptionProperties.addEncryptionProperty(newEncryptionProperty((Element)node)); 
      } 
      return encryptionProperties;
    }
    
    EncryptionProperty newEncryptionProperty(Element param1Element) {
      EncryptionProperty encryptionProperty = newEncryptionProperty();
      encryptionProperty.setTarget(param1Element.getAttributeNS(null, "Target"));
      encryptionProperty.setId(param1Element.getAttributeNS(null, "Id"));
      return encryptionProperty;
    }
    
    ReferenceList newReferenceList(Element param1Element) {
      byte b2;
      byte b1 = 0;
      if (null != param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "DataReference").item(false)) {
        b1 = 1;
      } else if (null != param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeyReference").item(false)) {
        b1 = 2;
      } 
      ReferenceListImpl referenceListImpl = new ReferenceListImpl(b1);
      NodeList nodeList = null;
      switch (b1) {
        case 1:
          nodeList = param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "DataReference");
          for (b2 = 0; b2 < nodeList.getLength(); b2++) {
            String str = ((Element)nodeList.item(b2)).getAttribute("URI");
            referenceListImpl.add(referenceListImpl.newDataReference(str));
          } 
          break;
        case 2:
          nodeList = param1Element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeyReference");
          for (b2 = 0; b2 < nodeList.getLength(); b2++) {
            String str = ((Element)nodeList.item(b2)).getAttribute("URI");
            referenceListImpl.add(referenceListImpl.newKeyReference(str));
          } 
          break;
      } 
      return referenceListImpl;
    }
    
    Element toElement(EncryptedData param1EncryptedData) { return ((EncryptedDataImpl)param1EncryptedData).toElement(); }
    
    Element toElement(EncryptedKey param1EncryptedKey) { return ((EncryptedKeyImpl)param1EncryptedKey).toElement(); }
    
    Element toElement(ReferenceList param1ReferenceList) { return ((ReferenceListImpl)param1ReferenceList).toElement(); }
    
    private class AgreementMethodImpl implements AgreementMethod {
      private byte[] kaNonce = null;
      
      private List<Element> agreementMethodInformation = null;
      
      private KeyInfo originatorKeyInfo = null;
      
      private KeyInfo recipientKeyInfo = null;
      
      private String algorithmURI = null;
      
      public AgreementMethodImpl(String param2String) {
        this.agreementMethodInformation = new LinkedList();
        URI uRI = null;
        try {
          uRI = new URI(param2String);
        } catch (URISyntaxException uRISyntaxException) {
          throw (IllegalArgumentException)(new IllegalArgumentException()).initCause(uRISyntaxException);
        } 
        this.algorithmURI = uRI.toString();
      }
      
      public byte[] getKANonce() { return this.kaNonce; }
      
      public void setKANonce(byte[] param2ArrayOfByte) { this.kaNonce = param2ArrayOfByte; }
      
      public Iterator<Element> getAgreementMethodInformation() { return this.agreementMethodInformation.iterator(); }
      
      public void addAgreementMethodInformation(Element param2Element) { this.agreementMethodInformation.add(param2Element); }
      
      public void revoveAgreementMethodInformation(Element param2Element) { this.agreementMethodInformation.remove(param2Element); }
      
      public KeyInfo getOriginatorKeyInfo() { return this.originatorKeyInfo; }
      
      public void setOriginatorKeyInfo(KeyInfo param2KeyInfo) { this.originatorKeyInfo = param2KeyInfo; }
      
      public KeyInfo getRecipientKeyInfo() { return this.recipientKeyInfo; }
      
      public void setRecipientKeyInfo(KeyInfo param2KeyInfo) { this.recipientKeyInfo = param2KeyInfo; }
      
      public String getAlgorithm() { return this.algorithmURI; }
    }
    
    private class CipherDataImpl implements CipherData {
      private static final String valueMessage = "Data type is reference type.";
      
      private static final String referenceMessage = "Data type is value type.";
      
      private CipherValue cipherValue = null;
      
      private CipherReference cipherReference = null;
      
      private int cipherType = Integer.MIN_VALUE;
      
      public CipherDataImpl(int param2Int) { this.cipherType = param2Int; }
      
      public CipherValue getCipherValue() { return this.cipherValue; }
      
      public void setCipherValue(CipherValue param2CipherValue) throws XMLEncryptionException {
        if (this.cipherType == 2)
          throw new XMLEncryptionException("empty", new UnsupportedOperationException("Data type is reference type.")); 
        this.cipherValue = param2CipherValue;
      }
      
      public CipherReference getCipherReference() { return this.cipherReference; }
      
      public void setCipherReference(CipherReference param2CipherReference) throws XMLEncryptionException {
        if (this.cipherType == 1)
          throw new XMLEncryptionException("empty", new UnsupportedOperationException("Data type is value type.")); 
        this.cipherReference = param2CipherReference;
      }
      
      public int getDataType() { return this.cipherType; }
      
      Element toElement() {
        Element element = XMLUtils.createElementInEncryptionSpace(XMLCipher.Factory.this.this$0.contextDocument, "CipherData");
        if (this.cipherType == 1) {
          element.appendChild(((XMLCipher.Factory.CipherValueImpl)this.cipherValue).toElement());
        } else if (this.cipherType == 2) {
          element.appendChild(((XMLCipher.Factory.CipherReferenceImpl)this.cipherReference).toElement());
        } 
        return element;
      }
    }
    
    private class CipherReferenceImpl implements CipherReference {
      private String referenceURI = null;
      
      private Transforms referenceTransforms = null;
      
      private Attr referenceNode = null;
      
      public CipherReferenceImpl(String param2String) {
        this.referenceURI = param2String;
        this.referenceNode = null;
      }
      
      public CipherReferenceImpl(Attr param2Attr) {
        this.referenceURI = param2Attr.getNodeValue();
        this.referenceNode = param2Attr;
      }
      
      public String getURI() { return this.referenceURI; }
      
      public Attr getURIAsAttr() { return this.referenceNode; }
      
      public Transforms getTransforms() { return this.referenceTransforms; }
      
      public void setTransforms(Transforms param2Transforms) { this.referenceTransforms = param2Transforms; }
      
      Element toElement() {
        Element element = XMLUtils.createElementInEncryptionSpace(XMLCipher.Factory.this.this$0.contextDocument, "CipherReference");
        element.setAttributeNS(null, "URI", this.referenceURI);
        if (null != this.referenceTransforms)
          element.appendChild(((XMLCipher.Factory.TransformsImpl)this.referenceTransforms).toElement()); 
        return element;
      }
    }
    
    private class CipherValueImpl implements CipherValue {
      private String cipherValue = null;
      
      public CipherValueImpl(String param2String) { this.cipherValue = param2String; }
      
      public String getValue() { return this.cipherValue; }
      
      public void setValue(String param2String) { this.cipherValue = param2String; }
      
      Element toElement() {
        Element element = XMLUtils.createElementInEncryptionSpace(XMLCipher.Factory.this.this$0.contextDocument, "CipherValue");
        element.appendChild(XMLCipher.Factory.this.this$0.contextDocument.createTextNode(this.cipherValue));
        return element;
      }
    }
    
    private class EncryptedDataImpl extends EncryptedTypeImpl implements EncryptedData {
      public EncryptedDataImpl(CipherData param2CipherData) { super(XMLCipher.Factory.this, param2CipherData); }
      
      Element toElement() {
        Element element = ElementProxy.createElementForFamily(XMLCipher.Factory.this.this$0.contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptedData");
        if (null != getId())
          element.setAttributeNS(null, "Id", getId()); 
        if (null != getType())
          element.setAttributeNS(null, "Type", getType()); 
        if (null != getMimeType())
          element.setAttributeNS(null, "MimeType", getMimeType()); 
        if (null != getEncoding())
          element.setAttributeNS(null, "Encoding", getEncoding()); 
        if (null != getEncryptionMethod())
          element.appendChild(((XMLCipher.Factory.EncryptionMethodImpl)getEncryptionMethod()).toElement()); 
        if (null != getKeyInfo())
          element.appendChild(getKeyInfo().getElement().cloneNode(true)); 
        element.appendChild(((XMLCipher.Factory.CipherDataImpl)getCipherData()).toElement());
        if (null != getEncryptionProperties())
          element.appendChild(((XMLCipher.Factory.EncryptionPropertiesImpl)getEncryptionProperties()).toElement()); 
        return element;
      }
    }
    
    private class EncryptedKeyImpl extends EncryptedTypeImpl implements EncryptedKey {
      private String keyRecipient = null;
      
      private ReferenceList referenceList = null;
      
      private String carriedName = null;
      
      public EncryptedKeyImpl(CipherData param2CipherData) { super(XMLCipher.Factory.this, param2CipherData); }
      
      public String getRecipient() { return this.keyRecipient; }
      
      public void setRecipient(String param2String) { this.keyRecipient = param2String; }
      
      public ReferenceList getReferenceList() { return this.referenceList; }
      
      public void setReferenceList(ReferenceList param2ReferenceList) { this.referenceList = param2ReferenceList; }
      
      public String getCarriedName() { return this.carriedName; }
      
      public void setCarriedName(String param2String) { this.carriedName = param2String; }
      
      Element toElement() {
        Element element = ElementProxy.createElementForFamily(XMLCipher.Factory.this.this$0.contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptedKey");
        if (null != getId())
          element.setAttributeNS(null, "Id", getId()); 
        if (null != getType())
          element.setAttributeNS(null, "Type", getType()); 
        if (null != getMimeType())
          element.setAttributeNS(null, "MimeType", getMimeType()); 
        if (null != getEncoding())
          element.setAttributeNS(null, "Encoding", getEncoding()); 
        if (null != getRecipient())
          element.setAttributeNS(null, "Recipient", getRecipient()); 
        if (null != getEncryptionMethod())
          element.appendChild(((XMLCipher.Factory.EncryptionMethodImpl)getEncryptionMethod()).toElement()); 
        if (null != getKeyInfo())
          element.appendChild(getKeyInfo().getElement().cloneNode(true)); 
        element.appendChild(((XMLCipher.Factory.CipherDataImpl)getCipherData()).toElement());
        if (null != getEncryptionProperties())
          element.appendChild(((XMLCipher.Factory.EncryptionPropertiesImpl)getEncryptionProperties()).toElement()); 
        if (this.referenceList != null && !this.referenceList.isEmpty())
          element.appendChild(((XMLCipher.Factory.ReferenceListImpl)getReferenceList()).toElement()); 
        if (null != this.carriedName) {
          Element element1 = ElementProxy.createElementForFamily(XMLCipher.Factory.this.this$0.contextDocument, "http://www.w3.org/2001/04/xmlenc#", "CarriedKeyName");
          Text text = XMLCipher.Factory.this.this$0.contextDocument.createTextNode(this.carriedName);
          element1.appendChild(text);
          element.appendChild(element1);
        } 
        return element;
      }
    }
    
    private abstract class EncryptedTypeImpl {
      private String id = null;
      
      private String type = null;
      
      private String mimeType = null;
      
      private String encoding = null;
      
      private EncryptionMethod encryptionMethod = null;
      
      private KeyInfo keyInfo = null;
      
      private CipherData cipherData = null;
      
      private EncryptionProperties encryptionProperties = null;
      
      protected EncryptedTypeImpl(CipherData param2CipherData) { this.cipherData = param2CipherData; }
      
      public String getId() { return this.id; }
      
      public void setId(String param2String) { this.id = param2String; }
      
      public String getType() { return this.type; }
      
      public void setType(String param2String) {
        if (param2String == null || param2String.length() == 0) {
          this.type = null;
        } else {
          URI uRI = null;
          try {
            uRI = new URI(param2String);
          } catch (URISyntaxException uRISyntaxException) {
            throw (IllegalArgumentException)(new IllegalArgumentException()).initCause(uRISyntaxException);
          } 
          this.type = uRI.toString();
        } 
      }
      
      public String getMimeType() { return this.mimeType; }
      
      public void setMimeType(String param2String) { this.mimeType = param2String; }
      
      public String getEncoding() { return this.encoding; }
      
      public void setEncoding(String param2String) {
        if (param2String == null || param2String.length() == 0) {
          this.encoding = null;
        } else {
          URI uRI = null;
          try {
            uRI = new URI(param2String);
          } catch (URISyntaxException uRISyntaxException) {
            throw (IllegalArgumentException)(new IllegalArgumentException()).initCause(uRISyntaxException);
          } 
          this.encoding = uRI.toString();
        } 
      }
      
      public EncryptionMethod getEncryptionMethod() { return this.encryptionMethod; }
      
      public void setEncryptionMethod(EncryptionMethod param2EncryptionMethod) { this.encryptionMethod = param2EncryptionMethod; }
      
      public KeyInfo getKeyInfo() { return this.keyInfo; }
      
      public void setKeyInfo(KeyInfo param2KeyInfo) { this.keyInfo = param2KeyInfo; }
      
      public CipherData getCipherData() { return this.cipherData; }
      
      public EncryptionProperties getEncryptionProperties() { return this.encryptionProperties; }
      
      public void setEncryptionProperties(EncryptionProperties param2EncryptionProperties) { this.encryptionProperties = param2EncryptionProperties; }
    }
    
    private class EncryptionMethodImpl implements EncryptionMethod {
      private String algorithm = null;
      
      private int keySize = Integer.MIN_VALUE;
      
      private byte[] oaepParams = null;
      
      private List<Element> encryptionMethodInformation = null;
      
      private String digestAlgorithm = null;
      
      private String mgfAlgorithm = null;
      
      public EncryptionMethodImpl(String param2String) {
        URI uRI = null;
        try {
          uRI = new URI(param2String);
        } catch (URISyntaxException uRISyntaxException) {
          throw (IllegalArgumentException)(new IllegalArgumentException()).initCause(uRISyntaxException);
        } 
        this.algorithm = uRI.toString();
        this.encryptionMethodInformation = new LinkedList();
      }
      
      public String getAlgorithm() { return this.algorithm; }
      
      public int getKeySize() { return this.keySize; }
      
      public void setKeySize(int param2Int) { this.keySize = param2Int; }
      
      public byte[] getOAEPparams() { return this.oaepParams; }
      
      public void setOAEPparams(byte[] param2ArrayOfByte) { this.oaepParams = param2ArrayOfByte; }
      
      public void setDigestAlgorithm(String param2String) { this.digestAlgorithm = param2String; }
      
      public String getDigestAlgorithm() { return this.digestAlgorithm; }
      
      public void setMGFAlgorithm(String param2String) { this.mgfAlgorithm = param2String; }
      
      public String getMGFAlgorithm() { return this.mgfAlgorithm; }
      
      public Iterator<Element> getEncryptionMethodInformation() { return this.encryptionMethodInformation.iterator(); }
      
      public void addEncryptionMethodInformation(Element param2Element) { this.encryptionMethodInformation.add(param2Element); }
      
      public void removeEncryptionMethodInformation(Element param2Element) { this.encryptionMethodInformation.remove(param2Element); }
      
      Element toElement() {
        Element element = XMLUtils.createElementInEncryptionSpace(XMLCipher.Factory.this.this$0.contextDocument, "EncryptionMethod");
        element.setAttributeNS(null, "Algorithm", this.algorithm);
        if (this.keySize > 0)
          element.appendChild(XMLUtils.createElementInEncryptionSpace(XMLCipher.Factory.this.this$0.contextDocument, "KeySize").appendChild(XMLCipher.Factory.this.this$0.contextDocument.createTextNode(String.valueOf(this.keySize)))); 
        if (null != this.oaepParams) {
          Element element1 = XMLUtils.createElementInEncryptionSpace(XMLCipher.Factory.this.this$0.contextDocument, "OAEPparams");
          element1.appendChild(XMLCipher.Factory.this.this$0.contextDocument.createTextNode(Base64.encode(this.oaepParams)));
          element.appendChild(element1);
        } 
        if (this.digestAlgorithm != null) {
          Element element1 = XMLUtils.createElementInSignatureSpace(XMLCipher.Factory.this.this$0.contextDocument, "DigestMethod");
          element1.setAttributeNS(null, "Algorithm", this.digestAlgorithm);
          element.appendChild(element1);
        } 
        if (this.mgfAlgorithm != null) {
          Element element1 = XMLUtils.createElementInEncryption11Space(XMLCipher.Factory.this.this$0.contextDocument, "MGF");
          element1.setAttributeNS(null, "Algorithm", this.mgfAlgorithm);
          element1.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + ElementProxy.getDefaultPrefix("http://www.w3.org/2009/xmlenc11#"), "http://www.w3.org/2009/xmlenc11#");
          element.appendChild(element1);
        } 
        Iterator iterator = this.encryptionMethodInformation.iterator();
        while (iterator.hasNext())
          element.appendChild((Node)iterator.next()); 
        return element;
      }
    }
    
    private class EncryptionPropertiesImpl implements EncryptionProperties {
      private String id = null;
      
      private List<EncryptionProperty> encryptionProperties = null;
      
      public EncryptionPropertiesImpl() { this.encryptionProperties = new LinkedList(); }
      
      public String getId() { return this.id; }
      
      public void setId(String param2String) { this.id = param2String; }
      
      public Iterator<EncryptionProperty> getEncryptionProperties() { return this.encryptionProperties.iterator(); }
      
      public void addEncryptionProperty(EncryptionProperty param2EncryptionProperty) { this.encryptionProperties.add(param2EncryptionProperty); }
      
      public void removeEncryptionProperty(EncryptionProperty param2EncryptionProperty) { this.encryptionProperties.remove(param2EncryptionProperty); }
      
      Element toElement() {
        Element element = XMLUtils.createElementInEncryptionSpace(XMLCipher.Factory.this.this$0.contextDocument, "EncryptionProperties");
        if (null != this.id)
          element.setAttributeNS(null, "Id", this.id); 
        Iterator iterator = getEncryptionProperties();
        while (iterator.hasNext())
          element.appendChild(((XMLCipher.Factory.EncryptionPropertyImpl)iterator.next()).toElement()); 
        return element;
      }
    }
    
    private class EncryptionPropertyImpl implements EncryptionProperty {
      private String target = null;
      
      private String id = null;
      
      private Map<String, String> attributeMap = new HashMap();
      
      private List<Element> encryptionInformation = null;
      
      public EncryptionPropertyImpl() { this.encryptionInformation = new LinkedList(); }
      
      public String getTarget() { return this.target; }
      
      public void setTarget(String param2String) {
        if (param2String == null || param2String.length() == 0) {
          this.target = null;
        } else if (param2String.startsWith("#")) {
          this.target = param2String;
        } else {
          URI uRI = null;
          try {
            uRI = new URI(param2String);
          } catch (URISyntaxException uRISyntaxException) {
            throw (IllegalArgumentException)(new IllegalArgumentException()).initCause(uRISyntaxException);
          } 
          this.target = uRI.toString();
        } 
      }
      
      public String getId() { return this.id; }
      
      public void setId(String param2String) { this.id = param2String; }
      
      public String getAttribute(String param2String) { return (String)this.attributeMap.get(param2String); }
      
      public void setAttribute(String param2String1, String param2String2) { this.attributeMap.put(param2String1, param2String2); }
      
      public Iterator<Element> getEncryptionInformation() { return this.encryptionInformation.iterator(); }
      
      public void addEncryptionInformation(Element param2Element) { this.encryptionInformation.add(param2Element); }
      
      public void removeEncryptionInformation(Element param2Element) { this.encryptionInformation.remove(param2Element); }
      
      Element toElement() {
        Element element = XMLUtils.createElementInEncryptionSpace(XMLCipher.Factory.this.this$0.contextDocument, "EncryptionProperty");
        if (null != this.target)
          element.setAttributeNS(null, "Target", this.target); 
        if (null != this.id)
          element.setAttributeNS(null, "Id", this.id); 
        return element;
      }
    }
    
    private class ReferenceListImpl implements ReferenceList {
      private Class<?> sentry;
      
      private List<Reference> references;
      
      public ReferenceListImpl(int param2Int) {
        if (param2Int == 1) {
          this.sentry = DataReference.class;
        } else if (param2Int == 2) {
          this.sentry = KeyReference.class;
        } else {
          throw new IllegalArgumentException();
        } 
        this.references = new LinkedList();
      }
      
      public void add(Reference param2Reference) {
        if (!param2Reference.getClass().equals(this.sentry))
          throw new IllegalArgumentException(); 
        this.references.add(param2Reference);
      }
      
      public void remove(Reference param2Reference) {
        if (!param2Reference.getClass().equals(this.sentry))
          throw new IllegalArgumentException(); 
        this.references.remove(param2Reference);
      }
      
      public int size() { return this.references.size(); }
      
      public boolean isEmpty() { return this.references.isEmpty(); }
      
      public Iterator<Reference> getReferences() { return this.references.iterator(); }
      
      Element toElement() {
        Element element = ElementProxy.createElementForFamily(XMLCipher.Factory.this.this$0.contextDocument, "http://www.w3.org/2001/04/xmlenc#", "ReferenceList");
        for (Reference reference : this.references)
          element.appendChild(((ReferenceImpl)reference).toElement()); 
        return element;
      }
      
      public Reference newDataReference(String param2String) { return new DataReference(param2String); }
      
      public Reference newKeyReference(String param2String) { return new KeyReference(param2String); }
      
      private class DataReference extends ReferenceImpl {
        DataReference(String param3String) { super(XMLCipher.Factory.ReferenceListImpl.this, param3String); }
        
        public String getType() { return "DataReference"; }
      }
      
      private class KeyReference extends ReferenceImpl {
        KeyReference(String param3String) { super(XMLCipher.Factory.ReferenceListImpl.this, param3String); }
        
        public String getType() { return "KeyReference"; }
      }
      
      private abstract class ReferenceImpl implements Reference {
        private String uri;
        
        private List<Element> referenceInformation;
        
        ReferenceImpl(String param3String) {
          this.uri = param3String;
          this.referenceInformation = new LinkedList();
        }
        
        public abstract String getType();
        
        public String getURI() { return this.uri; }
        
        public Iterator<Element> getElementRetrievalInformation() { return this.referenceInformation.iterator(); }
        
        public void setURI(String param3String) { this.uri = param3String; }
        
        public void removeElementRetrievalInformation(Element param3Element) { this.referenceInformation.remove(param3Element); }
        
        public void addElementRetrievalInformation(Element param3Element) { this.referenceInformation.add(param3Element); }
        
        public Element toElement() {
          String str = getType();
          Element element = ElementProxy.createElementForFamily(this.this$2.this$1.this$0.contextDocument, "http://www.w3.org/2001/04/xmlenc#", str);
          element.setAttribute("URI", this.uri);
          return element;
        }
      }
    }
    
    private class TransformsImpl extends Transforms implements Transforms {
      public TransformsImpl() { super(this$0.this$0.contextDocument); }
      
      public TransformsImpl(Document param2Document) {
        if (param2Document == null)
          throw new RuntimeException("Document is null"); 
        this.doc = param2Document;
        this.constructionElement = createElementForFamilyLocal(this.doc, getBaseNamespace(), getBaseLocalName());
      }
      
      public TransformsImpl(Element param2Element) throws XMLSignatureException, InvalidTransformException, XMLSecurityException, TransformationException { super(param2Element, ""); }
      
      public Element toElement() {
        if (this.doc == null)
          this.doc = XMLCipher.Factory.this.this$0.contextDocument; 
        return getElement();
      }
      
      public Transforms getDSTransforms() { return this; }
      
      public String getBaseNamespace() { return "http://www.w3.org/2001/04/xmlenc#"; }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\XMLCipher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */