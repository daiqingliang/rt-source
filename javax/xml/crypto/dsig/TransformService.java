package javax.xml.crypto.dsig;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.List;
import java.util.Map;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import sun.security.jca.GetInstance;

public abstract class TransformService implements Transform {
  private String algorithm;
  
  private String mechanism;
  
  private Provider provider;
  
  public static TransformService getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException {
    if (paramString2 == null || paramString1 == null)
      throw new NullPointerException(); 
    boolean bool = false;
    if (paramString2.equals("DOM"))
      bool = true; 
    List list = GetInstance.getServices("TransformService", paramString1);
    for (Provider.Service service : list) {
      String str = service.getAttribute("MechanismType");
      if ((str == null && bool) || (str != null && str.equals(paramString2))) {
        GetInstance.Instance instance = GetInstance.getInstance(service, null);
        TransformService transformService = (TransformService)instance.impl;
        transformService.algorithm = paramString1;
        transformService.mechanism = paramString2;
        transformService.provider = instance.provider;
        return transformService;
      } 
    } 
    throw new NoSuchAlgorithmException(paramString1 + " algorithm and " + paramString2 + " mechanism not available");
  }
  
  public static TransformService getInstance(String paramString1, String paramString2, Provider paramProvider) throws NoSuchAlgorithmException {
    if (paramString2 == null || paramString1 == null || paramProvider == null)
      throw new NullPointerException(); 
    boolean bool = false;
    if (paramString2.equals("DOM"))
      bool = true; 
    Provider.Service service = GetInstance.getService("TransformService", paramString1, paramProvider);
    String str = service.getAttribute("MechanismType");
    if ((str == null && bool) || (str != null && str.equals(paramString2))) {
      GetInstance.Instance instance = GetInstance.getInstance(service, null);
      TransformService transformService = (TransformService)instance.impl;
      transformService.algorithm = paramString1;
      transformService.mechanism = paramString2;
      transformService.provider = instance.provider;
      return transformService;
    } 
    throw new NoSuchAlgorithmException(paramString1 + " algorithm and " + paramString2 + " mechanism not available");
  }
  
  public static TransformService getInstance(String paramString1, String paramString2, String paramString3) throws NoSuchAlgorithmException, NoSuchProviderException {
    if (paramString2 == null || paramString1 == null || paramString3 == null)
      throw new NullPointerException(); 
    if (paramString3.length() == 0)
      throw new NoSuchProviderException(); 
    boolean bool = false;
    if (paramString2.equals("DOM"))
      bool = true; 
    Provider.Service service = GetInstance.getService("TransformService", paramString1, paramString3);
    String str = service.getAttribute("MechanismType");
    if ((str == null && bool) || (str != null && str.equals(paramString2))) {
      GetInstance.Instance instance = GetInstance.getInstance(service, null);
      TransformService transformService = (TransformService)instance.impl;
      transformService.algorithm = paramString1;
      transformService.mechanism = paramString2;
      transformService.provider = instance.provider;
      return transformService;
    } 
    throw new NoSuchAlgorithmException(paramString1 + " algorithm and " + paramString2 + " mechanism not available");
  }
  
  public final String getMechanismType() { return this.mechanism; }
  
  public final String getAlgorithm() { return this.algorithm; }
  
  public final Provider getProvider() { return this.provider; }
  
  public abstract void init(TransformParameterSpec paramTransformParameterSpec) throws InvalidAlgorithmParameterException;
  
  public abstract void marshalParams(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext) throws MarshalException;
  
  public abstract void init(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext) throws MarshalException;
  
  private static class MechanismMapEntry extends Object implements Map.Entry<String, String> {
    private final String mechanism;
    
    private final String algorithm;
    
    private final String key;
    
    MechanismMapEntry(String param1String1, String param1String2) {
      this.algorithm = param1String1;
      this.mechanism = param1String2;
      this.key = "TransformService." + param1String1 + " MechanismType";
    }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      return (((getKey() == null) ? (entry.getKey() == null) : getKey().equals(entry.getKey())) && ((getValue() == null) ? (entry.getValue() == null) : getValue().equals(entry.getValue())));
    }
    
    public String getKey() { return this.key; }
    
    public String getValue() { return this.mechanism; }
    
    public String setValue(String param1String) { throw new UnsupportedOperationException(); }
    
    public int hashCode() { return ((getKey() == null) ? 0 : getKey().hashCode()) ^ ((getValue() == null) ? 0 : getValue().hashCode()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\TransformService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */