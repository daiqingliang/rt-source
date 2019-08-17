package sun.misc;

public interface SignalHandler {
  public static final SignalHandler SIG_DFL = new NativeSignalHandler(0L);
  
  public static final SignalHandler SIG_IGN = new NativeSignalHandler(1L);
  
  void handle(Signal paramSignal);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\SignalHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */