package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl;
import com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import java.util.Map;

public abstract class DTDDVFactory {
  private static final String DEFAULT_FACTORY_CLASS = "com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl";
  
  private static final String XML11_DATATYPE_VALIDATOR_FACTORY = "com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl";
  
  public static final DTDDVFactory getInstance() throws DVFactoryException { return getInstance("com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl"); }
  
  public static final DTDDVFactory getInstance(String paramString) throws DVFactoryException {
    try {
      return "com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl".equals(paramString) ? new DTDDVFactoryImpl() : ("com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl".equals(paramString) ? new XML11DTDDVFactoryImpl() : (DTDDVFactory)ObjectFactory.newInstance(paramString, true));
    } catch (ClassCastException classCastException) {
      throw new DVFactoryException("DTD factory class " + paramString + " does not extend from DTDDVFactory.");
    } 
  }
  
  public abstract DatatypeValidator getBuiltInDV(String paramString);
  
  public abstract Map<String, DatatypeValidator> getBuiltInTypes();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\DTDDVFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */