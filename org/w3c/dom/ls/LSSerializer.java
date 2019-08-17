package org.w3c.dom.ls;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public interface LSSerializer {
  DOMConfiguration getDomConfig();
  
  String getNewLine();
  
  void setNewLine(String paramString);
  
  LSSerializerFilter getFilter();
  
  void setFilter(LSSerializerFilter paramLSSerializerFilter);
  
  boolean write(Node paramNode, LSOutput paramLSOutput) throws LSException;
  
  boolean writeToURI(Node paramNode, String paramString) throws LSException;
  
  String writeToString(Node paramNode) throws DOMException, LSException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\ls\LSSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */