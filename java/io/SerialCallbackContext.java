package java.io;

final class SerialCallbackContext {
  private final Object obj;
  
  private final ObjectStreamClass desc;
  
  private Thread thread;
  
  public SerialCallbackContext(Object paramObject, ObjectStreamClass paramObjectStreamClass) {
    this.obj = paramObject;
    this.desc = paramObjectStreamClass;
    this.thread = Thread.currentThread();
  }
  
  public Object getObj() throws NotActiveException {
    checkAndSetUsed();
    return this.obj;
  }
  
  public ObjectStreamClass getDesc() { return this.desc; }
  
  public void check() throws NotActiveException {
    if (this.thread != null && this.thread != Thread.currentThread())
      throw new NotActiveException("expected thread: " + this.thread + ", but got: " + Thread.currentThread()); 
  }
  
  private void checkAndSetUsed() throws NotActiveException {
    if (this.thread != Thread.currentThread())
      throw new NotActiveException("not in readObject invocation or fields already read"); 
    this.thread = null;
  }
  
  public void setUsed() throws NotActiveException { this.thread = null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\SerialCallbackContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */