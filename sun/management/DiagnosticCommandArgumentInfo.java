package sun.management;

class DiagnosticCommandArgumentInfo {
  private final String name;
  
  private final String description;
  
  private final String type;
  
  private final String defaultValue;
  
  private final boolean mandatory;
  
  private final boolean option;
  
  private final boolean multiple;
  
  private final int position;
  
  String getName() { return this.name; }
  
  String getDescription() { return this.description; }
  
  String getType() { return this.type; }
  
  String getDefault() { return this.defaultValue; }
  
  boolean isMandatory() { return this.mandatory; }
  
  boolean isOption() { return this.option; }
  
  boolean isMultiple() { return this.multiple; }
  
  int getPosition() { return this.position; }
  
  DiagnosticCommandArgumentInfo(String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt) {
    this.name = paramString1;
    this.description = paramString2;
    this.type = paramString3;
    this.defaultValue = paramString4;
    this.mandatory = paramBoolean1;
    this.option = paramBoolean2;
    this.multiple = paramBoolean3;
    this.position = paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\DiagnosticCommandArgumentInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */