package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

public interface StateFactory {
  Object getStateToBind(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws NamingException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\spi\StateFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */