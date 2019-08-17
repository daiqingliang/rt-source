package com.sun.xml.internal.ws.fault;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAnyElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class DetailType {
  @XmlAnyElement
  private final List<Element> detailEntry = new ArrayList();
  
  @NotNull
  List<Element> getDetails() { return this.detailEntry; }
  
  @Nullable
  Node getDetail(int paramInt) { return (paramInt < this.detailEntry.size()) ? (Node)this.detailEntry.get(paramInt) : null; }
  
  DetailType(Element paramElement) {
    if (paramElement != null)
      this.detailEntry.add(paramElement); 
  }
  
  DetailType() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\fault\DetailType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */