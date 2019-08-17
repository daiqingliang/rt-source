package com.sun.jmx.remote.security;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.management.remote.SubjectDelegationPermission;
import javax.security.auth.Subject;

public class SubjectDelegator {
  public AccessControlContext delegatedContext(AccessControlContext paramAccessControlContext, Subject paramSubject, boolean paramBoolean) throws SecurityException {
    if (System.getSecurityManager() != null && paramAccessControlContext == null)
      throw new SecurityException("Illegal AccessControlContext: null"); 
    Collection collection = getSubjectPrincipals(paramSubject);
    final ArrayList permissions = new ArrayList(collection.size());
    for (Principal principal : collection) {
      String str = principal.getClass().getName() + "." + principal.getName();
      arrayList.add(new SubjectDelegationPermission(str));
    } 
    PrivilegedAction<Void> privilegedAction = new PrivilegedAction<Void>() {
        public Void run() {
          for (Permission permission : permissions)
            AccessController.checkPermission(permission); 
          return null;
        }
      };
    AccessController.doPrivileged(privilegedAction, paramAccessControlContext);
    return getDelegatedAcc(paramSubject, paramBoolean);
  }
  
  private AccessControlContext getDelegatedAcc(Subject paramSubject, boolean paramBoolean) { return paramBoolean ? JMXSubjectDomainCombiner.getDomainCombinerContext(paramSubject) : JMXSubjectDomainCombiner.getContext(paramSubject); }
  
  public static boolean checkRemoveCallerContext(Subject paramSubject) {
    try {
      for (Principal principal : getSubjectPrincipals(paramSubject)) {
        String str = principal.getClass().getName() + "." + principal.getName();
        SubjectDelegationPermission subjectDelegationPermission = new SubjectDelegationPermission(str);
        AccessController.checkPermission(subjectDelegationPermission);
      } 
    } catch (SecurityException securityException) {
      return false;
    } 
    return true;
  }
  
  private static Collection<Principal> getSubjectPrincipals(Subject paramSubject) {
    if (paramSubject.isReadOnly())
      return paramSubject.getPrincipals(); 
    List list = Arrays.asList(paramSubject.getPrincipals().toArray(new Principal[0]));
    return Collections.unmodifiableList(list);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\security\SubjectDelegator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */