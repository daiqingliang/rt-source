package jdk.net;

import java.net.SocketOption;
import jdk.Exported;

@Exported
public final class ExtendedSocketOptions {
  public static final SocketOption<SocketFlow> SO_FLOW_SLA = new ExtSocketOption("SO_FLOW_SLA", SocketFlow.class);
  
  private static class ExtSocketOption<T> extends Object implements SocketOption<T> {
    private final String name;
    
    private final Class<T> type;
    
    ExtSocketOption(String param1String, Class<T> param1Class) {
      this.name = param1String;
      this.type = param1Class;
    }
    
    public String name() { return this.name; }
    
    public Class<T> type() { return this.type; }
    
    public String toString() { return this.name; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\net\ExtendedSocketOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */