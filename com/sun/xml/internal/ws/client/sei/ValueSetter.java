package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

public abstract class ValueSetter {
  private static final ValueSetter RETURN_VALUE = new ReturnValue(null);
  
  private static final ValueSetter[] POOL = new ValueSetter[16];
  
  static final ValueSetter SINGLE_VALUE;
  
  private ValueSetter() {}
  
  abstract Object put(Object paramObject, Object[] paramArrayOfObject);
  
  static ValueSetter getSync(ParameterImpl paramParameterImpl) {
    int i = paramParameterImpl.getIndex();
    return (i == -1) ? RETURN_VALUE : ((i < POOL.length) ? POOL[i] : new Param(i));
  }
  
  static  {
    for (byte b = 0; b < POOL.length; b++)
      POOL[b] = new Param(b); 
    SINGLE_VALUE = new SingleValue(null);
  }
  
  static final class AsyncBeanValueSetter extends ValueSetter {
    private final PropertyAccessor accessor;
    
    AsyncBeanValueSetter(ParameterImpl param1ParameterImpl, Class param1Class) {
      super(null);
      QName qName = param1ParameterImpl.getName();
      try {
        this.accessor = param1ParameterImpl.getOwner().getBindingContext().getElementPropertyAccessor(param1Class, qName.getNamespaceURI(), qName.getLocalPart());
      } catch (JAXBException jAXBException) {
        throw new WebServiceException(param1Class + " do not have a property of the name " + qName, jAXBException);
      } 
    }
    
    Object put(Object param1Object, Object[] param1ArrayOfObject) {
      assert param1ArrayOfObject != null;
      assert param1ArrayOfObject.length == 1;
      assert param1ArrayOfObject[false] != null;
      Object object = param1ArrayOfObject[0];
      try {
        this.accessor.set(object, param1Object);
      } catch (Exception exception) {
        throw new WebServiceException(exception);
      } 
      return null;
    }
  }
  
  static final class Param extends ValueSetter {
    private final int idx;
    
    public Param(int param1Int) {
      super(null);
      this.idx = param1Int;
    }
    
    Object put(Object param1Object, Object[] param1ArrayOfObject) {
      Object object = param1ArrayOfObject[this.idx];
      if (object != null) {
        assert object instanceof Holder;
        ((Holder)object).value = param1Object;
      } 
      return null;
    }
  }
  
  private static final class ReturnValue extends ValueSetter {
    private ReturnValue() { super(null); }
    
    Object put(Object param1Object, Object[] param1ArrayOfObject) { return param1Object; }
  }
  
  private static final class SingleValue extends ValueSetter {
    private SingleValue() { super(null); }
    
    Object put(Object param1Object, Object[] param1ArrayOfObject) {
      param1ArrayOfObject[0] = param1Object;
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\sei\ValueSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */