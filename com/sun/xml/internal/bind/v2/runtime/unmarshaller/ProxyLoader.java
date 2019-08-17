package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public abstract class ProxyLoader extends Loader {
  public ProxyLoader() { super(false); }
  
  public final void startElement(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {
    Loader loader = selectLoader(paramState, paramTagName);
    paramState.setLoader(loader);
    loader.startElement(paramState, paramTagName);
  }
  
  protected abstract Loader selectLoader(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException;
  
  public final void leaveElement(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException { throw new IllegalStateException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\ProxyLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */