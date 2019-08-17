package com.sun.xml.internal.ws.transport.http.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.resources.HttpserverMessages;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.spi.http.HttpExchange;
import javax.xml.ws.spi.http.HttpHandler;

final class PortableHttpHandler extends HttpHandler {
  private static final String GET_METHOD = "GET";
  
  private static final String POST_METHOD = "POST";
  
  private static final String HEAD_METHOD = "HEAD";
  
  private static final String PUT_METHOD = "PUT";
  
  private static final String DELETE_METHOD = "DELETE";
  
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server.http");
  
  private final HttpAdapter adapter;
  
  private final Executor executor;
  
  public PortableHttpHandler(@NotNull HttpAdapter paramHttpAdapter, @Nullable Executor paramExecutor) {
    assert paramHttpAdapter != null;
    this.adapter = paramHttpAdapter;
    this.executor = paramExecutor;
  }
  
  public void handle(HttpExchange paramHttpExchange) {
    try {
      if (logger.isLoggable(Level.FINE))
        logger.log(Level.FINE, "Received HTTP request:{0}", paramHttpExchange.getRequestURI()); 
      if (this.executor != null) {
        this.executor.execute(new HttpHandlerRunnable(paramHttpExchange));
      } else {
        handleExchange(paramHttpExchange);
      } 
    } catch (Throwable throwable) {
      logger.log(Level.SEVERE, null, throwable);
    } 
  }
  
  public void handleExchange(HttpExchange paramHttpExchange) {
    PortableConnectionImpl portableConnectionImpl = new PortableConnectionImpl(this.adapter, paramHttpExchange);
    try {
      if (logger.isLoggable(Level.FINE))
        logger.log(Level.FINE, "Received HTTP request:{0}", paramHttpExchange.getRequestURI()); 
      String str = paramHttpExchange.getRequestMethod();
      if (str.equals("GET") || str.equals("POST") || str.equals("HEAD") || str.equals("PUT") || str.equals("DELETE")) {
        this.adapter.handle(portableConnectionImpl);
      } else {
        logger.warning(HttpserverMessages.UNEXPECTED_HTTP_METHOD(str));
      } 
    } finally {
      paramHttpExchange.close();
    } 
  }
  
  class HttpHandlerRunnable implements Runnable {
    final HttpExchange msg;
    
    HttpHandlerRunnable(HttpExchange param1HttpExchange) { this.msg = param1HttpExchange; }
    
    public void run() {
      try {
        PortableHttpHandler.this.handleExchange(this.msg);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\http\server\PortableHttpHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */