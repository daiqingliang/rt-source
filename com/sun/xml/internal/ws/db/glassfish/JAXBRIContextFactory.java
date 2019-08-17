package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.ContextFactory;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.internal.bind.v2.runtime.MarshallerImpl;
import com.sun.xml.internal.ws.developer.JAXBContextFactory;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.spi.db.BindingInfo;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class JAXBRIContextFactory extends BindingContextFactory {
  public BindingContext newContext(JAXBContext paramJAXBContext) { return new JAXBRIContextWrapper((JAXBRIContext)paramJAXBContext, null); }
  
  public BindingContext newContext(BindingInfo paramBindingInfo) {
    Class[] arrayOfClass = (Class[])paramBindingInfo.contentClasses().toArray(new Class[paramBindingInfo.contentClasses().size()]);
    for (byte b = 0; b < arrayOfClass.length; b++) {
      if (com.sun.xml.internal.ws.spi.db.WrapperComposite.class.equals(arrayOfClass[b]))
        arrayOfClass[b] = com.sun.xml.internal.bind.api.CompositeStructure.class; 
    } 
    Map map1 = typeInfoMappings(paramBindingInfo.typeInfos());
    Map map2 = paramBindingInfo.subclassReplacements();
    String str = paramBindingInfo.getDefaultNamespace();
    Boolean bool = (Boolean)paramBindingInfo.properties().get("c14nSupport");
    RuntimeAnnotationReader runtimeAnnotationReader = (RuntimeAnnotationReader)paramBindingInfo.properties().get("com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader");
    JAXBContextFactory jAXBContextFactory = (JAXBContextFactory)paramBindingInfo.properties().get(JAXBContextFactory.class.getName());
    try {
      JAXBRIContext jAXBRIContext = (jAXBContextFactory != null) ? jAXBContextFactory.createJAXBContext(paramBindingInfo.getSEIModel(), toList(arrayOfClass), toList(map1.values())) : ContextFactory.createContext(arrayOfClass, map1.values(), map2, str, (bool != null) ? bool.booleanValue() : 0, runtimeAnnotationReader, false, false, false);
      return new JAXBRIContextWrapper(jAXBRIContext, map1);
    } catch (Exception exception) {
      throw new DatabindingException(exception);
    } 
  }
  
  private <T> List<T> toList(T[] paramArrayOfT) {
    ArrayList arrayList = new ArrayList();
    arrayList.addAll(Arrays.asList(paramArrayOfT));
    return arrayList;
  }
  
  private <T> List<T> toList(Collection<T> paramCollection) {
    if (paramCollection instanceof List)
      return (List)paramCollection; 
    ArrayList arrayList = new ArrayList();
    arrayList.addAll(paramCollection);
    return arrayList;
  }
  
  private Map<TypeInfo, TypeReference> typeInfoMappings(Collection<TypeInfo> paramCollection) {
    HashMap hashMap = new HashMap();
    for (TypeInfo typeInfo : paramCollection) {
      Class clazz = com.sun.xml.internal.ws.spi.db.WrapperComposite.class.equals(typeInfo.type) ? com.sun.xml.internal.bind.api.CompositeStructure.class : typeInfo.type;
      TypeReference typeReference = new TypeReference(typeInfo.tagName, clazz, typeInfo.annotations);
      hashMap.put(typeInfo, typeReference);
    } 
    return hashMap;
  }
  
  protected BindingContext getContext(Marshaller paramMarshaller) { return newContext(((MarshallerImpl)paramMarshaller).getContext()); }
  
  protected boolean isFor(String paramString) { return (paramString.equals("glassfish.jaxb") || paramString.equals(getClass().getName()) || paramString.equals("com.sun.xml.internal.bind.v2.runtime")); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\db\glassfish\JAXBRIContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */