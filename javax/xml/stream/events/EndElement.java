package javax.xml.stream.events;

import java.util.Iterator;
import javax.xml.namespace.QName;

public interface EndElement extends XMLEvent {
  QName getName();
  
  Iterator getNamespaces();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\stream\events\EndElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */