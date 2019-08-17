package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.peer.ScrollPanePeer;
import java.io.Serializable;

class PeerFixer implements AdjustmentListener, Serializable {
  private static final long serialVersionUID = 7051237413532574756L;
  
  private ScrollPane scroller;
  
  PeerFixer(ScrollPane paramScrollPane) { this.scroller = paramScrollPane; }
  
  public void adjustmentValueChanged(AdjustmentEvent paramAdjustmentEvent) {
    Adjustable adjustable = paramAdjustmentEvent.getAdjustable();
    int i = paramAdjustmentEvent.getValue();
    ScrollPanePeer scrollPanePeer = (ScrollPanePeer)this.scroller.peer;
    if (scrollPanePeer != null)
      scrollPanePeer.setValue(adjustable, i); 
    Component component = this.scroller.getComponent(0);
    switch (adjustable.getOrientation()) {
      case 1:
        component.move((component.getLocation()).x, -i);
        return;
      case 0:
        component.move(-i, (component.getLocation()).y);
        return;
    } 
    throw new IllegalArgumentException("Illegal adjustable orientation");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\PeerFixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */