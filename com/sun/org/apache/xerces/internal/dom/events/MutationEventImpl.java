package com.sun.org.apache.xerces.internal.dom.events;

import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;

public class MutationEventImpl extends EventImpl implements MutationEvent {
  Node relatedNode = null;
  
  String prevValue = null;
  
  String newValue = null;
  
  String attrName = null;
  
  public short attrChange;
  
  public static final String DOM_SUBTREE_MODIFIED = "DOMSubtreeModified";
  
  public static final String DOM_NODE_INSERTED = "DOMNodeInserted";
  
  public static final String DOM_NODE_REMOVED = "DOMNodeRemoved";
  
  public static final String DOM_NODE_REMOVED_FROM_DOCUMENT = "DOMNodeRemovedFromDocument";
  
  public static final String DOM_NODE_INSERTED_INTO_DOCUMENT = "DOMNodeInsertedIntoDocument";
  
  public static final String DOM_ATTR_MODIFIED = "DOMAttrModified";
  
  public static final String DOM_CHARACTER_DATA_MODIFIED = "DOMCharacterDataModified";
  
  public String getAttrName() { return this.attrName; }
  
  public short getAttrChange() { return this.attrChange; }
  
  public String getNewValue() { return this.newValue; }
  
  public String getPrevValue() { return this.prevValue; }
  
  public Node getRelatedNode() { return this.relatedNode; }
  
  public void initMutationEvent(String paramString1, boolean paramBoolean1, boolean paramBoolean2, Node paramNode, String paramString2, String paramString3, String paramString4, short paramShort) {
    this.relatedNode = paramNode;
    this.prevValue = paramString2;
    this.newValue = paramString3;
    this.attrName = paramString4;
    this.attrChange = paramShort;
    initEvent(paramString1, paramBoolean1, paramBoolean2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\events\MutationEventImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */