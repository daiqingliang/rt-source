package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class ETypeInfo {
  private int etype;
  
  private String salt = null;
  
  private static final byte TAG_TYPE = 0;
  
  private static final byte TAG_VALUE = 1;
  
  private ETypeInfo() {}
  
  public ETypeInfo(int paramInt, String paramString) {
    this.etype = paramInt;
    this.salt = paramString;
  }
  
  public Object clone() { return new ETypeInfo(this.etype, this.salt); }
  
  public ETypeInfo(DerValue paramDerValue) throws Asn1Exception, IOException {
    DerValue derValue = null;
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 0) {
      this.etype = derValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    if (paramDerValue.getData().available() > 0) {
      derValue = paramDerValue.getData().getDerValue();
      if ((derValue.getTag() & 0x1F) == 1) {
        byte[] arrayOfByte = derValue.getData().getOctetString();
        if (KerberosString.MSNAME) {
          this.salt = new String(arrayOfByte, "UTF8");
        } else {
          this.salt = new String(arrayOfByte);
        } 
      } 
    } 
    if (paramDerValue.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putInteger(this.etype);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
    if (this.salt != null) {
      derOutputStream2 = new DerOutputStream();
      if (KerberosString.MSNAME) {
        derOutputStream2.putOctetString(this.salt.getBytes("UTF8"));
      } else {
        derOutputStream2.putOctetString(this.salt.getBytes());
      } 
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), derOutputStream2);
    } 
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public int getEType() { return this.etype; }
  
  public String getSalt() { return this.salt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\ETypeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */