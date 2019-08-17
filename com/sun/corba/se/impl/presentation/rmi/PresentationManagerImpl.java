package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import com.sun.corba.se.impl.orbutil.graph.GraphImpl;
import com.sun.corba.se.impl.orbutil.graph.Node;
import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.rmi.CORBA.Tie;

public final class PresentationManagerImpl implements PresentationManager {
  private Map classToClassData;
  
  private Map methodToDMM;
  
  private PresentationManager.StubFactoryFactory staticStubFactoryFactory;
  
  private PresentationManager.StubFactoryFactory dynamicStubFactoryFactory;
  
  private ORBUtilSystemException wrapper = null;
  
  private boolean useDynamicStubs;
  
  public PresentationManagerImpl(boolean paramBoolean) {
    this.useDynamicStubs = paramBoolean;
    this.wrapper = ORBUtilSystemException.get("rpc.presentation");
    this.classToClassData = new HashMap();
    this.methodToDMM = new HashMap();
  }
  
  public DynamicMethodMarshaller getDynamicMethodMarshaller(Method paramMethod) {
    if (paramMethod == null)
      return null; 
    DynamicMethodMarshaller dynamicMethodMarshaller = (DynamicMethodMarshaller)this.methodToDMM.get(paramMethod);
    if (dynamicMethodMarshaller == null) {
      dynamicMethodMarshaller = new DynamicMethodMarshallerImpl(paramMethod);
      this.methodToDMM.put(paramMethod, dynamicMethodMarshaller);
    } 
    return dynamicMethodMarshaller;
  }
  
  public PresentationManager.ClassData getClassData(Class paramClass) {
    PresentationManager.ClassData classData = (PresentationManager.ClassData)this.classToClassData.get(paramClass);
    if (classData == null) {
      classData = new ClassDataImpl(paramClass);
      this.classToClassData.put(paramClass, classData);
    } 
    return classData;
  }
  
  public PresentationManager.StubFactoryFactory getStubFactoryFactory(boolean paramBoolean) { return paramBoolean ? this.dynamicStubFactoryFactory : this.staticStubFactoryFactory; }
  
  public void setStubFactoryFactory(boolean paramBoolean, PresentationManager.StubFactoryFactory paramStubFactoryFactory) {
    if (paramBoolean) {
      this.dynamicStubFactoryFactory = paramStubFactoryFactory;
    } else {
      this.staticStubFactoryFactory = paramStubFactoryFactory;
    } 
  }
  
  public Tie getTie() { return this.dynamicStubFactoryFactory.getTie(null); }
  
  public boolean useDynamicStubs() { return this.useDynamicStubs; }
  
  private Set getRootSet(Class paramClass, NodeImpl paramNodeImpl, Graph paramGraph) {
    Set set = null;
    if (paramClass.isInterface()) {
      paramGraph.add(paramNodeImpl);
      set = paramGraph.getRoots();
    } else {
      Class clazz = paramClass;
      HashSet hashSet = new HashSet();
      while (clazz != null && !clazz.equals(Object.class)) {
        NodeImpl nodeImpl = new NodeImpl(clazz);
        paramGraph.add(nodeImpl);
        hashSet.add(nodeImpl);
        clazz = clazz.getSuperclass();
      } 
      paramGraph.getRoots();
      paramGraph.removeAll(hashSet);
      set = paramGraph.getRoots();
    } 
    return set;
  }
  
  private Class[] getInterfaces(Set paramSet) {
    Class[] arrayOfClass = new Class[paramSet.size()];
    Iterator iterator = paramSet.iterator();
    byte b = 0;
    while (iterator.hasNext()) {
      NodeImpl nodeImpl = (NodeImpl)iterator.next();
      arrayOfClass[b++] = nodeImpl.getInterface();
    } 
    return arrayOfClass;
  }
  
  private String[] makeTypeIds(NodeImpl paramNodeImpl, Graph paramGraph, Set paramSet) {
    HashSet hashSet = new HashSet(paramGraph);
    hashSet.removeAll(paramSet);
    ArrayList arrayList = new ArrayList();
    if (paramSet.size() > 1)
      arrayList.add(paramNodeImpl.getTypeId()); 
    addNodes(arrayList, paramSet);
    addNodes(arrayList, hashSet);
    return (String[])arrayList.toArray(new String[arrayList.size()]);
  }
  
  private void addNodes(List paramList, Set paramSet) {
    for (NodeImpl nodeImpl : paramSet) {
      String str = nodeImpl.getTypeId();
      paramList.add(str);
    } 
  }
  
  private class ClassDataImpl implements PresentationManager.ClassData {
    private Class cls;
    
    private IDLNameTranslator nameTranslator;
    
    private String[] typeIds;
    
    private PresentationManager.StubFactory sfactory;
    
    private InvocationHandlerFactory ihfactory;
    
    private Map dictionary;
    
    public ClassDataImpl(Class param1Class) {
      this.cls = param1Class;
      GraphImpl graphImpl = new GraphImpl();
      PresentationManagerImpl.NodeImpl nodeImpl = new PresentationManagerImpl.NodeImpl(param1Class);
      Set set = this$0.getRootSet(param1Class, nodeImpl, graphImpl);
      Class[] arrayOfClass = this$0.getInterfaces(set);
      this.nameTranslator = IDLNameTranslatorImpl.get(arrayOfClass);
      this.typeIds = this$0.makeTypeIds(nodeImpl, graphImpl, set);
      this.ihfactory = new InvocationHandlerFactoryImpl(this$0, this);
      this.dictionary = new HashMap();
    }
    
    public Class getMyClass() { return this.cls; }
    
    public IDLNameTranslator getIDLNameTranslator() { return this.nameTranslator; }
    
    public String[] getTypeIds() { return this.typeIds; }
    
    public InvocationHandlerFactory getInvocationHandlerFactory() { return this.ihfactory; }
    
    public Map getDictionary() { return this.dictionary; }
  }
  
  private static class NodeImpl implements Node {
    private Class interf;
    
    public Class getInterface() { return this.interf; }
    
    public NodeImpl(Class param1Class) { this.interf = param1Class; }
    
    public String getTypeId() { return "RMI:" + this.interf.getName() + ":0000000000000000"; }
    
    public Set getChildren() {
      HashSet hashSet = new HashSet();
      Class[] arrayOfClass = this.interf.getInterfaces();
      for (byte b = 0; b < arrayOfClass.length; b++) {
        Class clazz = arrayOfClass[b];
        if (java.rmi.Remote.class.isAssignableFrom(clazz) && !java.rmi.Remote.class.equals(clazz))
          hashSet.add(new NodeImpl(clazz)); 
      } 
      return hashSet;
    }
    
    public String toString() { return "NodeImpl[" + this.interf + "]"; }
    
    public int hashCode() { return this.interf.hashCode(); }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (!(param1Object instanceof NodeImpl))
        return false; 
      NodeImpl nodeImpl = (NodeImpl)param1Object;
      return nodeImpl.interf.equals(this.interf);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\PresentationManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */