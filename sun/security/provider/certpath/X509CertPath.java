package sun.security.provider.certpath;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.PKCS7;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class X509CertPath extends CertPath {
  private static final long serialVersionUID = 4989800333263052980L;
  
  private List<X509Certificate> certs;
  
  private static final String COUNT_ENCODING = "count";
  
  private static final String PKCS7_ENCODING = "PKCS7";
  
  private static final String PKIPATH_ENCODING = "PkiPath";
  
  private static final Collection<String> encodingList;
  
  public X509CertPath(List<? extends Certificate> paramList) throws CertificateException {
    super("X.509");
    for (Object object : paramList) {
      if (!(object instanceof X509Certificate))
        throw new CertificateException("List is not all X509Certificates: " + object.getClass().getName()); 
    } 
    this.certs = Collections.unmodifiableList(new ArrayList(paramList));
  }
  
  public X509CertPath(InputStream paramInputStream) throws CertificateException { this(paramInputStream, "PkiPath"); }
  
  public X509CertPath(InputStream paramInputStream, String paramString) throws CertificateException {
    super("X.509");
    switch (paramString) {
      case "PkiPath":
        this.certs = parsePKIPATH(paramInputStream);
        return;
      case "PKCS7":
        this.certs = parsePKCS7(paramInputStream);
        return;
    } 
    throw new CertificateException("unsupported encoding");
  }
  
  private static List<X509Certificate> parsePKIPATH(InputStream paramInputStream) throws CertificateException {
    ArrayList arrayList = null;
    CertificateFactory certificateFactory = null;
    if (paramInputStream == null)
      throw new CertificateException("input stream is null"); 
    try {
      DerInputStream derInputStream = new DerInputStream(readAllBytes(paramInputStream));
      DerValue[] arrayOfDerValue = derInputStream.getSequence(3);
      if (arrayOfDerValue.length == 0)
        return Collections.emptyList(); 
      certificateFactory = CertificateFactory.getInstance("X.509");
      arrayList = new ArrayList(arrayOfDerValue.length);
      for (int i = arrayOfDerValue.length - 1; i >= 0; i--)
        arrayList.add((X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(arrayOfDerValue[i].toByteArray()))); 
      return Collections.unmodifiableList(arrayList);
    } catch (IOException iOException) {
      throw new CertificateException("IOException parsing PkiPath data: " + iOException, iOException);
    } 
  }
  
  private static List<X509Certificate> parsePKCS7(InputStream paramInputStream) throws CertificateException {
    ArrayList arrayList;
    if (paramInputStream == null)
      throw new CertificateException("input stream is null"); 
    try {
      if (!paramInputStream.markSupported())
        paramInputStream = new ByteArrayInputStream(readAllBytes(paramInputStream)); 
      PKCS7 pKCS7 = new PKCS7(paramInputStream);
      X509Certificate[] arrayOfX509Certificate = pKCS7.getCertificates();
      if (arrayOfX509Certificate != null) {
        arrayList = Arrays.asList(arrayOfX509Certificate);
      } else {
        arrayList = new ArrayList(0);
      } 
    } catch (IOException iOException) {
      throw new CertificateException("IOException parsing PKCS7 data: " + iOException);
    } 
    return Collections.unmodifiableList(arrayList);
  }
  
  private static byte[] readAllBytes(InputStream paramInputStream) throws IOException {
    byte[] arrayOfByte = new byte[8192];
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
    int i;
    while ((i = paramInputStream.read(arrayOfByte)) != -1)
      byteArrayOutputStream.write(arrayOfByte, 0, i); 
    return byteArrayOutputStream.toByteArray();
  }
  
  public byte[] getEncoded() throws CertificateEncodingException { return encodePKIPATH(); }
  
  private byte[] encodePKIPATH() throws CertificateEncodingException {
    ListIterator listIterator = this.certs.listIterator(this.certs.size());
    try {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      while (listIterator.hasPrevious()) {
        X509Certificate x509Certificate = (X509Certificate)listIterator.previous();
        if (this.certs.lastIndexOf(x509Certificate) != this.certs.indexOf(x509Certificate))
          throw new CertificateEncodingException("Duplicate Certificate"); 
        byte[] arrayOfByte = x509Certificate.getEncoded();
        derOutputStream1.write(arrayOfByte);
      } 
      DerOutputStream derOutputStream2 = new DerOutputStream();
      derOutputStream2.write((byte)48, derOutputStream1);
      return derOutputStream2.toByteArray();
    } catch (IOException iOException) {
      throw new CertificateEncodingException("IOException encoding PkiPath data: " + iOException, iOException);
    } 
  }
  
  private byte[] encodePKCS7() throws CertificateEncodingException {
    PKCS7 pKCS7 = new PKCS7(new sun.security.x509.AlgorithmId[0], new ContentInfo(ContentInfo.DATA_OID, null), (X509Certificate[])this.certs.toArray(new X509Certificate[this.certs.size()]), new sun.security.pkcs.SignerInfo[0]);
    DerOutputStream derOutputStream = new DerOutputStream();
    try {
      pKCS7.encodeSignedData(derOutputStream);
    } catch (IOException iOException) {
      throw new CertificateEncodingException(iOException.getMessage());
    } 
    return derOutputStream.toByteArray();
  }
  
  public byte[] getEncoded(String paramString) throws CertificateEncodingException {
    switch (paramString) {
      case "PkiPath":
        return encodePKIPATH();
      case "PKCS7":
        return encodePKCS7();
    } 
    throw new CertificateEncodingException("unsupported encoding");
  }
  
  public static Iterator<String> getEncodingsStatic() { return encodingList.iterator(); }
  
  public Iterator<String> getEncodings() { return getEncodingsStatic(); }
  
  public List<X509Certificate> getCertificates() { return this.certs; }
  
  static  {
    ArrayList arrayList = new ArrayList(2);
    arrayList.add("PkiPath");
    arrayList.add("PKCS7");
    encodingList = Collections.unmodifiableCollection(arrayList);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\X509CertPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */