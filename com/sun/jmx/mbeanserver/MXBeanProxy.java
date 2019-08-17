package com.sun.jmx.mbeanserver;

import java.lang.reflect.Method;
import java.util.Map;
import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class MXBeanProxy {
  private final Map<Method, Handler> handlerMap = Util.newMap();
  
  public MXBeanProxy(Class<?> paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException("Null parameter"); 
    try {
      mBeanAnalyzer = MXBeanIntrospector.getInstance().getAnalyzer(paramClass);
    } catch (NotCompliantMBeanException notCompliantMBeanException) {
      throw new IllegalArgumentException(notCompliantMBeanException);
    } 
    mBeanAnalyzer.visit(new Visitor(null));
  }
  
  public Object invoke(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    Handler handler = (Handler)this.handlerMap.get(paramMethod);
    ConvertingMethod convertingMethod = handler.getConvertingMethod();
    MXBeanLookup mXBeanLookup1;
    mXBeanLookup2 = (mXBeanLookup1 = MXBeanLookup.lookupFor(paramMBeanServerConnection)).getLookup();
    try {
      MXBeanLookup.setLookup(mXBeanLookup1);
      Object[] arrayOfObject = convertingMethod.toOpenParameters(mXBeanLookup1, paramArrayOfObject);
      Object object = handler.invoke(paramMBeanServerConnection, paramObjectName, arrayOfObject);
      return convertingMethod.fromOpenReturnValue(mXBeanLookup1, object);
    } finally {
      MXBeanLookup.setLookup(mXBeanLookup2);
    } 
  }
  
  private static class GetHandler extends Handler {
    GetHandler(String param1String, ConvertingMethod param1ConvertingMethod) { super(param1String, param1ConvertingMethod); }
    
    Object invoke(MBeanServerConnection param1MBeanServerConnection, ObjectName param1ObjectName, Object[] param1ArrayOfObject) throws Exception {
      assert param1ArrayOfObject == null || param1ArrayOfObject.length == 0;
      return param1MBeanServerConnection.getAttribute(param1ObjectName, getName());
    }
  }
  
  private static abstract class Handler {
    private final String name;
    
    private final ConvertingMethod convertingMethod;
    
    Handler(String param1String, ConvertingMethod param1ConvertingMethod) {
      this.name = param1String;
      this.convertingMethod = param1ConvertingMethod;
    }
    
    String getName() { return this.name; }
    
    ConvertingMethod getConvertingMethod() { return this.convertingMethod; }
    
    abstract Object invoke(MBeanServerConnection param1MBeanServerConnection, ObjectName param1ObjectName, Object[] param1ArrayOfObject) throws Exception;
  }
  
  private static class InvokeHandler extends Handler {
    private final String[] signature;
    
    InvokeHandler(String param1String, String[] param1ArrayOfString, ConvertingMethod param1ConvertingMethod) {
      super(param1String, param1ConvertingMethod);
      this.signature = param1ArrayOfString;
    }
    
    Object invoke(MBeanServerConnection param1MBeanServerConnection, ObjectName param1ObjectName, Object[] param1ArrayOfObject) throws Exception { return param1MBeanServerConnection.invoke(param1ObjectName, getName(), param1ArrayOfObject, this.signature); }
  }
  
  private static class SetHandler extends Handler {
    SetHandler(String param1String, ConvertingMethod param1ConvertingMethod) { super(param1String, param1ConvertingMethod); }
    
    Object invoke(MBeanServerConnection param1MBeanServerConnection, ObjectName param1ObjectName, Object[] param1ArrayOfObject) throws Exception {
      assert param1ArrayOfObject.length == 1;
      Attribute attribute = new Attribute(getName(), param1ArrayOfObject[0]);
      param1MBeanServerConnection.setAttribute(param1ObjectName, attribute);
      return null;
    }
  }
  
  private class Visitor extends Object implements MBeanAnalyzer.MBeanVisitor<ConvertingMethod> {
    private Visitor() {}
    
    public void visitAttribute(String param1String, ConvertingMethod param1ConvertingMethod1, ConvertingMethod param1ConvertingMethod2) {
      if (param1ConvertingMethod1 != null) {
        param1ConvertingMethod1.checkCallToOpen();
        Method method = param1ConvertingMethod1.getMethod();
        MXBeanProxy.this.handlerMap.put(method, new MXBeanProxy.GetHandler(param1String, param1ConvertingMethod1));
      } 
      if (param1ConvertingMethod2 != null) {
        Method method = param1ConvertingMethod2.getMethod();
        MXBeanProxy.this.handlerMap.put(method, new MXBeanProxy.SetHandler(param1String, param1ConvertingMethod2));
      } 
    }
    
    public void visitOperation(String param1String, ConvertingMethod param1ConvertingMethod) {
      param1ConvertingMethod.checkCallToOpen();
      Method method = param1ConvertingMethod.getMethod();
      String[] arrayOfString = param1ConvertingMethod.getOpenSignature();
      MXBeanProxy.this.handlerMap.put(method, new MXBeanProxy.InvokeHandler(param1String, arrayOfString, param1ConvertingMethod));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\MXBeanProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */