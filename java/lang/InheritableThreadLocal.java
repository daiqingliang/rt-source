package java.lang;

public class InheritableThreadLocal<T> extends ThreadLocal<T> {
  protected T childValue(T paramT) { return paramT; }
  
  ThreadLocal.ThreadLocalMap getMap(Thread paramThread) { return paramThread.inheritableThreadLocals; }
  
  void createMap(Thread paramThread, T paramT) { paramThread.inheritableThreadLocals = new ThreadLocal.ThreadLocalMap(this, paramT); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\InheritableThreadLocal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */