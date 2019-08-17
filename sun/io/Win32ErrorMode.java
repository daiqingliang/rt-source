package sun.io;

import sun.misc.VM;

public class Win32ErrorMode {
  private static final long SEM_FAILCRITICALERRORS = 1L;
  
  private static final long SEM_NOGPFAULTERRORBOX = 2L;
  
  private static final long SEM_NOALIGNMENTFAULTEXCEPT = 4L;
  
  private static final long SEM_NOOPENFILEERRORBOX = 32768L;
  
  public static void initialize() {
    if (!VM.isBooted()) {
      String str = System.getProperty("sun.io.allowCriticalErrorMessageBox");
      if (str == null || str.equals(Boolean.FALSE.toString())) {
        long l = setErrorMode(0L);
        l |= 0x1L;
        setErrorMode(l);
      } 
    } 
  }
  
  private static native long setErrorMode(long paramLong);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\io\Win32ErrorMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */