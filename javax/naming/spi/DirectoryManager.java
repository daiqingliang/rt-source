package javax.naming.spi;

import com.sun.naming.internal.FactoryEnumeration;
import com.sun.naming.internal.ResourceManager;
import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

public class DirectoryManager extends NamingManager {
  public static DirContext getContinuationDirContext(CannotProceedException paramCannotProceedException) throws NamingException {
    Hashtable hashtable = paramCannotProceedException.getEnvironment();
    if (hashtable == null) {
      hashtable = new Hashtable(7);
    } else {
      hashtable = (Hashtable)hashtable.clone();
    } 
    hashtable.put("java.naming.spi.CannotProceedException", paramCannotProceedException);
    return new ContinuationDirContext(paramCannotProceedException, hashtable);
  }
  
  public static Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable, Attributes paramAttributes) throws Exception {
    ObjectFactoryBuilder objectFactoryBuilder = getObjectFactoryBuilder();
    if (objectFactoryBuilder != null) {
      ObjectFactory objectFactory = objectFactoryBuilder.createObjectFactory(paramObject, paramHashtable);
      return (objectFactory instanceof DirObjectFactory) ? ((DirObjectFactory)objectFactory).getObjectInstance(paramObject, paramName, paramContext, paramHashtable, paramAttributes) : objectFactory.getObjectInstance(paramObject, paramName, paramContext, paramHashtable);
    } 
    Reference reference = null;
    if (paramObject instanceof Reference) {
      reference = (Reference)paramObject;
    } else if (paramObject instanceof Referenceable) {
      reference = ((Referenceable)paramObject).getReference();
    } 
    if (reference != null) {
      String str = reference.getFactoryClassName();
      if (str != null) {
        ObjectFactory objectFactory = getObjectFactoryFromReference(reference, str);
        return (objectFactory instanceof DirObjectFactory) ? ((DirObjectFactory)objectFactory).getObjectInstance(reference, paramName, paramContext, paramHashtable, paramAttributes) : ((objectFactory != null) ? objectFactory.getObjectInstance(reference, paramName, paramContext, paramHashtable) : paramObject);
      } 
      Object object1 = processURLAddrs(reference, paramName, paramContext, paramHashtable);
      if (object1 != null)
        return object1; 
    } 
    Object object = createObjectFromFactories(paramObject, paramName, paramContext, paramHashtable, paramAttributes);
    return (object != null) ? object : paramObject;
  }
  
  private static Object createObjectFromFactories(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable, Attributes paramAttributes) throws Exception {
    FactoryEnumeration factoryEnumeration = ResourceManager.getFactories("java.naming.factory.object", paramHashtable, paramContext);
    if (factoryEnumeration == null)
      return null; 
    Object object;
    for (object = null; object == null && factoryEnumeration.hasMore(); object = objectFactory.getObjectInstance(paramObject, paramName, paramContext, paramHashtable)) {
      ObjectFactory objectFactory = (ObjectFactory)factoryEnumeration.next();
      if (objectFactory instanceof DirObjectFactory) {
        object = ((DirObjectFactory)objectFactory).getObjectInstance(paramObject, paramName, paramContext, paramHashtable, paramAttributes);
        continue;
      } 
    } 
    return object;
  }
  
  public static DirStateFactory.Result getStateToBind(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable, Attributes paramAttributes) throws NamingException {
    FactoryEnumeration factoryEnumeration = ResourceManager.getFactories("java.naming.factory.state", paramHashtable, paramContext);
    if (factoryEnumeration == null)
      return new DirStateFactory.Result(paramObject, paramAttributes); 
    DirStateFactory.Result result = null;
    while (result == null && factoryEnumeration.hasMore()) {
      StateFactory stateFactory = (StateFactory)factoryEnumeration.next();
      if (stateFactory instanceof DirStateFactory) {
        result = ((DirStateFactory)stateFactory).getStateToBind(paramObject, paramName, paramContext, paramHashtable, paramAttributes);
        continue;
      } 
      Object object = stateFactory.getStateToBind(paramObject, paramName, paramContext, paramHashtable);
      if (object != null)
        result = new DirStateFactory.Result(object, paramAttributes); 
    } 
    return (result != null) ? result : new DirStateFactory.Result(paramObject, paramAttributes);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\spi\DirectoryManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */