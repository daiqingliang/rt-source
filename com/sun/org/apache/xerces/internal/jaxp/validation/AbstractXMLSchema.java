package com.sun.org.apache.xerces.internal.jaxp.validation;

import java.util.HashMap;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

abstract class AbstractXMLSchema extends Schema implements XSGrammarPoolContainer {
  private final HashMap fFeatures = new HashMap();
  
  private final HashMap fProperties = new HashMap();
  
  public final Validator newValidator() { return new ValidatorImpl(this); }
  
  public final ValidatorHandler newValidatorHandler() { return new ValidatorHandlerImpl(this); }
  
  public final Boolean getFeature(String paramString) { return (Boolean)this.fFeatures.get(paramString); }
  
  public final void setFeature(String paramString, boolean paramBoolean) { this.fFeatures.put(paramString, paramBoolean ? Boolean.TRUE : Boolean.FALSE); }
  
  public final Object getProperty(String paramString) { return this.fProperties.get(paramString); }
  
  public final void setProperty(String paramString, Object paramObject) { this.fProperties.put(paramString, paramObject); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\AbstractXMLSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */