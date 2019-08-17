package javax.xml.crypto.dsig;

import java.io.OutputStream;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.Data;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;

public interface Transform extends XMLStructure, AlgorithmMethod {
  public static final String BASE64 = "http://www.w3.org/2000/09/xmldsig#base64";
  
  public static final String ENVELOPED = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
  
  public static final String XPATH = "http://www.w3.org/TR/1999/REC-xpath-19991116";
  
  public static final String XPATH2 = "http://www.w3.org/2002/06/xmldsig-filter2";
  
  public static final String XSLT = "http://www.w3.org/TR/1999/REC-xslt-19991116";
  
  AlgorithmParameterSpec getParameterSpec();
  
  Data transform(Data paramData, XMLCryptoContext paramXMLCryptoContext) throws TransformException;
  
  Data transform(Data paramData, XMLCryptoContext paramXMLCryptoContext, OutputStream paramOutputStream) throws TransformException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\Transform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */