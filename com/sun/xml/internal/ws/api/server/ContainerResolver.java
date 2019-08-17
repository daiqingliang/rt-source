package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;

public abstract class ContainerResolver {
  private static final ThreadLocalContainerResolver DEFAULT = new ThreadLocalContainerResolver();
  
  public static void setInstance(ContainerResolver paramContainerResolver) {
    if (paramContainerResolver == null)
      paramContainerResolver = DEFAULT; 
    theResolver = paramContainerResolver;
  }
  
  @NotNull
  public static ContainerResolver getInstance() { return theResolver; }
  
  public static ThreadLocalContainerResolver getDefault() { return DEFAULT; }
  
  @NotNull
  public abstract Container getContainer();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\ContainerResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */