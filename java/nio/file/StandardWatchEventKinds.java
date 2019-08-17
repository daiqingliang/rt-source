package java.nio.file;

public final class StandardWatchEventKinds {
  public static final WatchEvent.Kind<Object> OVERFLOW = new StdWatchEventKind("OVERFLOW", Object.class);
  
  public static final WatchEvent.Kind<Path> ENTRY_CREATE = new StdWatchEventKind("ENTRY_CREATE", Path.class);
  
  public static final WatchEvent.Kind<Path> ENTRY_DELETE = new StdWatchEventKind("ENTRY_DELETE", Path.class);
  
  public static final WatchEvent.Kind<Path> ENTRY_MODIFY = new StdWatchEventKind("ENTRY_MODIFY", Path.class);
  
  private static class StdWatchEventKind<T> extends Object implements WatchEvent.Kind<T> {
    private final String name;
    
    private final Class<T> type;
    
    StdWatchEventKind(String param1String, Class<T> param1Class) {
      this.name = param1String;
      this.type = param1Class;
    }
    
    public String name() { return this.name; }
    
    public Class<T> type() { return this.type; }
    
    public String toString() { return this.name; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\StandardWatchEventKinds.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */