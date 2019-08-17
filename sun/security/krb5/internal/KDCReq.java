package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.KrbException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KDCReq {
  public KDCReqBody reqBody;
  
  private int pvno;
  
  private int msgType;
  
  private PAData[] pAData = null;
  
  public KDCReq(PAData[] paramArrayOfPAData, KDCReqBody paramKDCReqBody, int paramInt) throws IOException {
    this.pvno = 5;
    this.msgType = paramInt;
    if (paramArrayOfPAData != null) {
      this.pAData = new PAData[paramArrayOfPAData.length];
      for (byte b = 0; b < paramArrayOfPAData.length; b++) {
        if (paramArrayOfPAData[b] == null)
          throw new IOException("Cannot create a KDCRep"); 
        this.pAData[b] = (PAData)paramArrayOfPAData[b].clone();
      } 
    } 
    this.reqBody = paramKDCReqBody;
  }
  
  public KDCReq() {}
  
  public KDCReq(byte[] paramArrayOfByte, int paramInt) throws Asn1Exception, IOException, KrbException { init(new DerValue(paramArrayOfByte), paramInt); }
  
  public KDCReq(DerValue paramDerValue, int paramInt) throws Asn1Exception, IOException, KrbException { init(paramDerValue, paramInt); }
  
  protected void init(DerValue paramDerValue, int paramInt) throws Asn1Exception, IOException, KrbException {
    if ((paramDerValue.getTag() & 0x1F) != paramInt)
      throw new Asn1Exception(906); 
    DerValue derValue1 = paramDerValue.getData().getDerValue();
    if (derValue1.getTag() != 48)
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) == 1) {
      BigInteger bigInteger = derValue2.getData().getBigInteger();
      this.pvno = bigInteger.intValue();
      if (this.pvno != 5)
        throw new KrbApErrException(39); 
    } else {
      throw new Asn1Exception(906);
    } 
    derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) == 2) {
      BigInteger bigInteger = derValue2.getData().getBigInteger();
      this.msgType = bigInteger.intValue();
      if (this.msgType != paramInt)
        throw new KrbApErrException(40); 
    } else {
      throw new Asn1Exception(906);
    } 
    if ((derValue1.getData().peekByte() & 0x1F) == 3) {
      derValue2 = derValue1.getData().getDerValue();
      DerValue derValue = derValue2.getData().getDerValue();
      if (derValue.getTag() != 48)
        throw new Asn1Exception(906); 
      Vector vector = new Vector();
      while (derValue.getData().available() > 0)
        vector.addElement(new PAData(derValue.getData().getDerValue())); 
      if (vector.size() > 0) {
        this.pAData = new PAData[vector.size()];
        vector.copyInto(this.pAData);
      } 
    } else {
      this.pAData = null;
    } 
    derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) == 4) {
      DerValue derValue = derValue2.getData().getDerValue();
      this.reqBody = new KDCReqBody(derValue, this.msgType);
    } else {
      throw new Asn1Exception(906);
    } 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    derOutputStream1.putInteger(BigInteger.valueOf(this.pvno));
    DerOutputStream derOutputStream3 = new DerOutputStream();
    derOutputStream3.write(DerValue.createTag(-128, true, (byte)1), derOutputStream1);
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.putInteger(BigInteger.valueOf(this.msgType));
    derOutputStream3.write(DerValue.createTag(-128, true, (byte)2), derOutputStream1);
    if (this.pAData != null && this.pAData.length > 0) {
      derOutputStream1 = new DerOutputStream();
      for (byte b = 0; b < this.pAData.length; b++)
        derOutputStream1.write(this.pAData[b].asn1Encode()); 
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.write((byte)48, derOutputStream1);
      derOutputStream3.write(DerValue.createTag(-128, true, (byte)3), derOutputStream);
    } 
    derOutputStream3.write(DerValue.createTag(-128, true, (byte)4), this.reqBody.asn1Encode(this.msgType));
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream3);
    derOutputStream3 = new DerOutputStream();
    derOutputStream3.write(DerValue.createTag((byte)64, true, (byte)this.msgType), derOutputStream2);
    return derOutputStream3.toByteArray();
  }
  
  public byte[] asn1EncodeReqBody() throws Asn1Exception, IOException { return this.reqBody.asn1Encode(this.msgType); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\KDCReq.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */