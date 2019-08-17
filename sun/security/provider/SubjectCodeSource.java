package sun.security.provider;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.ResourceBundle;
import javax.security.auth.Subject;
import sun.security.util.Debug;

class SubjectCodeSource extends CodeSource implements Serializable {
  private static final long serialVersionUID = 6039418085604715275L;
  
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
        public ResourceBundle run() { return ResourceBundle.getBundle("sun.security.util.AuthResources"); }
      });
  
  private Subject subject;
  
  private LinkedList<PolicyParser.PrincipalEntry> principals;
  
  private static final Class<?>[] PARAMS = { String.class };
  
  private static final Debug debug = Debug.getInstance("auth", "\t[Auth Access]");
  
  private ClassLoader sysClassLoader;
  
  SubjectCodeSource(Subject paramSubject, LinkedList<PolicyParser.PrincipalEntry> paramLinkedList, URL paramURL, Certificate[] paramArrayOfCertificate) {
    super(paramURL, paramArrayOfCertificate);
    this.subject = paramSubject;
    this.principals = (paramLinkedList == null) ? new LinkedList() : new LinkedList(paramLinkedList);
    this.sysClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
          public ClassLoader run() { return ClassLoader.getSystemClassLoader(); }
        });
  }
  
  LinkedList<PolicyParser.PrincipalEntry> getPrincipals() { return this.principals; }
  
  Subject getSubject() { return this.subject; }
  
  public boolean implies(CodeSource paramCodeSource) {
    LinkedList linkedList = null;
    if (paramCodeSource == null || !(paramCodeSource instanceof SubjectCodeSource) || !super.implies(paramCodeSource)) {
      if (debug != null)
        debug.println("\tSubjectCodeSource.implies: FAILURE 1"); 
      return false;
    } 
    SubjectCodeSource subjectCodeSource = (SubjectCodeSource)paramCodeSource;
    if (this.principals == null) {
      if (debug != null)
        debug.println("\tSubjectCodeSource.implies: PASS 1"); 
      return true;
    } 
    if (subjectCodeSource.getSubject() == null || subjectCodeSource.getSubject().getPrincipals().size() == 0) {
      if (debug != null)
        debug.println("\tSubjectCodeSource.implies: FAILURE 2"); 
      return false;
    } 
    ListIterator listIterator = this.principals.listIterator(0);
    while (listIterator.hasNext()) {
      PolicyParser.PrincipalEntry principalEntry = (PolicyParser.PrincipalEntry)listIterator.next();
      try {
        Class clazz = Class.forName(principalEntry.principalClass, true, this.sysClassLoader);
        if (!Principal.class.isAssignableFrom(clazz))
          throw new ClassCastException(principalEntry.principalClass + " is not a Principal"); 
        Constructor constructor = clazz.getConstructor(PARAMS);
        Principal principal = (Principal)constructor.newInstance(new Object[] { principalEntry.principalName });
        if (!principal.implies(subjectCodeSource.getSubject())) {
          if (debug != null)
            debug.println("\tSubjectCodeSource.implies: FAILURE 3"); 
          return false;
        } 
        if (debug != null)
          debug.println("\tSubjectCodeSource.implies: PASS 2"); 
        return true;
      } catch (Exception exception) {
        if (linkedList == null) {
          if (subjectCodeSource.getSubject() == null) {
            if (debug != null)
              debug.println("\tSubjectCodeSource.implies: FAILURE 4"); 
            return false;
          } 
          Iterator iterator = subjectCodeSource.getSubject().getPrincipals().iterator();
          linkedList = new LinkedList();
          while (iterator.hasNext()) {
            Principal principal = (Principal)iterator.next();
            PolicyParser.PrincipalEntry principalEntry1 = new PolicyParser.PrincipalEntry(principal.getClass().getName(), principal.getName());
            linkedList.add(principalEntry1);
          } 
        } 
        if (!subjectListImpliesPrincipalEntry(linkedList, principalEntry)) {
          if (debug != null)
            debug.println("\tSubjectCodeSource.implies: FAILURE 5"); 
          return false;
        } 
      } 
    } 
    if (debug != null)
      debug.println("\tSubjectCodeSource.implies: PASS 3"); 
    return true;
  }
  
  private boolean subjectListImpliesPrincipalEntry(LinkedList<PolicyParser.PrincipalEntry> paramLinkedList, PolicyParser.PrincipalEntry paramPrincipalEntry) {
    ListIterator listIterator = paramLinkedList.listIterator(0);
    while (listIterator.hasNext()) {
      PolicyParser.PrincipalEntry principalEntry = (PolicyParser.PrincipalEntry)listIterator.next();
      if ((paramPrincipalEntry.getPrincipalClass().equals("WILDCARD_PRINCIPAL_CLASS") || paramPrincipalEntry.getPrincipalClass().equals(principalEntry.getPrincipalClass())) && (paramPrincipalEntry.getPrincipalName().equals("WILDCARD_PRINCIPAL_NAME") || paramPrincipalEntry.getPrincipalName().equals(principalEntry.getPrincipalName())))
        return true; 
    } 
    return false;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!super.equals(paramObject))
      return false; 
    if (!(paramObject instanceof SubjectCodeSource))
      return false; 
    SubjectCodeSource subjectCodeSource = (SubjectCodeSource)paramObject;
    try {
      if (getSubject() != subjectCodeSource.getSubject())
        return false; 
    } catch (SecurityException securityException) {
      return false;
    } 
    return ((this.principals == null && subjectCodeSource.principals != null) || (this.principals != null && subjectCodeSource.principals == null)) ? false : (!(this.principals != null && subjectCodeSource.principals != null && (!this.principals.containsAll(subjectCodeSource.principals) || !subjectCodeSource.principals.containsAll(this.principals))));
  }
  
  public int hashCode() { return super.hashCode(); }
  
  public String toString() {
    String str = super.toString();
    if (getSubject() != null)
      if (debug != null) {
        final Subject finalSubject = getSubject();
        str = str + "\n" + (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
              public String run() { return finalSubject.toString(); }
            });
      } else {
        str = str + "\n" + getSubject().toString();
      }  
    if (this.principals != null) {
      ListIterator listIterator = this.principals.listIterator();
      while (listIterator.hasNext()) {
        PolicyParser.PrincipalEntry principalEntry = (PolicyParser.PrincipalEntry)listIterator.next();
        str = str + rb.getString("NEWLINE") + principalEntry.getPrincipalClass() + " " + principalEntry.getPrincipalName();
      } 
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\SubjectCodeSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */