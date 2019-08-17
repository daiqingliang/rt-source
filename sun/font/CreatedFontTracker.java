package sun.font;

import java.io.File;
import java.io.OutputStream;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import sun.awt.AppContext;
import sun.misc.ThreadGroupUtils;

public class CreatedFontTracker {
  public static final int MAX_FILE_SIZE = 33554432;
  
  public static final int MAX_TOTAL_BYTES = 335544320;
  
  static CreatedFontTracker tracker;
  
  int numBytes = 0;
  
  public static CreatedFontTracker getTracker() {
    if (tracker == null)
      tracker = new CreatedFontTracker(); 
    return tracker;
  }
  
  public int getNumBytes() { return this.numBytes; }
  
  public void addBytes(int paramInt) { this.numBytes += paramInt; }
  
  public void subBytes(int paramInt) { this.numBytes -= paramInt; }
  
  private static Semaphore getCS() {
    AppContext appContext = AppContext.getAppContext();
    Semaphore semaphore = (Semaphore)appContext.get(CreatedFontTracker.class);
    if (semaphore == null) {
      semaphore = new Semaphore(5, true);
      appContext.put(CreatedFontTracker.class, semaphore);
    } 
    return semaphore;
  }
  
  public boolean acquirePermit() throws InterruptedException { return getCS().tryAcquire(120L, TimeUnit.SECONDS); }
  
  public void releasePermit() { getCS().release(); }
  
  public void add(File paramFile) { TempFileDeletionHook.add(paramFile); }
  
  public void set(File paramFile, OutputStream paramOutputStream) { TempFileDeletionHook.set(paramFile, paramOutputStream); }
  
  public void remove(File paramFile) { TempFileDeletionHook.remove(paramFile); }
  
  private static class TempFileDeletionHook {
    private static HashMap<File, OutputStream> files = new HashMap();
    
    private static Thread t = null;
    
    static void init() {
      if (t == null)
        AccessController.doPrivileged(() -> {
              ThreadGroup threadGroup = ThreadGroupUtils.getRootThreadGroup();
              t = new Thread(threadGroup, TempFileDeletionHook::runHooks);
              t.setContextClassLoader(null);
              Runtime.getRuntime().addShutdownHook(t);
              return null;
            }); 
    }
    
    static void add(File param1File) {
      init();
      files.put(param1File, null);
    }
    
    static void set(File param1File, OutputStream param1OutputStream) { files.put(param1File, param1OutputStream); }
    
    static void remove(File param1File) { files.remove(param1File); }
    
    static void runHooks() {
      if (files.isEmpty())
        return; 
      for (Map.Entry entry : files.entrySet()) {
        try {
          if (entry.getValue() != null)
            ((OutputStream)entry.getValue()).close(); 
        } catch (Exception exception) {}
        ((File)entry.getKey()).delete();
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\CreatedFontTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */