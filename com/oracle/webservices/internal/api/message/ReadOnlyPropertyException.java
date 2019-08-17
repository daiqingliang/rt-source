package com.oracle.webservices.internal.api.message;

public class ReadOnlyPropertyException extends IllegalArgumentException {
  private final String propertyName;
  
  public ReadOnlyPropertyException(String paramString) {
    super(paramString + " is a read-only property.");
    this.propertyName = paramString;
  }
  
  public String getPropertyName() { return this.propertyName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\webservices\internal\api\message\ReadOnlyPropertyException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */