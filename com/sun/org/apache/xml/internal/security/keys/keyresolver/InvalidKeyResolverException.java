package com.sun.org.apache.xml.internal.security.keys.keyresolver;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class InvalidKeyResolverException extends XMLSecurityException {
  private static final long serialVersionUID = 1L;
  
  public InvalidKeyResolverException() {}
  
  public InvalidKeyResolverException(String paramString) { super(paramString); }
  
  public InvalidKeyResolverException(String paramString, Object[] paramArrayOfObject) { super(paramString, paramArrayOfObject); }
  
  public InvalidKeyResolverException(String paramString, Exception paramException) { super(paramString, paramException); }
  
  public InvalidKeyResolverException(String paramString, Object[] paramArrayOfObject, Exception paramException) { super(paramString, paramArrayOfObject, paramException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\InvalidKeyResolverException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */