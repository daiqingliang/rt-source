package java.text;

public class ParsePosition {
  int index = 0;
  
  int errorIndex = -1;
  
  public int getIndex() { return this.index; }
  
  public void setIndex(int paramInt) { this.index = paramInt; }
  
  public ParsePosition(int paramInt) { this.index = paramInt; }
  
  public void setErrorIndex(int paramInt) { this.errorIndex = paramInt; }
  
  public int getErrorIndex() { return this.errorIndex; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof ParsePosition))
      return false; 
    ParsePosition parsePosition = (ParsePosition)paramObject;
    return (this.index == parsePosition.index && this.errorIndex == parsePosition.errorIndex);
  }
  
  public int hashCode() { return this.errorIndex << 16 | this.index; }
  
  public String toString() { return getClass().getName() + "[index=" + this.index + ",errorIndex=" + this.errorIndex + ']'; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\ParsePosition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */