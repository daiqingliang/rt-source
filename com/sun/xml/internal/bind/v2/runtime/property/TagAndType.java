package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;

class TagAndType {
  final Name tagName;
  
  final JaxBeanInfo beanInfo;
  
  TagAndType(Name paramName, JaxBeanInfo paramJaxBeanInfo) {
    this.tagName = paramName;
    this.beanInfo = paramJaxBeanInfo;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\TagAndType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */