package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.model.ParameterImpl;
import javax.xml.ws.WebServiceException;

public abstract class ValueSetterFactory {
  public static final ValueSetterFactory SYNC = new ValueSetterFactory() {
      public ValueSetter get(ParameterImpl param1ParameterImpl) { return ValueSetter.getSync(param1ParameterImpl); }
    };
  
  public static final ValueSetterFactory NONE = new ValueSetterFactory() {
      public ValueSetter get(ParameterImpl param1ParameterImpl) { throw new WebServiceException("This shouldn't happen. No response parameters."); }
    };
  
  public static final ValueSetterFactory SINGLE = new ValueSetterFactory() {
      public ValueSetter get(ParameterImpl param1ParameterImpl) { return ValueSetter.SINGLE_VALUE; }
    };
  
  public abstract ValueSetter get(ParameterImpl paramParameterImpl);
  
  public static final class AsyncBeanValueSetterFactory extends ValueSetterFactory {
    private Class asyncBean;
    
    public AsyncBeanValueSetterFactory(Class param1Class) { this.asyncBean = param1Class; }
    
    public ValueSetter get(ParameterImpl param1ParameterImpl) { return new ValueSetter.AsyncBeanValueSetter(param1ParameterImpl, this.asyncBean); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\sei\ValueSetterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */