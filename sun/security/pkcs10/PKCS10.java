package sun.security.pkcs10;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Base64;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X500Name;
import sun.security.x509.X509Key;

public class PKCS10 {
  private X500Name subject;
  
  private PublicKey subjectPublicKeyInfo;
  
  private String sigAlg;
  
  private PKCS10Attributes attributeSet;
  
  private byte[] encoded;
  
  public PKCS10(PublicKey paramPublicKey) {
    this.subjectPublicKeyInfo = paramPublicKey;
    this.attributeSet = new PKCS10Attributes();
  }
  
  public PKCS10(PublicKey paramPublicKey, PKCS10Attributes paramPKCS10Attributes) {
    this.subjectPublicKeyInfo = paramPublicKey;
    this.attributeSet = paramPKCS10Attributes;
  }
  
  public PKCS10(byte[] paramArrayOfByte) throws IOException, SignatureException, NoSuchAlgorithmException {
    this.encoded = paramArrayOfByte;
    DerInputStream derInputStream = new DerInputStream(paramArrayOfByte);
    DerValue[] arrayOfDerValue = derInputStream.getSequence(3);
    if (arrayOfDerValue.length != 3)
      throw new IllegalArgumentException("not a PKCS #10 request"); 
    paramArrayOfByte = arrayOfDerValue[0].toByteArray();
    AlgorithmId algorithmId = AlgorithmId.parse(arrayOfDerValue[1]);
    byte[] arrayOfByte = arrayOfDerValue[2].getBitString();
    BigInteger bigInteger = (arrayOfDerValue[0]).data.getBigInteger();
    if (!bigInteger.equals(BigInteger.ZERO))
      throw new IllegalArgumentException("not PKCS #10 v1"); 
    this.subject = new X500Name((arrayOfDerValue[0]).data);
    this.subjectPublicKeyInfo = X509Key.parse((arrayOfDerValue[0]).data.getDerValue());
    if ((arrayOfDerValue[0]).data.available() != 0) {
      this.attributeSet = new PKCS10Attributes((arrayOfDerValue[0]).data);
    } else {
      this.attributeSet = new PKCS10Attributes();
    } 
    if ((arrayOfDerValue[0]).data.available() != 0)
      throw new IllegalArgumentException("illegal PKCS #10 data"); 
    try {
      this.sigAlg = algorithmId.getName();
      Signature signature = Signature.getInstance(this.sigAlg);
      signature.initVerify(this.subjectPublicKeyInfo);
      signature.update(paramArrayOfByte);
      if (!signature.verify(arrayOfByte))
        throw new SignatureException("Invalid PKCS #10 signature"); 
    } catch (InvalidKeyException invalidKeyException) {
      throw new SignatureException("invalid key");
    } 
  }
  
  public void encodeAndSign(X500Name paramX500Name, Signature paramSignature) throws CertificateException, IOException, SignatureException {
    if (this.encoded != null)
      throw new SignatureException("request is already signed"); 
    this.subject = paramX500Name;
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putInteger(BigInteger.ZERO);
    paramX500Name.encode(derOutputStream2);
    derOutputStream2.write(this.subjectPublicKeyInfo.getEncoded());
    this.attributeSet.encode(derOutputStream2);
    DerOutputStream derOutputStream1 = new DerOutputStream();
    derOutputStream1.write((byte)48, derOutputStream2);
    byte[] arrayOfByte1 = derOutputStream1.toByteArray();
    derOutputStream2 = derOutputStream1;
    paramSignature.update(arrayOfByte1, 0, arrayOfByte1.length);
    byte[] arrayOfByte2 = paramSignature.sign();
    this.sigAlg = paramSignature.getAlgorithm();
    AlgorithmId algorithmId = null;
    try {
      algorithmId = AlgorithmId.get(paramSignature.getAlgorithm());
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new SignatureException(noSuchAlgorithmException);
    } 
    algorithmId.encode(derOutputStream2);
    derOutputStream2.putBitString(arrayOfByte2);
    derOutputStream1 = new DerOutputStream();
    derOutputStream1.write((byte)48, derOutputStream2);
    this.encoded = derOutputStream1.toByteArray();
  }
  
  public X500Name getSubjectName() { return this.subject; }
  
  public PublicKey getSubjectPublicKeyInfo() { return this.subjectPublicKeyInfo; }
  
  public String getSigAlg() { return this.sigAlg; }
  
  public PKCS10Attributes getAttributes() { return this.attributeSet; }
  
  public byte[] getEncoded() { return (this.encoded != null) ? (byte[])this.encoded.clone() : null; }
  
  public void print(PrintStream paramPrintStream) throws IOException, SignatureException {
    if (this.encoded == null)
      throw new SignatureException("Cert request was not signed"); 
    byte[] arrayOfByte = { 13, 10 };
    paramPrintStream.println("-----BEGIN NEW CERTIFICATE REQUEST-----");
    paramPrintStream.println(Base64.getMimeEncoder(64, arrayOfByte).encodeToString(this.encoded));
    paramPrintStream.println("-----END NEW CERTIFICATE REQUEST-----");
  }
  
  public String toString() { return "[PKCS #10 certificate request:\n" + this.subjectPublicKeyInfo.toString() + " subject: <" + this.subject + ">\n attributes: " + this.attributeSet.toString() + "\n]"; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof PKCS10))
      return false; 
    if (this.encoded == null)
      return false; 
    byte[] arrayOfByte = ((PKCS10)paramObject).getEncoded();
    return (arrayOfByte == null) ? false : Arrays.equals(this.encoded, arrayOfByte);
  }
  
  public int hashCode() {
    byte b = 0;
    if (this.encoded != null)
      for (byte b1 = 1; b1 < this.encoded.length; b1++)
        b += this.encoded[b1] * b1;  
    return b;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\pkcs10\PKCS10.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */