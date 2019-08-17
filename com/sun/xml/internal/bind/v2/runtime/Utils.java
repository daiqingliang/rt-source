package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;

final class Utils {
  private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());
  
  static final Navigator<Type, Class, Field, Method> REFLECTION_NAVIGATOR;
  
  static  {
    try {
      final Class refNav = Class.forName("com.sun.xml.internal.bind.v2.model.nav.ReflectionNavigator");
      Method method = (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
            public Method run() {
              try {
                Method method = refNav.getDeclaredMethod("getInstance", new Class[0]);
                method.setAccessible(true);
                return method;
              } catch (NoSuchMethodException noSuchMethodException) {
                throw new IllegalStateException("ReflectionNavigator.getInstance can't be found");
              } 
            }
          });
      REFLECTION_NAVIGATOR = (Navigator)method.invoke(null, new Object[0]);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new IllegalStateException("Can't find ReflectionNavigator class");
    } catch (InvocationTargetException invocationTargetException) {
      throw new IllegalStateException("ReflectionNavigator.getInstance throws the exception");
    } catch (IllegalAccessException illegalAccessException) {
      throw new IllegalStateException("ReflectionNavigator.getInstance method is inaccessible");
    } catch (SecurityException securityException) {
      LOGGER.log(Level.FINE, "Unable to access ReflectionNavigator.getInstance", securityException);
      throw securityException;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */