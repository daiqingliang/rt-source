package sun.security.util;

import java.security.spec.AlgorithmParameterSpec;

public class ECKeySizeParameterSpec implements AlgorithmParameterSpec {
  private int keySize;
  
  public ECKeySizeParameterSpec(int paramInt) { this.keySize = paramInt; }
  
  public int getKeySize() { return this.keySize; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\ECKeySizeParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */