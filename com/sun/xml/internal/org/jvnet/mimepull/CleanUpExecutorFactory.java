package com.sun.xml.internal.org.jvnet.mimepull;

import java.util.concurrent.Executor;

public abstract class CleanUpExecutorFactory {
  private static final String DEFAULT_PROPERTY_NAME = CleanUpExecutorFactory.class.getName();
  
  public static CleanUpExecutorFactory newInstance() {
    try {
      return (CleanUpExecutorFactory)FactoryFinder.find(DEFAULT_PROPERTY_NAME);
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public abstract Executor getExecutor();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\CleanUpExecutorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */