package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;
import java.util.Properties;

public abstract class ParserActionBase implements ParserAction {
  private String propertyName;
  
  private boolean prefix;
  
  private Operation operation;
  
  private String fieldName;
  
  public int hashCode() { return this.propertyName.hashCode() ^ this.operation.hashCode() ^ this.fieldName.hashCode() ^ (this.prefix ? 0 : 1); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof ParserActionBase))
      return false; 
    ParserActionBase parserActionBase = (ParserActionBase)paramObject;
    return (this.propertyName.equals(parserActionBase.propertyName) && this.prefix == parserActionBase.prefix && this.operation.equals(parserActionBase.operation) && this.fieldName.equals(parserActionBase.fieldName));
  }
  
  public ParserActionBase(String paramString1, boolean paramBoolean, Operation paramOperation, String paramString2) {
    this.propertyName = paramString1;
    this.prefix = paramBoolean;
    this.operation = paramOperation;
    this.fieldName = paramString2;
  }
  
  public String getPropertyName() { return this.propertyName; }
  
  public boolean isPrefix() { return this.prefix; }
  
  public String getFieldName() { return this.fieldName; }
  
  public abstract Object apply(Properties paramProperties);
  
  protected Operation getOperation() { return this.operation; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\ParserActionBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */