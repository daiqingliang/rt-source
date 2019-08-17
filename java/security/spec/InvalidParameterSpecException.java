package java.security.spec;

import java.security.GeneralSecurityException;

public class InvalidParameterSpecException extends GeneralSecurityException {
  private static final long serialVersionUID = -970468769593399342L;
  
  public InvalidParameterSpecException() {}
  
  public InvalidParameterSpecException(String paramString) { super(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\InvalidParameterSpecException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */