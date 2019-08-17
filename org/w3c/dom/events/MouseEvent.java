package org.w3c.dom.events;

import org.w3c.dom.views.AbstractView;

public interface MouseEvent extends UIEvent {
  int getScreenX();
  
  int getScreenY();
  
  int getClientX();
  
  int getClientY();
  
  boolean getCtrlKey();
  
  boolean getShiftKey();
  
  boolean getAltKey();
  
  boolean getMetaKey();
  
  short getButton();
  
  EventTarget getRelatedTarget();
  
  void initMouseEvent(String paramString, boolean paramBoolean1, boolean paramBoolean2, AbstractView paramAbstractView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6, short paramShort, EventTarget paramEventTarget);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\events\MouseEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */