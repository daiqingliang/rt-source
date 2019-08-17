package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;

public class XML11DTDValidator extends XMLDTDValidator {
  protected static final String DTD_VALIDATOR_PROPERTY = "http://apache.org/xml/properties/internal/validator/dtd";
  
  public void reset(XMLComponentManager paramXMLComponentManager) {
    XMLDTDValidator xMLDTDValidator = null;
    if ((xMLDTDValidator = (XMLDTDValidator)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/dtd")) != null && xMLDTDValidator != this)
      this.fGrammarBucket = xMLDTDValidator.getGrammarBucket(); 
    super.reset(paramXMLComponentManager);
  }
  
  protected void init() {
    if (this.fValidation || this.fDynamicValidation) {
      super.init();
      try {
        this.fValID = this.fDatatypeValidatorFactory.getBuiltInDV("XML11ID");
        this.fValIDRef = this.fDatatypeValidatorFactory.getBuiltInDV("XML11IDREF");
        this.fValIDRefs = this.fDatatypeValidatorFactory.getBuiltInDV("XML11IDREFS");
        this.fValNMTOKEN = this.fDatatypeValidatorFactory.getBuiltInDV("XML11NMTOKEN");
        this.fValNMTOKENS = this.fDatatypeValidatorFactory.getBuiltInDV("XML11NMTOKENS");
      } catch (Exception exception) {
        exception.printStackTrace(System.err);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\XML11DTDValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */