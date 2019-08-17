package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public interface RuntimeClassInfo extends ClassInfo<Type, Class>, RuntimeNonElement {
  RuntimeClassInfo getBaseClass();
  
  List<? extends RuntimePropertyInfo> getProperties();
  
  RuntimePropertyInfo getProperty(String paramString);
  
  Method getFactoryMethod();
  
  <BeanT> Accessor<BeanT, Map<QName, String>> getAttributeWildcard();
  
  <BeanT> Accessor<BeanT, Locator> getLocatorField();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\runtime\RuntimeClassInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */