package javax.swing.text;

import java.io.Serializable;

abstract class GapVector implements Serializable {
  private Object array;
  
  private int g0;
  
  private int g1;
  
  public GapVector() { this(10); }
  
  public GapVector(int paramInt) {
    this.array = allocateArray(paramInt);
    this.g0 = 0;
    this.g1 = paramInt;
  }
  
  protected abstract Object allocateArray(int paramInt);
  
  protected abstract int getArrayLength();
  
  protected final Object getArray() { return this.array; }
  
  protected final int getGapStart() { return this.g0; }
  
  protected final int getGapEnd() { return this.g1; }
  
  protected void replace(int paramInt1, int paramInt2, Object paramObject, int paramInt3) {
    byte b = 0;
    if (paramInt3 == 0) {
      close(paramInt1, paramInt2);
      return;
    } 
    if (paramInt2 > paramInt3) {
      close(paramInt1 + paramInt3, paramInt2 - paramInt3);
    } else {
      int i = paramInt3 - paramInt2;
      int j = open(paramInt1 + paramInt2, i);
      System.arraycopy(paramObject, paramInt2, this.array, j, i);
      paramInt3 = paramInt2;
    } 
    System.arraycopy(paramObject, b, this.array, paramInt1, paramInt3);
  }
  
  void close(int paramInt1, int paramInt2) {
    if (paramInt2 == 0)
      return; 
    int i = paramInt1 + paramInt2;
    int j = this.g1 - this.g0 + paramInt2;
    if (i <= this.g0) {
      if (this.g0 != i)
        shiftGap(i); 
      shiftGapStartDown(this.g0 - paramInt2);
    } else if (paramInt1 >= this.g0) {
      if (this.g0 != paramInt1)
        shiftGap(paramInt1); 
      shiftGapEndUp(this.g0 + j);
    } else {
      shiftGapStartDown(paramInt1);
      shiftGapEndUp(this.g0 + j);
    } 
  }
  
  int open(int paramInt1, int paramInt2) {
    int i = this.g1 - this.g0;
    if (paramInt2 == 0) {
      if (paramInt1 > this.g0)
        paramInt1 += i; 
      return paramInt1;
    } 
    shiftGap(paramInt1);
    if (paramInt2 >= i) {
      shiftEnd(getArrayLength() - i + paramInt2);
      i = this.g1 - this.g0;
    } 
    this.g0 += paramInt2;
    return paramInt1;
  }
  
  void resize(int paramInt) {
    Object object = allocateArray(paramInt);
    System.arraycopy(this.array, 0, object, 0, Math.min(paramInt, getArrayLength()));
    this.array = object;
  }
  
  protected void shiftEnd(int paramInt) {
    int i = getArrayLength();
    int j = this.g1;
    int k = i - j;
    int m = getNewArraySize(paramInt);
    int n = m - k;
    resize(m);
    this.g1 = n;
    if (k != 0)
      System.arraycopy(this.array, j, this.array, n, k); 
  }
  
  int getNewArraySize(int paramInt) { return (paramInt + 1) * 2; }
  
  protected void shiftGap(int paramInt) {
    if (paramInt == this.g0)
      return; 
    int i = this.g0;
    int j = paramInt - i;
    int k = this.g1;
    int m = k + j;
    int n = k - i;
    this.g0 = paramInt;
    this.g1 = m;
    if (j > 0) {
      System.arraycopy(this.array, k, this.array, i, j);
    } else if (j < 0) {
      System.arraycopy(this.array, paramInt, this.array, m, -j);
    } 
  }
  
  protected void shiftGapStartDown(int paramInt) { this.g0 = paramInt; }
  
  protected void shiftGapEndUp(int paramInt) { this.g1 = paramInt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\GapVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */