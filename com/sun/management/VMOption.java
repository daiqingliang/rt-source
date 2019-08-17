package com.sun.management;

import javax.management.openmbean.CompositeData;
import jdk.Exported;
import sun.management.VMOptionCompositeData;

@Exported
public class VMOption {
  private String name;
  
  private String value;
  
  private boolean writeable;
  
  private Origin origin;
  
  public VMOption(String paramString1, String paramString2, boolean paramBoolean, Origin paramOrigin) {
    this.name = paramString1;
    this.value = paramString2;
    this.writeable = paramBoolean;
    this.origin = paramOrigin;
  }
  
  private VMOption(CompositeData paramCompositeData) {
    VMOptionCompositeData.validateCompositeData(paramCompositeData);
    this.name = VMOptionCompositeData.getName(paramCompositeData);
    this.value = VMOptionCompositeData.getValue(paramCompositeData);
    this.writeable = VMOptionCompositeData.isWriteable(paramCompositeData);
    this.origin = VMOptionCompositeData.getOrigin(paramCompositeData);
  }
  
  public String getName() { return this.name; }
  
  public String getValue() { return this.value; }
  
  public Origin getOrigin() { return this.origin; }
  
  public boolean isWriteable() { return this.writeable; }
  
  public String toString() { return "VM option: " + getName() + " value: " + this.value + "  origin: " + this.origin + " " + (this.writeable ? "(read-write)" : "(read-only)"); }
  
  public static VMOption from(CompositeData paramCompositeData) { return (paramCompositeData == null) ? null : ((paramCompositeData instanceof VMOptionCompositeData) ? ((VMOptionCompositeData)paramCompositeData).getVMOption() : new VMOption(paramCompositeData)); }
  
  @Exported
  public enum Origin {
    DEFAULT, VM_CREATION, ENVIRON_VAR, CONFIG_FILE, MANAGEMENT, ERGONOMIC, OTHER;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\management\VMOption.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */