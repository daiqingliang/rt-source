package com.sun.imageio.stream;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import java.util.WeakHashMap;
import javax.imageio.stream.ImageInputStream;

public class StreamCloser {
  private static WeakHashMap<CloseAction, Object> toCloseQueue;
  
  private static Thread streamCloser;
  
  public static void addToQueue(CloseAction paramCloseAction) {
    synchronized (StreamCloser.class) {
      if (toCloseQueue == null)
        toCloseQueue = new WeakHashMap(); 
      toCloseQueue.put(paramCloseAction, null);
      if (streamCloser == null) {
        final Runnable streamCloserRunnable = new Runnable() {
            public void run() {
              if (toCloseQueue != null)
                synchronized (StreamCloser.class) {
                  Set set = toCloseQueue.keySet();
                  StreamCloser.CloseAction[] arrayOfCloseAction1 = new StreamCloser.CloseAction[set.size()];
                  CloseAction[] arrayOfCloseAction = (CloseAction[])set.toArray(arrayOfCloseAction1);
                  for (CloseAction closeAction : arrayOfCloseAction) {
                    if (closeAction != null)
                      try {
                        closeAction.performAction();
                      } catch (IOException iOException) {} 
                  } 
                }  
            }
          };
        AccessController.doPrivileged(new PrivilegedAction() {
              public Object run() {
                ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
                for (ThreadGroup threadGroup2 = threadGroup1; threadGroup2 != null; threadGroup2 = threadGroup1.getParent())
                  threadGroup1 = threadGroup2; 
                streamCloser = new Thread(threadGroup1, streamCloserRunnable);
                streamCloser.setContextClassLoader(null);
                Runtime.getRuntime().addShutdownHook(streamCloser);
                return null;
              }
            });
      } 
    } 
  }
  
  public static void removeFromQueue(CloseAction paramCloseAction) {
    synchronized (StreamCloser.class) {
      if (toCloseQueue != null)
        toCloseQueue.remove(paramCloseAction); 
    } 
  }
  
  public static CloseAction createCloseAction(ImageInputStream paramImageInputStream) { return new CloseAction(paramImageInputStream, null); }
  
  public static final class CloseAction {
    private ImageInputStream iis;
    
    private CloseAction(ImageInputStream param1ImageInputStream) { this.iis = param1ImageInputStream; }
    
    public void performAction() {
      if (this.iis != null)
        this.iis.close(); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\stream\StreamCloser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */