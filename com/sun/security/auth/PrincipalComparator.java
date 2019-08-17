package com.sun.security.auth;

import javax.security.auth.Subject;
import jdk.Exported;

@Exported
public interface PrincipalComparator {
  boolean implies(Subject paramSubject);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\PrincipalComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */