package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class BadServerDefinition extends UserException {
  public String reason = null;
  
  public BadServerDefinition() { super(BadServerDefinitionHelper.id()); }
  
  public BadServerDefinition(String paramString) {
    super(BadServerDefinitionHelper.id());
    this.reason = paramString;
  }
  
  public BadServerDefinition(String paramString1, String paramString2) {
    super(BadServerDefinitionHelper.id() + "  " + paramString1);
    this.reason = paramString2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\BadServerDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */