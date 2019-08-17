package sun.security.jgss;

import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

public class GSSExceptionImpl extends GSSException {
  private static final long serialVersionUID = 4251197939069005575L;
  
  private String majorMessage;
  
  GSSExceptionImpl(int paramInt, Oid paramOid) {
    super(paramInt);
    this.majorMessage = getMajorString() + ": " + paramOid;
  }
  
  public GSSExceptionImpl(int paramInt, String paramString) {
    super(paramInt);
    this.majorMessage = paramString;
  }
  
  public GSSExceptionImpl(int paramInt, Exception paramException) {
    super(paramInt);
    initCause(paramException);
  }
  
  public GSSExceptionImpl(int paramInt, String paramString, Exception paramException) {
    this(paramInt, paramString);
    initCause(paramException);
  }
  
  public String getMessage() { return (this.majorMessage != null) ? this.majorMessage : super.getMessage(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\GSSExceptionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */