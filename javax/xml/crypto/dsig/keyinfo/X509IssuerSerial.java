package javax.xml.crypto.dsig.keyinfo;

import java.math.BigInteger;
import javax.xml.crypto.XMLStructure;

public interface X509IssuerSerial extends XMLStructure {
  String getIssuerName();
  
  BigInteger getSerialNumber();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\keyinfo\X509IssuerSerial.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */