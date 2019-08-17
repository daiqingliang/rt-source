package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class XML11DTDDVFactoryImpl extends DTDDVFactoryImpl {
  static Map<String, DatatypeValidator> XML11BUILTINTYPES;
  
  public DatatypeValidator getBuiltInDV(String paramString) { return (XML11BUILTINTYPES.get(paramString) != null) ? (DatatypeValidator)XML11BUILTINTYPES.get(paramString) : (DatatypeValidator)fBuiltInTypes.get(paramString); }
  
  public Map<String, DatatypeValidator> getBuiltInTypes() {
    HashMap hashMap = new HashMap(fBuiltInTypes);
    hashMap.putAll(XML11BUILTINTYPES);
    return hashMap;
  }
  
  static  {
    HashMap hashMap = new HashMap();
    hashMap.put("XML11ID", new XML11IDDatatypeValidator());
    XML11IDREFDatatypeValidator xML11IDREFDatatypeValidator = new XML11IDREFDatatypeValidator();
    hashMap.put("XML11IDREF", xML11IDREFDatatypeValidator);
    hashMap.put("XML11IDREFS", new ListDatatypeValidator(xML11IDREFDatatypeValidator));
    XML11NMTOKENDatatypeValidator xML11NMTOKENDatatypeValidator = new XML11NMTOKENDatatypeValidator();
    hashMap.put("XML11NMTOKEN", xML11NMTOKENDatatypeValidator);
    hashMap.put("XML11NMTOKENS", new ListDatatypeValidator(xML11NMTOKENDatatypeValidator));
    XML11BUILTINTYPES = Collections.unmodifiableMap(hashMap);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\dtd\XML11DTDDVFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */