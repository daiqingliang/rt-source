package java.security.spec;

public class ECGenParameterSpec implements AlgorithmParameterSpec {
  private String name;
  
  public ECGenParameterSpec(String paramString) {
    if (paramString == null)
      throw new NullPointerException("stdName is null"); 
    this.name = paramString;
  }
  
  public String getName() { return this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\ECGenParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */