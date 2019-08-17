package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.istack.internal.SAXException2;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Patcher;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

public abstract class Lister<BeanT, PropT, ItemT, PackT> extends Object {
  private static final Map<Class, WeakReference<Lister>> arrayListerCache = Collections.synchronizedMap(new WeakHashMap());
  
  static final Map<Class, Lister> primitiveArrayListers = new HashMap();
  
  public static final Lister ERROR;
  
  private static final ListIterator EMPTY_ITERATOR;
  
  private static final Class[] COLLECTION_IMPL_CLASSES;
  
  public abstract ListIterator<ItemT> iterator(PropT paramPropT, XMLSerializer paramXMLSerializer);
  
  public abstract PackT startPacking(BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor) throws AccessorException;
  
  public abstract void addToPack(PackT paramPackT, ItemT paramItemT) throws AccessorException;
  
  public abstract void endPacking(PackT paramPackT, BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor) throws AccessorException;
  
  public abstract void reset(BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor) throws AccessorException;
  
  public static <BeanT, PropT, ItemT, PackT> Lister<BeanT, PropT, ItemT, PackT> create(Type paramType, ID paramID, Adapter<Type, Class> paramAdapter) {
    AdaptedLister adaptedLister;
    Class clazz2;
    Class clazz1 = (Class)Utils.REFLECTION_NAVIGATOR.erasure(paramType);
    if (clazz1.isArray()) {
      clazz2 = clazz1.getComponentType();
      adaptedLister = getArrayLister(clazz2);
    } else if (Collection.class.isAssignableFrom(clazz1)) {
      Type type = (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass(paramType, Collection.class);
      if (type instanceof ParameterizedType) {
        clazz2 = (Class)Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)type).getActualTypeArguments()[0]);
      } else {
        clazz2 = Object.class;
      } 
      adaptedLister = new CollectionLister(getImplClass(clazz1));
    } else {
      return null;
    } 
    if (paramID == ID.IDREF)
      adaptedLister = new IDREFS(adaptedLister, clazz2); 
    if (paramAdapter != null)
      adaptedLister = new AdaptedLister(adaptedLister, (Class)paramAdapter.adapterType); 
    return adaptedLister;
  }
  
  private static Class getImplClass(Class<?> paramClass) { return ClassFactory.inferImplClass(paramClass, COLLECTION_IMPL_CLASSES); }
  
  private static Lister getArrayLister(Class paramClass) {
    Lister lister = null;
    if (paramClass.isPrimitive()) {
      lister = (Lister)primitiveArrayListers.get(paramClass);
    } else {
      WeakReference weakReference = (WeakReference)arrayListerCache.get(paramClass);
      if (weakReference != null)
        lister = (Lister)weakReference.get(); 
      if (lister == null) {
        lister = new ArrayLister(paramClass);
        arrayListerCache.put(paramClass, new WeakReference(lister));
      } 
    } 
    assert lister != null;
    return lister;
  }
  
  public static <A, B, C, D> Lister<A, B, C, D> getErrorInstance() { return ERROR; }
  
  static  {
    PrimitiveArrayListerBoolean.register();
    PrimitiveArrayListerByte.register();
    PrimitiveArrayListerCharacter.register();
    PrimitiveArrayListerDouble.register();
    PrimitiveArrayListerFloat.register();
    PrimitiveArrayListerInteger.register();
    PrimitiveArrayListerLong.register();
    PrimitiveArrayListerShort.register();
    ERROR = new Lister() {
        public ListIterator iterator(Object param1Object, XMLSerializer param1XMLSerializer) { return EMPTY_ITERATOR; }
        
        public Object startPacking(Object param1Object, Accessor param1Accessor) { return null; }
        
        public void addToPack(Object param1Object1, Object param1Object2) {}
        
        public void endPacking(Object param1Object1, Object param1Object2, Accessor param1Accessor) {}
        
        public void reset(Object param1Object, Accessor param1Accessor) {}
      };
    EMPTY_ITERATOR = new ListIterator() {
        public boolean hasNext() { return false; }
        
        public Object next() { throw new IllegalStateException(); }
      };
    COLLECTION_IMPL_CLASSES = new Class[] { ArrayList.class, java.util.LinkedList.class, java.util.HashSet.class, java.util.TreeSet.class, java.util.Stack.class };
  }
  
  private static final class ArrayLister<BeanT, ItemT> extends Lister<BeanT, ItemT[], ItemT, Pack<ItemT>> {
    private final Class<ItemT> itemType;
    
    public ArrayLister(Class<ItemT> param1Class) { this.itemType = param1Class; }
    
    public ListIterator<ItemT> iterator(final ItemT[] objects, XMLSerializer param1XMLSerializer) { return new ListIterator<ItemT>() {
          int idx = 0;
          
          public boolean hasNext() { return (this.idx < objects.length); }
          
          public ItemT next() { return (ItemT)objects[this.idx++]; }
        }; }
    
    public Lister.Pack startPacking(BeanT param1BeanT, Accessor<BeanT, ItemT[]> param1Accessor) { return new Lister.Pack(this.itemType); }
    
    public void addToPack(Lister.Pack<ItemT> param1Pack, ItemT param1ItemT) { param1Pack.add(param1ItemT); }
    
    public void endPacking(Lister.Pack<ItemT> param1Pack, BeanT param1BeanT, Accessor<BeanT, ItemT[]> param1Accessor) throws AccessorException { param1Accessor.set(param1BeanT, param1Pack.build()); }
    
    public void reset(BeanT param1BeanT, Accessor<BeanT, ItemT[]> param1Accessor) throws AccessorException { param1Accessor.set(param1BeanT, (Object[])Array.newInstance(this.itemType, 0)); }
  }
  
  public static final class CollectionLister<BeanT, T extends Collection> extends Lister<BeanT, T, Object, T> {
    private final Class<? extends T> implClass;
    
    public CollectionLister(Class<? extends T> param1Class) { this.implClass = param1Class; }
    
    public ListIterator iterator(T param1T, XMLSerializer param1XMLSerializer) {
      final Iterator itr = param1T.iterator();
      return new ListIterator() {
          public boolean hasNext() { return itr.hasNext(); }
          
          public Object next() { return itr.next(); }
        };
    }
    
    public T startPacking(BeanT param1BeanT, Accessor<BeanT, T> param1Accessor) throws AccessorException {
      Collection collection = (Collection)param1Accessor.get(param1BeanT);
      if (collection == null) {
        collection = (Collection)ClassFactory.create(this.implClass);
        if (!param1Accessor.isAdapted())
          param1Accessor.set(param1BeanT, collection); 
      } 
      collection.clear();
      return (T)collection;
    }
    
    public void addToPack(T param1T, Object param1Object) { param1T.add(param1Object); }
    
    public void endPacking(T param1T, BeanT param1BeanT, Accessor<BeanT, T> param1Accessor) throws AccessorException {
      try {
        if (param1Accessor.isAdapted())
          param1Accessor.set(param1BeanT, param1T); 
      } catch (AccessorException accessorException) {
        if (param1Accessor.isAdapted())
          throw accessorException; 
      } 
    }
    
    public void reset(BeanT param1BeanT, Accessor<BeanT, T> param1Accessor) throws AccessorException {
      Collection collection = (Collection)param1Accessor.get(param1BeanT);
      if (collection == null)
        return; 
      collection.clear();
    }
  }
  
  private static final class IDREFS<BeanT, PropT> extends Lister<BeanT, PropT, String, IDREFS<BeanT, PropT>.Pack> {
    private final Lister<BeanT, PropT, Object, Object> core;
    
    private final Class itemType;
    
    public IDREFS(Lister param1Lister, Class param1Class) {
      this.core = param1Lister;
      this.itemType = param1Class;
    }
    
    public ListIterator<String> iterator(PropT param1PropT, XMLSerializer param1XMLSerializer) {
      ListIterator listIterator = this.core.iterator(param1PropT, param1XMLSerializer);
      return new Lister.IDREFSIterator(listIterator, param1XMLSerializer, null);
    }
    
    public Pack startPacking(BeanT param1BeanT, Accessor<BeanT, PropT> param1Accessor) { return new Pack(param1BeanT, param1Accessor); }
    
    public void addToPack(Pack param1Pack, String param1String) { param1Pack.add(param1String); }
    
    public void endPacking(Pack param1Pack, BeanT param1BeanT, Accessor<BeanT, PropT> param1Accessor) {}
    
    public void reset(BeanT param1BeanT, Accessor<BeanT, PropT> param1Accessor) throws AccessorException { this.core.reset(param1BeanT, param1Accessor); }
    
    private class Pack implements Patcher {
      private final BeanT bean;
      
      private final List<String> idrefs = new ArrayList();
      
      private final UnmarshallingContext context;
      
      private final Accessor<BeanT, PropT> acc;
      
      private final LocatorEx location;
      
      public Pack(BeanT param2BeanT, Accessor<BeanT, PropT> param2Accessor) throws AccessorException {
        this.bean = param2BeanT;
        this.acc = param2Accessor;
        this.context = UnmarshallingContext.getInstance();
        this.location = new LocatorEx.Snapshot(this.context.getLocator());
        this.context.addPatcher(this);
      }
      
      public void add(String param2String) { this.idrefs.add(param2String); }
      
      public void run() {
        try {
          Object object = Lister.IDREFS.this.core.startPacking(this.bean, this.acc);
          for (String str : this.idrefs) {
            Object object1;
            Callable callable = this.context.getObjectFromId(str, Lister.IDREFS.this.itemType);
            try {
              object1 = (callable != null) ? callable.call() : null;
            } catch (SAXException sAXException) {
              throw sAXException;
            } catch (Exception exception) {
              throw new SAXException2(exception);
            } 
            if (object1 == null) {
              this.context.errorUnresolvedIDREF(this.bean, str, this.location);
              continue;
            } 
            TODO.prototype();
            Lister.IDREFS.this.core.addToPack(object, object1);
          } 
          Lister.IDREFS.this.core.endPacking(object, this.bean, this.acc);
        } catch (AccessorException accessorException) {
          this.context.handleError(accessorException);
        } 
      }
    }
  }
  
  public static final class IDREFSIterator extends Object implements ListIterator<String> {
    private final ListIterator i;
    
    private final XMLSerializer context;
    
    private Object last;
    
    private IDREFSIterator(ListIterator param1ListIterator, XMLSerializer param1XMLSerializer) {
      this.i = param1ListIterator;
      this.context = param1XMLSerializer;
    }
    
    public boolean hasNext() { return this.i.hasNext(); }
    
    public Object last() { return this.last; }
    
    public String next() throws SAXException, JAXBException {
      this.last = this.i.next();
      String str = this.context.grammar.getBeanInfo(this.last, true).getId(this.last, this.context);
      if (str == null)
        this.context.errorMissingId(this.last); 
      return str;
    }
  }
  
  public static final class Pack<ItemT> extends ArrayList<ItemT> {
    private final Class<ItemT> itemType;
    
    public Pack(Class<ItemT> param1Class) { this.itemType = param1Class; }
    
    public ItemT[] build() { return (ItemT[])toArray((Object[])Array.newInstance(this.itemType, size())); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\Lister.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */