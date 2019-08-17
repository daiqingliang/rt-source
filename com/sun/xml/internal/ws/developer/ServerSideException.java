package com.sun.xml.internal.ws.developer;

public class ServerSideException extends Exception {
  private final String className;
  
  public ServerSideException(String paramString1, String paramString2) {
    super(paramString2);
    this.className = paramString1;
  }
  
  public String getMessage() { return "Client received an exception from server: " + super.getMessage() + " Please see the server log to find more detail regarding exact cause of the failure."; }
  
  public String toString() {
    String str1 = this.className;
    String str2 = getLocalizedMessage();
    return (str2 != null) ? (str1 + ": " + str2) : str1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\developer\ServerSideException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */