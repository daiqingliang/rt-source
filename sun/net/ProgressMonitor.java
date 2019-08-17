package sun.net;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class ProgressMonitor {
  private static ProgressMeteringPolicy meteringPolicy = new DefaultProgressMeteringPolicy();
  
  private static ProgressMonitor pm = new ProgressMonitor();
  
  private ArrayList<ProgressSource> progressSourceList = new ArrayList();
  
  private ArrayList<ProgressListener> progressListenerList = new ArrayList();
  
  public static ProgressMonitor getDefault() { return pm; }
  
  public static void setDefault(ProgressMonitor paramProgressMonitor) {
    if (paramProgressMonitor != null)
      pm = paramProgressMonitor; 
  }
  
  public static void setMeteringPolicy(ProgressMeteringPolicy paramProgressMeteringPolicy) {
    if (paramProgressMeteringPolicy != null)
      meteringPolicy = paramProgressMeteringPolicy; 
  }
  
  public ArrayList<ProgressSource> getProgressSources() {
    ArrayList arrayList = new ArrayList();
    try {
      synchronized (this.progressSourceList) {
        for (ProgressSource progressSource : this.progressSourceList)
          arrayList.add((ProgressSource)progressSource.clone()); 
      } 
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      cloneNotSupportedException.printStackTrace();
    } 
    return arrayList;
  }
  
  public int getProgressUpdateThreshold() { return meteringPolicy.getProgressUpdateThreshold(); }
  
  public boolean shouldMeterInput(URL paramURL, String paramString) { return meteringPolicy.shouldMeterInput(paramURL, paramString); }
  
  public void registerSource(ProgressSource paramProgressSource) {
    synchronized (this.progressSourceList) {
      if (this.progressSourceList.contains(paramProgressSource))
        return; 
      this.progressSourceList.add(paramProgressSource);
    } 
    if (this.progressListenerList.size() > 0) {
      ArrayList arrayList = new ArrayList();
      synchronized (this.progressListenerList) {
        Iterator iterator = this.progressListenerList.iterator();
        while (iterator.hasNext())
          arrayList.add(iterator.next()); 
      } 
      for (ProgressListener progressListener : arrayList) {
        ProgressEvent progressEvent = new ProgressEvent(paramProgressSource, paramProgressSource.getURL(), paramProgressSource.getMethod(), paramProgressSource.getContentType(), paramProgressSource.getState(), paramProgressSource.getProgress(), paramProgressSource.getExpected());
        progressListener.progressStart(progressEvent);
      } 
    } 
  }
  
  public void unregisterSource(ProgressSource paramProgressSource) {
    synchronized (this.progressSourceList) {
      if (!this.progressSourceList.contains(paramProgressSource))
        return; 
      paramProgressSource.close();
      this.progressSourceList.remove(paramProgressSource);
    } 
    if (this.progressListenerList.size() > 0) {
      ArrayList arrayList = new ArrayList();
      synchronized (this.progressListenerList) {
        Iterator iterator = this.progressListenerList.iterator();
        while (iterator.hasNext())
          arrayList.add(iterator.next()); 
      } 
      for (ProgressListener progressListener : arrayList) {
        ProgressEvent progressEvent = new ProgressEvent(paramProgressSource, paramProgressSource.getURL(), paramProgressSource.getMethod(), paramProgressSource.getContentType(), paramProgressSource.getState(), paramProgressSource.getProgress(), paramProgressSource.getExpected());
        progressListener.progressFinish(progressEvent);
      } 
    } 
  }
  
  public void updateProgress(ProgressSource paramProgressSource) {
    synchronized (this.progressSourceList) {
      if (!this.progressSourceList.contains(paramProgressSource))
        return; 
    } 
    if (this.progressListenerList.size() > 0) {
      ArrayList arrayList = new ArrayList();
      synchronized (this.progressListenerList) {
        Iterator iterator = this.progressListenerList.iterator();
        while (iterator.hasNext())
          arrayList.add(iterator.next()); 
      } 
      for (ProgressListener progressListener : arrayList) {
        ProgressEvent progressEvent = new ProgressEvent(paramProgressSource, paramProgressSource.getURL(), paramProgressSource.getMethod(), paramProgressSource.getContentType(), paramProgressSource.getState(), paramProgressSource.getProgress(), paramProgressSource.getExpected());
        progressListener.progressUpdate(progressEvent);
      } 
    } 
  }
  
  public void addProgressListener(ProgressListener paramProgressListener) {
    synchronized (this.progressListenerList) {
      this.progressListenerList.add(paramProgressListener);
    } 
  }
  
  public void removeProgressListener(ProgressListener paramProgressListener) {
    synchronized (this.progressListenerList) {
      this.progressListenerList.remove(paramProgressListener);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\ProgressMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */