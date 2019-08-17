package sun.security.tools.policytool;

class AuthPerm extends Perm {
  public AuthPerm() { super("AuthPermission", "javax.security.auth.AuthPermission", new String[] { 
          "doAs", "doAsPrivileged", "getSubject", "getSubjectFromDomainCombiner", "setReadOnly", "modifyPrincipals", "modifyPublicCredentials", "modifyPrivateCredentials", "refreshCredential", "destroyCredential", 
          "createLoginContext.<" + PolicyTool.getMessage("name") + ">", "getLoginConfiguration", "setLoginConfiguration", "createLoginConfiguration.<" + PolicyTool.getMessage("configuration.type") + ">", "refreshLoginConfiguration" }, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\AuthPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */