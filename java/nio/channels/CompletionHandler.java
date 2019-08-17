package java.nio.channels;

public interface CompletionHandler<V, A> {
  void completed(V paramV, A paramA);
  
  void failed(Throwable paramThrowable, A paramA);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\CompletionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */