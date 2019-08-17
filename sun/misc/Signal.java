package sun.misc;

import java.util.Hashtable;

public final class Signal {
  private static Hashtable<Signal, SignalHandler> handlers = new Hashtable(4);
  
  private static Hashtable<Integer, Signal> signals = new Hashtable(4);
  
  private int number;
  
  private String name;
  
  public int getNumber() { return this.number; }
  
  public String getName() { return this.name; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || !(paramObject instanceof Signal))
      return false; 
    Signal signal = (Signal)paramObject;
    return (this.name.equals(signal.name) && this.number == signal.number);
  }
  
  public int hashCode() { return this.number; }
  
  public String toString() { return "SIG" + this.name; }
  
  public Signal(String paramString) {
    this.number = findSignal(paramString);
    this.name = paramString;
    if (this.number < 0)
      throw new IllegalArgumentException("Unknown signal: " + paramString); 
  }
  
  public static SignalHandler handle(Signal paramSignal, SignalHandler paramSignalHandler) throws IllegalArgumentException {
    long l1 = (paramSignalHandler instanceof NativeSignalHandler) ? ((NativeSignalHandler)paramSignalHandler).getHandler() : 2L;
    long l2 = handle0(paramSignal.number, l1);
    if (l2 == -1L)
      throw new IllegalArgumentException("Signal already used by VM or OS: " + paramSignal); 
    signals.put(Integer.valueOf(paramSignal.number), paramSignal);
    synchronized (handlers) {
      SignalHandler signalHandler = (SignalHandler)handlers.get(paramSignal);
      handlers.remove(paramSignal);
      if (l1 == 2L)
        handlers.put(paramSignal, paramSignalHandler); 
      if (l2 == 0L)
        return SignalHandler.SIG_DFL; 
      if (l2 == 1L)
        return SignalHandler.SIG_IGN; 
      if (l2 == 2L)
        return signalHandler; 
      return new NativeSignalHandler(l2);
    } 
  }
  
  public static void raise(Signal paramSignal) throws IllegalArgumentException {
    if (handlers.get(paramSignal) == null)
      throw new IllegalArgumentException("Unhandled signal: " + paramSignal); 
    raise0(paramSignal.number);
  }
  
  private static void dispatch(int paramInt) {
    final Signal sig = (Signal)signals.get(Integer.valueOf(paramInt));
    final SignalHandler handler = (SignalHandler)handlers.get(signal);
    Runnable runnable = new Runnable() {
        public void run() { handler.handle(sig); }
      };
    if (signalHandler != null)
      (new Thread(runnable, signal + " handler")).start(); 
  }
  
  private static native int findSignal(String paramString);
  
  private static native long handle0(int paramInt, long paramLong);
  
  private static native void raise0(int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\Signal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */