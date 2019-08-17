package javax.xml.crypto.dsig;

import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.XMLStructure;

public interface DigestMethod extends XMLStructure, AlgorithmMethod {
  public static final String SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
  
  public static final String SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
  
  public static final String SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
  
  public static final String RIPEMD160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
  
  AlgorithmParameterSpec getParameterSpec();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\DigestMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */