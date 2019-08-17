package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.ParserData;

public abstract class ParserDataBase implements ParserData {
  private String propertyName;
  
  private Operation operation;
  
  private String fieldName;
  
  private Object defaultValue;
  
  private Object testValue;
  
  protected ParserDataBase(String paramString1, Operation paramOperation, String paramString2, Object paramObject1, Object paramObject2) {
    this.propertyName = paramString1;
    this.operation = paramOperation;
    this.fieldName = paramString2;
    this.defaultValue = paramObject1;
    this.testValue = paramObject2;
  }
  
  public String getPropertyName() { return this.propertyName; }
  
  public Operation getOperation() { return this.operation; }
  
  public String getFieldName() { return this.fieldName; }
  
  public Object getDefaultValue() { return this.defaultValue; }
  
  public Object getTestValue() { return this.testValue; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\ParserDataBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */