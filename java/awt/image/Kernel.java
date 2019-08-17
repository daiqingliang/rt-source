package java.awt.image;

public class Kernel implements Cloneable {
  private int width;
  
  private int height;
  
  private int xOrigin;
  
  private int yOrigin;
  
  private float[] data;
  
  private static native void initIDs();
  
  public Kernel(int paramInt1, int paramInt2, float[] paramArrayOfFloat) {
    this.width = paramInt1;
    this.height = paramInt2;
    this.xOrigin = paramInt1 - 1 >> 1;
    this.yOrigin = paramInt2 - 1 >> 1;
    int i = paramInt1 * paramInt2;
    if (paramArrayOfFloat.length < i)
      throw new IllegalArgumentException("Data array too small (is " + paramArrayOfFloat.length + " and should be " + i); 
    this.data = new float[i];
    System.arraycopy(paramArrayOfFloat, 0, this.data, 0, i);
  }
  
  public final int getXOrigin() { return this.xOrigin; }
  
  public final int getYOrigin() { return this.yOrigin; }
  
  public final int getWidth() { return this.width; }
  
  public final int getHeight() { return this.height; }
  
  public final float[] getKernelData(float[] paramArrayOfFloat) {
    if (paramArrayOfFloat == null) {
      paramArrayOfFloat = new float[this.data.length];
    } else if (paramArrayOfFloat.length < this.data.length) {
      throw new IllegalArgumentException("Data array too small (should be " + this.data.length + " but is " + paramArrayOfFloat.length + " )");
    } 
    System.arraycopy(this.data, 0, paramArrayOfFloat, 0, this.data.length);
    return paramArrayOfFloat;
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  static  {
    ColorModel.loadLibraries();
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\Kernel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */