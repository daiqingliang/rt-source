package com.sun.jmx.mbeanserver;

import java.security.AccessController;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;

final class PerInterface<M> extends Object {
  private final Class<?> mbeanInterface;
  
  private final MBeanIntrospector<M> introspector;
  
  private final MBeanInfo mbeanInfo;
  
  private final Map<String, M> getters = Util.newMap();
  
  private final Map<String, M> setters = Util.newMap();
  
  private final Map<String, List<MethodAndSig>> ops = Util.newMap();
  
  PerInterface(Class<?> paramClass, MBeanIntrospector<M> paramMBeanIntrospector, MBeanAnalyzer<M> paramMBeanAnalyzer, MBeanInfo paramMBeanInfo) {
    this.mbeanInterface = paramClass;
    this.introspector = paramMBeanIntrospector;
    this.mbeanInfo = paramMBeanInfo;
    paramMBeanAnalyzer.visit(new InitMaps(null));
  }
  
  Class<?> getMBeanInterface() { return this.mbeanInterface; }
  
  MBeanInfo getMBeanInfo() { return this.mbeanInfo; }
  
  boolean isMXBean() { return this.introspector.isMXBean(); }
  
  Object getAttribute(Object paramObject1, String paramString, Object paramObject2) throws AttributeNotFoundException, MBeanException, ReflectionException {
    Object object = this.getters.get(paramString);
    if (object == null) {
      String str;
      if (this.setters.containsKey(paramString)) {
        str = "Write-only attribute: " + paramString;
      } else {
        str = "No such attribute: " + paramString;
      } 
      throw new AttributeNotFoundException(str);
    } 
    return this.introspector.invokeM(object, paramObject1, (Object[])null, paramObject2);
  }
  
  void setAttribute(Object paramObject1, String paramString, Object paramObject2, Object paramObject3) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
    Object object = this.setters.get(paramString);
    if (object == null) {
      String str;
      if (this.getters.containsKey(paramString)) {
        str = "Read-only attribute: " + paramString;
      } else {
        str = "No such attribute: " + paramString;
      } 
      throw new AttributeNotFoundException(str);
    } 
    this.introspector.invokeSetter(paramString, object, paramObject1, paramObject2, paramObject3);
  }
  
  Object invoke(Object paramObject1, String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString, Object paramObject2) throws MBeanException, ReflectionException {
    List list = (List)this.ops.get(paramString);
    if (list == null) {
      String str = "No such operation: " + paramString;
      return noSuchMethod(str, paramObject1, paramString, paramArrayOfObject, paramArrayOfString, paramObject2);
    } 
    if (paramArrayOfString == null)
      paramArrayOfString = new String[0]; 
    MethodAndSig methodAndSig = null;
    for (MethodAndSig methodAndSig1 : list) {
      if (Arrays.equals(methodAndSig1.signature, paramArrayOfString)) {
        methodAndSig = methodAndSig1;
        break;
      } 
    } 
    if (methodAndSig == null) {
      String str2;
      String str1 = sigString(paramArrayOfString);
      if (list.size() == 1) {
        str2 = "Signature mismatch for operation " + paramString + ": " + str1 + " should be " + sigString(((MethodAndSig)list.get(0)).signature);
      } else {
        str2 = "Operation " + paramString + " exists but not with this signature: " + str1;
      } 
      return noSuchMethod(str2, paramObject1, paramString, paramArrayOfObject, paramArrayOfString, paramObject2);
    } 
    return this.introspector.invokeM(methodAndSig.method, paramObject1, paramArrayOfObject, paramObject2);
  }
  
  private Object noSuchMethod(String paramString1, Object paramObject1, String paramString2, Object[] paramArrayOfObject, String[] paramArrayOfString, Object paramObject2) throws MBeanException, ReflectionException {
    Object object;
    NoSuchMethodException noSuchMethodException = new NoSuchMethodException(paramString2 + sigString(paramArrayOfString));
    ReflectionException reflectionException = new ReflectionException(noSuchMethodException, paramString1);
    if (this.introspector.isMXBean())
      throw reflectionException; 
    GetPropertyAction getPropertyAction = new GetPropertyAction("jmx.invoke.getters");
    try {
      object = (String)AccessController.doPrivileged(getPropertyAction);
    } catch (Exception exception) {
      object = null;
    } 
    if (object == null)
      throw reflectionException; 
    byte b = 0;
    Map map = null;
    if (paramArrayOfString == null || paramArrayOfString.length == 0) {
      if (paramString2.startsWith("get")) {
        b = 3;
      } else if (paramString2.startsWith("is")) {
        b = 2;
      } 
      if (b != 0)
        map = this.getters; 
    } else if (paramArrayOfString.length == 1 && paramString2.startsWith("set")) {
      b = 3;
      map = this.setters;
    } 
    if (b != 0) {
      String str = paramString2.substring(b);
      Object object1 = map.get(str);
      if (object1 != null && this.introspector.getName(object1).equals(paramString2)) {
        String[] arrayOfString = this.introspector.getSignature(object1);
        if ((paramArrayOfString == null && arrayOfString.length == 0) || Arrays.equals(paramArrayOfString, arrayOfString))
          return this.introspector.invokeM(object1, paramObject1, paramArrayOfObject, paramObject2); 
      } 
    } 
    throw reflectionException;
  }
  
  private String sigString(String[] paramArrayOfString) {
    StringBuilder stringBuilder = new StringBuilder("(");
    if (paramArrayOfString != null)
      for (String str : paramArrayOfString) {
        if (stringBuilder.length() > 1)
          stringBuilder.append(", "); 
        stringBuilder.append(str);
      }  
    return stringBuilder.append(")").toString();
  }
  
  private class InitMaps extends Object implements MBeanAnalyzer.MBeanVisitor<M> {
    private InitMaps() {}
    
    public void visitAttribute(String param1String, M param1M1, M param1M2) {
      if (param1M1 != null) {
        PerInterface.this.introspector.checkMethod(param1M1);
        Object object = PerInterface.this.getters.put(param1String, param1M1);
        assert object == null;
      } 
      if (param1M2 != null) {
        PerInterface.this.introspector.checkMethod(param1M2);
        Object object = PerInterface.this.setters.put(param1String, param1M2);
        assert object == null;
      } 
    }
    
    public void visitOperation(String param1String, M param1M) {
      PerInterface.this.introspector.checkMethod(param1M);
      String[] arrayOfString = PerInterface.this.introspector.getSignature(param1M);
      PerInterface.MethodAndSig methodAndSig = new PerInterface.MethodAndSig(PerInterface.this, null);
      methodAndSig.method = param1M;
      methodAndSig.signature = arrayOfString;
      List list = (List)PerInterface.this.ops.get(param1String);
      if (list == null) {
        list = Collections.singletonList(methodAndSig);
      } else {
        if (list.size() == 1)
          list = Util.newList(list); 
        list.add(methodAndSig);
      } 
      PerInterface.this.ops.put(param1String, list);
    }
  }
  
  private class MethodAndSig {
    M method;
    
    String[] signature;
    
    private MethodAndSig() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\PerInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */