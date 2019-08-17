package javax.print;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import javax.print.attribute.AttributeSet;
import sun.awt.AppContext;

public abstract class PrintServiceLookup {
  private static Services getServicesForContext() {
    Services services = (Services)AppContext.getAppContext().get(Services.class);
    if (services == null) {
      services = new Services();
      AppContext.getAppContext().put(Services.class, services);
    } 
    return services;
  }
  
  private static ArrayList getListOfLookupServices() { return (getServicesForContext()).listOfLookupServices; }
  
  private static ArrayList initListOfLookupServices() {
    ArrayList arrayList = new ArrayList();
    (getServicesForContext()).listOfLookupServices = arrayList;
    return arrayList;
  }
  
  private static ArrayList getRegisteredServices() { return (getServicesForContext()).registeredServices; }
  
  private static ArrayList initRegisteredServices() {
    ArrayList arrayList = new ArrayList();
    (getServicesForContext()).registeredServices = arrayList;
    return arrayList;
  }
  
  public static final PrintService[] lookupPrintServices(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet) {
    ArrayList arrayList = getServices(paramDocFlavor, paramAttributeSet);
    return (PrintService[])arrayList.toArray(new PrintService[arrayList.size()]);
  }
  
  public static final MultiDocPrintService[] lookupMultiDocPrintServices(DocFlavor[] paramArrayOfDocFlavor, AttributeSet paramAttributeSet) {
    ArrayList arrayList = getMultiDocServices(paramArrayOfDocFlavor, paramAttributeSet);
    return (MultiDocPrintService[])arrayList.toArray(new MultiDocPrintService[arrayList.size()]);
  }
  
  public static final PrintService lookupDefaultPrintService() {
    Iterator iterator = getAllLookupServices().iterator();
    while (iterator.hasNext()) {
      try {
        PrintServiceLookup printServiceLookup = (PrintServiceLookup)iterator.next();
        PrintService printService = printServiceLookup.getDefaultPrintService();
        if (printService != null)
          return printService; 
      } catch (Exception exception) {}
    } 
    return null;
  }
  
  public static boolean registerServiceProvider(PrintServiceLookup paramPrintServiceLookup) {
    synchronized (PrintServiceLookup.class) {
      Iterator iterator = getAllLookupServices().iterator();
      while (iterator.hasNext()) {
        try {
          Object object = iterator.next();
          if (object.getClass() == paramPrintServiceLookup.getClass())
            return false; 
        } catch (Exception exception) {}
      } 
      getListOfLookupServices().add(paramPrintServiceLookup);
      return true;
    } 
  }
  
  public static boolean registerService(PrintService paramPrintService) {
    synchronized (PrintServiceLookup.class) {
      if (paramPrintService instanceof StreamPrintService)
        return false; 
      ArrayList arrayList = getRegisteredServices();
      if (arrayList == null) {
        arrayList = initRegisteredServices();
      } else if (arrayList.contains(paramPrintService)) {
        return false;
      } 
      arrayList.add(paramPrintService);
      return true;
    } 
  }
  
  public abstract PrintService[] getPrintServices(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet);
  
  public abstract PrintService[] getPrintServices();
  
  public abstract MultiDocPrintService[] getMultiDocPrintServices(DocFlavor[] paramArrayOfDocFlavor, AttributeSet paramAttributeSet);
  
  public abstract PrintService getDefaultPrintService();
  
  private static ArrayList getAllLookupServices() {
    synchronized (PrintServiceLookup.class) {
      ArrayList arrayList = getListOfLookupServices();
      if (arrayList != null)
        return arrayList; 
      arrayList = initListOfLookupServices();
      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction() {
              public Object run() {
                Iterator iterator = ServiceLoader.load(PrintServiceLookup.class).iterator();
                ArrayList arrayList = PrintServiceLookup.getListOfLookupServices();
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
  
  private static ArrayList getServices(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet) {
    ArrayList arrayList1 = new ArrayList();
    Iterator iterator = getAllLookupServices().iterator();
    while (iterator.hasNext()) {
      try {
        PrintServiceLookup printServiceLookup = (PrintServiceLookup)iterator.next();
        PrintService[] arrayOfPrintService = null;
        if (paramDocFlavor == null && paramAttributeSet == null) {
          try {
            arrayOfPrintService = printServiceLookup.getPrintServices();
          } catch (Throwable throwable) {}
        } else {
          arrayOfPrintService = printServiceLookup.getPrintServices(paramDocFlavor, paramAttributeSet);
        } 
        if (arrayOfPrintService == null)
          continue; 
        for (byte b = 0; b < arrayOfPrintService.length; b++)
          arrayList1.add(arrayOfPrintService[b]); 
      } catch (Exception exception) {}
    } 
    ArrayList arrayList2 = null;
    try {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPrintJobAccess(); 
      arrayList2 = getRegisteredServices();
    } catch (SecurityException securityException) {}
    if (arrayList2 != null) {
      PrintService[] arrayOfPrintService = (PrintService[])arrayList2.toArray(new PrintService[arrayList2.size()]);
      for (byte b = 0; b < arrayOfPrintService.length; b++) {
        if (!arrayList1.contains(arrayOfPrintService[b]))
          if (paramDocFlavor == null && paramAttributeSet == null) {
            arrayList1.add(arrayOfPrintService[b]);
          } else if (((paramDocFlavor != null && arrayOfPrintService[b].isDocFlavorSupported(paramDocFlavor)) || paramDocFlavor == null) && null == arrayOfPrintService[b].getUnsupportedAttributes(paramDocFlavor, paramAttributeSet)) {
            arrayList1.add(arrayOfPrintService[b]);
          }  
      } 
    } 
    return arrayList1;
  }
  
  private static ArrayList getMultiDocServices(DocFlavor[] paramArrayOfDocFlavor, AttributeSet paramAttributeSet) {
    ArrayList arrayList1 = new ArrayList();
    Iterator iterator = getAllLookupServices().iterator();
    while (iterator.hasNext()) {
      try {
        PrintServiceLookup printServiceLookup = (PrintServiceLookup)iterator.next();
        MultiDocPrintService[] arrayOfMultiDocPrintService = printServiceLookup.getMultiDocPrintServices(paramArrayOfDocFlavor, paramAttributeSet);
        if (arrayOfMultiDocPrintService == null)
          continue; 
        for (byte b = 0; b < arrayOfMultiDocPrintService.length; b++)
          arrayList1.add(arrayOfMultiDocPrintService[b]); 
      } catch (Exception exception) {}
    } 
    ArrayList arrayList2 = null;
    try {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPrintJobAccess(); 
      arrayList2 = getRegisteredServices();
    } catch (Exception exception) {}
    if (arrayList2 != null) {
      PrintService[] arrayOfPrintService = (PrintService[])arrayList2.toArray(new PrintService[arrayList2.size()]);
      for (byte b = 0; b < arrayOfPrintService.length; b++) {
        if (arrayOfPrintService[b] instanceof MultiDocPrintService && !arrayList1.contains(arrayOfPrintService[b]))
          if (paramArrayOfDocFlavor == null || paramArrayOfDocFlavor.length == 0) {
            arrayList1.add(arrayOfPrintService[b]);
          } else {
            boolean bool = true;
            for (byte b1 = 0; b1 < paramArrayOfDocFlavor.length; b1++) {
              if (arrayOfPrintService[b].isDocFlavorSupported(paramArrayOfDocFlavor[b1])) {
                if (arrayOfPrintService[b].getUnsupportedAttributes(paramArrayOfDocFlavor[b1], paramAttributeSet) != null) {
                  bool = false;
                  break;
                } 
              } else {
                bool = false;
                break;
              } 
            } 
            if (bool)
              arrayList1.add(arrayOfPrintService[b]); 
          }  
      } 
    } 
    return arrayList1;
  }
  
  static class Services {
    private ArrayList listOfLookupServices = null;
    
    private ArrayList registeredServices = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\PrintServiceLookup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */