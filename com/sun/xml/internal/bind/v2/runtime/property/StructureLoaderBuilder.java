package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import javax.xml.namespace.QName;

public interface StructureLoaderBuilder {
  public static final QName TEXT_HANDLER = new QName("\000", "text");
  
  public static final QName CATCH_ALL = new QName("\000", "catchAll");
  
  void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\StructureLoaderBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */