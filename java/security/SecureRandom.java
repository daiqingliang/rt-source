package java.security;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.security.jca.GetInstance;
import sun.security.jca.Providers;
import sun.security.provider.SecureRandom;
import sun.security.util.Debug;

public class SecureRandom extends Random {
  private static final Debug pdebug;
  
  private static final boolean skipDebug = ((pdebug = Debug.getInstance("provider", "Provider")).isOn("engine=") && !Debug.isOn("securerandom"));
  
  private Provider provider = null;
  
  private SecureRandomSpi secureRandomSpi = null;
  
  private String algorithm;
  
  static final long serialVersionUID = 4940670005562187L;
  
  private byte[] state;
  
  private MessageDigest digest = null;
  
  private byte[] randomBytes;
  
  private int randomBytesUsed;
  
  private long counter;
  
  public SecureRandom() {
    super(0L);
    getDefaultPRNG(false, null);
  }
  
  public SecureRandom(byte[] paramArrayOfByte) {
    super(0L);
    getDefaultPRNG(true, paramArrayOfByte);
  }
  
  private void getDefaultPRNG(boolean paramBoolean, byte[] paramArrayOfByte) {
    String str = getPrngAlgorithm();
    if (str == null) {
      str = "SHA1PRNG";
      this.secureRandomSpi = new SecureRandom();
      this.provider = Providers.getSunProvider();
      if (paramBoolean)
        this.secureRandomSpi.engineSetSeed(paramArrayOfByte); 
    } else {
      try {
        SecureRandom secureRandom = getInstance(str);
        this.secureRandomSpi = secureRandom.getSecureRandomSpi();
        this.provider = secureRandom.getProvider();
        if (paramBoolean)
          this.secureRandomSpi.engineSetSeed(paramArrayOfByte); 
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new RuntimeException(noSuchAlgorithmException);
      } 
    } 
    if (getClass() == SecureRandom.class)
      this.algorithm = str; 
  }
  
  protected SecureRandom(SecureRandomSpi paramSecureRandomSpi, Provider paramProvider) { this(paramSecureRandomSpi, paramProvider, null); }
  
  private SecureRandom(SecureRandomSpi paramSecureRandomSpi, Provider paramProvider, String paramString) {
    super(0L);
    this.secureRandomSpi = paramSecureRandomSpi;
    this.provider = paramProvider;
    this.algorithm = paramString;
    if (!skipDebug && pdebug != null)
      pdebug.println("SecureRandom." + paramString + " algorithm from: " + this.provider.getName()); 
  }
  
  public static SecureRandom getInstance(String paramString) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("SecureRandom", SecureRandomSpi.class, paramString);
    return new SecureRandom((SecureRandomSpi)instance.impl, instance.provider, paramString);
  }
  
  public static SecureRandom getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    GetInstance.Instance instance = GetInstance.getInstance("SecureRandom", SecureRandomSpi.class, paramString1, paramString2);
    return new SecureRandom((SecureRandomSpi)instance.impl, instance.provider, paramString1);
  }
  
  public static SecureRandom getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("SecureRandom", SecureRandomSpi.class, paramString, paramProvider);
    return new SecureRandom((SecureRandomSpi)instance.impl, instance.provider, paramString);
  }
  
  SecureRandomSpi getSecureRandomSpi() { return this.secureRandomSpi; }
  
  public final Provider getProvider() { return this.provider; }
  
  public String getAlgorithm() { return (this.algorithm != null) ? this.algorithm : "unknown"; }
  
  public void setSeed(byte[] paramArrayOfByte) { this.secureRandomSpi.engineSetSeed(paramArrayOfByte); }
  
  public void setSeed(long paramLong) {
    if (paramLong != 0L)
      this.secureRandomSpi.engineSetSeed(longToByteArray(paramLong)); 
  }
  
  public void nextBytes(byte[] paramArrayOfByte) { this.secureRandomSpi.engineNextBytes(paramArrayOfByte); }
  
  protected final int next(int paramInt) {
    int i = (paramInt + 7) / 8;
    byte[] arrayOfByte = new byte[i];
    int j = 0;
    nextBytes(arrayOfByte);
    for (byte b = 0; b < i; b++)
      j = (j << 8) + (arrayOfByte[b] & 0xFF); 
    return j >>> i * 8 - paramInt;
  }
  
  public static byte[] getSeed(int paramInt) {
    if (seedGenerator == null)
      seedGenerator = new SecureRandom(); 
    return seedGenerator.generateSeed(paramInt);
  }
  
  public byte[] generateSeed(int paramInt) { return this.secureRandomSpi.engineGenerateSeed(paramInt); }
  
  private static byte[] longToByteArray(long paramLong) {
    byte[] arrayOfByte = new byte[8];
    for (byte b = 0; b < 8; b++) {
      arrayOfByte[b] = (byte)(int)paramLong;
      paramLong >>= 8;
    } 
    return arrayOfByte;
  }
  
  private static String getPrngAlgorithm() {
    for (Provider provider1 : Providers.getProviderList().providers()) {
      for (Provider.Service service : provider1.getServices()) {
        if (service.getType().equals("SecureRandom"))
          return service.getAlgorithm(); 
      } 
    } 
    return null;
  }
  
  public static SecureRandom getInstanceStrong() throws NoSuchAlgorithmException {
    String str1 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return Security.getProperty("securerandom.strongAlgorithms"); }
        });
    if (str1 == null || str1.length() == 0)
      throw new NoSuchAlgorithmException("Null/empty securerandom.strongAlgorithms Security Property"); 
    for (String str2 = str1; str2 != null; str2 = null) {
      Matcher matcher;
      if ((matcher = pattern.matcher(str2)).matches()) {
        String str3 = matcher.group(1);
        String str4 = matcher.group(3);
        try {
          return (str4 == null) ? getInstance(str3) : getInstance(str3, str4);
        } catch (NoSuchAlgorithmException|NoSuchProviderException noSuchAlgorithmException) {
          str2 = matcher.group(5);
          continue;
        } 
      } 
    } 
    throw new NoSuchAlgorithmException("No strong SecureRandom impls available: " + str1);
  }
  
  private static final class StrongPatternHolder {
    private static Pattern pattern = Pattern.compile("\\s*([\\S&&[^:,]]*)(\\:([\\S&&[^,]]*))?\\s*(\\,(.*))?");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\SecureRandom.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */