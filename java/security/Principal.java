package java.security;

import javax.security.auth.Subject;

public interface Principal {
  boolean equals(Object paramObject);
  
  String toString();
  
  int hashCode();
  
  String getName();
  
  default boolean implies(Subject paramSubject) { return (paramSubject == null) ? false : paramSubject.getPrincipals().contains(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\Principal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */