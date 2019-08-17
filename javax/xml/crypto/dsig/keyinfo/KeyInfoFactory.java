package javax.xml.crypto.dsig.keyinfo;

import java.math.BigInteger;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NoSuchMechanismException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLStructure;
import sun.security.jca.GetInstance;

public abstract class KeyInfoFactory {
  private String mechanismType;
  
  private Provider provider;
  
  public static KeyInfoFactory getInstance(String paramString) {
    GetInstance.Instance instance;
    if (paramString == null)
      throw new NullPointerException("mechanismType cannot be null"); 
    try {
      instance = GetInstance.getInstance("KeyInfoFactory", null, paramString);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new NoSuchMechanismException(noSuchAlgorithmException);
    } 
    KeyInfoFactory keyInfoFactory = (KeyInfoFactory)instance.impl;
    keyInfoFactory.mechanismType = paramString;
    keyInfoFactory.provider = instance.provider;
    return keyInfoFactory;
  }
  
  public static KeyInfoFactory getInstance(String paramString, Provider paramProvider) {
    GetInstance.Instance instance;
    if (paramString == null)
      throw new NullPointerException("mechanismType cannot be null"); 
    if (paramProvider == null)
      throw new NullPointerException("provider cannot be null"); 
    try {
      instance = GetInstance.getInstance("KeyInfoFactory", null, paramString, paramProvider);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new NoSuchMechanismException(noSuchAlgorithmException);
    } 
    KeyInfoFactory keyInfoFactory = (KeyInfoFactory)instance.impl;
    keyInfoFactory.mechanismType = paramString;
    keyInfoFactory.provider = instance.provider;
    return keyInfoFactory;
  }
  
  public static KeyInfoFactory getInstance(String paramString1, String paramString2) throws NoSuchProviderException {
    GetInstance.Instance instance;
    if (paramString1 == null)
      throw new NullPointerException("mechanismType cannot be null"); 
    if (paramString2 == null)
      throw new NullPointerException("provider cannot be null"); 
    if (paramString2.length() == 0)
      throw new NoSuchProviderException(); 
    try {
      instance = GetInstance.getInstance("KeyInfoFactory", null, paramString1, paramString2);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new NoSuchMechanismException(noSuchAlgorithmException);
    } 
    KeyInfoFactory keyInfoFactory = (KeyInfoFactory)instance.impl;
    keyInfoFactory.mechanismType = paramString1;
    keyInfoFactory.provider = instance.provider;
    return keyInfoFactory;
  }
  
  public static KeyInfoFactory getInstance() { return getInstance("DOM"); }
  
  public final String getMechanismType() { return this.mechanismType; }
  
  public final Provider getProvider() { return this.provider; }
  
  public abstract KeyInfo newKeyInfo(List paramList);
  
  public abstract KeyInfo newKeyInfo(List paramList, String paramString);
  
  public abstract KeyName newKeyName(String paramString);
  
  public abstract KeyValue newKeyValue(PublicKey paramPublicKey) throws KeyException;
  
  public abstract PGPData newPGPData(byte[] paramArrayOfByte);
  
  public abstract PGPData newPGPData(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, List paramList);
  
  public abstract PGPData newPGPData(byte[] paramArrayOfByte, List paramList);
  
  public abstract RetrievalMethod newRetrievalMethod(String paramString);
  
  public abstract RetrievalMethod newRetrievalMethod(String paramString1, String paramString2, List paramList);
  
  public abstract X509Data newX509Data(List paramList);
  
  public abstract X509IssuerSerial newX509IssuerSerial(String paramString, BigInteger paramBigInteger);
  
  public abstract boolean isFeatureSupported(String paramString);
  
  public abstract URIDereferencer getURIDereferencer();
  
  public abstract KeyInfo unmarshalKeyInfo(XMLStructure paramXMLStructure) throws MarshalException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\keyinfo\KeyInfoFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */