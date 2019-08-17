package com.sun.org.apache.xml.internal.security.transforms;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class InvalidTransformException extends XMLSecurityException {
  private static final long serialVersionUID = 1L;
  
  public InvalidTransformException() {}
  
  public InvalidTransformException(String paramString) { super(paramString); }
  
  public InvalidTransformException(String paramString, Object[] paramArrayOfObject) { super(paramString, paramArrayOfObject); }
  
  public InvalidTransformException(String paramString, Exception paramException) { super(paramString, paramException); }
  
  public InvalidTransformException(String paramString, Object[] paramArrayOfObject, Exception paramException) { super(paramString, paramArrayOfObject, paramException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\InvalidTransformException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */