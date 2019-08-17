package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PAEncTSEnc {
  public KerberosTime pATimeStamp;
  
  public Integer pAUSec;
  
  public PAEncTSEnc(KerberosTime paramKerberosTime, Integer paramInteger) {
    this.pATimeStamp = paramKerberosTime;
    this.pAUSec = paramInteger;
  }
  
  public PAEncTSEnc() {
    KerberosTime kerberosTime = KerberosTime.now();
    this.pATimeStamp = kerberosTime;
    this.pAUSec = new Integer(kerberosTime.getMicroSeconds());
  }
  
  public PAEncTSEnc(DerValue paramDerValue) throws Asn1Exception, IOException {
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    this.pATimeStamp = KerberosTime.parse(paramDerValue.getData(), (byte)0, false);
    if (paramDerValue.getData().available() > 0) {
      DerValue derValue = paramDerValue.getData().getDerValue();
      if ((derValue.getTag() & 0x1F) == 1) {
        this.pAUSec = new Integer(derValue.getData().getBigInteger().intValue());
      } else {
        throw new Asn1Exception(906);
      } 
    } 
    if (paramDerValue.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), this.pATimeStamp.asn1Encode());
    if (this.pAUSec != null) {
      derOutputStream2 = new DerOutputStream();
      derOutputStream2.putInteger(BigInteger.valueOf(this.pAUSec.intValue()));
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), derOutputStream2);
    } 
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\PAEncTSEnc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */