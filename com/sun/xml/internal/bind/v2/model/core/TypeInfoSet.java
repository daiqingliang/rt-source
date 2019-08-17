package com.sun.xml.internal.bind.v2.model.core;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;

public interface TypeInfoSet<T, C, F, M> {
  Navigator<T, C, F, M> getNavigator();
  
  NonElement<T, C> getTypeInfo(T paramT);
  
  NonElement<T, C> getAnyTypeInfo();
  
  NonElement<T, C> getClassInfo(C paramC);
  
  Map<? extends T, ? extends ArrayInfo<T, C>> arrays();
  
  Map<C, ? extends ClassInfo<T, C>> beans();
  
  Map<T, ? extends BuiltinLeafInfo<T, C>> builtins();
  
  Map<C, ? extends EnumLeafInfo<T, C>> enums();
  
  ElementInfo<T, C> getElementInfo(C paramC, QName paramQName);
  
  NonElement<T, C> getTypeInfo(Ref<T, C> paramRef);
  
  Map<QName, ? extends ElementInfo<T, C>> getElementMappings(C paramC);
  
  Iterable<? extends ElementInfo<T, C>> getAllElements();
  
  Map<String, String> getXmlNs(String paramString);
  
  Map<String, String> getSchemaLocations();
  
  XmlNsForm getElementFormDefault(String paramString);
  
  XmlNsForm getAttributeFormDefault(String paramString);
  
  void dump(Result paramResult) throws JAXBException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\core\TypeInfoSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */