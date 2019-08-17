package sun.security.provider;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.Map;
import sun.security.action.GetPropertyAction;

final class SunEntries {
  private static final boolean useLegacyDSA = Boolean.parseBoolean(GetPropertyAction.privilegedGetProperty("jdk.security.legacyDSAKeyPairGenerator"));
  
  private static final String PROP_EGD = "java.security.egd";
  
  private static final String PROP_RNDSOURCE = "securerandom.source";
  
  static final String URL_DEV_RANDOM = "file:/dev/random";
  
  static final String URL_DEV_URANDOM = "file:/dev/urandom";
  
  private static final String seedSource = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
        public String run() {
          String str = System.getProperty("java.security.egd", "");
          if (str.length() != 0)
            return str; 
          str = Security.getProperty("securerandom.source");
          return (str == null) ? "" : str;
        }
      });
  
  static void putEntries(Map<Object, Object> paramMap) {
    boolean bool = NativePRNG.isAvailable();
    boolean bool1 = (seedSource.equals("file:/dev/urandom") || seedSource.equals("file:/dev/random")) ? 1 : 0;
    if (bool && bool1)
      paramMap.put("SecureRandom.NativePRNG", "sun.security.provider.NativePRNG"); 
    paramMap.put("SecureRandom.SHA1PRNG", "sun.security.provider.SecureRandom");
    if (bool && !bool1)
      paramMap.put("SecureRandom.NativePRNG", "sun.security.provider.NativePRNG"); 
    if (NativePRNG.Blocking.isAvailable())
      paramMap.put("SecureRandom.NativePRNGBlocking", "sun.security.provider.NativePRNG$Blocking"); 
    if (NativePRNG.NonBlocking.isAvailable())
      paramMap.put("SecureRandom.NativePRNGNonBlocking", "sun.security.provider.NativePRNG$NonBlocking"); 
    paramMap.put("Signature.SHA1withDSA", "sun.security.provider.DSA$SHA1withDSA");
    paramMap.put("Signature.NONEwithDSA", "sun.security.provider.DSA$RawDSA");
    paramMap.put("Alg.Alias.Signature.RawDSA", "NONEwithDSA");
    paramMap.put("Signature.SHA224withDSA", "sun.security.provider.DSA$SHA224withDSA");
    paramMap.put("Signature.SHA256withDSA", "sun.security.provider.DSA$SHA256withDSA");
    String str1 = "java.security.interfaces.DSAPublicKey|java.security.interfaces.DSAPrivateKey";
    paramMap.put("Signature.SHA1withDSA SupportedKeyClasses", str1);
    paramMap.put("Signature.NONEwithDSA SupportedKeyClasses", str1);
    paramMap.put("Signature.SHA224withDSA SupportedKeyClasses", str1);
    paramMap.put("Signature.SHA256withDSA SupportedKeyClasses", str1);
    paramMap.put("Alg.Alias.Signature.DSA", "SHA1withDSA");
    paramMap.put("Alg.Alias.Signature.DSS", "SHA1withDSA");
    paramMap.put("Alg.Alias.Signature.SHA/DSA", "SHA1withDSA");
    paramMap.put("Alg.Alias.Signature.SHA-1/DSA", "SHA1withDSA");
    paramMap.put("Alg.Alias.Signature.SHA1/DSA", "SHA1withDSA");
    paramMap.put("Alg.Alias.Signature.SHAwithDSA", "SHA1withDSA");
    paramMap.put("Alg.Alias.Signature.DSAWithSHA1", "SHA1withDSA");
    paramMap.put("Alg.Alias.Signature.OID.1.2.840.10040.4.3", "SHA1withDSA");
    paramMap.put("Alg.Alias.Signature.1.2.840.10040.4.3", "SHA1withDSA");
    paramMap.put("Alg.Alias.Signature.1.3.14.3.2.13", "SHA1withDSA");
    paramMap.put("Alg.Alias.Signature.1.3.14.3.2.27", "SHA1withDSA");
    paramMap.put("Alg.Alias.Signature.OID.2.16.840.1.101.3.4.3.1", "SHA224withDSA");
    paramMap.put("Alg.Alias.Signature.2.16.840.1.101.3.4.3.1", "SHA224withDSA");
    paramMap.put("Alg.Alias.Signature.OID.2.16.840.1.101.3.4.3.2", "SHA256withDSA");
    paramMap.put("Alg.Alias.Signature.2.16.840.1.101.3.4.3.2", "SHA256withDSA");
    String str2 = "sun.security.provider.DSAKeyPairGenerator$";
    str2 = str2 + (useLegacyDSA ? "Legacy" : "Current");
    paramMap.put("KeyPairGenerator.DSA", str2);
    paramMap.put("Alg.Alias.KeyPairGenerator.OID.1.2.840.10040.4.1", "DSA");
    paramMap.put("Alg.Alias.KeyPairGenerator.1.2.840.10040.4.1", "DSA");
    paramMap.put("Alg.Alias.KeyPairGenerator.1.3.14.3.2.12", "DSA");
    paramMap.put("MessageDigest.MD2", "sun.security.provider.MD2");
    paramMap.put("MessageDigest.MD5", "sun.security.provider.MD5");
    paramMap.put("MessageDigest.SHA", "sun.security.provider.SHA");
    paramMap.put("Alg.Alias.MessageDigest.SHA-1", "SHA");
    paramMap.put("Alg.Alias.MessageDigest.SHA1", "SHA");
    paramMap.put("Alg.Alias.MessageDigest.1.3.14.3.2.26", "SHA");
    paramMap.put("Alg.Alias.MessageDigest.OID.1.3.14.3.2.26", "SHA");
    paramMap.put("MessageDigest.SHA-224", "sun.security.provider.SHA2$SHA224");
    paramMap.put("Alg.Alias.MessageDigest.2.16.840.1.101.3.4.2.4", "SHA-224");
    paramMap.put("Alg.Alias.MessageDigest.OID.2.16.840.1.101.3.4.2.4", "SHA-224");
    paramMap.put("MessageDigest.SHA-256", "sun.security.provider.SHA2$SHA256");
    paramMap.put("Alg.Alias.MessageDigest.2.16.840.1.101.3.4.2.1", "SHA-256");
    paramMap.put("Alg.Alias.MessageDigest.OID.2.16.840.1.101.3.4.2.1", "SHA-256");
    paramMap.put("MessageDigest.SHA-384", "sun.security.provider.SHA5$SHA384");
    paramMap.put("Alg.Alias.MessageDigest.2.16.840.1.101.3.4.2.2", "SHA-384");
    paramMap.put("Alg.Alias.MessageDigest.OID.2.16.840.1.101.3.4.2.2", "SHA-384");
    paramMap.put("MessageDigest.SHA-512", "sun.security.provider.SHA5$SHA512");
    paramMap.put("Alg.Alias.MessageDigest.2.16.840.1.101.3.4.2.3", "SHA-512");
    paramMap.put("Alg.Alias.MessageDigest.OID.2.16.840.1.101.3.4.2.3", "SHA-512");
    paramMap.put("AlgorithmParameterGenerator.DSA", "sun.security.provider.DSAParameterGenerator");
    paramMap.put("AlgorithmParameters.DSA", "sun.security.provider.DSAParameters");
    paramMap.put("Alg.Alias.AlgorithmParameters.OID.1.2.840.10040.4.1", "DSA");
    paramMap.put("Alg.Alias.AlgorithmParameters.1.2.840.10040.4.1", "DSA");
    paramMap.put("Alg.Alias.AlgorithmParameters.1.3.14.3.2.12", "DSA");
    paramMap.put("KeyFactory.DSA", "sun.security.provider.DSAKeyFactory");
    paramMap.put("Alg.Alias.KeyFactory.OID.1.2.840.10040.4.1", "DSA");
    paramMap.put("Alg.Alias.KeyFactory.1.2.840.10040.4.1", "DSA");
    paramMap.put("Alg.Alias.KeyFactory.1.3.14.3.2.12", "DSA");
    paramMap.put("CertificateFactory.X.509", "sun.security.provider.X509Factory");
    paramMap.put("Alg.Alias.CertificateFactory.X509", "X.509");
    paramMap.put("KeyStore.JKS", "sun.security.provider.JavaKeyStore$DualFormatJKS");
    paramMap.put("KeyStore.CaseExactJKS", "sun.security.provider.JavaKeyStore$CaseExactJKS");
    paramMap.put("KeyStore.DKS", "sun.security.provider.DomainKeyStore$DKS");
    paramMap.put("Policy.JavaPolicy", "sun.security.provider.PolicySpiFile");
    paramMap.put("Configuration.JavaLoginConfig", "sun.security.provider.ConfigFile$Spi");
    paramMap.put("CertPathBuilder.PKIX", "sun.security.provider.certpath.SunCertPathBuilder");
    paramMap.put("CertPathBuilder.PKIX ValidationAlgorithm", "RFC3280");
    paramMap.put("CertPathValidator.PKIX", "sun.security.provider.certpath.PKIXCertPathValidator");
    paramMap.put("CertPathValidator.PKIX ValidationAlgorithm", "RFC3280");
    paramMap.put("CertStore.LDAP", "sun.security.provider.certpath.ldap.LDAPCertStore");
    paramMap.put("CertStore.LDAP LDAPSchema", "RFC2587");
    paramMap.put("CertStore.Collection", "sun.security.provider.certpath.CollectionCertStore");
    paramMap.put("CertStore.com.sun.security.IndexedCollection", "sun.security.provider.certpath.IndexedCollectionCertStore");
    paramMap.put("Signature.NONEwithDSA KeySize", "1024");
    paramMap.put("Signature.SHA1withDSA KeySize", "1024");
    paramMap.put("Signature.SHA224withDSA KeySize", "2048");
    paramMap.put("Signature.SHA256withDSA KeySize", "2048");
    paramMap.put("KeyPairGenerator.DSA KeySize", "2048");
    paramMap.put("AlgorithmParameterGenerator.DSA KeySize", "2048");
    paramMap.put("Signature.SHA1withDSA ImplementedIn", "Software");
    paramMap.put("KeyPairGenerator.DSA ImplementedIn", "Software");
    paramMap.put("MessageDigest.MD5 ImplementedIn", "Software");
    paramMap.put("MessageDigest.SHA ImplementedIn", "Software");
    paramMap.put("AlgorithmParameterGenerator.DSA ImplementedIn", "Software");
    paramMap.put("AlgorithmParameters.DSA ImplementedIn", "Software");
    paramMap.put("KeyFactory.DSA ImplementedIn", "Software");
    paramMap.put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
    paramMap.put("CertificateFactory.X.509 ImplementedIn", "Software");
    paramMap.put("KeyStore.JKS ImplementedIn", "Software");
    paramMap.put("CertPathValidator.PKIX ImplementedIn", "Software");
    paramMap.put("CertPathBuilder.PKIX ImplementedIn", "Software");
    paramMap.put("CertStore.LDAP ImplementedIn", "Software");
    paramMap.put("CertStore.Collection ImplementedIn", "Software");
    paramMap.put("CertStore.com.sun.security.IndexedCollection ImplementedIn", "Software");
  }
  
  static String getSeedSource() { return seedSource; }
  
  static File getDeviceFile(URL paramURL) throws IOException {
    try {
      URI uRI = paramURL.toURI();
      if (uRI.isOpaque()) {
        URI uRI1 = (new File(System.getProperty("user.dir"))).toURI();
        String str = uRI1.toString() + uRI.toString().substring(5);
        return new File(URI.create(str));
      } 
      return new File(uRI);
    } catch (URISyntaxException uRISyntaxException) {
      return new File(paramURL.getPath());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\SunEntries.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */