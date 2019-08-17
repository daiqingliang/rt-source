package javax.swing;

public class SizeSequence {
  private static int[] emptyArray = new int[0];
  
  private int[] a = emptyArray;
  
  public SizeSequence() {}
  
  public SizeSequence(int paramInt) { this(paramInt, 0); }
  
  public SizeSequence(int paramInt1, int paramInt2) {
    this();
    insertEntries(0, paramInt1, paramInt2);
  }
  
  public SizeSequence(int[] paramArrayOfInt) {
    this();
    setSizes(paramArrayOfInt);
  }
  
  void setSizes(int paramInt1, int paramInt2) {
    if (this.a.length != paramInt1)
      this.a = new int[paramInt1]; 
    setSizes(0, paramInt1, paramInt2);
  }
  
  private int setSizes(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 <= paramInt1)
      return 0; 
    int i = (paramInt1 + paramInt2) / 2;
    this.a[i] = paramInt3 + setSizes(paramInt1, i, paramInt3);
    return this.a[i] + setSizes(i + 1, paramInt2, paramInt3);
  }
  
  public void setSizes(int[] paramArrayOfInt) {
    if (this.a.length != paramArrayOfInt.length)
      this.a = new int[paramArrayOfInt.length]; 
    setSizes(0, this.a.length, paramArrayOfInt);
  }
  
  private int setSizes(int paramInt1, int paramInt2, int[] paramArrayOfInt) {
    if (paramInt2 <= paramInt1)
      return 0; 
    int i = (paramInt1 + paramInt2) / 2;
    this.a[i] = paramArrayOfInt[i] + setSizes(paramInt1, i, paramArrayOfInt);
    return this.a[i] + setSizes(i + 1, paramInt2, paramArrayOfInt);
  }
  
  public int[] getSizes() {
    int i = this.a.length;
    int[] arrayOfInt = new int[i];
    getSizes(0, i, arrayOfInt);
    return arrayOfInt;
  }
  
  private int getSizes(int paramInt1, int paramInt2, int[] paramArrayOfInt) {
    if (paramInt2 <= paramInt1)
      return 0; 
    int i = (paramInt1 + paramInt2) / 2;
    paramArrayOfInt[i] = this.a[i] - getSizes(paramInt1, i, paramArrayOfInt);
    return this.a[i] + getSizes(i + 1, paramInt2, paramArrayOfInt);
  }
  
  public int getPosition(int paramInt) { return getPosition(0, this.a.length, paramInt); }
  
  private int getPosition(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 <= paramInt1)
      return 0; 
    int i = (paramInt1 + paramInt2) / 2;
    return (paramInt3 <= i) ? getPosition(paramInt1, i, paramInt3) : (this.a[i] + getPosition(i + 1, paramInt2, paramInt3));
  }
  
  public int getIndex(int paramInt) { return getIndex(0, this.a.length, paramInt); }
  
  private int getIndex(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 <= paramInt1)
      return paramInt1; 
    int i = (paramInt1 + paramInt2) / 2;
    int j = this.a[i];
    return (paramInt3 < j) ? getIndex(paramInt1, i, paramInt3) : getIndex(i + 1, paramInt2, paramInt3 - j);
  }
  
  public int getSize(int paramInt) { return getPosition(paramInt + 1) - getPosition(paramInt); }
  
  public void setSize(int paramInt1, int paramInt2) { changeSize(0, this.a.length, paramInt1, paramInt2 - getSize(paramInt1)); }
  
  private void changeSize(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt2 <= paramInt1)
      return; 
    int i = (paramInt1 + paramInt2) / 2;
    if (paramInt3 <= i) {
      this.a[i] = this.a[i] + paramInt4;
      changeSize(paramInt1, i, paramInt3, paramInt4);
    } else {
      changeSize(i + 1, paramInt2, paramInt3, paramInt4);
    } 
  }
  
  public void insertEntries(int paramInt1, int paramInt2, int paramInt3) {
    int[] arrayOfInt = getSizes();
    int i = paramInt1 + paramInt2;
    int j = this.a.length + paramInt2;
    this.a = new int[j];
    int k;
    for (k = 0; k < paramInt1; k++)
      this.a[k] = arrayOfInt[k]; 
    for (k = paramInt1; k < i; k++)
      this.a[k] = paramInt3; 
    for (k = i; k < j; k++)
      this.a[k] = arrayOfInt[k - paramInt2]; 
    setSizes(this.a);
  }
  
  public void removeEntries(int paramInt1, int paramInt2) {
    int[] arrayOfInt = getSizes();
    int i = paramInt1 + paramInt2;
    int j = this.a.length - paramInt2;
    this.a = new int[j];
    int k;
    for (k = 0; k < paramInt1; k++)
      this.a[k] = arrayOfInt[k]; 
    for (k = paramInt1; k < j; k++)
      this.a[k] = arrayOfInt[k + paramInt2]; 
    setSizes(this.a);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\SizeSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */