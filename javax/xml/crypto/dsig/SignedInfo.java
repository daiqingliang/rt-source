package javax.xml.crypto.dsig;

import java.io.InputStream;
import java.util.List;
import javax.xml.crypto.XMLStructure;

public interface SignedInfo extends XMLStructure {
  CanonicalizationMethod getCanonicalizationMethod();
  
  SignatureMethod getSignatureMethod();
  
  List getReferences();
  
  String getId();
  
  InputStream getCanonicalizedData();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\SignedInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */