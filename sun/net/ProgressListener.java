package sun.net;

import java.util.EventListener;

public interface ProgressListener extends EventListener {
  void progressStart(ProgressEvent paramProgressEvent);
  
  void progressUpdate(ProgressEvent paramProgressEvent);
  
  void progressFinish(ProgressEvent paramProgressEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\ProgressListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */