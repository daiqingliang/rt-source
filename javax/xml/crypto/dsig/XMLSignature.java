package javax.xml.crypto.dsig;

import java.util.List;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;

public interface XMLSignature extends XMLStructure {
  public static final String XMLNS = "http://www.w3.org/2000/09/xmldsig#";
  
  boolean validate(XMLValidateContext paramXMLValidateContext) throws XMLSignatureException;
  
  KeyInfo getKeyInfo();
  
  SignedInfo getSignedInfo();
  
  List getObjects();
  
  String getId();
  
  SignatureValue getSignatureValue();
  
  void sign(XMLSignContext paramXMLSignContext) throws MarshalException, XMLSignatureException;
  
  KeySelectorResult getKeySelectorResult();
  
  public static interface SignatureValue extends XMLStructure {
    String getId();
    
    byte[] getValue();
    
    boolean validate(XMLValidateContext param1XMLValidateContext) throws XMLSignatureException;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\XMLSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */