package org.w3c.dom.ls;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface LSParser {
  public static final short ACTION_APPEND_AS_CHILDREN = 1;
  
  public static final short ACTION_REPLACE_CHILDREN = 2;
  
  public static final short ACTION_INSERT_BEFORE = 3;
  
  public static final short ACTION_INSERT_AFTER = 4;
  
  public static final short ACTION_REPLACE = 5;
  
  DOMConfiguration getDomConfig();
  
  LSParserFilter getFilter();
  
  void setFilter(LSParserFilter paramLSParserFilter);
  
  boolean getAsync();
  
  boolean getBusy();
  
  Document parse(LSInput paramLSInput) throws DOMException, LSException;
  
  Document parseURI(String paramString) throws DOMException, LSException;
  
  Node parseWithContext(LSInput paramLSInput, Node paramNode, short paramShort) throws DOMException, LSException;
  
  void abort();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\ls\LSParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */