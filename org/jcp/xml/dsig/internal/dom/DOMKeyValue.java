package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class DOMKeyValue extends DOMStructure implements KeyValue {
  private static final String XMLDSIG_11_XMLNS = "http://www.w3.org/2009/xmldsig11#";
  
  private final PublicKey publicKey;
  
  public DOMKeyValue(PublicKey paramPublicKey) throws KeyException {
    if (paramPublicKey == null)
      throw new NullPointerException("key cannot be null"); 
    this.publicKey = paramPublicKey;
  }
  
  public DOMKeyValue(Element paramElement) throws MarshalException { this.publicKey = unmarshalKeyValue(paramElement); }
  
  static KeyValue unmarshal(Element paramElement) throws MarshalException {
    Element element = DOMUtils.getFirstChildElement(paramElement);
    return element.getLocalName().equals("DSAKeyValue") ? new DSA(element) : (element.getLocalName().equals("RSAKeyValue") ? new RSA(element) : (element.getLocalName().equals("ECKeyValue") ? new EC(element) : new Unknown(element)));
  }
  
  public PublicKey getPublicKey() throws KeyException {
    if (this.publicKey == null)
      throw new KeyException("can't convert KeyValue to PublicKey"); 
    return this.publicKey;
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    Document document = DOMUtils.getOwnerDocument(paramNode);
    Element element = DOMUtils.createElement(document, "KeyValue", "http://www.w3.org/2000/09/xmldsig#", paramString);
    marshalPublicKey(element, document, paramString, paramDOMCryptoContext);
    paramNode.appendChild(element);
  }
  
  abstract void marshalPublicKey(Node paramNode, Document paramDocument, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException;
  
  abstract PublicKey unmarshalKeyValue(Element paramElement) throws MarshalException;
  
  private static PublicKey generatePublicKey(KeyFactory paramKeyFactory, KeySpec paramKeySpec) {
    try {
      return paramKeyFactory.generatePublic(paramKeySpec);
    } catch (InvalidKeySpecException invalidKeySpecException) {
      return null;
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof KeyValue))
      return false; 
    try {
      KeyValue keyValue = (KeyValue)paramObject;
      if (this.publicKey == null) {
        if (keyValue.getPublicKey() != null)
          return false; 
      } else if (!this.publicKey.equals(keyValue.getPublicKey())) {
        return false;
      } 
    } catch (KeyException keyException) {
      return false;
    } 
    return true;
  }
  
  public int hashCode() {
    int i = 17;
    if (this.publicKey != null)
      i = 31 * i + this.publicKey.hashCode(); 
    return i;
  }
  
  static final class DSA extends DOMKeyValue {
    private DOMCryptoBinary p;
    
    private DOMCryptoBinary q;
    
    private DOMCryptoBinary g;
    
    private DOMCryptoBinary y;
    
    private DOMCryptoBinary j;
    
    private KeyFactory dsakf;
    
    DSA(PublicKey param1PublicKey) throws KeyException {
      super(param1PublicKey);
      DSAPublicKey dSAPublicKey = (DSAPublicKey)param1PublicKey;
      DSAParams dSAParams = dSAPublicKey.getParams();
      this.p = new DOMCryptoBinary(dSAParams.getP());
      this.q = new DOMCryptoBinary(dSAParams.getQ());
      this.g = new DOMCryptoBinary(dSAParams.getG());
      this.y = new DOMCryptoBinary(dSAPublicKey.getY());
    }
    
    DSA(Element param1Element) throws MarshalException { super(param1Element); }
    
    void marshalPublicKey(Node param1Node, Document param1Document, String param1String, DOMCryptoContext param1DOMCryptoContext) throws MarshalException {
      Element element1 = DOMUtils.createElement(param1Document, "DSAKeyValue", "http://www.w3.org/2000/09/xmldsig#", param1String);
      Element element2 = DOMUtils.createElement(param1Document, "P", "http://www.w3.org/2000/09/xmldsig#", param1String);
      Element element3 = DOMUtils.createElement(param1Document, "Q", "http://www.w3.org/2000/09/xmldsig#", param1String);
      Element element4 = DOMUtils.createElement(param1Document, "G", "http://www.w3.org/2000/09/xmldsig#", param1String);
      Element element5 = DOMUtils.createElement(param1Document, "Y", "http://www.w3.org/2000/09/xmldsig#", param1String);
      this.p.marshal(element2, param1String, param1DOMCryptoContext);
      this.q.marshal(element3, param1String, param1DOMCryptoContext);
      this.g.marshal(element4, param1String, param1DOMCryptoContext);
      this.y.marshal(element5, param1String, param1DOMCryptoContext);
      element1.appendChild(element2);
      element1.appendChild(element3);
      element1.appendChild(element4);
      element1.appendChild(element5);
      param1Node.appendChild(element1);
    }
    
    PublicKey unmarshalKeyValue(Element param1Element) throws MarshalException {
      if (this.dsakf == null)
        try {
          this.dsakf = KeyFactory.getInstance("DSA");
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
          throw new RuntimeException("unable to create DSA KeyFactory: " + noSuchAlgorithmException.getMessage());
        }  
      Element element = DOMUtils.getFirstChildElement(param1Element);
      if (element.getLocalName().equals("P")) {
        this.p = new DOMCryptoBinary(element.getFirstChild());
        element = DOMUtils.getNextSiblingElement(element, "Q");
        this.q = new DOMCryptoBinary(element.getFirstChild());
        element = DOMUtils.getNextSiblingElement(element);
      } 
      if (element.getLocalName().equals("G")) {
        this.g = new DOMCryptoBinary(element.getFirstChild());
        element = DOMUtils.getNextSiblingElement(element, "Y");
      } 
      this.y = new DOMCryptoBinary(element.getFirstChild());
      element = DOMUtils.getNextSiblingElement(element);
      if (element != null && element.getLocalName().equals("J"))
        this.j = new DOMCryptoBinary(element.getFirstChild()); 
      DSAPublicKeySpec dSAPublicKeySpec = new DSAPublicKeySpec(this.y.getBigNum(), this.p.getBigNum(), this.q.getBigNum(), this.g.getBigNum());
      return DOMKeyValue.generatePublicKey(this.dsakf, dSAPublicKeySpec);
    }
  }
  
  static final class EC extends DOMKeyValue {
    private byte[] ecPublicKey;
    
    private KeyFactory eckf;
    
    private ECParameterSpec ecParams;
    
    private Method encodePoint;
    
    private Method decodePoint;
    
    private Method getCurveName;
    
    private Method getECParameterSpec;
    
    EC(PublicKey param1PublicKey) throws KeyException {
      super(param1PublicKey);
      ECPublicKey eCPublicKey = (ECPublicKey)param1PublicKey;
      ECPoint eCPoint = eCPublicKey.getW();
      this.ecParams = eCPublicKey.getParams();
      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
              public Void run() throws ClassNotFoundException, NoSuchMethodException {
                DOMKeyValue.EC.this.getMethods();
                return null;
              }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        throw new KeyException("ECKeyValue not supported", privilegedActionException.getException());
      } 
      Object[] arrayOfObject = { eCPoint, this.ecParams.getCurve() };
      try {
        this.ecPublicKey = (byte[])this.encodePoint.invoke(null, arrayOfObject);
      } catch (IllegalAccessException illegalAccessException) {
        throw new KeyException(illegalAccessException);
      } catch (InvocationTargetException invocationTargetException) {
        throw new KeyException(invocationTargetException);
      } 
    }
    
    EC(Element param1Element) throws MarshalException { super(param1Element); }
    
    void getMethods() throws ClassNotFoundException, NoSuchMethodException {
      Class clazz = Class.forName("sun.security.ec.ECParameters");
      Class[] arrayOfClass = { ECPoint.class, java.security.spec.EllipticCurve.class };
      this.encodePoint = clazz.getMethod("encodePoint", arrayOfClass);
      arrayOfClass = new Class[] { ECParameterSpec.class };
      this.getCurveName = clazz.getMethod("getCurveName", arrayOfClass);
      arrayOfClass = new Class[] { byte[].class, java.security.spec.EllipticCurve.class };
      this.decodePoint = clazz.getMethod("decodePoint", arrayOfClass);
      clazz = Class.forName("sun.security.ec.NamedCurve");
      arrayOfClass = new Class[] { String.class };
      this.getECParameterSpec = clazz.getMethod("getECParameterSpec", arrayOfClass);
    }
    
    void marshalPublicKey(Node param1Node, Document param1Document, String param1String, DOMCryptoContext param1DOMCryptoContext) throws MarshalException {
      String str1 = DOMUtils.getNSPrefix(param1DOMCryptoContext, "http://www.w3.org/2009/xmldsig11#");
      Element element1 = DOMUtils.createElement(param1Document, "ECKeyValue", "http://www.w3.org/2009/xmldsig11#", str1);
      Element element2 = DOMUtils.createElement(param1Document, "NamedCurve", "http://www.w3.org/2009/xmldsig11#", str1);
      Element element3 = DOMUtils.createElement(param1Document, "PublicKey", "http://www.w3.org/2009/xmldsig11#", str1);
      Object[] arrayOfObject = { this.ecParams };
      try {
        String str = (String)this.getCurveName.invoke(null, arrayOfObject);
        DOMUtils.setAttribute(element2, "URI", "urn:oid:" + str);
      } catch (IllegalAccessException illegalAccessException) {
        throw new MarshalException(illegalAccessException);
      } catch (InvocationTargetException invocationTargetException) {
        throw new MarshalException(invocationTargetException);
      } 
      String str2 = (str1 == null || str1.length() == 0) ? "xmlns" : ("xmlns:" + str1);
      element2.setAttributeNS("http://www.w3.org/2000/xmlns/", str2, "http://www.w3.org/2009/xmldsig11#");
      element1.appendChild(element2);
      String str3 = Base64.encode(this.ecPublicKey);
      element3.appendChild(DOMUtils.getOwnerDocument(element3).createTextNode(str3));
      element1.appendChild(element3);
      param1Node.appendChild(element1);
    }
    
    PublicKey unmarshalKeyValue(Element param1Element) throws MarshalException {
      if (this.eckf == null)
        try {
          this.eckf = KeyFactory.getInstance("EC");
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
          throw new RuntimeException("unable to create EC KeyFactory: " + noSuchAlgorithmException.getMessage());
        }  
      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
              public Void run() throws ClassNotFoundException, NoSuchMethodException {
                DOMKeyValue.EC.this.getMethods();
                return null;
              }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        throw new MarshalException("ECKeyValue not supported", privilegedActionException.getException());
      } 
      ECParameterSpec eCParameterSpec = null;
      Element element = DOMUtils.getFirstChildElement(param1Element);
      if (element.getLocalName().equals("ECParameters"))
        throw new UnsupportedOperationException("ECParameters not supported"); 
      if (element.getLocalName().equals("NamedCurve")) {
        String str = DOMUtils.getAttributeValue(element, "URI");
        if (str.startsWith("urn:oid:")) {
          String str1 = str.substring(8);
          try {
            Object[] arrayOfObject = { str1 };
            eCParameterSpec = (ECParameterSpec)this.getECParameterSpec.invoke(null, arrayOfObject);
          } catch (IllegalAccessException illegalAccessException) {
            throw new MarshalException(illegalAccessException);
          } catch (InvocationTargetException invocationTargetException) {
            throw new MarshalException(invocationTargetException);
          } 
        } else {
          throw new MarshalException("Invalid NamedCurve URI");
        } 
      } else {
        throw new MarshalException("Invalid ECKeyValue");
      } 
      element = DOMUtils.getNextSiblingElement(element, "PublicKey");
      ECPoint eCPoint = null;
      try {
        Object[] arrayOfObject = { Base64.decode(element), eCParameterSpec.getCurve() };
        eCPoint = (ECPoint)this.decodePoint.invoke(null, arrayOfObject);
      } catch (Base64DecodingException base64DecodingException) {
        throw new MarshalException("Invalid EC PublicKey", base64DecodingException);
      } catch (IllegalAccessException illegalAccessException) {
        throw new MarshalException(illegalAccessException);
      } catch (InvocationTargetException invocationTargetException) {
        throw new MarshalException(invocationTargetException);
      } 
      ECPublicKeySpec eCPublicKeySpec = new ECPublicKeySpec(eCPoint, eCParameterSpec);
      return DOMKeyValue.generatePublicKey(this.eckf, eCPublicKeySpec);
    }
  }
  
  static final class RSA extends DOMKeyValue {
    private DOMCryptoBinary modulus;
    
    private DOMCryptoBinary exponent;
    
    private KeyFactory rsakf;
    
    RSA(PublicKey param1PublicKey) throws KeyException {
      super(param1PublicKey);
      RSAPublicKey rSAPublicKey = (RSAPublicKey)param1PublicKey;
      this.exponent = new DOMCryptoBinary(rSAPublicKey.getPublicExponent());
      this.modulus = new DOMCryptoBinary(rSAPublicKey.getModulus());
    }
    
    RSA(Element param1Element) throws MarshalException { super(param1Element); }
    
    void marshalPublicKey(Node param1Node, Document param1Document, String param1String, DOMCryptoContext param1DOMCryptoContext) throws MarshalException {
      Element element1 = DOMUtils.createElement(param1Document, "RSAKeyValue", "http://www.w3.org/2000/09/xmldsig#", param1String);
      Element element2 = DOMUtils.createElement(param1Document, "Modulus", "http://www.w3.org/2000/09/xmldsig#", param1String);
      Element element3 = DOMUtils.createElement(param1Document, "Exponent", "http://www.w3.org/2000/09/xmldsig#", param1String);
      this.modulus.marshal(element2, param1String, param1DOMCryptoContext);
      this.exponent.marshal(element3, param1String, param1DOMCryptoContext);
      element1.appendChild(element2);
      element1.appendChild(element3);
      param1Node.appendChild(element1);
    }
    
    PublicKey unmarshalKeyValue(Element param1Element) throws MarshalException {
      if (this.rsakf == null)
        try {
          this.rsakf = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
          throw new RuntimeException("unable to create RSA KeyFactory: " + noSuchAlgorithmException.getMessage());
        }  
      Element element1 = DOMUtils.getFirstChildElement(param1Element, "Modulus");
      this.modulus = new DOMCryptoBinary(element1.getFirstChild());
      Element element2 = DOMUtils.getNextSiblingElement(element1, "Exponent");
      this.exponent = new DOMCryptoBinary(element2.getFirstChild());
      RSAPublicKeySpec rSAPublicKeySpec = new RSAPublicKeySpec(this.modulus.getBigNum(), this.exponent.getBigNum());
      return DOMKeyValue.generatePublicKey(this.rsakf, rSAPublicKeySpec);
    }
  }
  
  static final class Unknown extends DOMKeyValue {
    private DOMStructure externalPublicKey;
    
    Unknown(Element param1Element) throws MarshalException { super(param1Element); }
    
    PublicKey unmarshalKeyValue(Element param1Element) throws MarshalException {
      this.externalPublicKey = new DOMStructure(param1Element);
      return null;
    }
    
    void marshalPublicKey(Node param1Node, Document param1Document, String param1String, DOMCryptoContext param1DOMCryptoContext) throws MarshalException { param1Node.appendChild(this.externalPublicKey.getNode()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMKeyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */