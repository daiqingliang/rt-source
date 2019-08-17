package com.sun.jmx.mbeanserver;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.NotCompliantMBeanException;

class MBeanAnalyzer<M> extends Object {
  private Map<String, List<M>> opMap = Util.newInsertionOrderMap();
  
  private Map<String, AttrMethods<M>> attrMap = Util.newInsertionOrderMap();
  
  void visit(MBeanVisitor<M> paramMBeanVisitor) {
    for (Map.Entry entry : this.attrMap.entrySet()) {
      String str = (String)entry.getKey();
      AttrMethods attrMethods = (AttrMethods)entry.getValue();
      paramMBeanVisitor.visitAttribute(str, attrMethods.getter, attrMethods.setter);
    } 
    for (Map.Entry entry : this.opMap.entrySet()) {
      for (Object object : (List)entry.getValue())
        paramMBeanVisitor.visitOperation((String)entry.getKey(), object); 
    } 
  }
  
  static <M> MBeanAnalyzer<M> analyzer(Class<?> paramClass, MBeanIntrospector<M> paramMBeanIntrospector) throws NotCompliantMBeanException { return new MBeanAnalyzer(paramClass, paramMBeanIntrospector); }
  
  private MBeanAnalyzer(Class<?> paramClass, MBeanIntrospector<M> paramMBeanIntrospector) throws NotCompliantMBeanException {
    if (!paramClass.isInterface())
      throw new NotCompliantMBeanException("Not an interface: " + paramClass.getName()); 
    if (!Modifier.isPublic(paramClass.getModifiers()) && !Introspector.ALLOW_NONPUBLIC_MBEAN)
      throw new NotCompliantMBeanException("Interface is not public: " + paramClass.getName()); 
    try {
      initMaps(paramClass, paramMBeanIntrospector);
    } catch (Exception exception) {
      throw Introspector.throwException(paramClass, exception);
    } 
  }
  
  private void initMaps(Class<?> paramClass, MBeanIntrospector<M> paramMBeanIntrospector) throws NotCompliantMBeanException {
    List list1 = paramMBeanIntrospector.getMethods(paramClass);
    List list2 = eliminateCovariantMethods(list1);
    for (Method method : list2) {
      String str1 = method.getName();
      int i = method.getParameterTypes().length;
      Object object = paramMBeanIntrospector.mFrom(method);
      String str2 = "";
      if (str1.startsWith("get")) {
        str2 = str1.substring(3);
      } else if (str1.startsWith("is") && method.getReturnType() == boolean.class) {
        str2 = str1.substring(2);
      } 
      if (str2.length() != 0 && i == 0 && method.getReturnType() != void.class) {
        AttrMethods attrMethods = (AttrMethods)this.attrMap.get(str2);
        if (attrMethods == null) {
          attrMethods = new AttrMethods(null);
        } else if (attrMethods.getter != null) {
          String str = "Attribute " + str2 + " has more than one getter";
          throw new NotCompliantMBeanException(str);
        } 
        attrMethods.getter = object;
        this.attrMap.put(str2, attrMethods);
        continue;
      } 
      if (str1.startsWith("set") && str1.length() > 3 && i == 1 && method.getReturnType() == void.class) {
        str2 = str1.substring(3);
        AttrMethods attrMethods = (AttrMethods)this.attrMap.get(str2);
        if (attrMethods == null) {
          attrMethods = new AttrMethods(null);
        } else if (attrMethods.setter != null) {
          String str = "Attribute " + str2 + " has more than one setter";
          throw new NotCompliantMBeanException(str);
        } 
        attrMethods.setter = object;
        this.attrMap.put(str2, attrMethods);
        continue;
      } 
      List list = (List)this.opMap.get(str1);
      if (list == null)
        list = Util.newList(); 
      list.add(object);
      this.opMap.put(str1, list);
    } 
    for (Map.Entry entry : this.attrMap.entrySet()) {
      AttrMethods attrMethods = (AttrMethods)entry.getValue();
      if (!paramMBeanIntrospector.consistent(attrMethods.getter, attrMethods.setter)) {
        String str = "Getter and setter for " + (String)entry.getKey() + " have inconsistent types";
        throw new NotCompliantMBeanException(str);
      } 
    } 
  }
  
  static List<Method> eliminateCovariantMethods(List<Method> paramList) {
    int i = paramList.size();
    Method[] arrayOfMethod = (Method[])paramList.toArray(new Method[i]);
    Arrays.sort(arrayOfMethod, MethodOrder.instance);
    Set set = Util.newSet();
    for (byte b = 1; b < i; b++) {
      Method method1 = arrayOfMethod[b - true];
      Method method2 = arrayOfMethod[b];
      if (method1.getName().equals(method2.getName()) && Arrays.equals(method1.getParameterTypes(), method2.getParameterTypes()) && !set.add(method1))
        throw new RuntimeException("Internal error: duplicate Method"); 
    } 
    List list = Util.newList(paramList);
    list.removeAll(set);
    return list;
  }
  
  private static class AttrMethods<M> extends Object {
    M getter;
    
    M setter;
    
    private AttrMethods() {}
  }
  
  static interface MBeanVisitor<M> {
    void visitAttribute(String param1String, M param1M1, M param1M2);
    
    void visitOperation(String param1String, M param1M);
  }
  
  private static class MethodOrder extends Object implements Comparator<Method> {
    public static final MethodOrder instance = new MethodOrder();
    
    public int compare(Method param1Method1, Method param1Method2) {
      int i = param1Method1.getName().compareTo(param1Method2.getName());
      if (i != 0)
        return i; 
      Class[] arrayOfClass1 = param1Method1.getParameterTypes();
      Class[] arrayOfClass2 = param1Method2.getParameterTypes();
      if (arrayOfClass1.length != arrayOfClass2.length)
        return arrayOfClass1.length - arrayOfClass2.length; 
      if (!Arrays.equals(arrayOfClass1, arrayOfClass2))
        return Arrays.toString(arrayOfClass1).compareTo(Arrays.toString(arrayOfClass2)); 
      Class clazz1 = param1Method1.getReturnType();
      Class clazz2 = param1Method2.getReturnType();
      return (clazz1 == clazz2) ? 0 : (clazz1.isAssignableFrom(clazz2) ? -1 : 1);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\MBeanAnalyzer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */