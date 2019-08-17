package javax.xml.bind;

import java.net.URL;
import org.w3c.dom.Node;

public interface ValidationEventLocator {
  URL getURL();
  
  int getOffset();
  
  int getLineNumber();
  
  int getColumnNumber();
  
  Object getObject();
  
  Node getNode();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\ValidationEventLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */