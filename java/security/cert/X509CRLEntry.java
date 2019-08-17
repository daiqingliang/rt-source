package java.security.cert;

import java.math.BigInteger;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import sun.security.x509.X509CRLEntryImpl;

public abstract class X509CRLEntry implements X509Extension {
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof X509CRLEntry))
      return false; 
    try {
      byte[] arrayOfByte1 = getEncoded();
      byte[] arrayOfByte2 = ((X509CRLEntry)paramObject).getEncoded();
      if (arrayOfByte1.length != arrayOfByte2.length)
        return false; 
      for (byte b = 0; b < arrayOfByte1.length; b++) {
        if (arrayOfByte1[b] != arrayOfByte2[b])
          return false; 
      } 
    } catch (CRLException cRLException) {
      return false;
    } 
    return true;
  }
  
  public int hashCode() {
    byte b = 0;
    try {
      byte[] arrayOfByte = getEncoded();
      for (byte b1 = 1; b1 < arrayOfByte.length; b1++)
        b += arrayOfByte[b1] * b1; 
    } catch (CRLException cRLException) {
      return b;
    } 
    return b;
  }
  
  public abstract byte[] getEncoded() throws CRLException;
  
  public abstract BigInteger getSerialNumber();
  
  public X500Principal getCertificateIssuer() { return null; }
  
  public abstract Date getRevocationDate();
  
  public abstract boolean hasExtensions();
  
  public abstract String toString();
  
  public CRLReason getRevocationReason() { return !hasExtensions() ? null : X509CRLEntryImpl.getRevocationReason(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\X509CRLEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */