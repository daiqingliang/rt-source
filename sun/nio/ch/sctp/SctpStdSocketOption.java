package sun.nio.ch.sctp;

import com.sun.nio.sctp.SctpSocketOption;

public class SctpStdSocketOption<T> extends Object implements SctpSocketOption<T> {
  public static final int SCTP_DISABLE_FRAGMENTS = 1;
  
  public static final int SCTP_EXPLICIT_COMPLETE = 2;
  
  public static final int SCTP_FRAGMENT_INTERLEAVE = 3;
  
  public static final int SCTP_NODELAY = 4;
  
  public static final int SO_SNDBUF = 5;
  
  public static final int SO_RCVBUF = 6;
  
  public static final int SO_LINGER = 7;
  
  private final String name;
  
  private final Class<T> type;
  
  private int constValue;
  
  public SctpStdSocketOption(String paramString, Class<T> paramClass) {
    this.name = paramString;
    this.type = paramClass;
  }
  
  public SctpStdSocketOption(String paramString, Class<T> paramClass, int paramInt) {
    this.name = paramString;
    this.type = paramClass;
    this.constValue = paramInt;
  }
  
  public String name() { return this.name; }
  
  public Class<T> type() { return this.type; }
  
  public String toString() { return this.name; }
  
  int constValue() { return this.constValue; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\sctp\SctpStdSocketOption.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */