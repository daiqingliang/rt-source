package java.security.spec;

public final class DSAGenParameterSpec implements AlgorithmParameterSpec {
  private final int pLen;
  
  private final int qLen;
  
  private final int seedLen;
  
  public DSAGenParameterSpec(int paramInt1, int paramInt2) { this(paramInt1, paramInt2, paramInt2); }
  
  public DSAGenParameterSpec(int paramInt1, int paramInt2, int paramInt3) {
    switch (paramInt1) {
      case 1024:
        if (paramInt2 != 160)
          throw new IllegalArgumentException("subprimeQLen must be 160 when primePLen=1024"); 
        break;
      case 2048:
        if (paramInt2 != 224 && paramInt2 != 256)
          throw new IllegalArgumentException("subprimeQLen must be 224 or 256 when primePLen=2048"); 
        break;
      case 3072:
        if (paramInt2 != 256)
          throw new IllegalArgumentException("subprimeQLen must be 256 when primePLen=3072"); 
        break;
      default:
        throw new IllegalArgumentException("primePLen must be 1024, 2048, or 3072");
    } 
    if (paramInt3 < paramInt2)
      throw new IllegalArgumentException("seedLen must be equal to or greater than subprimeQLen"); 
    this.pLen = paramInt1;
    this.qLen = paramInt2;
    this.seedLen = paramInt3;
  }
  
  public int getPrimePLength() { return this.pLen; }
  
  public int getSubprimeQLength() { return this.qLen; }
  
  public int getSeedLength() { return this.seedLen; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\DSAGenParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */