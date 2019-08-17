package com.sun.net.httpserver;

import jdk.Exported;

@Exported
public abstract class Authenticator {
  public abstract Result authenticate(HttpExchange paramHttpExchange);
  
  @Exported
  public static class Failure extends Result {
    private int responseCode;
    
    public Failure(int param1Int) { this.responseCode = param1Int; }
    
    public int getResponseCode() { return this.responseCode; }
  }
  
  public static abstract class Result {}
  
  @Exported
  public static class Retry extends Result {
    private int responseCode;
    
    public Retry(int param1Int) { this.responseCode = param1Int; }
    
    public int getResponseCode() { return this.responseCode; }
  }
  
  @Exported
  public static class Success extends Result {
    private HttpPrincipal principal;
    
    public Success(HttpPrincipal param1HttpPrincipal) { this.principal = param1HttpPrincipal; }
    
    public HttpPrincipal getPrincipal() { return this.principal; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\httpserver\Authenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */