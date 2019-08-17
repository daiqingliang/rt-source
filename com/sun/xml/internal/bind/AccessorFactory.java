package com.sun.xml.internal.bind;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.xml.bind.JAXBException;

public interface AccessorFactory {
  Accessor createFieldAccessor(Class paramClass, Field paramField, boolean paramBoolean) throws JAXBException;
  
  Accessor createPropertyAccessor(Class paramClass, Method paramMethod1, Method paramMethod2) throws JAXBException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\AccessorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */