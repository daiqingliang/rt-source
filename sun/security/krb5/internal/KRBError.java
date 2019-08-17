package sun.security.krb5.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.Checksum;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KRBError implements Serializable {
  static final long serialVersionUID = 3643809337475284503L;
  
  private int pvno;
  
  private int msgType;
  
  private KerberosTime cTime;
  
  private Integer cuSec;
  
  private KerberosTime sTime;
  
  private Integer suSec;
  
  private int errorCode;
  
  private PrincipalName cname;
  
  private PrincipalName sname;
  
  private String eText;
  
  private byte[] eData;
  
  private Checksum eCksum;
  
  private PAData[] pa;
  
  private static boolean DEBUG = Krb5.DEBUG;
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    try {
      init(new DerValue((byte[])paramObjectInputStream.readObject()));
      parseEData(this.eData);
    } catch (Exception exception) {
      throw new IOException(exception);
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    try {
      paramObjectOutputStream.writeObject(asn1Encode());
    } catch (Exception exception) {
      throw new IOException(exception);
    } 
  }
  
  public KRBError(APOptions paramAPOptions, KerberosTime paramKerberosTime1, Integer paramInteger1, KerberosTime paramKerberosTime2, Integer paramInteger2, int paramInt, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, String paramString, byte[] paramArrayOfByte) throws IOException, Asn1Exception {
    this.pvno = 5;
    this.msgType = 30;
    this.cTime = paramKerberosTime1;
    this.cuSec = paramInteger1;
    this.sTime = paramKerberosTime2;
    this.suSec = paramInteger2;
    this.errorCode = paramInt;
    this.cname = paramPrincipalName1;
    this.sname = paramPrincipalName2;
    this.eText = paramString;
    this.eData = paramArrayOfByte;
    parseEData(this.eData);
  }
  
  public KRBError(APOptions paramAPOptions, KerberosTime paramKerberosTime1, Integer paramInteger1, KerberosTime paramKerberosTime2, Integer paramInteger2, int paramInt, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, String paramString, byte[] paramArrayOfByte, Checksum paramChecksum) throws IOException, Asn1Exception {
    this.pvno = 5;
    this.msgType = 30;
    this.cTime = paramKerberosTime1;
    this.cuSec = paramInteger1;
    this.sTime = paramKerberosTime2;
    this.suSec = paramInteger2;
    this.errorCode = paramInt;
    this.cname = paramPrincipalName1;
    this.sname = paramPrincipalName2;
    this.eText = paramString;
    this.eData = paramArrayOfByte;
    this.eCksum = paramChecksum;
    parseEData(this.eData);
  }
  
  public KRBError(byte[] paramArrayOfByte) throws Asn1Exception, RealmException, KrbApErrException, IOException {
    init(new DerValue(paramArrayOfByte));
    parseEData(this.eData);
  }
  
  public KRBError(DerValue paramDerValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
    init(paramDerValue);
    showDebug();
    parseEData(this.eData);
  }
  
  private void parseEData(byte[] paramArrayOfByte) throws Asn1Exception, RealmException, KrbApErrException, IOException {
    if (paramArrayOfByte == null)
      return; 
    if (this.errorCode == 25 || this.errorCode == 24) {
      try {
        parsePAData(paramArrayOfByte);
      } catch (Exception exception) {
        if (DEBUG)
          System.out.println("Unable to parse eData field of KRB-ERROR:\n" + (new HexDumpEncoder()).encodeBuffer(paramArrayOfByte)); 
        IOException iOException = new IOException("Unable to parse eData field of KRB-ERROR");
        iOException.initCause(exception);
        throw iOException;
      } 
    } else if (DEBUG) {
      System.out.println("Unknown eData field of KRB-ERROR:\n" + (new HexDumpEncoder()).encodeBuffer(paramArrayOfByte));
    } 
  }
  
  private void parsePAData(byte[] paramArrayOfByte) throws Asn1Exception, RealmException, KrbApErrException, IOException {
    DerValue derValue = new DerValue(paramArrayOfByte);
    ArrayList arrayList = new ArrayList();
    while (derValue.data.available() > 0) {
      DerValue derValue1 = derValue.data.getDerValue();
      PAData pAData = new PAData(derValue1);
      arrayList.add(pAData);
      if (DEBUG)
        System.out.println(pAData); 
    } 
    this.pa = (PAData[])arrayList.toArray(new PAData[arrayList.size()]);
  }
  
  public final KerberosTime getServerTime() { return this.sTime; }
  
  public final KerberosTime getClientTime() { return this.cTime; }
  
  public final Integer getServerMicroSeconds() { return this.suSec; }
  
  public final Integer getClientMicroSeconds() { return this.cuSec; }
  
  public final int getErrorCode() { return this.errorCode; }
  
  public final PAData[] getPA() { return this.pa; }
  
  public final String getErrorString() { return this.eText; }
  
  private void init(DerValue paramDerValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
    if ((paramDerValue.getTag() & 0x1F) != 30 || paramDerValue.isApplication() != true || paramDerValue.isConstructed() != true)
      throw new Asn1Exception(906); 
    DerValue derValue1 = paramDerValue.getData().getDerValue();
    if (derValue1.getTag() != 48)
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) == 0) {
      this.pvno = derValue2.getData().getBigInteger().intValue();
      if (this.pvno != 5)
        throw new KrbApErrException(39); 
    } else {
      throw new Asn1Exception(906);
    } 
    derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) == 1) {
      this.msgType = derValue2.getData().getBigInteger().intValue();
      if (this.msgType != 30)
        throw new KrbApErrException(40); 
    } else {
      throw new Asn1Exception(906);
    } 
    this.cTime = KerberosTime.parse(derValue1.getData(), (byte)2, true);
    if ((derValue1.getData().peekByte() & 0x1F) == 3) {
      derValue2 = derValue1.getData().getDerValue();
      this.cuSec = new Integer(derValue2.getData().getBigInteger().intValue());
    } else {
      this.cuSec = null;
    } 
    this.sTime = KerberosTime.parse(derValue1.getData(), (byte)4, false);
    derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) == 5) {
      this.suSec = new Integer(derValue2.getData().getBigInteger().intValue());
    } else {
      throw new Asn1Exception(906);
    } 
    derValue2 = derValue1.getData().getDerValue();
    if ((derValue2.getTag() & 0x1F) == 6) {
      this.errorCode = derValue2.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    Realm realm1 = Realm.parse(derValue1.getData(), (byte)7, true);
    this.cname = PrincipalName.parse(derValue1.getData(), (byte)8, true, realm1);
    Realm realm2 = Realm.parse(derValue1.getData(), (byte)9, false);
    this.sname = PrincipalName.parse(derValue1.getData(), (byte)10, false, realm2);
    this.eText = null;
    this.eData = null;
    this.eCksum = null;
    if (derValue1.getData().available() > 0 && (derValue1.getData().peekByte() & 0x1F) == 11) {
      derValue2 = derValue1.getData().getDerValue();
      this.eText = (new KerberosString(derValue2.getData().getDerValue())).toString();
    } 
    if (derValue1.getData().available() > 0 && (derValue1.getData().peekByte() & 0x1F) == 12) {
      derValue2 = derValue1.getData().getDerValue();
      this.eData = derValue2.getData().getOctetString();
    } 
    if (derValue1.getData().available() > 0)
      this.eCksum = Checksum.parse(derValue1.getData(), (byte)13, true); 
    if (derValue1.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  private void showDebug() {
    if (DEBUG) {
      System.out.println(">>>KRBError:");
      if (this.cTime != null)
        System.out.println("\t cTime is " + this.cTime.toDate().toString() + " " + this.cTime.toDate().getTime()); 
      if (this.cuSec != null)
        System.out.println("\t cuSec is " + this.cuSec.intValue()); 
      System.out.println("\t sTime is " + this.sTime.toDate().toString() + " " + this.sTime.toDate().getTime());
      System.out.println("\t suSec is " + this.suSec);
      System.out.println("\t error code is " + this.errorCode);
      System.out.println("\t error Message is " + Krb5.getErrorMessage(this.errorCode));
      if (this.cname != null)
        System.out.println("\t cname is " + this.cname.toString()); 
      if (this.sname != null)
        System.out.println("\t sname is " + this.sname.toString()); 
      if (this.eData != null)
        System.out.println("\t eData provided."); 
      if (this.eCksum != null)
        System.out.println("\t checksum provided."); 
      System.out.println("\t msgType is " + this.msgType);
    } 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream1.putInteger(BigInteger.valueOf(this.pvno));
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)0), derOutputStream1);
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.putInteger(BigInteger.valueOf(this.msgType));
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)1), derOutputStream1);
    if (this.cTime != null)
      derOutputStream2.write(DerValue.createTag(-128, true, (byte)2), this.cTime.asn1Encode()); 
    if (this.cuSec != null) {
      derOutputStream1 = new DerOutputStream();
      derOutputStream1.putInteger(BigInteger.valueOf(this.cuSec.intValue()));
      derOutputStream2.write(DerValue.createTag(-128, true, (byte)3), derOutputStream1);
    } 
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)4), this.sTime.asn1Encode());
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.putInteger(BigInteger.valueOf(this.suSec.intValue()));
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)5), derOutputStream1);
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.putInteger(BigInteger.valueOf(this.errorCode));
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)6), derOutputStream1);
    if (this.cname != null) {
      derOutputStream2.write(DerValue.createTag(-128, true, (byte)7), this.cname.getRealm().asn1Encode());
      derOutputStream2.write(DerValue.createTag(-128, true, (byte)8), this.cname.asn1Encode());
    } 
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)9), this.sname.getRealm().asn1Encode());
    derOutputStream2.write(DerValue.createTag(-128, true, (byte)10), this.sname.asn1Encode());
    if (this.eText != null) {
      derOutputStream1 = new DerOutputStream();
      derOutputStream1.putDerValue((new KerberosString(this.eText)).toDerValue());
      derOutputStream2.write(DerValue.createTag(-128, true, (byte)11), derOutputStream1);
    } 
    if (this.eData != null) {
      derOutputStream1 = new DerOutputStream();
      derOutputStream1.putOctetString(this.eData);
      derOutputStream2.write(DerValue.createTag(-128, true, (byte)12), derOutputStream1);
    } 
    if (this.eCksum != null)
      derOutputStream2.write(DerValue.createTag(-128, true, (byte)13), this.eCksum.asn1Encode()); 
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.write((byte)48, derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write(DerValue.createTag((byte)64, true, (byte)30), derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public boolean equals(Object paramObject) {
    KRBError kRBError;
    return (this == paramObject) ? true : (!(paramObject instanceof KRBError) ? false : ((this.pvno == kRBError.pvno && this.msgType == kRBError.msgType && (kRBError = (KRBError)paramObject).isEqual(this.cTime, kRBError.cTime) && isEqual(this.cuSec, kRBError.cuSec) && isEqual(this.sTime, kRBError.sTime) && isEqual(this.suSec, kRBError.suSec) && this.errorCode == kRBError.errorCode && isEqual(this.cname, kRBError.cname) && isEqual(this.sname, kRBError.sname) && isEqual(this.eText, kRBError.eText) && Arrays.equals(this.eData, kRBError.eData) && isEqual(this.eCksum, kRBError.eCksum))));
  }
  
  private static boolean isEqual(Object paramObject1, Object paramObject2) { return (paramObject1 == null) ? ((paramObject2 == null)) : paramObject1.equals(paramObject2); }
  
  public int hashCode() {
    int i = 17;
    i = 37 * i + this.pvno;
    i = 37 * i + this.msgType;
    if (this.cTime != null)
      i = 37 * i + this.cTime.hashCode(); 
    if (this.cuSec != null)
      i = 37 * i + this.cuSec.hashCode(); 
    if (this.sTime != null)
      i = 37 * i + this.sTime.hashCode(); 
    if (this.suSec != null)
      i = 37 * i + this.suSec.hashCode(); 
    i = 37 * i + this.errorCode;
    if (this.cname != null)
      i = 37 * i + this.cname.hashCode(); 
    if (this.sname != null)
      i = 37 * i + this.sname.hashCode(); 
    if (this.eText != null)
      i = 37 * i + this.eText.hashCode(); 
    i = 37 * i + Arrays.hashCode(this.eData);
    if (this.eCksum != null)
      i = 37 * i + this.eCksum.hashCode(); 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\KRBError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */