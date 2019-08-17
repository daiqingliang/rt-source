package javax.naming.ldap;

import com.sun.naming.internal.FactoryEnumeration;
import com.sun.naming.internal.ResourceManager;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;

public abstract class ControlFactory {
  public abstract Control getControlInstance(Control paramControl) throws NamingException;
  
  public static Control getControlInstance(Control paramControl, Context paramContext, Hashtable<?, ?> paramHashtable) throws NamingException {
    FactoryEnumeration factoryEnumeration = ResourceManager.getFactories("java.naming.factory.control", paramHashtable, paramContext);
    if (factoryEnumeration == null)
      return paramControl; 
    Control control;
    for (control = null; control == null && factoryEnumeration.hasMore(); control = controlFactory.getControlInstance(paramControl))
      ControlFactory controlFactory = (ControlFactory)factoryEnumeration.next(); 
    return (control != null) ? control : paramControl;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\ControlFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */