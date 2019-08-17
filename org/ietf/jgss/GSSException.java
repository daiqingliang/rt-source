package org.ietf.jgss;

public class GSSException extends Exception {
  private static final long serialVersionUID = -2706218945227726672L;
  
  public static final int BAD_BINDINGS = 1;
  
  public static final int BAD_MECH = 2;
  
  public static final int BAD_NAME = 3;
  
  public static final int BAD_NAMETYPE = 4;
  
  public static final int BAD_STATUS = 5;
  
  public static final int BAD_MIC = 6;
  
  public static final int CONTEXT_EXPIRED = 7;
  
  public static final int CREDENTIALS_EXPIRED = 8;
  
  public static final int DEFECTIVE_CREDENTIAL = 9;
  
  public static final int DEFECTIVE_TOKEN = 10;
  
  public static final int FAILURE = 11;
  
  public static final int NO_CONTEXT = 12;
  
  public static final int NO_CRED = 13;
  
  public static final int BAD_QOP = 14;
  
  public static final int UNAUTHORIZED = 15;
  
  public static final int UNAVAILABLE = 16;
  
  public static final int DUPLICATE_ELEMENT = 17;
  
  public static final int NAME_NOT_MN = 18;
  
  public static final int DUPLICATE_TOKEN = 19;
  
  public static final int OLD_TOKEN = 20;
  
  public static final int UNSEQ_TOKEN = 21;
  
  public static final int GAP_TOKEN = 22;
  
  private static String[] messages = { 
      "Channel binding mismatch", "Unsupported mechanism requested", "Invalid name provided", "Name of unsupported type provided", "Invalid input status selector", "Token had invalid integrity check", "Specified security context expired", "Expired credentials detected", "Defective credential detected", "Defective token detected", 
      "Failure unspecified at GSS-API level", "Security context init/accept not yet called or context deleted", "No valid credentials provided", "Unsupported QOP value", "Operation unauthorized", "Operation unavailable", "Duplicate credential element requested", "Name contains multi-mechanism elements", "The token was a duplicate of an earlier token", "The token's validity period has expired", 
      "A later token has already been processed", "An expected per-message token was not received" };
  
  private int major;
  
  private int minor = 0;
  
  private String minorMessage = null;
  
  private String majorString = null;
  
  public GSSException(int paramInt) {
    if (validateMajor(paramInt)) {
      this.major = paramInt;
    } else {
      this.major = 11;
    } 
  }
  
  GSSException(int paramInt, String paramString) {
    if (validateMajor(paramInt)) {
      this.major = paramInt;
    } else {
      this.major = 11;
    } 
    this.majorString = paramString;
  }
  
  public GSSException(int paramInt1, int paramInt2, String paramString) {
    if (validateMajor(paramInt1)) {
      this.major = paramInt1;
    } else {
      this.major = 11;
    } 
    this.minor = paramInt2;
    this.minorMessage = paramString;
  }
  
  public int getMajor() { return this.major; }
  
  public int getMinor() { return this.minor; }
  
  public String getMajorString() { return (this.majorString != null) ? this.majorString : messages[this.major - 1]; }
  
  public String getMinorString() { return this.minorMessage; }
  
  public void setMinor(int paramInt, String paramString) {
    this.minor = paramInt;
    this.minorMessage = paramString;
  }
  
  public String toString() { return "GSSException: " + getMessage(); }
  
  public String getMessage() { return (this.minor == 0) ? getMajorString() : (getMajorString() + " (Mechanism level: " + getMinorString() + ")"); }
  
  private boolean validateMajor(int paramInt) { return (paramInt > 0 && paramInt <= messages.length); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\ietf\jgss\GSSException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */