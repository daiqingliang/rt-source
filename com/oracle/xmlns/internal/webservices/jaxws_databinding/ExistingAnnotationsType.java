package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "existing-annotations-type")
@XmlEnum
public static enum ExistingAnnotationsType {
  MERGE("merge"),
  IGNORE("ignore");
  
  private final String value;
  
  ExistingAnnotationsType(String paramString1) { this.value = paramString1; }
  
  public String value() { return this.value; }
  
  public static ExistingAnnotationsType fromValue(String paramString) {
    for (ExistingAnnotationsType existingAnnotationsType : values()) {
      if (existingAnnotationsType.value.equals(paramString))
        return existingAnnotationsType; 
    } 
    throw new IllegalArgumentException(paramString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\ExistingAnnotationsType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */