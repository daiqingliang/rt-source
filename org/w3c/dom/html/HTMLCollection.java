package org.w3c.dom.html;

import org.w3c.dom.Node;

public interface HTMLCollection {
  int getLength();
  
  Node item(int paramInt);
  
  Node namedItem(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\html\HTMLCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */