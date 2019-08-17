package javax.management.openmbean;

import com.sun.jmx.mbeanserver.DefaultMXBeanMappingFactory;
import com.sun.jmx.mbeanserver.MXBeanLookup;
import com.sun.jmx.mbeanserver.MXBeanMapping;
import com.sun.jmx.mbeanserver.MXBeanMappingFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class CompositeDataInvocationHandler implements InvocationHandler {
  private final CompositeData compositeData;
  
  private final MXBeanLookup lookup;
  
  public CompositeDataInvocationHandler(CompositeData paramCompositeData) { this(paramCompositeData, null); }
  
  CompositeDataInvocationHandler(CompositeData paramCompositeData, MXBeanLookup paramMXBeanLookup) {
    if (paramCompositeData == null)
      throw new IllegalArgumentException("compositeData"); 
    this.compositeData = paramCompositeData;
    this.lookup = paramMXBeanLookup;
  }
  
  public CompositeData getCompositeData() {
    assert this.compositeData != null;
    return this.compositeData;
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    Object object;
    String str1 = paramMethod.getName();
    if (paramMethod.getDeclaringClass() == Object.class)
      return (str1.equals("toString") && paramArrayOfObject == null) ? ("Proxy[" + this.compositeData + "]") : ((str1.equals("hashCode") && paramArrayOfObject == null) ? Integer.valueOf(this.compositeData.hashCode() + 1128548680) : ((str1.equals("equals") && paramArrayOfObject.length == 1 && paramMethod.getParameterTypes()[false] == Object.class) ? Boolean.valueOf(equals(paramObject, paramArrayOfObject[0])) : paramMethod.invoke(this, paramArrayOfObject))); 
    String str2 = DefaultMXBeanMappingFactory.propertyName(paramMethod);
    if (str2 == null)
      throw new IllegalArgumentException("Method is not getter: " + paramMethod.getName()); 
    if (this.compositeData.containsKey(str2)) {
      object = this.compositeData.get(str2);
    } else {
      String str = DefaultMXBeanMappingFactory.decapitalize(str2);
      if (this.compositeData.containsKey(str)) {
        object = this.compositeData.get(str);
      } else {
        String str3 = "No CompositeData item " + str2 + (str.equals(str2) ? "" : (" or " + str)) + " to match " + str1;
        throw new IllegalArgumentException(str3);
      } 
    } 
    MXBeanMapping mXBeanMapping = MXBeanMappingFactory.DEFAULT.mappingForType(paramMethod.getGenericReturnType(), MXBeanMappingFactory.DEFAULT);
    return mXBeanMapping.fromOpenValue(object);
  }
  
  private boolean equals(Object paramObject1, Object paramObject2) {
    if (paramObject2 == null)
      return false; 
    Class clazz1 = paramObject1.getClass();
    Class clazz2 = paramObject2.getClass();
    if (clazz1 != clazz2)
      return false; 
    InvocationHandler invocationHandler = Proxy.getInvocationHandler(paramObject2);
    if (!(invocationHandler instanceof CompositeDataInvocationHandler))
      return false; 
    CompositeDataInvocationHandler compositeDataInvocationHandler = (CompositeDataInvocationHandler)invocationHandler;
    return this.compositeData.equals(compositeDataInvocationHandler.compositeData);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\openmbean\CompositeDataInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */