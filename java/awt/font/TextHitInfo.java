package java.awt.font;

public final class TextHitInfo {
  private int charIndex;
  
  private boolean isLeadingEdge;
  
  private TextHitInfo(int paramInt, boolean paramBoolean) {
    this.charIndex = paramInt;
    this.isLeadingEdge = paramBoolean;
  }
  
  public int getCharIndex() { return this.charIndex; }
  
  public boolean isLeadingEdge() { return this.isLeadingEdge; }
  
  public int getInsertionIndex() { return this.isLeadingEdge ? this.charIndex : (this.charIndex + 1); }
  
  public int hashCode() { return this.charIndex; }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof TextHitInfo && equals((TextHitInfo)paramObject)); }
  
  public boolean equals(TextHitInfo paramTextHitInfo) { return (paramTextHitInfo != null && this.charIndex == paramTextHitInfo.charIndex && this.isLeadingEdge == paramTextHitInfo.isLeadingEdge); }
  
  public String toString() { return "TextHitInfo[" + this.charIndex + (this.isLeadingEdge ? "L" : "T") + "]"; }
  
  public static TextHitInfo leading(int paramInt) { return new TextHitInfo(paramInt, true); }
  
  public static TextHitInfo trailing(int paramInt) { return new TextHitInfo(paramInt, false); }
  
  public static TextHitInfo beforeOffset(int paramInt) { return new TextHitInfo(paramInt - 1, false); }
  
  public static TextHitInfo afterOffset(int paramInt) { return new TextHitInfo(paramInt, true); }
  
  public TextHitInfo getOtherHit() { return this.isLeadingEdge ? trailing(this.charIndex - 1) : leading(this.charIndex + 1); }
  
  public TextHitInfo getOffsetHit(int paramInt) { return new TextHitInfo(this.charIndex + paramInt, this.isLeadingEdge); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\TextHitInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */