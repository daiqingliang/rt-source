package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class XsiTypeLoader extends Loader {
  private final JaxBeanInfo defaultBeanInfo;
  
  static final QName XsiTypeQNAME = new QName("http://www.w3.org/2001/XMLSchema-instance", "type");
  
  public XsiTypeLoader(JaxBeanInfo paramJaxBeanInfo) {
    super(true);
    this.defaultBeanInfo = paramJaxBeanInfo;
  }
  
  public void startElement(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {
    JaxBeanInfo jaxBeanInfo = parseXsiType(paramState, paramTagName, this.defaultBeanInfo);
    if (jaxBeanInfo == null)
      jaxBeanInfo = this.defaultBeanInfo; 
    Loader loader = jaxBeanInfo.getLoader(null, false);
    paramState.setLoader(loader);
    loader.startElement(paramState, paramTagName);
  }
  
  static JaxBeanInfo parseXsiType(UnmarshallingContext.State paramState, TagName paramTagName, @Nullable JaxBeanInfo paramJaxBeanInfo) throws SAXException {
    UnmarshallingContext unmarshallingContext = paramState.getContext();
    JaxBeanInfo jaxBeanInfo = null;
    Attributes attributes = paramTagName.atts;
    int i = attributes.getIndex("http://www.w3.org/2001/XMLSchema-instance", "type");
    if (i >= 0) {
      String str = attributes.getValue(i);
      QName qName = DatatypeConverterImpl._parseQName(str, unmarshallingContext);
      if (qName == null) {
        reportError(Messages.NOT_A_QNAME.format(new Object[] { str }, ), true);
      } else {
        if (paramJaxBeanInfo != null && paramJaxBeanInfo.getTypeNames().contains(qName))
          return paramJaxBeanInfo; 
        jaxBeanInfo = unmarshallingContext.getJAXBContext().getGlobalType(qName);
        if (jaxBeanInfo == null && unmarshallingContext.parent.hasEventHandler() && unmarshallingContext.shouldErrorBeReported()) {
          String str1 = unmarshallingContext.getJAXBContext().getNearestTypeName(qName);
          if (str1 != null) {
            reportError(Messages.UNRECOGNIZED_TYPE_NAME_MAYBE.format(new Object[] { qName, str1 }, ), true);
          } else {
            reportError(Messages.UNRECOGNIZED_TYPE_NAME.format(new Object[] { qName }, ), true);
          } 
        } 
      } 
    } 
    return jaxBeanInfo;
  }
  
  public Collection<QName> getExpectedAttributes() {
    HashSet hashSet = new HashSet();
    hashSet.addAll(super.getExpectedAttributes());
    hashSet.add(XsiTypeQNAME);
    return Collections.unmodifiableCollection(hashSet);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\XsiTypeLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */