package sun.security.provider.certpath;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;
import sun.security.x509.SerialNumber;

public class CertId {
  private static final boolean debug = false;
  
  private static final AlgorithmId SHA1_ALGID = new AlgorithmId(AlgorithmId.SHA_oid);
  
  private final AlgorithmId hashAlgId;
  
  private final byte[] issuerNameHash;
  
  private final byte[] issuerKeyHash;
  
  private final SerialNumber certSerialNumber;
  
  private int myhash = -1;
  
  public CertId(X509Certificate paramX509Certificate, SerialNumber paramSerialNumber) throws IOException { this(paramX509Certificate.getSubjectX500Principal(), paramX509Certificate.getPublicKey(), paramSerialNumber); }
  
  public CertId(X500Principal paramX500Principal, PublicKey paramPublicKey, SerialNumber paramSerialNumber) throws IOException {
    MessageDigest messageDigest = null;
    try {
      messageDigest = MessageDigest.getInstance("SHA1");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new IOException("Unable to create CertId", noSuchAlgorithmException);
    } 
    this.hashAlgId = SHA1_ALGID;
    messageDigest.update(paramX500Principal.getEncoded());
    this.issuerNameHash = messageDigest.digest();
    byte[] arrayOfByte1 = paramPublicKey.getEncoded();
    DerValue derValue = new DerValue(arrayOfByte1);
    DerValue[] arrayOfDerValue = new DerValue[2];
    arrayOfDerValue[0] = derValue.data.getDerValue();
    arrayOfDerValue[1] = derValue.data.getDerValue();
    byte[] arrayOfByte2 = arrayOfDerValue[1].getBitString();
    messageDigest.update(arrayOfByte2);
    this.issuerKeyHash = messageDigest.digest();
    this.certSerialNumber = paramSerialNumber;
  }
  
  public CertId(DerInputStream paramDerInputStream) throws IOException {
    this.hashAlgId = AlgorithmId.parse(paramDerInputStream.getDerValue());
    this.issuerNameHash = paramDerInputStream.getOctetString();
    this.issuerKeyHash = paramDerInputStream.getOctetString();
    this.certSerialNumber = new SerialNumber(paramDerInputStream);
  }
  
  public AlgorithmId getHashAlgorithm() { return this.hashAlgId; }
  
  public byte[] getIssuerNameHash() { return this.issuerNameHash; }
  
  public byte[] getIssuerKeyHash() { return this.issuerKeyHash; }
  
  public BigInteger getSerialNumber() { return this.certSerialNumber.getNumber(); }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    this.hashAlgId.encode(derOutputStream);
    derOutputStream.putOctetString(this.issuerNameHash);
    derOutputStream.putOctetString(this.issuerKeyHash);
    this.certSerialNumber.encode(derOutputStream);
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
  
  public int hashCode() {
    if (this.myhash == -1) {
      this.myhash = this.hashAlgId.hashCode();
      byte b;
      for (b = 0; b < this.issuerNameHash.length; b++)
        this.myhash += this.issuerNameHash[b] * b; 
      for (b = 0; b < this.issuerKeyHash.length; b++)
        this.myhash += this.issuerKeyHash[b] * b; 
      this.myhash += this.certSerialNumber.getNumber().hashCode();
    } 
    return this.myhash;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || !(paramObject instanceof CertId))
      return false; 
    CertId certId = (CertId)paramObject;
    return (this.hashAlgId.equals(certId.getHashAlgorithm()) && Arrays.equals(this.issuerNameHash, certId.getIssuerNameHash()) && Arrays.equals(this.issuerKeyHash, certId.getIssuerKeyHash()) && this.certSerialNumber.getNumber().equals(certId.getSerialNumber()));
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("CertId \n");
    stringBuilder.append("Algorithm: " + this.hashAlgId.toString() + "\n");
    stringBuilder.append("issuerNameHash \n");
    HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
    stringBuilder.append(hexDumpEncoder.encode(this.issuerNameHash));
    stringBuilder.append("\nissuerKeyHash: \n");
    stringBuilder.append(hexDumpEncoder.encode(this.issuerKeyHash));
    stringBuilder.append("\n" + this.certSerialNumber.toString());
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\CertId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */