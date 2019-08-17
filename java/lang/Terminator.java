package java.lang;

import sun.misc.Signal;
import sun.misc.SignalHandler;

class Terminator {
  private static SignalHandler handler = null;
  
  static void setup() {
    if (handler != null)
      return; 
    SignalHandler signalHandler = new SignalHandler() {
        public void handle(Signal param1Signal) { Shutdown.exit(param1Signal.getNumber() + 128); }
      };
    handler = signalHandler;
    try {
      Signal.handle(new Signal("INT"), signalHandler);
    } catch (IllegalArgumentException illegalArgumentException) {}
    try {
      Signal.handle(new Signal("TERM"), signalHandler);
    } catch (IllegalArgumentException illegalArgumentException) {}
  }
  
  static void teardown() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Terminator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */