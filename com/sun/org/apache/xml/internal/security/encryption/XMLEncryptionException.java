package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class XMLEncryptionException extends XMLSecurityException {
  private static final long serialVersionUID = 1L;
  
  public XMLEncryptionException() {}
  
  public XMLEncryptionException(String paramString) { super(paramString); }
  
  public XMLEncryptionException(String paramString, Object[] paramArrayOfObject) { super(paramString, paramArrayOfObject); }
  
  public XMLEncryptionException(String paramString, Exception paramException) { super(paramString, paramException); }
  
  public XMLEncryptionException(String paramString, Object[] paramArrayOfObject, Exception paramException) { super(paramString, paramArrayOfObject, paramException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\XMLEncryptionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */