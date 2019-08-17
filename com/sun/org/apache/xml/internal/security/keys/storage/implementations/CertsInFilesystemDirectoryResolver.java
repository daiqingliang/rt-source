package com.sun.org.apache.xml.internal.security.keys.storage.implementations;

import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverException;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverSpi;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CertsInFilesystemDirectoryResolver extends StorageResolverSpi {
  private static Logger log = Logger.getLogger(CertsInFilesystemDirectoryResolver.class.getName());
  
  private String merlinsCertificatesDir = null;
  
  private List<X509Certificate> certs = new ArrayList();
  
  public CertsInFilesystemDirectoryResolver(String paramString) throws StorageResolverException {
    this.merlinsCertificatesDir = paramString;
    readCertsFromHarddrive();
  }
  
  private void readCertsFromHarddrive() throws StorageResolverException {
    File file = new File(this.merlinsCertificatesDir);
    ArrayList arrayList = new ArrayList();
    String[] arrayOfString = file.list();
    for (byte b1 = 0; b1 < arrayOfString.length; b1++) {
      String str = arrayOfString[b1];
      if (str.endsWith(".crt"))
        arrayList.add(arrayOfString[b1]); 
    } 
    CertificateFactory certificateFactory = null;
    try {
      certificateFactory = CertificateFactory.getInstance("X.509");
    } catch (CertificateException certificateException) {
      throw new StorageResolverException("empty", certificateException);
    } 
    if (certificateFactory == null)
      throw new StorageResolverException("empty"); 
    for (byte b2 = 0; b2 < arrayList.size(); b2++) {
      str1 = file.getAbsolutePath() + File.separator + (String)arrayList.get(b2);
      File file1 = new File(str1);
      boolean bool = false;
      String str2 = null;
      fileInputStream = null;
      try {
        fileInputStream = new FileInputStream(file1);
        x509Certificate = (X509Certificate)certificateFactory.generateCertificate(fileInputStream);
        x509Certificate.checkValidity();
        this.certs.add(x509Certificate);
        str2 = x509Certificate.getSubjectX500Principal().getName();
        bool = true;
      } catch (FileNotFoundException fileNotFoundException) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "Could not add certificate from file " + str1, fileNotFoundException); 
      } catch (CertificateNotYetValidException certificateNotYetValidException) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "Could not add certificate from file " + str1, certificateNotYetValidException); 
      } catch (CertificateExpiredException certificateExpiredException) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "Could not add certificate from file " + str1, certificateExpiredException); 
      } catch (CertificateException certificateException) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "Could not add certificate from file " + str1, certificateException); 
      } finally {
        try {
          if (fileInputStream != null)
            fileInputStream.close(); 
        } catch (IOException iOException) {
          if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, "Could not add certificate from file " + str1, iOException); 
        } 
      } 
      if (bool && log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Added certificate: " + str2); 
    } 
  }
  
  public Iterator<Certificate> getIterator() { return new FilesystemIterator(this.certs); }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    CertsInFilesystemDirectoryResolver certsInFilesystemDirectoryResolver = new CertsInFilesystemDirectoryResolver("data/ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/certs");
    Iterator iterator = certsInFilesystemDirectoryResolver.getIterator();
    while (iterator.hasNext()) {
      X509Certificate x509Certificate = (X509Certificate)iterator.next();
      byte[] arrayOfByte = XMLX509SKI.getSKIBytesFromCert(x509Certificate);
      System.out.println();
      System.out.println("Base64(SKI())=                 \"" + Base64.encode(arrayOfByte) + "\"");
      System.out.println("cert.getSerialNumber()=        \"" + x509Certificate.getSerialNumber().toString() + "\"");
      System.out.println("cert.getSubjectX500Principal().getName()= \"" + x509Certificate.getSubjectX500Principal().getName() + "\"");
      System.out.println("cert.getIssuerX500Principal().getName()=  \"" + x509Certificate.getIssuerX500Principal().getName() + "\"");
    } 
  }
  
  private static class FilesystemIterator extends Object implements Iterator<Certificate> {
    List<X509Certificate> certs = null;
    
    int i;
    
    public FilesystemIterator(List<X509Certificate> param1List) {
      this.certs = param1List;
      this.i = 0;
    }
    
    public boolean hasNext() { return (this.i < this.certs.size()); }
    
    public Certificate next() { return (Certificate)this.certs.get(this.i++); }
    
    public void remove() throws StorageResolverException { throw new UnsupportedOperationException("Can't remove keys from KeyStore"); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\storage\implementations\CertsInFilesystemDirectoryResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */