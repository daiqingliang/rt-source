package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class ETypeInfo2 {
  private int etype;
  
  private String saltStr = null;
  
  private byte[] s2kparams = null;
  
  private static final byte TAG_TYPE = 0;
  
  private static final byte TAG_VALUE1 = 1;
  
  private static final byte TAG_VALUE2 = 2;
  
  private ETypeInfo2() {}
  
  public ETypeInfo2(int paramInt, String paramString, byte[] paramArrayOfByte) {
    this.etype = paramInt;
    this.saltStr = paramString;
    if (paramArrayOfByte != null)
      this.s2kparams = (byte[])paramArrayOfByte.clone(); 
  }
  
  public Object clone() {
    ETypeInfo2 eTypeInfo2 = new ETypeInfo2();
    eTypeInfo2.etype = this.etype;
    eTypeInfo2.saltStr = this.saltStr;
    if (this.s2kparams != null) {
      eTypeInfo2.s2kparams = new byte[this.s2kparams.length];
      System.arraycopy(this.s2kparams, 0, eTypeInfo2.s2kparams, 0, this.s2kparams.length);
    } 
    return eTypeInfo2;
  }
  
  public ETypeInfo2(DerValue paramDerValue) throws Asn1Exception, IOException {
    DerValue derValue = null;
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 0) {
      this.etype = derValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    if (paramDerValue.getData().available() > 0 && (paramDerValue.getData().peekByte() & 0x1F) == 1) {
      derValue = paramDerValue.getData().getDerValue();
      this.saltStr = (new KerberosString(derValue.getData().getDerValue())).toString();
    } 
    if (paramDerValue.getData().available() > 0 && (paramDerValue.getData().peekByte() & 0x1F) == 2) {
      derValue = paramDerValue.getData().getDerValue();
      this.s2kparams = derValue.getData().getOctetString();
    } 
    if (paramDerValue.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putInteger(this.etype);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
    if (this.saltStr != null) {
      derOutputStream2 = new DerOutputStream();
      derOutputStream2.putDerValue((new KerberosString(this.saltStr)).toDerValue());
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), derOutputStream2);
    } 
    if (this.s2kparams != null) {
      derOutputStream2 = new DerOutputStream();
      derOutputStream2.putOctetString(this.s2kparams);
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)2), derOutputStream2);
    } 
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public int getEType() { return this.etype; }
  
  public String getSalt() { return this.saltStr; }
  
  public byte[] getParams() throws Asn1Exception, IOException { return (this.s2kparams == null) ? null : (byte[])this.s2kparams.clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\ETypeInfo2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */