package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.OptimizedAccessorFactory;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.xml.sax.SAXException;

public abstract class Accessor<BeanT, ValueT> extends Object implements Receiver {
  public final Class<ValueT> valueType;
  
  private static List<Class> nonAbstractableClasses = Arrays.asList(new Class[] { 
        Object.class, java.util.Calendar.class, javax.xml.datatype.Duration.class, javax.xml.datatype.XMLGregorianCalendar.class, java.awt.Image.class, javax.activation.DataHandler.class, javax.xml.transform.Source.class, java.util.Date.class, java.io.File.class, java.net.URI.class, 
        java.net.URL.class, Class.class, String.class, javax.xml.transform.Source.class });
  
  private static boolean accessWarned = false;
  
  private static final Accessor ERROR = new Accessor<Object, Object>(Object.class) {
      public Object get(Object param1Object) { return null; }
      
      public void set(Object param1Object1, Object param1Object2) {}
    };
  
  public static final Accessor<JAXBElement, Object> JAXB_ELEMENT_VALUE = new Accessor<JAXBElement, Object>(Object.class) {
      public Object get(JAXBElement param1JAXBElement) { return param1JAXBElement.getValue(); }
      
      public void set(JAXBElement param1JAXBElement, Object param1Object) { param1JAXBElement.setValue(param1Object); }
    };
  
  private static final Map<Class, Object> uninitializedValues = new HashMap();
  
  public Class<ValueT> getValueType() { return this.valueType; }
  
  protected Accessor(Class<ValueT> paramClass) { this.valueType = paramClass; }
  
  public Accessor<BeanT, ValueT> optimize(@Nullable JAXBContextImpl paramJAXBContextImpl) { return this; }
  
  public abstract ValueT get(BeanT paramBeanT) throws AccessorException;
  
  public abstract void set(BeanT paramBeanT, ValueT paramValueT) throws AccessorException;
  
  public Object getUnadapted(BeanT paramBeanT) throws AccessorException { return get(paramBeanT); }
  
  public boolean isAdapted() { return false; }
  
  public void setUnadapted(BeanT paramBeanT, Object paramObject) throws AccessorException { set(paramBeanT, paramObject); }
  
  public void receive(UnmarshallingContext.State paramState, Object paramObject) throws SAXException {
    try {
      set(paramState.getTarget(), paramObject);
    } catch (AccessorException accessorException) {
      Loader.handleGenericException(accessorException, true);
    } catch (IllegalAccessError illegalAccessError) {
      Loader.handleGenericError(illegalAccessError);
    } 
  }
  
  public boolean isValueTypeAbstractable() { return !nonAbstractableClasses.contains(getValueType()); }
  
  public boolean isAbstractable(Class paramClass) { return !nonAbstractableClasses.contains(paramClass); }
  
  public final <T> Accessor<BeanT, T> adapt(Class<T> paramClass1, Class<? extends XmlAdapter<T, ValueT>> paramClass2) { return new AdaptedAccessor(paramClass1, this, paramClass2); }
  
  public final <T> Accessor<BeanT, T> adapt(Adapter<Type, Class> paramAdapter) { return new AdaptedAccessor((Class)Utils.REFLECTION_NAVIGATOR.erasure(paramAdapter.defaultType), this, (Class)paramAdapter.adapterType); }
  
  public static <A, B> Accessor<A, B> getErrorInstance() { return ERROR; }
  
  static  {
    uninitializedValues.put(byte.class, Byte.valueOf((byte)0));
    uninitializedValues.put(boolean.class, Boolean.valueOf(false));
    uninitializedValues.put(char.class, Character.valueOf(false));
    uninitializedValues.put(float.class, Float.valueOf(0.0F));
    uninitializedValues.put(double.class, Double.valueOf(0.0D));
    uninitializedValues.put(int.class, Integer.valueOf(0));
    uninitializedValues.put(long.class, Long.valueOf(0L));
    uninitializedValues.put(short.class, Short.valueOf((short)0));
  }
  
  public static class FieldReflection<BeanT, ValueT> extends Accessor<BeanT, ValueT> {
    public final Field f;
    
    private static final Logger logger = Util.getClassLogger();
    
    public FieldReflection(Field param1Field) { this(param1Field, false); }
    
    public FieldReflection(Field param1Field, boolean param1Boolean) {
      super(param1Field.getType());
      this.f = param1Field;
      int i = param1Field.getModifiers();
      if (!Modifier.isPublic(i) || Modifier.isFinal(i) || !Modifier.isPublic(param1Field.getDeclaringClass().getModifiers()))
        try {
          param1Field.setAccessible(true);
        } catch (SecurityException securityException) {
          if (!accessWarned && !param1Boolean)
            logger.log(Level.WARNING, Messages.UNABLE_TO_ACCESS_NON_PUBLIC_FIELD.format(new Object[] { param1Field.getDeclaringClass().getName(), param1Field.getName() }, ), securityException); 
          accessWarned = true;
        }  
    }
    
    public ValueT get(BeanT param1BeanT) throws AccessorException {
      try {
        return (ValueT)this.f.get(param1BeanT);
      } catch (IllegalAccessException illegalAccessException) {
        throw new IllegalAccessError(illegalAccessException.getMessage());
      } 
    }
    
    public void set(BeanT param1BeanT, ValueT param1ValueT) throws AccessorException {
      try {
        if (param1ValueT == null)
          param1ValueT = (ValueT)uninitializedValues.get(this.valueType); 
        this.f.set(param1BeanT, param1ValueT);
      } catch (IllegalAccessException illegalAccessException) {
        throw new IllegalAccessError(illegalAccessException.getMessage());
      } 
    }
    
    public Accessor<BeanT, ValueT> optimize(JAXBContextImpl param1JAXBContextImpl) {
      if (param1JAXBContextImpl != null && param1JAXBContextImpl.fastBoot)
        return this; 
      Accessor accessor = OptimizedAccessorFactory.get(this.f);
      return (accessor != null) ? accessor : this;
    }
  }
  
  public static class GetterOnlyReflection<BeanT, ValueT> extends GetterSetterReflection<BeanT, ValueT> {
    public GetterOnlyReflection(Method param1Method) { super(param1Method, null); }
    
    public void set(BeanT param1BeanT, ValueT param1ValueT) throws AccessorException { throw new AccessorException(Messages.NO_SETTER.format(new Object[] { this.getter.toString() })); }
  }
  
  public static class GetterSetterReflection<BeanT, ValueT> extends Accessor<BeanT, ValueT> {
    public final Method getter;
    
    public final Method setter;
    
    private static final Logger logger = Util.getClassLogger();
    
    public GetterSetterReflection(Method param1Method1, Method param1Method2) {
      super((param1Method1 != null) ? param1Method1.getReturnType() : param1Method2.getParameterTypes()[0]);
      this.getter = param1Method1;
      this.setter = param1Method2;
      if (param1Method1 != null)
        makeAccessible(param1Method1); 
      if (param1Method2 != null)
        makeAccessible(param1Method2); 
    }
    
    private void makeAccessible(Method param1Method) {
      if (!Modifier.isPublic(param1Method.getModifiers()) || !Modifier.isPublic(param1Method.getDeclaringClass().getModifiers()))
        try {
          param1Method.setAccessible(true);
        } catch (SecurityException securityException) {
          if (!accessWarned)
            logger.log(Level.WARNING, Messages.UNABLE_TO_ACCESS_NON_PUBLIC_FIELD.format(new Object[] { param1Method.getDeclaringClass().getName(), param1Method.getName() }, ), securityException); 
          accessWarned = true;
        }  
    }
    
    public ValueT get(BeanT param1BeanT) throws AccessorException {
      try {
        return (ValueT)this.getter.invoke(param1BeanT, new Object[0]);
      } catch (IllegalAccessException illegalAccessException) {
        throw new IllegalAccessError(illegalAccessException.getMessage());
      } catch (InvocationTargetException invocationTargetException) {
        throw handleInvocationTargetException(invocationTargetException);
      } 
    }
    
    public void set(BeanT param1BeanT, ValueT param1ValueT) throws AccessorException {
      try {
        if (param1ValueT == null)
          param1ValueT = (ValueT)uninitializedValues.get(this.valueType); 
        this.setter.invoke(param1BeanT, new Object[] { param1ValueT });
      } catch (IllegalAccessException illegalAccessException) {
        throw new IllegalAccessError(illegalAccessException.getMessage());
      } catch (InvocationTargetException invocationTargetException) {
        throw handleInvocationTargetException(invocationTargetException);
      } 
    }
    
    private AccessorException handleInvocationTargetException(InvocationTargetException param1InvocationTargetException) {
      Throwable throwable = param1InvocationTargetException.getTargetException();
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof Error)
        throw (Error)throwable; 
      return new AccessorException(throwable);
    }
    
    public Accessor<BeanT, ValueT> optimize(JAXBContextImpl param1JAXBContextImpl) {
      if (this.getter == null || this.setter == null)
        return this; 
      if (param1JAXBContextImpl != null && param1JAXBContextImpl.fastBoot)
        return this; 
      Accessor accessor = OptimizedAccessorFactory.get(this.getter, this.setter);
      return (accessor != null) ? accessor : this;
    }
  }
  
  public static final class ReadOnlyFieldReflection<BeanT, ValueT> extends FieldReflection<BeanT, ValueT> {
    public ReadOnlyFieldReflection(Field param1Field, boolean param1Boolean) { super(param1Field, param1Boolean); }
    
    public ReadOnlyFieldReflection(Field param1Field) { super(param1Field); }
    
    public void set(BeanT param1BeanT, ValueT param1ValueT) throws AccessorException {}
    
    public Accessor<BeanT, ValueT> optimize(JAXBContextImpl param1JAXBContextImpl) { return this; }
  }
  
  public static class SetterOnlyReflection<BeanT, ValueT> extends GetterSetterReflection<BeanT, ValueT> {
    public SetterOnlyReflection(Method param1Method) { super(null, param1Method); }
    
    public ValueT get(BeanT param1BeanT) throws AccessorException { throw new AccessorException(Messages.NO_GETTER.format(new Object[] { this.setter.toString() })); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\Accessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */