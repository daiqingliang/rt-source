package sun.nio.fs;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.Watchable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

abstract class AbstractWatchKey implements WatchKey {
  static final int MAX_EVENT_LIST_SIZE = 512;
  
  static final Event<Object> OVERFLOW_EVENT = new Event(StandardWatchEventKinds.OVERFLOW, null);
  
  private final AbstractWatchService watcher;
  
  private final Path dir;
  
  private State state;
  
  private List<WatchEvent<?>> events;
  
  private Map<Object, WatchEvent<?>> lastModifyEvents;
  
  protected AbstractWatchKey(Path paramPath, AbstractWatchService paramAbstractWatchService) {
    this.watcher = paramAbstractWatchService;
    this.dir = paramPath;
    this.state = State.READY;
    this.events = new ArrayList();
    this.lastModifyEvents = new HashMap();
  }
  
  final AbstractWatchService watcher() { return this.watcher; }
  
  public Path watchable() { return this.dir; }
  
  final void signal() {
    synchronized (this) {
      if (this.state == State.READY) {
        this.state = State.SIGNALLED;
        this.watcher.enqueueKey(this);
      } 
    } 
  }
  
  final void signalEvent(WatchEvent.Kind<?> paramKind, Object paramObject) {
    boolean bool = (paramKind == StandardWatchEventKinds.ENTRY_MODIFY) ? 1 : 0;
    synchronized (this) {
      int i = this.events.size();
      if (i > 0) {
        WatchEvent watchEvent = (WatchEvent)this.events.get(i - 1);
        if (watchEvent.kind() == StandardWatchEventKinds.OVERFLOW || (paramKind == watchEvent.kind() && Objects.equals(paramObject, watchEvent.context()))) {
          ((Event)watchEvent).increment();
          return;
        } 
        if (!this.lastModifyEvents.isEmpty())
          if (bool) {
            WatchEvent watchEvent1 = (WatchEvent)this.lastModifyEvents.get(paramObject);
            if (watchEvent1 != null) {
              assert watchEvent1.kind() == StandardWatchEventKinds.ENTRY_MODIFY;
              ((Event)watchEvent1).increment();
              return;
            } 
          } else {
            this.lastModifyEvents.remove(paramObject);
          }  
        if (i >= 512) {
          paramKind = StandardWatchEventKinds.OVERFLOW;
          bool = false;
          paramObject = null;
        } 
      } 
      Event event = new Event(paramKind, paramObject);
      if (bool) {
        this.lastModifyEvents.put(paramObject, event);
      } else if (paramKind == StandardWatchEventKinds.OVERFLOW) {
        this.events.clear();
        this.lastModifyEvents.clear();
      } 
      this.events.add(event);
      signal();
    } 
  }
  
  public final List<WatchEvent<?>> pollEvents() {
    synchronized (this) {
      List list = this.events;
      this.events = new ArrayList();
      this.lastModifyEvents.clear();
      return list;
    } 
  }
  
  public final boolean reset() {
    synchronized (this) {
      if (this.state == State.SIGNALLED && isValid())
        if (this.events.isEmpty()) {
          this.state = State.READY;
        } else {
          this.watcher.enqueueKey(this);
        }  
      return isValid();
    } 
  }
  
  private static class Event<T> extends Object implements WatchEvent<T> {
    private final WatchEvent.Kind<T> kind;
    
    private final T context;
    
    private int count;
    
    Event(WatchEvent.Kind<T> param1Kind, T param1T) {
      this.kind = param1Kind;
      this.context = param1T;
      this.count = 1;
    }
    
    public WatchEvent.Kind<T> kind() { return this.kind; }
    
    public T context() { return (T)this.context; }
    
    public int count() { return this.count; }
    
    void increment() { this.count++; }
  }
  
  private enum State {
    READY, SIGNALLED;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\AbstractWatchKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */