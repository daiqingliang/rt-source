package com.sun.corba.se.impl.orbutil.concurrent;

public class SyncUtil {
  public static void acquire(Sync paramSync) {
    boolean bool = false;
    while (!bool) {
      try {
        paramSync.acquire();
        bool = true;
      } catch (InterruptedException interruptedException) {
        bool = false;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\concurrent\SyncUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */