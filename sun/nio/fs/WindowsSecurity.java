package sun.nio.fs;

class WindowsSecurity {
  static final long processTokenWithDuplicateAccess = openProcessToken(2);
  
  static final long processTokenWithQueryAccess = openProcessToken(8);
  
  private static long openProcessToken(int paramInt) {
    try {
      return WindowsNativeDispatcher.OpenProcessToken(WindowsNativeDispatcher.GetCurrentProcess(), paramInt);
    } catch (WindowsException windowsException) {
      return 0L;
    } 
  }
  
  static Privilege enablePrivilege(String paramString) {
    final long pLuid;
    try {
      l1 = WindowsNativeDispatcher.LookupPrivilegeValue(paramString);
    } catch (WindowsException windowsException) {
      throw new AssertionError(windowsException);
    } 
    long l2 = 0L;
    boolean bool1 = false;
    boolean bool2 = false;
    try {
      l2 = WindowsNativeDispatcher.OpenThreadToken(WindowsNativeDispatcher.GetCurrentThread(), 32, false);
      if (l2 == 0L && processTokenWithDuplicateAccess != 0L) {
        l2 = WindowsNativeDispatcher.DuplicateTokenEx(processTokenWithDuplicateAccess, 36);
        WindowsNativeDispatcher.SetThreadToken(0L, l2);
        bool1 = true;
      } 
      if (l2 != 0L) {
        WindowsNativeDispatcher.AdjustTokenPrivileges(l2, l1, 2);
        bool2 = true;
      } 
    } catch (WindowsException windowsException) {}
    final long token = l2;
    final boolean stopImpersontating = bool1;
    final boolean needToRevert = bool2;
    return new Privilege() {
        public void drop() {
          if (token != 0L)
            try {
              if (stopImpersontating) {
                WindowsNativeDispatcher.SetThreadToken(0L, 0L);
              } else if (needToRevert) {
                WindowsNativeDispatcher.AdjustTokenPrivileges(token, pLuid, 0);
              } 
            } catch (WindowsException windowsException) {
              throw new AssertionError(windowsException);
            } finally {
              WindowsNativeDispatcher.CloseHandle(token);
            }  
        }
      };
  }
  
  static boolean checkAccessMask(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) throws WindowsException {
    byte b = 8;
    l = WindowsNativeDispatcher.OpenThreadToken(WindowsNativeDispatcher.GetCurrentThread(), b, false);
    if (l == 0L && processTokenWithDuplicateAccess != 0L)
      l = WindowsNativeDispatcher.DuplicateTokenEx(processTokenWithDuplicateAccess, b); 
    boolean bool = false;
    if (l != 0L)
      try {
        bool = WindowsNativeDispatcher.AccessCheck(l, paramLong, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      } finally {
        WindowsNativeDispatcher.CloseHandle(l);
      }  
    return bool;
  }
  
  static interface Privilege {
    void drop();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsSecurity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */