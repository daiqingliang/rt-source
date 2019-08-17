package javax.xml.bind;

import javax.xml.namespace.QName;

public abstract class JAXBIntrospector {
  public abstract boolean isElement(Object paramObject);
  
  public abstract QName getElementName(Object paramObject);
  
  public static Object getValue(Object paramObject) { return (paramObject instanceof JAXBElement) ? ((JAXBElement)paramObject).getValue() : paramObject; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\JAXBIntrospector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */