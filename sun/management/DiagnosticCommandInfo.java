package sun.management;

import java.util.List;

class DiagnosticCommandInfo {
  private final String name;
  
  private final String description;
  
  private final String impact;
  
  private final String permissionClass;
  
  private final String permissionName;
  
  private final String permissionAction;
  
  private final boolean enabled;
  
  private final List<DiagnosticCommandArgumentInfo> arguments;
  
  String getName() { return this.name; }
  
  String getDescription() { return this.description; }
  
  String getImpact() { return this.impact; }
  
  String getPermissionClass() { return this.permissionClass; }
  
  String getPermissionName() { return this.permissionName; }
  
  String getPermissionAction() { return this.permissionAction; }
  
  boolean isEnabled() { return this.enabled; }
  
  List<DiagnosticCommandArgumentInfo> getArgumentsInfo() { return this.arguments; }
  
  DiagnosticCommandInfo(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, boolean paramBoolean, List<DiagnosticCommandArgumentInfo> paramList) {
    this.name = paramString1;
    this.description = paramString2;
    this.impact = paramString3;
    this.permissionClass = paramString4;
    this.permissionName = paramString5;
    this.permissionAction = paramString6;
    this.enabled = paramBoolean;
    this.arguments = paramList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\DiagnosticCommandInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */