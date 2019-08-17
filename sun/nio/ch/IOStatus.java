package sun.nio.ch;

public final class IOStatus {
  public static final int EOF = -1;
  
  public static final int UNAVAILABLE = -2;
  
  public static final int INTERRUPTED = -3;
  
  public static final int UNSUPPORTED = -4;
  
  public static final int THROWN = -5;
  
  public static final int UNSUPPORTED_CASE = -6;
  
  public static int normalize(int paramInt) { return (paramInt == -2) ? 0 : paramInt; }
  
  public static boolean check(int paramInt) { return (paramInt >= -2); }
  
  public static long normalize(long paramLong) { return (paramLong == -2L) ? 0L : paramLong; }
  
  public static boolean check(long paramLong) { return (paramLong >= -2L); }
  
  public static boolean checkAll(long paramLong) { return (paramLong > -1L || paramLong < -6L); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\IOStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */