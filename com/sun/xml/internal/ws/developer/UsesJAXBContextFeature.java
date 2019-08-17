package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import com.sun.xml.internal.ws.api.model.SEIModel;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class UsesJAXBContextFeature extends WebServiceFeature {
  public static final String ID = "http://jax-ws.dev.java.net/features/uses-jaxb-context";
  
  private final JAXBContextFactory factory;
  
  @FeatureConstructor({"value"})
  public UsesJAXBContextFeature(@NotNull Class<? extends JAXBContextFactory> paramClass) {
    try {
      this.factory = (JAXBContextFactory)paramClass.getConstructor(new Class[0]).newInstance(new Object[0]);
    } catch (InstantiationException instantiationException) {
      InstantiationError instantiationError = new InstantiationError(instantiationException.getMessage());
      instantiationError.initCause(instantiationException);
      throw instantiationError;
    } catch (IllegalAccessException illegalAccessException) {
      IllegalAccessError illegalAccessError = new IllegalAccessError(illegalAccessException.getMessage());
      illegalAccessError.initCause(illegalAccessException);
      throw illegalAccessError;
    } catch (InvocationTargetException invocationTargetException) {
      InstantiationError instantiationError = new InstantiationError(invocationTargetException.getMessage());
      instantiationError.initCause(invocationTargetException);
      throw instantiationError;
    } catch (NoSuchMethodException noSuchMethodException) {
      NoSuchMethodError noSuchMethodError = new NoSuchMethodError(noSuchMethodException.getMessage());
      noSuchMethodError.initCause(noSuchMethodException);
      throw noSuchMethodError;
    } 
  }
  
  public UsesJAXBContextFeature(@Nullable JAXBContextFactory paramJAXBContextFactory) { this.factory = paramJAXBContextFactory; }
  
  public UsesJAXBContextFeature(@Nullable final JAXBRIContext context) { this.factory = new JAXBContextFactory() {
        @NotNull
        public JAXBRIContext createJAXBContext(@NotNull SEIModel param1SEIModel, @NotNull List<Class> param1List1, @NotNull List<TypeReference> param1List2) throws JAXBException { return context; }
      }; }
  
  @ManagedAttribute
  @Nullable
  public JAXBContextFactory getFactory() { return this.factory; }
  
  @ManagedAttribute
  public String getID() { return "http://jax-ws.dev.java.net/features/uses-jaxb-context"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\developer\UsesJAXBContextFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */