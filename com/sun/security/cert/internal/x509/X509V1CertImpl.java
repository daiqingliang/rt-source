package com.sun.security.cert.internal.x509;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.security.cert.CertificateEncodingException;
import javax.security.cert.CertificateException;
import javax.security.cert.CertificateExpiredException;
import javax.security.cert.CertificateNotYetValidException;
import javax.security.cert.X509Certificate;

public class X509V1CertImpl extends X509Certificate implements Serializable {
  static final long serialVersionUID = -2048442350420423405L;
  
  private X509Certificate wrappedCert;
  
  private static CertificateFactory getFactory() throws CertificateException { return CertificateFactory.getInstance("X.509"); }
  
  public X509V1CertImpl() {}
  
  public X509V1CertImpl(byte[] paramArrayOfByte) throws CertificateException {
    try {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
      this.wrappedCert = (X509Certificate)getFactory().generateCertificate(byteArrayInputStream);
    } catch (CertificateException certificateException) {
      throw new CertificateException(certificateException.getMessage());
    } 
  }
  
  public X509V1CertImpl(InputStream paramInputStream) throws CertificateException {
    try {
      this.wrappedCert = (X509Certificate)getFactory().generateCertificate(paramInputStream);
    } catch (CertificateException certificateException) {
      throw new CertificateException(certificateException.getMessage());
    } 
  }
  
  public byte[] getEncoded() throws CertificateEncodingException {
    try {
      return this.wrappedCert.getEncoded();
    } catch (CertificateEncodingException certificateEncodingException) {
      throw new CertificateEncodingException(certificateEncodingException.getMessage());
    } 
  }
  
  public void verify(PublicKey paramPublicKey) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
    try {
      this.wrappedCert.verify(paramPublicKey);
    } catch (CertificateException certificateException) {
      throw new CertificateException(certificateException.getMessage());
    } 
  }
  
  public void verify(PublicKey paramPublicKey, String paramString) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
    try {
      this.wrappedCert.verify(paramPublicKey, paramString);
    } catch (CertificateException certificateException) {
      throw new CertificateException(certificateException.getMessage());
    } 
  }
  
  public void checkValidity() { checkValidity(new Date()); }
  
  public void checkValidity(Date paramDate) throws CertificateExpiredException, CertificateNotYetValidException {
    try {
      this.wrappedCert.checkValidity(paramDate);
    } catch (CertificateNotYetValidException certificateNotYetValidException) {
      throw new CertificateNotYetValidException(certificateNotYetValidException.getMessage());
    } catch (CertificateExpiredException certificateExpiredException) {
      throw new CertificateExpiredException(certificateExpiredException.getMessage());
    } 
  }
  
  public String toString() { return this.wrappedCert.toString(); }
  
  public PublicKey getPublicKey() { return this.wrappedCert.getPublicKey(); }
  
  public int getVersion() { return this.wrappedCert.getVersion() - 1; }
  
  public BigInteger getSerialNumber() { return this.wrappedCert.getSerialNumber(); }
  
  public Principal getSubjectDN() { return this.wrappedCert.getSubjectDN(); }
  
  public Principal getIssuerDN() { return this.wrappedCert.getIssuerDN(); }
  
  public Date getNotBefore() { return this.wrappedCert.getNotBefore(); }
  
  public Date getNotAfter() { return this.wrappedCert.getNotAfter(); }
  
  public String getSigAlgName() { return this.wrappedCert.getSigAlgName(); }
  
  public String getSigAlgOID() { return this.wrappedCert.getSigAlgOID(); }
  
  public byte[] getSigAlgParams() throws CertificateEncodingException { return this.wrappedCert.getSigAlgParams(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    try {
      paramObjectOutputStream.write(getEncoded());
    } catch (CertificateEncodingException certificateEncodingException) {
      throw new IOException("getEncoded failed: " + certificateEncodingException.getMessage());
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException {
    try {
      this.wrappedCert = (X509Certificate)getFactory().generateCertificate(paramObjectInputStream);
    } catch (CertificateException certificateException) {
      throw new IOException("generateCertificate failed: " + certificateException.getMessage());
    } 
  }
  
  public X509Certificate getX509Certificate() { return this.wrappedCert; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\cert\internal\x509\X509V1CertImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */