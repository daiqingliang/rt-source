package org.w3c.dom.css;

import org.w3c.dom.Element;
import org.w3c.dom.views.AbstractView;

public interface ViewCSS extends AbstractView {
  CSSStyleDeclaration getComputedStyle(Element paramElement, String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\css\ViewCSS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */