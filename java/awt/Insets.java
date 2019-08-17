package java.awt;

import java.io.Serializable;

public class Insets implements Cloneable, Serializable {
  public int top;
  
  public int left;
  
  public int bottom;
  
  public int right;
  
  private static final long serialVersionUID = -2272572637695466749L;
  
  public Insets(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.top = paramInt1;
    this.left = paramInt2;
    this.bottom = paramInt3;
    this.right = paramInt4;
  }
  
  public void set(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.top = paramInt1;
    this.left = paramInt2;
    this.bottom = paramInt3;
    this.right = paramInt4;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof Insets) {
      Insets insets = (Insets)paramObject;
      return (this.top == insets.top && this.left == insets.left && this.bottom == insets.bottom && this.right == insets.right);
    } 
    return false;
  }
  
  public int hashCode() {
    int i = this.left + this.bottom;
    int j = this.right + this.top;
    int k = i * (i + 1) / 2 + this.left;
    int m = j * (j + 1) / 2 + this.top;
    int n = k + m;
    return n * (n + 1) / 2 + m;
  }
  
  public String toString() { return getClass().getName() + "[top=" + this.top + ",left=" + this.left + ",bottom=" + this.bottom + ",right=" + this.right + "]"; }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  private static native void initIDs();
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Insets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */