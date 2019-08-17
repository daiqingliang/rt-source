package sun.security.tools.policytool;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ListIterator;
import sun.security.provider.PolicyParser;

class PolicyEntry {
  private CodeSource codesource;
  
  private PolicyTool tool;
  
  private PolicyParser.GrantEntry grantEntry;
  
  private boolean testing = false;
  
  PolicyEntry(PolicyTool paramPolicyTool, PolicyParser.GrantEntry paramGrantEntry) throws MalformedURLException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
    this.tool = paramPolicyTool;
    URL uRL = null;
    if (paramGrantEntry.codeBase != null)
      uRL = new URL(paramGrantEntry.codeBase); 
    this.codesource = new CodeSource(uRL, (Certificate[])null);
    if (this.testing) {
      System.out.println("Adding Policy Entry:");
      System.out.println("    CodeBase = " + uRL);
      System.out.println("    Signers = " + paramGrantEntry.signedBy);
      System.out.println("    with " + paramGrantEntry.principals.size() + " Principals");
    } 
    this.grantEntry = paramGrantEntry;
  }
  
  CodeSource getCodeSource() { return this.codesource; }
  
  PolicyParser.GrantEntry getGrantEntry() { return this.grantEntry; }
  
  String headerToString() {
    String str = principalsToString();
    return (str.length() == 0) ? codebaseToString() : (codebaseToString() + ", " + str);
  }
  
  String codebaseToString() {
    String str = new String();
    if (this.grantEntry.codeBase != null && !this.grantEntry.codeBase.equals(""))
      str = str.concat("CodeBase \"" + this.grantEntry.codeBase + "\""); 
    if (this.grantEntry.signedBy != null && !this.grantEntry.signedBy.equals(""))
      str = (str.length() > 0) ? str.concat(", SignedBy \"" + this.grantEntry.signedBy + "\"") : str.concat("SignedBy \"" + this.grantEntry.signedBy + "\""); 
    return (str.length() == 0) ? new String("CodeBase <ALL>") : str;
  }
  
  String principalsToString() {
    String str = "";
    if (this.grantEntry.principals != null && !this.grantEntry.principals.isEmpty()) {
      StringBuffer stringBuffer = new StringBuffer(200);
      ListIterator listIterator = this.grantEntry.principals.listIterator();
      while (listIterator.hasNext()) {
        PolicyParser.PrincipalEntry principalEntry = (PolicyParser.PrincipalEntry)listIterator.next();
        stringBuffer.append(" Principal " + principalEntry.getDisplayClass() + " " + principalEntry.getDisplayName(true));
        if (listIterator.hasNext())
          stringBuffer.append(", "); 
      } 
      str = stringBuffer.toString();
    } 
    return str;
  }
  
  PolicyParser.PermissionEntry toPermissionEntry(Permission paramPermission) {
    String str = null;
    if (paramPermission.getActions() != null && paramPermission.getActions().trim() != "")
      str = paramPermission.getActions(); 
    return new PolicyParser.PermissionEntry(paramPermission.getClass().getName(), paramPermission.getName(), str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\PolicyEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */