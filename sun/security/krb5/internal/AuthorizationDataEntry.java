package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class AuthorizationDataEntry implements Cloneable {
  public int adType;
  
  public byte[] adData;
  
  private AuthorizationDataEntry() {}
  
  public AuthorizationDataEntry(int paramInt, byte[] paramArrayOfByte) {
    this.adType = paramInt;
    this.adData = paramArrayOfByte;
  }
  
  public Object clone() {
    AuthorizationDataEntry authorizationDataEntry = new AuthorizationDataEntry();
    authorizationDataEntry.adType = this.adType;
    if (this.adData != null) {
      authorizationDataEntry.adData = new byte[this.adData.length];
      System.arraycopy(this.adData, 0, authorizationDataEntry.adData, 0, this.adData.length);
    } 
    return authorizationDataEntry;
  }
  
  public AuthorizationDataEntry(DerValue paramDerValue) throws Asn1Exception, IOException {
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    DerValue derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 0) {
      this.adType = derValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 1) {
      this.adData = derValue.getData().getOctetString();
    } else {
      throw new Asn1Exception(906);
    } 
    if (paramDerValue.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putInteger(this.adType);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.putOctetString(this.adData);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public void writeEntry(CCacheOutputStream paramCCacheOutputStream) throws IOException {
    paramCCacheOutputStream.write16(this.adType);
    paramCCacheOutputStream.write32(this.adData.length);
    paramCCacheOutputStream.write(this.adData, 0, this.adData.length);
  }
  
  public String toString() { return "adType=" + this.adType + " adData.length=" + this.adData.length; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\AuthorizationDataEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */