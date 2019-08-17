package sun.net.www.protocol.http.ntlm;

import java.io.IOException;
import java.util.Base64;

public class NTLMAuthSequence {
  private String username;
  
  private String password;
  
  private String ntdomain;
  
  private int state;
  
  private long crdHandle;
  
  private long ctxHandle;
  
  Status status;
  
  NTLMAuthSequence(String paramString1, String paramString2, String paramString3) throws IOException {
    this.username = paramString1;
    this.password = paramString2;
    this.ntdomain = paramString3;
    this.status = new Status();
    this.state = 0;
    this.crdHandle = getCredentialsHandle(paramString1, paramString3, paramString2);
    if (this.crdHandle == 0L)
      throw new IOException("could not get credentials handle"); 
  }
  
  public String getAuthHeader(String paramString) throws IOException {
    byte[] arrayOfByte1 = null;
    assert !this.status.sequenceComplete;
    if (paramString != null)
      arrayOfByte1 = Base64.getDecoder().decode(paramString); 
    byte[] arrayOfByte2 = getNextToken(this.crdHandle, arrayOfByte1, this.status);
    if (arrayOfByte2 == null)
      throw new IOException("Internal authentication error"); 
    return Base64.getEncoder().encodeToString(arrayOfByte2);
  }
  
  public boolean isComplete() { return this.status.sequenceComplete; }
  
  private static native void initFirst(Class<Status> paramClass);
  
  private native long getCredentialsHandle(String paramString1, String paramString2, String paramString3);
  
  private native byte[] getNextToken(long paramLong, byte[] paramArrayOfByte, Status paramStatus);
  
  static  {
    initFirst(Status.class);
  }
  
  class Status {
    boolean sequenceComplete;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\ntlm\NTLMAuthSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */