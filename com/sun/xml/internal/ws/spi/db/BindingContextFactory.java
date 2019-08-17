package com.sun.xml.internal.ws.spi.db;

import com.sun.xml.internal.ws.db.glassfish.JAXBRIContextFactory;
import com.sun.xml.internal.ws.util.ServiceConfigurationError;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public abstract class BindingContextFactory {
  public static final String DefaultDatabindingMode = "glassfish.jaxb";
  
  public static final String JAXB_CONTEXT_FACTORY_PROPERTY = BindingContextFactory.class.getName();
  
  public static final Logger LOGGER = Logger.getLogger(BindingContextFactory.class.getName());
  
  public static Iterator<BindingContextFactory> serviceIterator() {
    ServiceFinder serviceFinder = ServiceFinder.find(BindingContextFactory.class);
    final Iterator ibcf = serviceFinder.iterator();
    return new Iterator<BindingContextFactory>() {
        private BindingContextFactory bcf;
        
        public boolean hasNext() {
          while (true) {
            try {
              if (ibcf.hasNext()) {
                this.bcf = (BindingContextFactory)ibcf.next();
                return true;
              } 
              return false;
            } catch (ServiceConfigurationError serviceConfigurationError) {
              BindingContextFactory.LOGGER.warning("skipping factory: ServiceConfigurationError: " + serviceConfigurationError.getMessage());
            } catch (NoClassDefFoundError noClassDefFoundError) {
              BindingContextFactory.LOGGER.fine("skipping factory: NoClassDefFoundError: " + noClassDefFoundError.getMessage());
            } 
          } 
        }
        
        public BindingContextFactory next() {
          if (BindingContextFactory.LOGGER.isLoggable(Level.FINER))
            BindingContextFactory.LOGGER.finer("SPI found provider: " + this.bcf.getClass().getName()); 
          return this.bcf;
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
      };
  }
  
  private static List<BindingContextFactory> factories() {
    ArrayList arrayList = new ArrayList();
    Iterator iterator = serviceIterator();
    while (iterator.hasNext())
      arrayList.add(iterator.next()); 
    if (arrayList.isEmpty()) {
      if (LOGGER.isLoggable(Level.FINER))
        LOGGER.log(Level.FINER, "No SPI providers for BindingContextFactory found, adding: " + JAXBRIContextFactory.class.getName()); 
      arrayList.add(new JAXBRIContextFactory());
    } 
    return arrayList;
  }
  
  protected abstract BindingContext newContext(JAXBContext paramJAXBContext);
  
  protected abstract BindingContext newContext(BindingInfo paramBindingInfo);
  
  protected abstract boolean isFor(String paramString);
  
  protected abstract BindingContext getContext(Marshaller paramMarshaller);
  
  private static BindingContextFactory getFactory(String paramString) {
    for (BindingContextFactory bindingContextFactory : factories()) {
      if (bindingContextFactory.isFor(paramString))
        return bindingContextFactory; 
    } 
    return null;
  }
  
  public static BindingContext create(JAXBContext paramJAXBContext) { return getJAXBFactory(paramJAXBContext).newContext(paramJAXBContext); }
  
  public static BindingContext create(BindingInfo paramBindingInfo) {
    String str = paramBindingInfo.getDatabindingMode();
    if (str != null) {
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "Using SEI-configured databindng mode: " + str); 
    } else if ((str = System.getProperty("BindingContextFactory")) != null) {
      paramBindingInfo.setDatabindingMode(str);
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "Using databindng: " + str + " based on 'BindingContextFactory' System property"); 
    } else if ((str = System.getProperty(JAXB_CONTEXT_FACTORY_PROPERTY)) != null) {
      paramBindingInfo.setDatabindingMode(str);
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "Using databindng: " + str + " based on '" + JAXB_CONTEXT_FACTORY_PROPERTY + "' System property"); 
    } else {
      Iterator iterator = factories().iterator();
      if (iterator.hasNext()) {
        BindingContextFactory bindingContextFactory1 = (BindingContextFactory)iterator.next();
        if (LOGGER.isLoggable(Level.FINE))
          LOGGER.log(Level.FINE, "Using SPI-determined databindng mode: " + bindingContextFactory1.getClass().getName()); 
        return bindingContextFactory1.newContext(paramBindingInfo);
      } 
      LOGGER.log(Level.SEVERE, "No Binding Context Factories found.");
      throw new DatabindingException("No Binding Context Factories found.");
    } 
    BindingContextFactory bindingContextFactory = getFactory(str);
    if (bindingContextFactory != null)
      return bindingContextFactory.newContext(paramBindingInfo); 
    LOGGER.severe("Unknown Databinding mode: " + str);
    throw new DatabindingException("Unknown Databinding mode: " + str);
  }
  
  public static boolean isContextSupported(Object paramObject) {
    if (paramObject == null)
      return false; 
    String str = paramObject.getClass().getPackage().getName();
    for (BindingContextFactory bindingContextFactory : factories()) {
      if (bindingContextFactory.isFor(str))
        return true; 
    } 
    return false;
  }
  
  static BindingContextFactory getJAXBFactory(Object paramObject) {
    String str = paramObject.getClass().getPackage().getName();
    BindingContextFactory bindingContextFactory = getFactory(str);
    if (bindingContextFactory != null)
      return bindingContextFactory; 
    throw new DatabindingException("Unknown JAXBContext implementation: " + paramObject.getClass());
  }
  
  public static BindingContext getBindingContext(Marshaller paramMarshaller) { return getJAXBFactory(paramMarshaller).getContext(paramMarshaller); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\BindingContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */