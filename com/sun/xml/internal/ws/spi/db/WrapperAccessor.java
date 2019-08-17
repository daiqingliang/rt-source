package com.sun.xml.internal.ws.spi.db;

import java.util.Map;
import javax.xml.namespace.QName;

public abstract class WrapperAccessor {
  protected Map<Object, PropertySetter> propertySetters;
  
  protected Map<Object, PropertyGetter> propertyGetters;
  
  protected boolean elementLocalNameCollision;
  
  protected PropertySetter getPropertySetter(QName paramQName) {
    QName qName = this.elementLocalNameCollision ? paramQName : paramQName.getLocalPart();
    return (PropertySetter)this.propertySetters.get(qName);
  }
  
  protected PropertyGetter getPropertyGetter(QName paramQName) {
    QName qName = this.elementLocalNameCollision ? paramQName : paramQName.getLocalPart();
    return (PropertyGetter)this.propertyGetters.get(qName);
  }
  
  public PropertyAccessor getPropertyAccessor(String paramString1, String paramString2) {
    QName qName = new QName(paramString1, paramString2);
    final PropertySetter setter = getPropertySetter(qName);
    final PropertyGetter getter = getPropertyGetter(qName);
    return new PropertyAccessor() {
        public Object get(Object param1Object) throws DatabindingException { return getter.get(param1Object); }
        
        public void set(Object param1Object1, Object param1Object2) throws DatabindingException { setter.set(param1Object1, param1Object2); }
      };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\WrapperAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */