package sun.security.krb5;

import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.Krb5;

public class KrbException extends Exception {
  private static final long serialVersionUID = -4993302876451928596L;
  
  private int returnCode;
  
  private KRBError error;
  
  public KrbException(String paramString) { super(paramString); }
  
  public KrbException(Throwable paramThrowable) { super(paramThrowable); }
  
  public KrbException(int paramInt) { this.returnCode = paramInt; }
  
  public KrbException(int paramInt, String paramString) {
    this(paramString);
    this.returnCode = paramInt;
  }
  
  public KrbException(KRBError paramKRBError) {
    this.returnCode = paramKRBError.getErrorCode();
    this.error = paramKRBError;
  }
  
  public KrbException(KRBError paramKRBError, String paramString) {
    this(paramString);
    this.returnCode = paramKRBError.getErrorCode();
    this.error = paramKRBError;
  }
  
  public KRBError getError() { return this.error; }
  
  public int returnCode() { return this.returnCode; }
  
  public String returnCodeSymbol() { return returnCodeSymbol(this.returnCode); }
  
  public static String returnCodeSymbol(int paramInt) { return "not yet implemented"; }
  
  public String returnCodeMessage() { return Krb5.getErrorMessage(this.returnCode); }
  
  public static String errorMessage(int paramInt) { return Krb5.getErrorMessage(paramInt); }
  
  public String krbErrorMessage() {
    StringBuffer stringBuffer = new StringBuffer("krb_error " + this.returnCode);
    String str = getMessage();
    if (str != null) {
      stringBuffer.append(" ");
      stringBuffer.append(str);
    } 
    return stringBuffer.toString();
  }
  
  public String getMessage() {
    StringBuffer stringBuffer = new StringBuffer();
    int i = returnCode();
    if (i != 0) {
      stringBuffer.append(returnCodeMessage());
      stringBuffer.append(" (").append(returnCode()).append(')');
    } 
    String str = super.getMessage();
    if (str != null && str.length() != 0) {
      if (i != 0)
        stringBuffer.append(" - "); 
      stringBuffer.append(str);
    } 
    return stringBuffer.toString();
  }
  
  public String toString() { return "KrbException: " + getMessage(); }
  
  public int hashCode() {
    int i = 17;
    i = 37 * i + this.returnCode;
    if (this.error != null)
      i = 37 * i + this.error.hashCode(); 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof KrbException))
      return false; 
    KrbException krbException = (KrbException)paramObject;
    return (this.returnCode != krbException.returnCode) ? false : ((this.error == null) ? ((krbException.error == null)) : this.error.equals(krbException.error));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\KrbException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */