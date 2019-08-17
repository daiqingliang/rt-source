package java.security.spec;

public class MGF1ParameterSpec implements AlgorithmParameterSpec {
  public static final MGF1ParameterSpec SHA1 = new MGF1ParameterSpec("SHA-1");
  
  public static final MGF1ParameterSpec SHA224 = new MGF1ParameterSpec("SHA-224");
  
  public static final MGF1ParameterSpec SHA256 = new MGF1ParameterSpec("SHA-256");
  
  public static final MGF1ParameterSpec SHA384 = new MGF1ParameterSpec("SHA-384");
  
  public static final MGF1ParameterSpec SHA512 = new MGF1ParameterSpec("SHA-512");
  
  private String mdName;
  
  public MGF1ParameterSpec(String paramString) {
    if (paramString == null)
      throw new NullPointerException("digest algorithm is null"); 
    this.mdName = paramString;
  }
  
  public String getDigestAlgorithm() { return this.mdName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\MGF1ParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */