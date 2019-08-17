package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public final class DefaultValueLoaderDecorator extends Loader {
  private final Loader l;
  
  private final String defaultValue;
  
  public DefaultValueLoaderDecorator(Loader paramLoader, String paramString) {
    this.l = paramLoader;
    this.defaultValue = paramString;
  }
  
  public void startElement(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {
    if (paramState.getElementDefaultValue() == null)
      paramState.setElementDefaultValue(this.defaultValue); 
    paramState.setLoader(this.l);
    this.l.startElement(paramState, paramTagName);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\DefaultValueLoaderDecorator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */