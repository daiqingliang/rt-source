package sun.security.rsa;

import java.util.Map;

public final class SunRsaSignEntries {
  public static void putEntries(Map<Object, Object> paramMap) {
    paramMap.put("KeyFactory.RSA", "sun.security.rsa.RSAKeyFactory");
    paramMap.put("KeyPairGenerator.RSA", "sun.security.rsa.RSAKeyPairGenerator");
    paramMap.put("Signature.MD2withRSA", "sun.security.rsa.RSASignature$MD2withRSA");
    paramMap.put("Signature.MD5withRSA", "sun.security.rsa.RSASignature$MD5withRSA");
    paramMap.put("Signature.SHA1withRSA", "sun.security.rsa.RSASignature$SHA1withRSA");
    paramMap.put("Signature.SHA224withRSA", "sun.security.rsa.RSASignature$SHA224withRSA");
    paramMap.put("Signature.SHA256withRSA", "sun.security.rsa.RSASignature$SHA256withRSA");
    paramMap.put("Signature.SHA384withRSA", "sun.security.rsa.RSASignature$SHA384withRSA");
    paramMap.put("Signature.SHA512withRSA", "sun.security.rsa.RSASignature$SHA512withRSA");
    String str = "java.security.interfaces.RSAPublicKey|java.security.interfaces.RSAPrivateKey";
    paramMap.put("Signature.MD2withRSA SupportedKeyClasses", str);
    paramMap.put("Signature.MD5withRSA SupportedKeyClasses", str);
    paramMap.put("Signature.SHA1withRSA SupportedKeyClasses", str);
    paramMap.put("Signature.SHA224withRSA SupportedKeyClasses", str);
    paramMap.put("Signature.SHA256withRSA SupportedKeyClasses", str);
    paramMap.put("Signature.SHA384withRSA SupportedKeyClasses", str);
    paramMap.put("Signature.SHA512withRSA SupportedKeyClasses", str);
    paramMap.put("Alg.Alias.KeyFactory.1.2.840.113549.1.1", "RSA");
    paramMap.put("Alg.Alias.KeyFactory.OID.1.2.840.113549.1.1", "RSA");
    paramMap.put("Alg.Alias.KeyPairGenerator.1.2.840.113549.1.1", "RSA");
    paramMap.put("Alg.Alias.KeyPairGenerator.OID.1.2.840.113549.1.1", "RSA");
    paramMap.put("Alg.Alias.Signature.1.2.840.113549.1.1.2", "MD2withRSA");
    paramMap.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.2", "MD2withRSA");
    paramMap.put("Alg.Alias.Signature.1.2.840.113549.1.1.4", "MD5withRSA");
    paramMap.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.4", "MD5withRSA");
    paramMap.put("Alg.Alias.Signature.1.2.840.113549.1.1.5", "SHA1withRSA");
    paramMap.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.5", "SHA1withRSA");
    paramMap.put("Alg.Alias.Signature.1.3.14.3.2.29", "SHA1withRSA");
    paramMap.put("Alg.Alias.Signature.1.2.840.113549.1.1.14", "SHA224withRSA");
    paramMap.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.14", "SHA224withRSA");
    paramMap.put("Alg.Alias.Signature.1.2.840.113549.1.1.11", "SHA256withRSA");
    paramMap.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.11", "SHA256withRSA");
    paramMap.put("Alg.Alias.Signature.1.2.840.113549.1.1.12", "SHA384withRSA");
    paramMap.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.12", "SHA384withRSA");
    paramMap.put("Alg.Alias.Signature.1.2.840.113549.1.1.13", "SHA512withRSA");
    paramMap.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.13", "SHA512withRSA");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\rsa\SunRsaSignEntries.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */