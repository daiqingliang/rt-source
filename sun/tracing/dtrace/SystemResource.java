package sun.tracing.dtrace;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;

class SystemResource extends WeakReference<Activation> {
  private long handle;
  
  private static ReferenceQueue<Activation> referenceQueue = new ReferenceQueue();
  
  static HashSet<SystemResource> resources = new HashSet();
  
  SystemResource(Activation paramActivation, long paramLong) {
    super(paramActivation, referenceQueue);
    this.handle = paramLong;
    flush();
    resources.add(this);
  }
  
  void dispose() {
    JVM.dispose(this.handle);
    resources.remove(this);
    this.handle = 0L;
  }
  
  static void flush() {
    SystemResource systemResource = null;
    while ((systemResource = (SystemResource)referenceQueue.poll()) != null) {
      if (systemResource.handle != 0L)
        systemResource.dispose(); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\dtrace\SystemResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */