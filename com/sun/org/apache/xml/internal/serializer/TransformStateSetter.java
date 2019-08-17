package com.sun.org.apache.xml.internal.serializer;

import javax.xml.transform.Transformer;
import org.w3c.dom.Node;

public interface TransformStateSetter {
  void setCurrentNode(Node paramNode);
  
  void resetState(Transformer paramTransformer);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\TransformStateSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */