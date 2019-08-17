package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.ClassBeanInfoImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import java.util.Collection;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public final class LeafPropertyXsiLoader extends Loader {
  private final Loader defaultLoader;
  
  private final TransducedAccessor xacc;
  
  private final Accessor acc;
  
  public LeafPropertyXsiLoader(Loader paramLoader, TransducedAccessor paramTransducedAccessor, Accessor paramAccessor) {
    this.defaultLoader = paramLoader;
    this.expectText = true;
    this.xacc = paramTransducedAccessor;
    this.acc = paramAccessor;
  }
  
  public void startElement(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {
    Loader loader = selectLoader(paramState, paramTagName);
    paramState.setLoader(loader);
    loader.startElement(paramState, paramTagName);
  }
  
  protected Loader selectLoader(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {
    UnmarshallingContext unmarshallingContext = paramState.getContext();
    JaxBeanInfo jaxBeanInfo = null;
    Attributes attributes = paramTagName.atts;
    int i = attributes.getIndex("http://www.w3.org/2001/XMLSchema-instance", "type");
    if (i >= 0) {
      ClassBeanInfoImpl classBeanInfoImpl;
      String str = attributes.getValue(i);
      QName qName = DatatypeConverterImpl._parseQName(str, unmarshallingContext);
      if (qName == null)
        return this.defaultLoader; 
      jaxBeanInfo = unmarshallingContext.getJAXBContext().getGlobalType(qName);
      if (jaxBeanInfo == null)
        return this.defaultLoader; 
      try {
        classBeanInfoImpl = (ClassBeanInfoImpl)jaxBeanInfo;
      } catch (ClassCastException classCastException) {
        return this.defaultLoader;
      } 
      return (null == classBeanInfoImpl.getTransducer()) ? this.defaultLoader : new LeafPropertyLoader(new TransducedAccessor.CompositeTransducedAccessorImpl(paramState.getContext().getJAXBContext(), classBeanInfoImpl.getTransducer(), this.acc));
    } 
    return this.defaultLoader;
  }
  
  public Collection<QName> getExpectedChildElements() { return this.defaultLoader.getExpectedChildElements(); }
  
  public Collection<QName> getExpectedAttributes() { return this.defaultLoader.getExpectedAttributes(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\LeafPropertyXsiLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */