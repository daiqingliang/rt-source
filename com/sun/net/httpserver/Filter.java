package com.sun.net.httpserver;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import jdk.Exported;

@Exported
public abstract class Filter {
  public abstract void doFilter(HttpExchange paramHttpExchange, Chain paramChain) throws IOException;
  
  public abstract String description();
  
  @Exported
  public static class Chain {
    private ListIterator<Filter> iter;
    
    private HttpHandler handler;
    
    public Chain(List<Filter> param1List, HttpHandler param1HttpHandler) {
      this.iter = param1List.listIterator();
      this.handler = param1HttpHandler;
    }
    
    public void doFilter(HttpExchange param1HttpExchange) throws IOException {
      if (!this.iter.hasNext()) {
        this.handler.handle(param1HttpExchange);
      } else {
        Filter filter = (Filter)this.iter.next();
        filter.doFilter(param1HttpExchange, this);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\httpserver\Filter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */