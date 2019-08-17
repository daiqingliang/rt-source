package javax.print.attribute;

import java.io.Serializable;

public abstract class ResolutionSyntax implements Serializable, Cloneable {
  private static final long serialVersionUID = 2706743076526672017L;
  
  private int crossFeedResolution;
  
  private int feedResolution;
  
  public static final int DPI = 100;
  
  public static final int DPCM = 254;
  
  public ResolutionSyntax(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt1 < 1)
      throw new IllegalArgumentException("crossFeedResolution is < 1"); 
    if (paramInt2 < 1)
      throw new IllegalArgumentException("feedResolution is < 1"); 
    if (paramInt3 < 1)
      throw new IllegalArgumentException("units is < 1"); 
    this.crossFeedResolution = paramInt1 * paramInt3;
    this.feedResolution = paramInt2 * paramInt3;
  }
  
  private static int convertFromDphi(int paramInt1, int paramInt2) {
    if (paramInt2 < 1)
      throw new IllegalArgumentException(": units is < 1"); 
    int i = paramInt2 / 2;
    return (paramInt1 + i) / paramInt2;
  }
  
  public int[] getResolution(int paramInt) { return new int[] { getCrossFeedResolution(paramInt), getFeedResolution(paramInt) }; }
  
  public int getCrossFeedResolution(int paramInt) { return convertFromDphi(this.crossFeedResolution, paramInt); }
  
  public int getFeedResolution(int paramInt) { return convertFromDphi(this.feedResolution, paramInt); }
  
  public String toString(int paramInt, String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(getCrossFeedResolution(paramInt));
    stringBuffer.append('x');
    stringBuffer.append(getFeedResolution(paramInt));
    if (paramString != null) {
      stringBuffer.append(' ');
      stringBuffer.append(paramString);
    } 
    return stringBuffer.toString();
  }
  
  public boolean lessThanOrEquals(ResolutionSyntax paramResolutionSyntax) { return (this.crossFeedResolution <= paramResolutionSyntax.crossFeedResolution && this.feedResolution <= paramResolutionSyntax.feedResolution); }
  
  public boolean equals(Object paramObject) { return (paramObject != null && paramObject instanceof ResolutionSyntax && this.crossFeedResolution == ((ResolutionSyntax)paramObject).crossFeedResolution && this.feedResolution == ((ResolutionSyntax)paramObject).feedResolution); }
  
  public int hashCode() { return this.crossFeedResolution & 0xFFFF | (this.feedResolution & 0xFFFF) << 16; }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(this.crossFeedResolution);
    stringBuffer.append('x');
    stringBuffer.append(this.feedResolution);
    stringBuffer.append(" dphi");
    return stringBuffer.toString();
  }
  
  protected int getCrossFeedResolutionDphi() { return this.crossFeedResolution; }
  
  protected int getFeedResolutionDphi() { return this.feedResolution; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\ResolutionSyntax.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */