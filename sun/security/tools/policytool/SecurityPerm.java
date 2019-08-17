package sun.security.tools.policytool;

class SecurityPerm extends Perm {
  public SecurityPerm() { super("SecurityPermission", "java.security.SecurityPermission", new String[] { 
          "createAccessControlContext", "getDomainCombiner", "getPolicy", "setPolicy", "createPolicy.<" + PolicyTool.getMessage("policy.type") + ">", "getProperty.<" + PolicyTool.getMessage("property.name") + ">", "setProperty.<" + PolicyTool.getMessage("property.name") + ">", "insertProvider.<" + PolicyTool.getMessage("provider.name") + ">", "removeProvider.<" + PolicyTool.getMessage("provider.name") + ">", "clearProviderProperties.<" + PolicyTool.getMessage("provider.name") + ">", 
          "putProviderProperty.<" + PolicyTool.getMessage("provider.name") + ">", "removeProviderProperty.<" + PolicyTool.getMessage("provider.name") + ">" }, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\SecurityPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */