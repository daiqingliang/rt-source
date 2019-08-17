package com.sun.jmx.snmp;

public class ThreadContext implements Cloneable {
  private ThreadContext previous;
  
  private String key;
  
  private Object value;
  
  private static ThreadLocal<ThreadContext> localContext = new ThreadLocal();
  
  private ThreadContext(ThreadContext paramThreadContext, String paramString, Object paramObject) {
    this.previous = paramThreadContext;
    this.key = paramString;
    this.value = paramObject;
  }
  
  public static Object get(String paramString) throws IllegalArgumentException {
    ThreadContext threadContext = contextContaining(paramString);
    return (threadContext == null) ? null : threadContext.value;
  }
  
  public static boolean contains(String paramString) throws IllegalArgumentException { return (contextContaining(paramString) != null); }
  
  private static ThreadContext contextContaining(String paramString) throws IllegalArgumentException {
    if (paramString == null)
      throw new IllegalArgumentException("null key"); 
    for (ThreadContext threadContext = getContext(); threadContext != null; threadContext = threadContext.previous) {
      if (paramString.equals(threadContext.key))
        return threadContext; 
    } 
    return null;
  }
  
  public static ThreadContext push(String paramString, Object paramObject) throws IllegalArgumentException {
    if (paramString == null)
      throw new IllegalArgumentException("null key"); 
    ThreadContext threadContext1 = getContext();
    if (threadContext1 == null)
      threadContext1 = new ThreadContext(null, null, null); 
    ThreadContext threadContext2;
    (threadContext2 = new ThreadContext(threadContext1, paramString, paramObject)).setContext(threadContext2);
    return threadContext1;
  }
  
  public static ThreadContext getThreadContext() { return getContext(); }
  
  public static void restore(ThreadContext paramThreadContext) throws NullPointerException, IllegalArgumentException {
    if (paramThreadContext == null)
      throw new NullPointerException(); 
    for (ThreadContext threadContext = getContext(); threadContext != paramThreadContext; threadContext = threadContext.previous) {
      if (threadContext == null)
        throw new IllegalArgumentException("Restored context is not contained in current context"); 
    } 
    if (paramThreadContext.key == null)
      paramThreadContext = null; 
    setContext(paramThreadContext);
  }
  
  public void setInitialContext(ThreadContext paramThreadContext) throws NullPointerException, IllegalArgumentException {
    if (getContext() != null)
      throw new IllegalArgumentException("previous context not empty"); 
    setContext(paramThreadContext);
  }
  
  private static ThreadContext getContext() { return (ThreadContext)localContext.get(); }
  
  private static void setContext(ThreadContext paramThreadContext) throws NullPointerException, IllegalArgumentException { localContext.set(paramThreadContext); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\ThreadContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */