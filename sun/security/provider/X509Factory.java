package sun.security.provider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactorySpi;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.ParsingException;
import sun.security.provider.certpath.X509CertPath;
import sun.security.provider.certpath.X509CertificatePair;
import sun.security.util.Cache;
import sun.security.util.Pem;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;

public class X509Factory extends CertificateFactorySpi {
  public static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
  
  public static final String END_CERT = "-----END CERTIFICATE-----";
  
  private static final int ENC_MAX_LENGTH = 4194304;
  
  private static final Cache<Object, X509CertImpl> certCache;
  
  private static final Cache<Object, X509CRLImpl> crlCache = (certCache = Cache.newSoftMemoryCache(750)).newSoftMemoryCache(750);
  
  public Certificate engineGenerateCertificate(InputStream paramInputStream) throws CertificateException {
    if (paramInputStream == null) {
      certCache.clear();
      X509CertificatePair.clearCache();
      throw new CertificateException("Missing input stream");
    } 
    try {
      byte[] arrayOfByte = readOneBlock(paramInputStream);
      if (arrayOfByte != null) {
        X509CertImpl x509CertImpl = (X509CertImpl)getFromCache(certCache, arrayOfByte);
        if (x509CertImpl != null)
          return x509CertImpl; 
        x509CertImpl = new X509CertImpl(arrayOfByte);
        addToCache(certCache, x509CertImpl.getEncodedInternal(), x509CertImpl);
        return x509CertImpl;
      } 
      throw new IOException("Empty input");
    } catch (IOException iOException) {
      throw new CertificateException("Could not parse certificate: " + iOException.toString(), iOException);
    } 
  }
  
  private static int readFully(InputStream paramInputStream, ByteArrayOutputStream paramByteArrayOutputStream, int paramInt) throws IOException {
    int i = 0;
    byte[] arrayOfByte = new byte[2048];
    while (paramInt > 0) {
      int j = paramInputStream.read(arrayOfByte, 0, (paramInt < 2048) ? paramInt : 2048);
      if (j <= 0)
        break; 
      paramByteArrayOutputStream.write(arrayOfByte, 0, j);
      i += j;
      paramInt -= j;
    } 
    return i;
  }
  
  public static X509CertImpl intern(X509Certificate paramX509Certificate) throws CertificateException {
    byte[] arrayOfByte;
    if (paramX509Certificate == null)
      return null; 
    boolean bool = paramX509Certificate instanceof X509CertImpl;
    if (bool) {
      arrayOfByte = ((X509CertImpl)paramX509Certificate).getEncodedInternal();
    } else {
      arrayOfByte = paramX509Certificate.getEncoded();
    } 
    X509CertImpl x509CertImpl = (X509CertImpl)getFromCache(certCache, arrayOfByte);
    if (x509CertImpl != null)
      return x509CertImpl; 
    if (bool) {
      x509CertImpl = (X509CertImpl)paramX509Certificate;
    } else {
      x509CertImpl = new X509CertImpl(arrayOfByte);
      arrayOfByte = x509CertImpl.getEncodedInternal();
    } 
    addToCache(certCache, arrayOfByte, x509CertImpl);
    return x509CertImpl;
  }
  
  public static X509CRLImpl intern(X509CRL paramX509CRL) throws CRLException {
    byte[] arrayOfByte;
    if (paramX509CRL == null)
      return null; 
    boolean bool = paramX509CRL instanceof X509CRLImpl;
    if (bool) {
      arrayOfByte = ((X509CRLImpl)paramX509CRL).getEncodedInternal();
    } else {
      arrayOfByte = paramX509CRL.getEncoded();
    } 
    X509CRLImpl x509CRLImpl = (X509CRLImpl)getFromCache(crlCache, arrayOfByte);
    if (x509CRLImpl != null)
      return x509CRLImpl; 
    if (bool) {
      x509CRLImpl = (X509CRLImpl)paramX509CRL;
    } else {
      x509CRLImpl = new X509CRLImpl(arrayOfByte);
      arrayOfByte = x509CRLImpl.getEncodedInternal();
    } 
    addToCache(crlCache, arrayOfByte, x509CRLImpl);
    return x509CRLImpl;
  }
  
  private static <K, V> V getFromCache(Cache<K, V> paramCache, byte[] paramArrayOfByte) {
    Cache.EqualByteArray equalByteArray = new Cache.EqualByteArray(paramArrayOfByte);
    return (V)paramCache.get(equalByteArray);
  }
  
  private static <V> void addToCache(Cache<Object, V> paramCache, byte[] paramArrayOfByte, V paramV) {
    if (paramArrayOfByte.length > 4194304)
      return; 
    Cache.EqualByteArray equalByteArray = new Cache.EqualByteArray(paramArrayOfByte);
    paramCache.put(equalByteArray, paramV);
  }
  
  public CertPath engineGenerateCertPath(InputStream paramInputStream) throws CertificateException {
    if (paramInputStream == null)
      throw new CertificateException("Missing input stream"); 
    try {
      byte[] arrayOfByte = readOneBlock(paramInputStream);
      if (arrayOfByte != null)
        return new X509CertPath(new ByteArrayInputStream(arrayOfByte)); 
      throw new IOException("Empty input");
    } catch (IOException iOException) {
      throw new CertificateException(iOException.getMessage());
    } 
  }
  
  public CertPath engineGenerateCertPath(InputStream paramInputStream, String paramString) throws CertificateException {
    if (paramInputStream == null)
      throw new CertificateException("Missing input stream"); 
    try {
      byte[] arrayOfByte = readOneBlock(paramInputStream);
      if (arrayOfByte != null)
        return new X509CertPath(new ByteArrayInputStream(arrayOfByte), paramString); 
      throw new IOException("Empty input");
    } catch (IOException iOException) {
      throw new CertificateException(iOException.getMessage());
    } 
  }
  
  public CertPath engineGenerateCertPath(List<? extends Certificate> paramList) throws CertificateException { return new X509CertPath(paramList); }
  
  public Iterator<String> engineGetCertPathEncodings() { return X509CertPath.getEncodingsStatic(); }
  
  public Collection<? extends Certificate> engineGenerateCertificates(InputStream paramInputStream) throws CertificateException {
    if (paramInputStream == null)
      throw new CertificateException("Missing input stream"); 
    try {
      return parseX509orPKCS7Cert(paramInputStream);
    } catch (IOException iOException) {
      throw new CertificateException(iOException);
    } 
  }
  
  public CRL engineGenerateCRL(InputStream paramInputStream) throws CRLException {
    if (paramInputStream == null) {
      crlCache.clear();
      throw new CRLException("Missing input stream");
    } 
    try {
      byte[] arrayOfByte = readOneBlock(paramInputStream);
      if (arrayOfByte != null) {
        X509CRLImpl x509CRLImpl = (X509CRLImpl)getFromCache(crlCache, arrayOfByte);
        if (x509CRLImpl != null)
          return x509CRLImpl; 
        x509CRLImpl = new X509CRLImpl(arrayOfByte);
        addToCache(crlCache, x509CRLImpl.getEncodedInternal(), x509CRLImpl);
        return x509CRLImpl;
      } 
      throw new IOException("Empty input");
    } catch (IOException iOException) {
      throw new CRLException(iOException.getMessage());
    } 
  }
  
  public Collection<? extends CRL> engineGenerateCRLs(InputStream paramInputStream) throws CRLException {
    if (paramInputStream == null)
      throw new CRLException("Missing input stream"); 
    try {
      return parseX509orPKCS7CRL(paramInputStream);
    } catch (IOException iOException) {
      throw new CRLException(iOException.getMessage());
    } 
  }
  
  private Collection<? extends Certificate> parseX509orPKCS7Cert(InputStream paramInputStream) throws CertificateException {
    PushbackInputStream pushbackInputStream = new PushbackInputStream(paramInputStream);
    ArrayList arrayList = new ArrayList();
    int i = pushbackInputStream.read();
    if (i == -1)
      return new ArrayList(0); 
    pushbackInputStream.unread(i);
    byte[] arrayOfByte = readOneBlock(pushbackInputStream);
    if (arrayOfByte == null)
      throw new CertificateException("No certificate data found"); 
    try {
      PKCS7 pKCS7 = new PKCS7(arrayOfByte);
      X509Certificate[] arrayOfX509Certificate = pKCS7.getCertificates();
      return (arrayOfX509Certificate != null) ? Arrays.asList(arrayOfX509Certificate) : new ArrayList(0);
    } catch (ParsingException parsingException) {
      while (arrayOfByte != null) {
        arrayList.add(new X509CertImpl(arrayOfByte));
        arrayOfByte = readOneBlock(pushbackInputStream);
      } 
      return arrayList;
    } 
  }
  
  private Collection<? extends CRL> parseX509orPKCS7CRL(InputStream paramInputStream) throws CRLException {
    PushbackInputStream pushbackInputStream = new PushbackInputStream(paramInputStream);
    ArrayList arrayList = new ArrayList();
    int i = pushbackInputStream.read();
    if (i == -1)
      return new ArrayList(0); 
    pushbackInputStream.unread(i);
    byte[] arrayOfByte = readOneBlock(pushbackInputStream);
    if (arrayOfByte == null)
      throw new CRLException("No CRL data found"); 
    try {
      PKCS7 pKCS7 = new PKCS7(arrayOfByte);
      X509CRL[] arrayOfX509CRL = pKCS7.getCRLs();
      return (arrayOfX509CRL != null) ? Arrays.asList(arrayOfX509CRL) : new ArrayList(0);
    } catch (ParsingException parsingException) {
      while (arrayOfByte != null) {
        arrayList.add(new X509CRLImpl(arrayOfByte));
        arrayOfByte = readOneBlock(pushbackInputStream);
      } 
      return arrayList;
    } 
  }
  
  private static byte[] readOneBlock(InputStream paramInputStream) throws IOException {
    byte b3;
    int i = paramInputStream.read();
    if (i == -1)
      return null; 
    if (i == 48) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
      byteArrayOutputStream.write(i);
      readBERInternal(paramInputStream, byteArrayOutputStream, i);
      return byteArrayOutputStream.toByteArray();
    } 
    char[] arrayOfChar = new char[2048];
    byte b1 = 0;
    byte b2 = (i == 45) ? 1 : 0;
    int j = (i == 45) ? -1 : i;
    do {
      b3 = paramInputStream.read();
      if (b3 == -1)
        return null; 
      if (b3 == 45) {
        b2++;
      } else {
        b2 = 0;
        j = b3;
      } 
    } while (b2 != 5 || (j != -1 && j != 13 && j != 10));
    StringBuilder stringBuilder1 = new StringBuilder("-----");
    while (true) {
      int k = paramInputStream.read();
      if (k == -1)
        throw new IOException("Incomplete data"); 
      if (k == 10) {
        b3 = 10;
        break;
      } 
      if (k == 13) {
        k = paramInputStream.read();
        if (k == -1)
          throw new IOException("Incomplete data"); 
        if (k == 10) {
          byte b = 10;
          break;
        } 
        b3 = 13;
        arrayOfChar[b1++] = (char)k;
        break;
      } 
      stringBuilder1.append((char)k);
    } 
    while (true) {
      int k = paramInputStream.read();
      if (k == -1)
        throw new IOException("Incomplete data"); 
      if (k != 45) {
        arrayOfChar[b1++] = (char)k;
        if (b1 >= arrayOfChar.length)
          arrayOfChar = Arrays.copyOf(arrayOfChar, arrayOfChar.length + 1024); 
        continue;
      } 
      break;
    } 
    StringBuilder stringBuilder2 = new StringBuilder("-");
    while (true) {
      int k = paramInputStream.read();
      if (k == -1 || k == b3 || k == 10)
        break; 
      if (k != 13)
        stringBuilder2.append((char)k); 
    } 
    checkHeaderFooter(stringBuilder1.toString(), stringBuilder2.toString());
    return Pem.decode(new String(arrayOfChar, 0, b1));
  }
  
  private static void checkHeaderFooter(String paramString1, String paramString2) throws IOException {
    if (paramString1.length() < 16 || !paramString1.startsWith("-----BEGIN ") || !paramString1.endsWith("-----"))
      throw new IOException("Illegal header: " + paramString1); 
    if (paramString2.length() < 14 || !paramString2.startsWith("-----END ") || !paramString2.endsWith("-----"))
      throw new IOException("Illegal footer: " + paramString2); 
    String str1 = paramString1.substring(11, paramString1.length() - 5);
    String str2 = paramString2.substring(9, paramString2.length() - 5);
    if (!str1.equals(str2))
      throw new IOException("Header and footer do not match: " + paramString1 + " " + paramString2); 
  }
  
  private static int readBERInternal(InputStream paramInputStream, ByteArrayOutputStream paramByteArrayOutputStream, int paramInt) throws IOException {
    if (paramInt == -1) {
      paramInt = paramInputStream.read();
      if (paramInt == -1)
        throw new IOException("BER/DER tag info absent"); 
      if ((paramInt & 0x1F) == 31)
        throw new IOException("Multi octets tag not supported"); 
      paramByteArrayOutputStream.write(paramInt);
    } 
    int i = paramInputStream.read();
    if (i == -1)
      throw new IOException("BER/DER length info absent"); 
    paramByteArrayOutputStream.write(i);
    if (i == 128) {
      int j;
      if ((paramInt & 0x20) != 32)
        throw new IOException("Non constructed encoding must have definite length"); 
      do {
        j = readBERInternal(paramInputStream, paramByteArrayOutputStream, -1);
      } while (j != 0);
    } else {
      int j;
      if (i < 128) {
        j = i;
      } else if (i == 129) {
        j = paramInputStream.read();
        if (j == -1)
          throw new IOException("Incomplete BER/DER length info"); 
        paramByteArrayOutputStream.write(j);
      } else if (i == 130) {
        int k = paramInputStream.read();
        int m = paramInputStream.read();
        if (m == -1)
          throw new IOException("Incomplete BER/DER length info"); 
        paramByteArrayOutputStream.write(k);
        paramByteArrayOutputStream.write(m);
        j = k << 8 | m;
      } else if (i == 131) {
        int k = paramInputStream.read();
        int m = paramInputStream.read();
        int n = paramInputStream.read();
        if (n == -1)
          throw new IOException("Incomplete BER/DER length info"); 
        paramByteArrayOutputStream.write(k);
        paramByteArrayOutputStream.write(m);
        paramByteArrayOutputStream.write(n);
        j = k << 16 | m << 8 | n;
      } else if (i == 132) {
        int k = paramInputStream.read();
        int m = paramInputStream.read();
        int n = paramInputStream.read();
        int i1 = paramInputStream.read();
        if (i1 == -1)
          throw new IOException("Incomplete BER/DER length info"); 
        if (k > 127)
          throw new IOException("Invalid BER/DER data (a little huge?)"); 
        paramByteArrayOutputStream.write(k);
        paramByteArrayOutputStream.write(m);
        paramByteArrayOutputStream.write(n);
        paramByteArrayOutputStream.write(i1);
        j = k << 24 | m << 16 | n << 8 | i1;
      } else {
        throw new IOException("Invalid BER/DER data (too huge?)");
      } 
      if (readFully(paramInputStream, paramByteArrayOutputStream, j) != j)
        throw new IOException("Incomplete BER/DER data"); 
    } 
    return paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\X509Factory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */