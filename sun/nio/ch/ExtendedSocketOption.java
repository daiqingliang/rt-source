package sun.nio.ch;

import java.net.SocketOption;

class ExtendedSocketOption {
  static final SocketOption<Boolean> SO_OOBINLINE = new SocketOption<Boolean>() {
      public String name() { return "SO_OOBINLINE"; }
      
      public Class<Boolean> type() { return Boolean.class; }
      
      public String toString() { return name(); }
    };
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\ExtendedSocketOption.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */