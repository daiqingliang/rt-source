package sun.misc;

final class NativeSignalHandler implements SignalHandler {
  private final long handler;
  
  long getHandler() { return this.handler; }
  
  NativeSignalHandler(long paramLong) { this.handler = paramLong; }
  
  public void handle(Signal paramSignal) { handle0(paramSignal.getNumber(), this.handler); }
  
  private static native void handle0(int paramInt, long paramLong);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\NativeSignalHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */