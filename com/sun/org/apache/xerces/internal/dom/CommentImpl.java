package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;

public class CommentImpl extends CharacterDataImpl implements CharacterData, Comment {
  static final long serialVersionUID = -2685736833408134044L;
  
  public CommentImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString) { super(paramCoreDocumentImpl, paramString); }
  
  public short getNodeType() { return 8; }
  
  public String getNodeName() { return "#comment"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\CommentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */