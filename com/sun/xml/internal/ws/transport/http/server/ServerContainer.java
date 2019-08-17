package com.sun.xml.internal.ws.transport.http.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.Module;
import java.util.ArrayList;
import java.util.List;

class ServerContainer extends Container {
  private final Module module = new Module() {
      private final List<BoundEndpoint> endpoints = new ArrayList();
      
      @NotNull
      public List<BoundEndpoint> getBoundEndpoints() { return this.endpoints; }
    };
  
  public <T> T getSPI(Class<T> paramClass) {
    Object object = super.getSPI(paramClass);
    return (object != null) ? (T)object : ((paramClass == Module.class) ? (T)paramClass.cast(this.module) : null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\http\server\ServerContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */