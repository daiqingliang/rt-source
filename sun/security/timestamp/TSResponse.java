package sun.security.timestamp;

import java.io.IOException;
import sun.security.pkcs.PKCS7;
import sun.security.util.Debug;
import sun.security.util.DerValue;

public class TSResponse {
  public static final int GRANTED = 0;
  
  public static final int GRANTED_WITH_MODS = 1;
  
  public static final int REJECTION = 2;
  
  public static final int WAITING = 3;
  
  public static final int REVOCATION_WARNING = 4;
  
  public static final int REVOCATION_NOTIFICATION = 5;
  
  public static final int BAD_ALG = 0;
  
  public static final int BAD_REQUEST = 2;
  
  public static final int BAD_DATA_FORMAT = 5;
  
  public static final int TIME_NOT_AVAILABLE = 14;
  
  public static final int UNACCEPTED_POLICY = 15;
  
  public static final int UNACCEPTED_EXTENSION = 16;
  
  public static final int ADD_INFO_NOT_AVAILABLE = 17;
  
  public static final int SYSTEM_FAILURE = 25;
  
  private static final Debug debug = Debug.getInstance("ts");
  
  private int status;
  
  private String[] statusString = null;
  
  private boolean[] failureInfo = null;
  
  private byte[] encodedTsToken = null;
  
  private PKCS7 tsToken = null;
  
  private TimestampToken tstInfo;
  
  TSResponse(byte[] paramArrayOfByte) throws IOException { parse(paramArrayOfByte); }
  
  public int getStatusCode() { return this.status; }
  
  public String[] getStatusMessages() { return this.statusString; }
  
  public boolean[] getFailureInfo() { return this.failureInfo; }
  
  public String getStatusCodeAsText() {
    switch (this.status) {
      case 0:
        return "the timestamp request was granted.";
      case 1:
        return "the timestamp request was granted with some modifications.";
      case 2:
        return "the timestamp request was rejected.";
      case 3:
        return "the timestamp request has not yet been processed.";
      case 4:
        return "warning: a certificate revocation is imminent.";
      case 5:
        return "notification: a certificate revocation has occurred.";
    } 
    return "unknown status code " + this.status + ".";
  }
  
  private boolean isSet(int paramInt) { return this.failureInfo[paramInt]; }
  
  public String getFailureCodeAsText() {
    if (this.failureInfo == null)
      return ""; 
    try {
      if (isSet(0))
        return "Unrecognized or unsupported algorithm identifier."; 
      if (isSet(2))
        return "The requested transaction is not permitted or supported."; 
      if (isSet(5))
        return "The data submitted has the wrong format."; 
      if (isSet(14))
        return "The TSA's time source is not available."; 
      if (isSet(15))
        return "The requested TSA policy is not supported by the TSA."; 
      if (isSet(16))
        return "The requested extension is not supported by the TSA."; 
      if (isSet(17))
        return "The additional information requested could not be understood or is not available."; 
      if (isSet(25))
        return "The request cannot be handled due to system failure."; 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {}
    return "unknown failure code";
  }
  
  public PKCS7 getToken() { return this.tsToken; }
  
  public TimestampToken getTimestampToken() { return this.tstInfo; }
  
  public byte[] getEncodedToken() { return this.encodedTsToken; }
  
  private void parse(byte[] paramArrayOfByte) throws IOException {
    DerValue derValue1 = new DerValue(paramArrayOfByte);
    if (derValue1.tag != 48)
      throw new IOException("Bad encoding for timestamp response"); 
    DerValue derValue2 = derValue1.data.getDerValue();
    this.status = derValue2.data.getInteger();
    if (debug != null)
      debug.println("timestamp response: status=" + this.status); 
    if (derValue2.data.available() > 0) {
      byte b = (byte)derValue2.data.peekByte();
      if (b == 48) {
        DerValue[] arrayOfDerValue = derValue2.data.getSequence(1);
        this.statusString = new String[arrayOfDerValue.length];
        for (byte b1 = 0; b1 < arrayOfDerValue.length; b1++) {
          this.statusString[b1] = arrayOfDerValue[b1].getUTF8String();
          if (debug != null)
            debug.println("timestamp response: statusString=" + this.statusString[b1]); 
        } 
      } 
    } 
    if (derValue2.data.available() > 0)
      this.failureInfo = derValue2.data.getUnalignedBitString().toBooleanArray(); 
    if (derValue1.data.available() > 0) {
      DerValue derValue = derValue1.data.getDerValue();
      this.encodedTsToken = derValue.toByteArray();
      this.tsToken = new PKCS7(this.encodedTsToken);
      this.tstInfo = new TimestampToken(this.tsToken.getContentInfo().getData());
    } 
    if (this.status == 0 || this.status == 1) {
      if (this.tsToken == null)
        throw new TimestampException("Bad encoding for timestamp response: expected a timeStampToken element to be present"); 
    } else if (this.tsToken != null) {
      throw new TimestampException("Bad encoding for timestamp response: expected no timeStampToken element to be present");
    } 
  }
  
  static final class TimestampException extends IOException {
    private static final long serialVersionUID = -1631631794891940953L;
    
    TimestampException(String param1String) { super(param1String); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\timestamp\TSResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */