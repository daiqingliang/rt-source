package com.sun.tracing;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import sun.security.action.GetPropertyAction;
import sun.tracing.MultiplexProviderFactory;
import sun.tracing.NullProviderFactory;
import sun.tracing.PrintStreamProviderFactory;
import sun.tracing.dtrace.DTraceProviderFactory;

public abstract class ProviderFactory {
  public abstract <T extends Provider> T createProvider(Class<T> paramClass);
  
  public static ProviderFactory getDefaultFactory() {
    HashSet hashSet = new HashSet();
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("com.sun.tracing.dtrace"));
    if ((str == null || !str.equals("disable")) && DTraceProviderFactory.isSupported())
      hashSet.add(new DTraceProviderFactory()); 
    str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.tracing.stream"));
    if (str != null)
      for (String str1 : str.split(",")) {
        PrintStream printStream = getPrintStreamFromSpec(str1);
        if (printStream != null)
          hashSet.add(new PrintStreamProviderFactory(printStream)); 
      }  
    return (hashSet.size() == 0) ? new NullProviderFactory() : ((hashSet.size() == 1) ? (ProviderFactory[])hashSet.toArray(new ProviderFactory[1])[0] : new MultiplexProviderFactory(hashSet));
  }
  
  private static PrintStream getPrintStreamFromSpec(final String spec) {
    try {
      final int fieldpos = paramString.lastIndexOf('.');
      final Class cls = Class.forName(paramString.substring(0, i));
      Field field = (Field)AccessController.doPrivileged(new PrivilegedExceptionAction<Field>() {
            public Field run() throws NoSuchFieldException { return cls.getField(spec.substring(fieldpos + 1)); }
          });
      return (PrintStream)field.get(null);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new AssertionError(classNotFoundException);
    } catch (IllegalAccessException illegalAccessException) {
      throw new AssertionError(illegalAccessException);
    } catch (PrivilegedActionException privilegedActionException) {
      throw new AssertionError(privilegedActionException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\tracing\ProviderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */