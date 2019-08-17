package javax.xml.stream.events;

import javax.xml.namespace.QName;

public interface Attribute extends XMLEvent {
  QName getName();
  
  String getValue();
  
  String getDTDType();
  
  boolean isSpecified();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\stream\events\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */