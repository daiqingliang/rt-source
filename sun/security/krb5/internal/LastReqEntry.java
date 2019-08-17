package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class LastReqEntry {
  private int lrType;
  
  private KerberosTime lrValue;
  
  private LastReqEntry() {}
  
  public LastReqEntry(int paramInt, KerberosTime paramKerberosTime) {
    this.lrType = paramInt;
    this.lrValue = paramKerberosTime;
  }
  
  public LastReqEntry(DerValue paramDerValue) throws Asn1Exception, IOException {
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    DerValue derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 0) {
      this.lrType = derValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    this.lrValue = KerberosTime.parse(paramDerValue.getData(), (byte)1, false);
    if (paramDerValue.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putInteger(this.lrType);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), this.lrValue.asn1Encode());
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public Object clone() {
    LastReqEntry lastReqEntry = new LastReqEntry();
    lastReqEntry.lrType = this.lrType;
    lastReqEntry.lrValue = this.lrValue;
    return lastReqEntry;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\LastReqEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */