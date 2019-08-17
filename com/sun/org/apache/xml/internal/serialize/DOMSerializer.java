package com.sun.org.apache.xml.internal.serialize;

import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

public interface DOMSerializer {
  void serialize(Element paramElement) throws IOException;
  
  void serialize(Document paramDocument) throws IOException;
  
  void serialize(DocumentFragment paramDocumentFragment) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\DOMSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */