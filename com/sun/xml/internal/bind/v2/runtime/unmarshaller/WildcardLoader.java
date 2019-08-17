package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import javax.xml.bind.annotation.DomHandler;
import org.xml.sax.SAXException;

public final class WildcardLoader extends ProxyLoader {
  private final DomLoader dom;
  
  private final WildcardMode mode;
  
  public WildcardLoader(DomHandler paramDomHandler, WildcardMode paramWildcardMode) {
    this.dom = new DomLoader(paramDomHandler);
    this.mode = paramWildcardMode;
  }
  
  protected Loader selectLoader(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {
    UnmarshallingContext unmarshallingContext = paramState.getContext();
    if (this.mode.allowTypedObject) {
      Loader loader = unmarshallingContext.selectRootLoader(paramState, paramTagName);
      if (loader != null)
        return loader; 
    } 
    return this.mode.allowDom ? this.dom : Discarder.INSTANCE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\WildcardLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */