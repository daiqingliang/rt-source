package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.util.Collection;
import javax.xml.namespace.QName;
import org.xml.sax.SAXException;

public class XsiNilLoader extends ProxyLoader {
  private final Loader defaultLoader;
  
  public XsiNilLoader(Loader paramLoader) {
    this.defaultLoader = paramLoader;
    assert paramLoader != null;
  }
  
  protected Loader selectLoader(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {
    int i = paramTagName.atts.getIndex("http://www.w3.org/2001/XMLSchema-instance", "nil");
    if (i != -1) {
      Boolean bool = DatatypeConverterImpl._parseBoolean(paramTagName.atts.getValue(i));
      if (bool != null && bool.booleanValue()) {
        onNil(paramState);
        boolean bool1 = (paramTagName.atts.getLength() - 1 > 0) ? 1 : 0;
        if (!bool1 || !(paramState.getPrev().getTarget() instanceof javax.xml.bind.JAXBElement))
          return Discarder.INSTANCE; 
      } 
    } 
    return this.defaultLoader;
  }
  
  public Collection<QName> getExpectedChildElements() { return this.defaultLoader.getExpectedChildElements(); }
  
  public Collection<QName> getExpectedAttributes() { return this.defaultLoader.getExpectedAttributes(); }
  
  protected void onNil(UnmarshallingContext.State paramState) throws SAXException {}
  
  public static final class Array extends XsiNilLoader {
    public Array(Loader param1Loader) { super(param1Loader); }
    
    protected void onNil(UnmarshallingContext.State param1State) throws SAXException { param1State.setTarget(null); }
  }
  
  public static final class Single extends XsiNilLoader {
    private final Accessor acc;
    
    public Single(Loader param1Loader, Accessor param1Accessor) {
      super(param1Loader);
      this.acc = param1Accessor;
    }
    
    protected void onNil(UnmarshallingContext.State param1State) throws SAXException {
      try {
        this.acc.set(param1State.getPrev().getTarget(), null);
        param1State.getPrev().setNil(true);
      } catch (AccessorException accessorException) {
        handleGenericException(accessorException, true);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\XsiNilLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */