package com.sun.nio.sctp;

import java.net.SocketAddress;
import jdk.Exported;
import sun.nio.ch.sctp.SctpStdSocketOption;

@Exported
public class SctpStandardSocketOptions {
  public static final SctpSocketOption<Boolean> SCTP_DISABLE_FRAGMENTS = new SctpStdSocketOption("SCTP_DISABLE_FRAGMENTS", Boolean.class, 1);
  
  public static final SctpSocketOption<Boolean> SCTP_EXPLICIT_COMPLETE = new SctpStdSocketOption("SCTP_EXPLICIT_COMPLETE", Boolean.class, 2);
  
  public static final SctpSocketOption<Integer> SCTP_FRAGMENT_INTERLEAVE = new SctpStdSocketOption("SCTP_FRAGMENT_INTERLEAVE", Integer.class, 3);
  
  public static final SctpSocketOption<InitMaxStreams> SCTP_INIT_MAXSTREAMS = new SctpStdSocketOption("SCTP_INIT_MAXSTREAMS", InitMaxStreams.class);
  
  public static final SctpSocketOption<Boolean> SCTP_NODELAY = new SctpStdSocketOption("SCTP_NODELAY", Boolean.class, 4);
  
  public static final SctpSocketOption<SocketAddress> SCTP_PRIMARY_ADDR = new SctpStdSocketOption("SCTP_PRIMARY_ADDR", SocketAddress.class);
  
  public static final SctpSocketOption<SocketAddress> SCTP_SET_PEER_PRIMARY_ADDR = new SctpStdSocketOption("SCTP_SET_PEER_PRIMARY_ADDR", SocketAddress.class);
  
  public static final SctpSocketOption<Integer> SO_SNDBUF = new SctpStdSocketOption("SO_SNDBUF", Integer.class, 5);
  
  public static final SctpSocketOption<Integer> SO_RCVBUF = new SctpStdSocketOption("SO_RCVBUF", Integer.class, 6);
  
  public static final SctpSocketOption<Integer> SO_LINGER = new SctpStdSocketOption("SO_LINGER", Integer.class, 7);
  
  @Exported
  public static class InitMaxStreams {
    private int maxInStreams;
    
    private int maxOutStreams;
    
    private InitMaxStreams(int param1Int1, int param1Int2) {
      this.maxInStreams = param1Int1;
      this.maxOutStreams = param1Int2;
    }
    
    public static InitMaxStreams create(int param1Int1, int param1Int2) {
      if (param1Int2 < 0 || param1Int2 > 65535)
        throw new IllegalArgumentException("Invalid maxOutStreams value"); 
      if (param1Int1 < 0 || param1Int1 > 65535)
        throw new IllegalArgumentException("Invalid maxInStreams value"); 
      return new InitMaxStreams(param1Int1, param1Int2);
    }
    
    public int maxInStreams() { return this.maxInStreams; }
    
    public int maxOutStreams() { return this.maxOutStreams; }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(super.toString()).append(" [");
      stringBuilder.append("maxInStreams:").append(this.maxInStreams);
      stringBuilder.append("maxOutStreams:").append(this.maxOutStreams).append("]");
      return stringBuilder.toString();
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object != null && param1Object instanceof InitMaxStreams) {
        InitMaxStreams initMaxStreams = (InitMaxStreams)param1Object;
        if (this.maxInStreams == initMaxStreams.maxInStreams && this.maxOutStreams == initMaxStreams.maxOutStreams)
          return true; 
      } 
      return false;
    }
    
    public int hashCode() { return 0x7 ^ this.maxInStreams ^ this.maxOutStreams; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\nio\sctp\SctpStandardSocketOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */