package javax.xml.crypto.dsig.spec;

public final class HMACParameterSpec implements SignatureMethodParameterSpec {
  private int outputLength;
  
  public HMACParameterSpec(int paramInt) { this.outputLength = paramInt; }
  
  public int getOutputLength() { return this.outputLength; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\spec\HMACParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */