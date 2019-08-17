package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeAttributePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeValuePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public abstract class PropertyFactory {
  private static final Constructor<? extends Property>[] propImpls;
  
  public static Property create(JAXBContextImpl paramJAXBContextImpl, RuntimePropertyInfo paramRuntimePropertyInfo) {
    PropertyKind propertyKind = paramRuntimePropertyInfo.kind();
    switch (propertyKind) {
      case ATTRIBUTE:
        return new AttributeProperty(paramJAXBContextImpl, (RuntimeAttributePropertyInfo)paramRuntimePropertyInfo);
      case VALUE:
        return new ValueProperty(paramJAXBContextImpl, (RuntimeValuePropertyInfo)paramRuntimePropertyInfo);
      case ELEMENT:
        if (((RuntimeElementPropertyInfo)paramRuntimePropertyInfo).isValueList())
          return new ListElementProperty(paramJAXBContextImpl, (RuntimeElementPropertyInfo)paramRuntimePropertyInfo); 
        break;
      case REFERENCE:
      case MAP:
        break;
      default:
        assert false;
        break;
    } 
    boolean bool1 = paramRuntimePropertyInfo.isCollection();
    boolean bool2 = isLeaf(paramRuntimePropertyInfo);
    Constructor constructor = propImpls[(bool2 ? 0 : 6) + (bool1 ? 3 : 0) + propertyKind.propertyIndex];
    try {
      return (Property)constructor.newInstance(new Object[] { paramJAXBContextImpl, paramRuntimePropertyInfo });
    } catch (InstantiationException instantiationException) {
      throw new InstantiationError(instantiationException.getMessage());
    } catch (IllegalAccessException illegalAccessException) {
      throw new IllegalAccessError(illegalAccessException.getMessage());
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getCause();
      if (throwable instanceof Error)
        throw (Error)throwable; 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      throw new AssertionError(throwable);
    } 
  }
  
  static boolean isLeaf(RuntimePropertyInfo paramRuntimePropertyInfo) {
    Collection collection = paramRuntimePropertyInfo.ref();
    if (collection.size() != 1)
      return false; 
    RuntimeTypeInfo runtimeTypeInfo = (RuntimeTypeInfo)collection.iterator().next();
    return !(runtimeTypeInfo instanceof RuntimeNonElement) ? false : ((paramRuntimePropertyInfo.id() == ID.IDREF) ? true : ((((RuntimeNonElement)runtimeTypeInfo).getTransducer() == null) ? false : (!!paramRuntimePropertyInfo.getIndividualType().equals(runtimeTypeInfo.getType()))));
  }
  
  static  {
    Class[] arrayOfClass = { 
        SingleElementLeafProperty.class, null, null, ArrayElementLeafProperty.class, null, null, SingleElementNodeProperty.class, SingleReferenceNodeProperty.class, SingleMapNodeProperty.class, ArrayElementNodeProperty.class, 
        ArrayReferenceNodeProperty.class, null };
    propImpls = new Constructor[arrayOfClass.length];
    for (byte b = 0; b < propImpls.length; b++) {
      if (arrayOfClass[b] != null)
        propImpls[b] = arrayOfClass[b].getConstructors()[0]; 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\PropertyFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */