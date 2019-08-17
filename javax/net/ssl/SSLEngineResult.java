package javax.net.ssl;

public class SSLEngineResult {
  private final Status status;
  
  private final HandshakeStatus handshakeStatus;
  
  private final int bytesConsumed;
  
  private final int bytesProduced;
  
  public SSLEngineResult(Status paramStatus, HandshakeStatus paramHandshakeStatus, int paramInt1, int paramInt2) {
    if (paramStatus == null || paramHandshakeStatus == null || paramInt1 < 0 || paramInt2 < 0)
      throw new IllegalArgumentException("Invalid Parameter(s)"); 
    this.status = paramStatus;
    this.handshakeStatus = paramHandshakeStatus;
    this.bytesConsumed = paramInt1;
    this.bytesProduced = paramInt2;
  }
  
  public final Status getStatus() { return this.status; }
  
  public final HandshakeStatus getHandshakeStatus() { return this.handshakeStatus; }
  
  public final int bytesConsumed() { return this.bytesConsumed; }
  
  public final int bytesProduced() { return this.bytesProduced; }
  
  public String toString() { return "Status = " + this.status + " HandshakeStatus = " + this.handshakeStatus + "\nbytesConsumed = " + this.bytesConsumed + " bytesProduced = " + this.bytesProduced; }
  
  public enum HandshakeStatus {
    NOT_HANDSHAKING, FINISHED, NEED_TASK, NEED_WRAP, NEED_UNWRAP;
  }
  
  public enum Status {
    BUFFER_UNDERFLOW, BUFFER_OVERFLOW, OK, CLOSED;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SSLEngineResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */