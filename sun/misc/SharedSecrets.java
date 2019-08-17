package sun.misc;

public class SharedSecrets {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static JavaUtilJarAccess javaUtilJarAccess;
  
  private static JavaLangAccess javaLangAccess;
  
  private static JavaLangRefAccess javaLangRefAccess;
  
  private static JavaIOAccess javaIOAccess;
  
  private static JavaNetAccess javaNetAccess;
  
  private static JavaNetHttpCookieAccess javaNetHttpCookieAccess;
  
  private static JavaNioAccess javaNioAccess;
  
  private static JavaIOFileDescriptorAccess javaIOFileDescriptorAccess;
  
  private static JavaSecurityProtectionDomainAccess javaSecurityProtectionDomainAccess;
  
  private static JavaSecurityAccess javaSecurityAccess;
  
  private static JavaUtilZipFileAccess javaUtilZipFileAccess;
  
  private static JavaAWTAccess javaAWTAccess;
  
  private static JavaOISAccess javaOISAccess;
  
  private static JavaxCryptoSealedObjectAccess javaxCryptoSealedObjectAccess;
  
  private static JavaObjectInputStreamAccess javaObjectInputStreamAccess;
  
  public static JavaUtilJarAccess javaUtilJarAccess() {
    if (javaUtilJarAccess == null)
      unsafe.ensureClassInitialized(java.util.jar.JarFile.class); 
    return javaUtilJarAccess;
  }
  
  public static void setJavaUtilJarAccess(JavaUtilJarAccess paramJavaUtilJarAccess) { javaUtilJarAccess = paramJavaUtilJarAccess; }
  
  public static void setJavaLangAccess(JavaLangAccess paramJavaLangAccess) { javaLangAccess = paramJavaLangAccess; }
  
  public static JavaLangAccess getJavaLangAccess() { return javaLangAccess; }
  
  public static void setJavaLangRefAccess(JavaLangRefAccess paramJavaLangRefAccess) { javaLangRefAccess = paramJavaLangRefAccess; }
  
  public static JavaLangRefAccess getJavaLangRefAccess() { return javaLangRefAccess; }
  
  public static void setJavaNetAccess(JavaNetAccess paramJavaNetAccess) { javaNetAccess = paramJavaNetAccess; }
  
  public static JavaNetAccess getJavaNetAccess() { return javaNetAccess; }
  
  public static void setJavaNetHttpCookieAccess(JavaNetHttpCookieAccess paramJavaNetHttpCookieAccess) { javaNetHttpCookieAccess = paramJavaNetHttpCookieAccess; }
  
  public static JavaNetHttpCookieAccess getJavaNetHttpCookieAccess() {
    if (javaNetHttpCookieAccess == null)
      unsafe.ensureClassInitialized(java.net.HttpCookie.class); 
    return javaNetHttpCookieAccess;
  }
  
  public static void setJavaNioAccess(JavaNioAccess paramJavaNioAccess) { javaNioAccess = paramJavaNioAccess; }
  
  public static JavaNioAccess getJavaNioAccess() {
    if (javaNioAccess == null)
      unsafe.ensureClassInitialized(java.nio.ByteOrder.class); 
    return javaNioAccess;
  }
  
  public static void setJavaIOAccess(JavaIOAccess paramJavaIOAccess) { javaIOAccess = paramJavaIOAccess; }
  
  public static JavaIOAccess getJavaIOAccess() {
    if (javaIOAccess == null)
      unsafe.ensureClassInitialized(java.io.Console.class); 
    return javaIOAccess;
  }
  
  public static void setJavaIOFileDescriptorAccess(JavaIOFileDescriptorAccess paramJavaIOFileDescriptorAccess) { javaIOFileDescriptorAccess = paramJavaIOFileDescriptorAccess; }
  
  public static JavaIOFileDescriptorAccess getJavaIOFileDescriptorAccess() {
    if (javaIOFileDescriptorAccess == null)
      unsafe.ensureClassInitialized(java.io.FileDescriptor.class); 
    return javaIOFileDescriptorAccess;
  }
  
  public static void setJavaOISAccess(JavaOISAccess paramJavaOISAccess) { javaOISAccess = paramJavaOISAccess; }
  
  public static JavaOISAccess getJavaOISAccess() {
    if (javaOISAccess == null)
      unsafe.ensureClassInitialized(java.io.ObjectInputStream.class); 
    return javaOISAccess;
  }
  
  public static void setJavaSecurityProtectionDomainAccess(JavaSecurityProtectionDomainAccess paramJavaSecurityProtectionDomainAccess) { javaSecurityProtectionDomainAccess = paramJavaSecurityProtectionDomainAccess; }
  
  public static JavaSecurityProtectionDomainAccess getJavaSecurityProtectionDomainAccess() {
    if (javaSecurityProtectionDomainAccess == null)
      unsafe.ensureClassInitialized(java.security.ProtectionDomain.class); 
    return javaSecurityProtectionDomainAccess;
  }
  
  public static void setJavaSecurityAccess(JavaSecurityAccess paramJavaSecurityAccess) { javaSecurityAccess = paramJavaSecurityAccess; }
  
  public static JavaSecurityAccess getJavaSecurityAccess() {
    if (javaSecurityAccess == null)
      unsafe.ensureClassInitialized(java.security.AccessController.class); 
    return javaSecurityAccess;
  }
  
  public static JavaUtilZipFileAccess getJavaUtilZipFileAccess() {
    if (javaUtilZipFileAccess == null)
      unsafe.ensureClassInitialized(java.util.zip.ZipFile.class); 
    return javaUtilZipFileAccess;
  }
  
  public static void setJavaUtilZipFileAccess(JavaUtilZipFileAccess paramJavaUtilZipFileAccess) { javaUtilZipFileAccess = paramJavaUtilZipFileAccess; }
  
  public static void setJavaAWTAccess(JavaAWTAccess paramJavaAWTAccess) { javaAWTAccess = paramJavaAWTAccess; }
  
  public static JavaAWTAccess getJavaAWTAccess() { return (javaAWTAccess == null) ? null : javaAWTAccess; }
  
  public static JavaObjectInputStreamAccess getJavaObjectInputStreamAccess() {
    if (javaObjectInputStreamAccess == null)
      unsafe.ensureClassInitialized(java.io.ObjectInputStream.class); 
    return javaObjectInputStreamAccess;
  }
  
  public static void setJavaObjectInputStreamAccess(JavaObjectInputStreamAccess paramJavaObjectInputStreamAccess) { javaObjectInputStreamAccess = paramJavaObjectInputStreamAccess; }
  
  public static void setJavaxCryptoSealedObjectAccess(JavaxCryptoSealedObjectAccess paramJavaxCryptoSealedObjectAccess) { javaxCryptoSealedObjectAccess = paramJavaxCryptoSealedObjectAccess; }
  
  public static JavaxCryptoSealedObjectAccess getJavaxCryptoSealedObjectAccess() {
    if (javaxCryptoSealedObjectAccess == null)
      unsafe.ensureClassInitialized(javax.crypto.SealedObject.class); 
    return javaxCryptoSealedObjectAccess;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\SharedSecrets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */