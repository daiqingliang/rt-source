package javax.xml.transform.dom;

import javax.xml.transform.SourceLocator;
import org.w3c.dom.Node;

public interface DOMLocator extends SourceLocator {
  Node getOriginatingNode();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\dom\DOMLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */