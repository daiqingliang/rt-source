package java.lang;

public class Object {
  private static native void registerNatives();
  
  public final native Class<?> getClass();
  
  public native int hashCode();
  
  public boolean equals(Object paramObject) { return (this == paramObject); }
  
  protected native Object clone() throws CloneNotSupportedException;
  
  public String toString() { return getClass().getName() + "@" + Integer.toHexString(hashCode()); }
  
  public final native void notify();
  
  public final native void notifyAll();
  
  public final native void wait(long paramLong) throws InterruptedException;
  
  public final void wait(long paramLong, int paramInt) throws InterruptedException {
    if (paramLong < 0L)
      throw new IllegalArgumentException("timeout value is negative"); 
    if (paramInt < 0 || paramInt > 999999)
      throw new IllegalArgumentException("nanosecond timeout value out of range"); 
    if (paramInt > 0)
      paramLong++; 
    wait(paramLong);
  }
  
  public final void wait() { wait(0L); }
  
  protected void finalize() {}
  
  static  {
    registerNatives();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Object.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */