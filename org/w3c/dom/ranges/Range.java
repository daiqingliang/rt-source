package org.w3c.dom.ranges;

import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

public interface Range {
  public static final short START_TO_START = 0;
  
  public static final short START_TO_END = 1;
  
  public static final short END_TO_END = 2;
  
  public static final short END_TO_START = 3;
  
  Node getStartContainer() throws DOMException;
  
  int getStartOffset() throws DOMException;
  
  Node getEndContainer() throws DOMException;
  
  int getEndOffset() throws DOMException;
  
  boolean getCollapsed() throws DOMException;
  
  Node getCommonAncestorContainer() throws DOMException;
  
  void setStart(Node paramNode, int paramInt) throws RangeException, DOMException;
  
  void setEnd(Node paramNode, int paramInt) throws RangeException, DOMException;
  
  void setStartBefore(Node paramNode) throws RangeException, DOMException;
  
  void setStartAfter(Node paramNode) throws RangeException, DOMException;
  
  void setEndBefore(Node paramNode) throws RangeException, DOMException;
  
  void setEndAfter(Node paramNode) throws RangeException, DOMException;
  
  void collapse(boolean paramBoolean) throws DOMException;
  
  void selectNode(Node paramNode) throws RangeException, DOMException;
  
  void selectNodeContents(Node paramNode) throws RangeException, DOMException;
  
  short compareBoundaryPoints(short paramShort, Range paramRange) throws DOMException;
  
  void deleteContents() throws DOMException;
  
  DocumentFragment extractContents() throws DOMException;
  
  DocumentFragment cloneContents() throws DOMException;
  
  void insertNode(Node paramNode) throws RangeException, DOMException;
  
  void surroundContents(Node paramNode) throws RangeException, DOMException;
  
  Range cloneRange() throws DOMException;
  
  String toString() throws DOMException;
  
  void detach() throws DOMException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\ranges\Range.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */