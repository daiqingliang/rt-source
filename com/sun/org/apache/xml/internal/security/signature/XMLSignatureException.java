package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class XMLSignatureException extends XMLSecurityException {
  private static final long serialVersionUID = 1L;
  
  public XMLSignatureException() {}
  
  public XMLSignatureException(String paramString) { super(paramString); }
  
  public XMLSignatureException(String paramString, Object[] paramArrayOfObject) { super(paramString, paramArrayOfObject); }
  
  public XMLSignatureException(String paramString, Exception paramException) { super(paramString, paramException); }
  
  public XMLSignatureException(String paramString, Object[] paramArrayOfObject, Exception paramException) { super(paramString, paramArrayOfObject, paramException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\signature\XMLSignatureException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */