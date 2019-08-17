package java.security.spec;

public class PSSParameterSpec implements AlgorithmParameterSpec {
  private String mdName = "SHA-1";
  
  private String mgfName = "MGF1";
  
  private AlgorithmParameterSpec mgfSpec = MGF1ParameterSpec.SHA1;
  
  private int saltLen = 20;
  
  private int trailerField = 1;
  
  public static final PSSParameterSpec DEFAULT = new PSSParameterSpec();
  
  private PSSParameterSpec() {}
  
  public PSSParameterSpec(String paramString1, String paramString2, AlgorithmParameterSpec paramAlgorithmParameterSpec, int paramInt1, int paramInt2) {
    if (paramString1 == null)
      throw new NullPointerException("digest algorithm is null"); 
    if (paramString2 == null)
      throw new NullPointerException("mask generation function algorithm is null"); 
    if (paramInt1 < 0)
      throw new IllegalArgumentException("negative saltLen value: " + paramInt1); 
    if (paramInt2 < 0)
      throw new IllegalArgumentException("negative trailerField: " + paramInt2); 
    this.mdName = paramString1;
    this.mgfName = paramString2;
    this.mgfSpec = paramAlgorithmParameterSpec;
    this.saltLen = paramInt1;
    this.trailerField = paramInt2;
  }
  
  public PSSParameterSpec(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("negative saltLen value: " + paramInt); 
    this.saltLen = paramInt;
  }
  
  public String getDigestAlgorithm() { return this.mdName; }
  
  public String getMGFAlgorithm() { return this.mgfName; }
  
  public AlgorithmParameterSpec getMGFParameters() { return this.mgfSpec; }
  
  public int getSaltLength() { return this.saltLen; }
  
  public int getTrailerField() { return this.trailerField; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\PSSParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */