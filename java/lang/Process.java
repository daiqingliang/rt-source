package java.lang;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public abstract class Process {
  public abstract OutputStream getOutputStream();
  
  public abstract InputStream getInputStream();
  
  public abstract InputStream getErrorStream();
  
  public abstract int waitFor() throws InterruptedException;
  
  public boolean waitFor(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    long l1 = System.nanoTime();
    long l2 = paramTimeUnit.toNanos(paramLong);
    while (true) {
      try {
        exitValue();
        return true;
      } catch (IllegalThreadStateException illegalThreadStateException) {
        if (l2 > 0L)
          Thread.sleep(Math.min(TimeUnit.NANOSECONDS.toMillis(l2) + 1L, 100L)); 
        l2 = paramTimeUnit.toNanos(paramLong) - System.nanoTime() - l1;
        if (l2 <= 0L)
          break; 
      } 
    } 
    return false;
  }
  
  public abstract int exitValue() throws InterruptedException;
  
  public abstract void destroy();
  
  public Process destroyForcibly() {
    destroy();
    return this;
  }
  
  public boolean isAlive() {
    try {
      exitValue();
      return false;
    } catch (IllegalThreadStateException illegalThreadStateException) {
      return true;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Process.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */