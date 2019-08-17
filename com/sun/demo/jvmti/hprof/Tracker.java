package com.sun.demo.jvmti.hprof;

public class Tracker {
  private static int engaged = 0;
  
  private static native void nativeObjectInit(Object paramObject1, Object paramObject2);
  
  public static void ObjectInit(Object paramObject) {
    if (engaged != 0) {
      if (paramObject == null)
        throw new IllegalArgumentException("Null object."); 
      nativeObjectInit(Thread.currentThread(), paramObject);
    } 
  }
  
  private static native void nativeNewArray(Object paramObject1, Object paramObject2);
  
  public static void NewArray(Object paramObject) {
    if (engaged != 0) {
      if (paramObject == null)
        throw new IllegalArgumentException("Null object."); 
      nativeNewArray(Thread.currentThread(), paramObject);
    } 
  }
  
  private static native void nativeCallSite(Object paramObject, int paramInt1, int paramInt2);
  
  public static void CallSite(int paramInt1, int paramInt2) {
    if (engaged != 0) {
      if (paramInt1 < 0)
        throw new IllegalArgumentException("Negative class index"); 
      if (paramInt2 < 0)
        throw new IllegalArgumentException("Negative method index"); 
      nativeCallSite(Thread.currentThread(), paramInt1, paramInt2);
    } 
  }
  
  private static native void nativeReturnSite(Object paramObject, int paramInt1, int paramInt2);
  
  public static void ReturnSite(int paramInt1, int paramInt2) {
    if (engaged != 0) {
      if (paramInt1 < 0)
        throw new IllegalArgumentException("Negative class index"); 
      if (paramInt2 < 0)
        throw new IllegalArgumentException("Negative method index"); 
      nativeReturnSite(Thread.currentThread(), paramInt1, paramInt2);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\demo\jvmti\hprof\Tracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */