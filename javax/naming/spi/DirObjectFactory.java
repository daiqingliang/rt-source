package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.directory.Attributes;

public interface DirObjectFactory extends ObjectFactory {
  Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable, Attributes paramAttributes) throws Exception;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\spi\DirObjectFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */