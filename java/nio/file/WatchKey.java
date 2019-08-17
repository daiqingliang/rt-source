package java.nio.file;

import java.util.List;

public interface WatchKey {
  boolean isValid();
  
  List<WatchEvent<?>> pollEvents();
  
  boolean reset();
  
  void cancel();
  
  Watchable watchable();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\WatchKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */