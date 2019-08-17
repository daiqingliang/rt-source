package sun.net.ftp;

public class FtpProtocolException extends Exception {
  private static final long serialVersionUID = 5978077070276545054L;
  
  private final FtpReplyCode code = FtpReplyCode.UNKNOWN_ERROR;
  
  public FtpProtocolException(String paramString) { super(paramString); }
  
  public FtpProtocolException(String paramString, FtpReplyCode paramFtpReplyCode) { super(paramString); }
  
  public FtpReplyCode getReplyCode() { return this.code; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\ftp\FtpProtocolException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */