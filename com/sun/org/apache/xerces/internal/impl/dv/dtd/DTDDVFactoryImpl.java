package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DTDDVFactoryImpl extends DTDDVFactory {
  static final Map<String, DatatypeValidator> fBuiltInTypes;
  
  public DatatypeValidator getBuiltInDV(String paramString) { return (DatatypeValidator)fBuiltInTypes.get(paramString); }
  
  public Map<String, DatatypeValidator> getBuiltInTypes() { return new HashMap(fBuiltInTypes); }
  
  static  {
    HashMap hashMap = new HashMap();
    hashMap.put("string", new StringDatatypeValidator());
    hashMap.put("ID", new IDDatatypeValidator());
    IDREFDatatypeValidator iDREFDatatypeValidator = new IDREFDatatypeValidator();
    hashMap.put("IDREF", iDREFDatatypeValidator);
    hashMap.put("IDREFS", new ListDatatypeValidator(iDREFDatatypeValidator));
    ENTITYDatatypeValidator eNTITYDatatypeValidator = new ENTITYDatatypeValidator();
    hashMap.put("ENTITY", new ENTITYDatatypeValidator());
    hashMap.put("ENTITIES", new ListDatatypeValidator(eNTITYDatatypeValidator));
    hashMap.put("NOTATION", new NOTATIONDatatypeValidator());
    NMTOKENDatatypeValidator nMTOKENDatatypeValidator = new NMTOKENDatatypeValidator();
    hashMap.put("NMTOKEN", nMTOKENDatatypeValidator);
    hashMap.put("NMTOKENS", new ListDatatypeValidator(nMTOKENDatatypeValidator));
    fBuiltInTypes = Collections.unmodifiableMap(hashMap);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\dtd\DTDDVFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */