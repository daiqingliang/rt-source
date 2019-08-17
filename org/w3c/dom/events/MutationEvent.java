package org.w3c.dom.events;

import org.w3c.dom.Node;

public interface MutationEvent extends Event {
  public static final short MODIFICATION = 1;
  
  public static final short ADDITION = 2;
  
  public static final short REMOVAL = 3;
  
  Node getRelatedNode();
  
  String getPrevValue();
  
  String getNewValue();
  
  String getAttrName();
  
  short getAttrChange();
  
  void initMutationEvent(String paramString1, boolean paramBoolean1, boolean paramBoolean2, Node paramNode, String paramString2, String paramString3, String paramString4, short paramShort);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\events\MutationEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */