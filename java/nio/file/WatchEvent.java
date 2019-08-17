package java.nio.file;

public interface WatchEvent<T> {
  Kind<T> kind();
  
  int count();
  
  T context();
  
  public static interface Kind<T> {
    String name();
    
    Class<T> type();
  }
  
  public static interface Modifier {
    String name();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\WatchEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */