package javax.print;

import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import sun.awt.AppContext;

public abstract class StreamPrintServiceFactory {
  private static Services getServices() {
    Services services = (Services)AppContext.getAppContext().get(Services.class);
    if (services == null) {
      services = new Services();
      AppContext.getAppContext().put(Services.class, services);
    } 
    return services;
  }
  
  private static ArrayList getListOfFactories() { return (getServices()).listOfFactories; }
  
  private static ArrayList initListOfFactories() {
    ArrayList arrayList = new ArrayList();
    (getServices()).listOfFactories = arrayList;
    return arrayList;
  }
  
  public static StreamPrintServiceFactory[] lookupStreamPrintServiceFactories(DocFlavor paramDocFlavor, String paramString) {
    ArrayList arrayList = getFactories(paramDocFlavor, paramString);
    return (StreamPrintServiceFactory[])arrayList.toArray(new StreamPrintServiceFactory[arrayList.size()]);
  }
  
  public abstract String getOutputFormat();
  
  public abstract DocFlavor[] getSupportedDocFlavors();
  
  public abstract StreamPrintService getPrintService(OutputStream paramOutputStream);
  
  private static ArrayList getAllFactories() {
    synchronized (StreamPrintServiceFactory.class) {
      ArrayList arrayList = getListOfFactories();
      if (arrayList != null)
        return arrayList; 
      arrayList = initListOfFactories();
      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction() {
              public Object run() {
                Iterator iterator = ServiceLoader.load(StreamPrintServiceFactory.class).iterator();
                ArrayList arrayList = StreamPrintServiceFactory.getListOfFactories();
                while (iterator.hasNext()) {
                  try {
                    arrayList.add(iterator.next());
                  } catch (ServiceConfigurationError serviceConfigurationError) {
                    if (System.getSecurityManager() != null) {
                      serviceConfigurationError.printStackTrace();
                      continue;
                    } 
                    throw serviceConfigurationError;
                  } 
                } 
                return null;
              }
            });
      } catch (PrivilegedActionException privilegedActionException) {}
      return arrayList;
    } 
  }
  
  private static boolean isMember(DocFlavor paramDocFlavor, DocFlavor[] paramArrayOfDocFlavor) {
    for (byte b = 0; b < paramArrayOfDocFlavor.length; b++) {
      if (paramDocFlavor.equals(paramArrayOfDocFlavor[b]))
        return true; 
    } 
    return false;
  }
  
  private static ArrayList getFactories(DocFlavor paramDocFlavor, String paramString) {
    if (paramDocFlavor == null && paramString == null)
      return getAllFactories(); 
    ArrayList arrayList = new ArrayList();
    Iterator iterator = getAllFactories().iterator();
    while (iterator.hasNext()) {
      StreamPrintServiceFactory streamPrintServiceFactory;
      if ((paramString == null || paramString.equalsIgnoreCase(streamPrintServiceFactory.getOutputFormat())) && (paramDocFlavor == null || (streamPrintServiceFactory = (StreamPrintServiceFactory)iterator.next()).isMember(paramDocFlavor, streamPrintServiceFactory.getSupportedDocFlavors())))
        arrayList.add(streamPrintServiceFactory); 
    } 
    return arrayList;
  }
  
  static class Services {
    private ArrayList listOfFactories = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\StreamPrintServiceFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */