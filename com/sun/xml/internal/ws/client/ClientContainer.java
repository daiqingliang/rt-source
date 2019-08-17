package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.api.ResourceLoader;
import com.sun.xml.internal.ws.api.server.Container;
import java.net.MalformedURLException;
import java.net.URL;

final class ClientContainer extends Container {
  private final ResourceLoader loader = new ResourceLoader() {
      public URL getResource(String param1String) throws MalformedURLException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null)
          classLoader = getClass().getClassLoader(); 
        return classLoader.getResource("META-INF/" + param1String);
      }
    };
  
  public <T> T getSPI(Class<T> paramClass) {
    Object object = super.getSPI(paramClass);
    return (object != null) ? (T)object : ((paramClass == ResourceLoader.class) ? (T)paramClass.cast(this.loader) : null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\ClientContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */