package sun.net.httpserver;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;

public class AuthFilter extends Filter {
  private Authenticator authenticator;
  
  public AuthFilter(Authenticator paramAuthenticator) { this.authenticator = paramAuthenticator; }
  
  public String description() { return "Authentication filter"; }
  
  public void setAuthenticator(Authenticator paramAuthenticator) { this.authenticator = paramAuthenticator; }
  
  public void consumeInput(HttpExchange paramHttpExchange) throws IOException {
    InputStream inputStream = paramHttpExchange.getRequestBody();
    byte[] arrayOfByte = new byte[4096];
    while (inputStream.read(arrayOfByte) != -1);
    inputStream.close();
  }
  
  public void doFilter(HttpExchange paramHttpExchange, Filter.Chain paramChain) throws IOException {
    if (this.authenticator != null) {
      Authenticator.Result result = this.authenticator.authenticate(paramHttpExchange);
      if (result instanceof Authenticator.Success) {
        Authenticator.Success success = (Authenticator.Success)result;
        ExchangeImpl exchangeImpl = ExchangeImpl.get(paramHttpExchange);
        exchangeImpl.setPrincipal(success.getPrincipal());
        paramChain.doFilter(paramHttpExchange);
      } else if (result instanceof Authenticator.Retry) {
        Authenticator.Retry retry = (Authenticator.Retry)result;
        consumeInput(paramHttpExchange);
        paramHttpExchange.sendResponseHeaders(retry.getResponseCode(), -1L);
      } else if (result instanceof Authenticator.Failure) {
        Authenticator.Failure failure = (Authenticator.Failure)result;
        consumeInput(paramHttpExchange);
        paramHttpExchange.sendResponseHeaders(failure.getResponseCode(), -1L);
      } 
    } else {
      paramChain.doFilter(paramHttpExchange);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\AuthFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */