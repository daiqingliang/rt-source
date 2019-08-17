package org.jcp.xml.dsig.internal.dom;

import java.math.BigInteger;
import java.security.KeyException;
import java.security.PublicKey;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyName;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.PGPData;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMKeyInfoFactory extends KeyInfoFactory {
  public KeyInfo newKeyInfo(List paramList) { return newKeyInfo(paramList, null); }
  
  public KeyInfo newKeyInfo(List paramList, String paramString) { return new DOMKeyInfo(paramList, paramString); }
  
  public KeyName newKeyName(String paramString) { return new DOMKeyName(paramString); }
  
  public KeyValue newKeyValue(PublicKey paramPublicKey) throws KeyException {
    String str = paramPublicKey.getAlgorithm();
    if (str.equals("DSA"))
      return new DOMKeyValue.DSA(paramPublicKey); 
    if (str.equals("RSA"))
      return new DOMKeyValue.RSA(paramPublicKey); 
    if (str.equals("EC"))
      return new DOMKeyValue.EC(paramPublicKey); 
    throw new KeyException("unsupported key algorithm: " + str);
  }
  
  public PGPData newPGPData(byte[] paramArrayOfByte) { return newPGPData(paramArrayOfByte, null, null); }
  
  public PGPData newPGPData(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, List paramList) { return new DOMPGPData(paramArrayOfByte1, paramArrayOfByte2, paramList); }
  
  public PGPData newPGPData(byte[] paramArrayOfByte, List paramList) { return new DOMPGPData(paramArrayOfByte, paramList); }
  
  public RetrievalMethod newRetrievalMethod(String paramString) { return newRetrievalMethod(paramString, null, null); }
  
  public RetrievalMethod newRetrievalMethod(String paramString1, String paramString2, List paramList) {
    if (paramString1 == null)
      throw new NullPointerException("uri must not be null"); 
    return new DOMRetrievalMethod(paramString1, paramString2, paramList);
  }
  
  public X509Data newX509Data(List paramList) { return new DOMX509Data(paramList); }
  
  public X509IssuerSerial newX509IssuerSerial(String paramString, BigInteger paramBigInteger) { return new DOMX509IssuerSerial(paramString, paramBigInteger); }
  
  public boolean isFeatureSupported(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    return false;
  }
  
  public URIDereferencer getURIDereferencer() { return DOMURIDereferencer.INSTANCE; }
  
  public KeyInfo unmarshalKeyInfo(XMLStructure paramXMLStructure) throws MarshalException {
    if (paramXMLStructure == null)
      throw new NullPointerException("xmlStructure cannot be null"); 
    if (!(paramXMLStructure instanceof DOMStructure))
      throw new ClassCastException("xmlStructure must be of type DOMStructure"); 
    Node node = ((DOMStructure)paramXMLStructure).getNode();
    node.normalize();
    Element element = null;
    if (node.getNodeType() == 9) {
      element = ((Document)node).getDocumentElement();
    } else if (node.getNodeType() == 1) {
      element = (Element)node;
    } else {
      throw new MarshalException("xmlStructure does not contain a proper Node");
    } 
    String str = element.getLocalName();
    if (str == null)
      throw new MarshalException("Document implementation must support DOM Level 2 and be namespace aware"); 
    if (str.equals("KeyInfo"))
      return new DOMKeyInfo(element, new UnmarshalContext(), getProvider()); 
    throw new MarshalException("invalid KeyInfo tag: " + str);
  }
  
  private static class UnmarshalContext extends DOMCryptoContext {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMKeyInfoFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */