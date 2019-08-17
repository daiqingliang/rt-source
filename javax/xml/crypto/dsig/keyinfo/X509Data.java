package javax.xml.crypto.dsig.keyinfo;

import java.util.List;
import javax.xml.crypto.XMLStructure;

public interface X509Data extends XMLStructure {
  public static final String TYPE = "http://www.w3.org/2000/09/xmldsig#X509Data";
  
  public static final String RAW_X509_CERTIFICATE_TYPE = "http://www.w3.org/2000/09/xmldsig#rawX509Certificate";
  
  List getContent();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\keyinfo\X509Data.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */