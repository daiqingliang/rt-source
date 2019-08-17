package javax.security.auth;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.DomainCombiner;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.text.MessageFormat;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import sun.security.util.ResourcesMgr;

public final class Subject implements Serializable {
  private static final long serialVersionUID = -8308522755600156056L;
  
  Set<Principal> principals;
  
  Set<Object> pubCredentials;
  
  Set<Object> privCredentials;
  
  private static final int PRINCIPAL_SET = 1;
  
  private static final int PUB_CREDENTIAL_SET = 2;
  
  private static final int PRIV_CREDENTIAL_SET = 3;
  
  private static final ProtectionDomain[] NULL_PD_ARRAY = new ProtectionDomain[0];
  
  public Subject() {
    this.principals = Collections.synchronizedSet(new SecureSet(this, 1));
    this.pubCredentials = Collections.synchronizedSet(new SecureSet(this, 2));
    this.privCredentials = Collections.synchronizedSet(new SecureSet(this, 3));
  }
  
  public Subject(boolean paramBoolean, Set<? extends Principal> paramSet1, Set<?> paramSet2, Set<?> paramSet3) {
    if (paramSet1 == null || paramSet2 == null || paramSet3 == null)
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.input.s.")); 
    this.principals = Collections.synchronizedSet(new SecureSet(this, 1, paramSet1));
    this.pubCredentials = Collections.synchronizedSet(new SecureSet(this, 2, paramSet2));
    this.privCredentials = Collections.synchronizedSet(new SecureSet(this, 3, paramSet3));
    this.readOnly = paramBoolean;
  }
  
  public void setReadOnly() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(AuthPermissionHolder.SET_READ_ONLY_PERMISSION); 
    this.readOnly = true;
  }
  
  public boolean isReadOnly() { return this.readOnly; }
  
  public static Subject getSubject(final AccessControlContext acc) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(AuthPermissionHolder.GET_SUBJECT_PERMISSION); 
    if (paramAccessControlContext == null)
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.AccessControlContext.provided")); 
    return (Subject)AccessController.doPrivileged(new PrivilegedAction<Subject>() {
          public Subject run() {
            DomainCombiner domainCombiner = acc.getDomainCombiner();
            if (!(domainCombiner instanceof SubjectDomainCombiner))
              return null; 
            SubjectDomainCombiner subjectDomainCombiner = (SubjectDomainCombiner)domainCombiner;
            return subjectDomainCombiner.getSubject();
          }
        });
  }
  
  public static <T> T doAs(Subject paramSubject, PrivilegedAction<T> paramPrivilegedAction) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(AuthPermissionHolder.DO_AS_PERMISSION); 
    if (paramPrivilegedAction == null)
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided")); 
    AccessControlContext accessControlContext = AccessController.getContext();
    return (T)AccessController.doPrivileged(paramPrivilegedAction, createContext(paramSubject, accessControlContext));
  }
  
  public static <T> T doAs(Subject paramSubject, PrivilegedExceptionAction<T> paramPrivilegedExceptionAction) throws PrivilegedActionException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(AuthPermissionHolder.DO_AS_PERMISSION); 
    if (paramPrivilegedExceptionAction == null)
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided")); 
    AccessControlContext accessControlContext = AccessController.getContext();
    return (T)AccessController.doPrivileged(paramPrivilegedExceptionAction, createContext(paramSubject, accessControlContext));
  }
  
  public static <T> T doAsPrivileged(Subject paramSubject, PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(AuthPermissionHolder.DO_AS_PRIVILEGED_PERMISSION); 
    if (paramPrivilegedAction == null)
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided")); 
    AccessControlContext accessControlContext = (paramAccessControlContext == null) ? new AccessControlContext(NULL_PD_ARRAY) : paramAccessControlContext;
    return (T)AccessController.doPrivileged(paramPrivilegedAction, createContext(paramSubject, accessControlContext));
  }
  
  public static <T> T doAsPrivileged(Subject paramSubject, PrivilegedExceptionAction<T> paramPrivilegedExceptionAction, AccessControlContext paramAccessControlContext) throws PrivilegedActionException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(AuthPermissionHolder.DO_AS_PRIVILEGED_PERMISSION); 
    if (paramPrivilegedExceptionAction == null)
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided")); 
    AccessControlContext accessControlContext = (paramAccessControlContext == null) ? new AccessControlContext(NULL_PD_ARRAY) : paramAccessControlContext;
    return (T)AccessController.doPrivileged(paramPrivilegedExceptionAction, createContext(paramSubject, accessControlContext));
  }
  
  private static AccessControlContext createContext(final Subject subject, final AccessControlContext acc) { return (AccessControlContext)AccessController.doPrivileged(new PrivilegedAction<AccessControlContext>() {
          public AccessControlContext run() { return (subject == null) ? new AccessControlContext(acc, null) : new AccessControlContext(acc, new SubjectDomainCombiner(subject)); }
        }); }
  
  public Set<Principal> getPrincipals() { return this.principals; }
  
  public <T extends Principal> Set<T> getPrincipals(Class<T> paramClass) {
    if (paramClass == null)
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.Class.provided")); 
    return new ClassSet(1, paramClass);
  }
  
  public Set<Object> getPublicCredentials() { return this.pubCredentials; }
  
  public Set<Object> getPrivateCredentials() { return this.privCredentials; }
  
  public <T> Set<T> getPublicCredentials(Class<T> paramClass) {
    if (paramClass == null)
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.Class.provided")); 
    return new ClassSet(2, paramClass);
  }
  
  public <T> Set<T> getPrivateCredentials(Class<T> paramClass) {
    if (paramClass == null)
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.Class.provided")); 
    return new ClassSet(3, paramClass);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (paramObject instanceof Subject) {
      HashSet hashSet3;
      HashSet hashSet2;
      HashSet hashSet1;
      Subject subject = (Subject)paramObject;
      synchronized (subject.principals) {
        hashSet1 = new HashSet(subject.principals);
      } 
      if (!this.principals.equals(hashSet1))
        return false; 
      synchronized (subject.pubCredentials) {
        hashSet2 = new HashSet(subject.pubCredentials);
      } 
      if (!this.pubCredentials.equals(hashSet2))
        return false; 
      synchronized (subject.privCredentials) {
        hashSet3 = new HashSet(subject.privCredentials);
      } 
      return !!this.privCredentials.equals(hashSet3);
    } 
    return false;
  }
  
  public String toString() { return toString(true); }
  
  String toString(boolean paramBoolean) {
    String str1 = ResourcesMgr.getString("Subject.");
    String str2 = "";
    synchronized (this.principals) {
      for (Principal principal : this.principals)
        str2 = str2 + ResourcesMgr.getString(".Principal.") + principal.toString() + ResourcesMgr.getString("NEWLINE"); 
    } 
    synchronized (this.pubCredentials) {
      for (Object object : this.pubCredentials)
        str2 = str2 + ResourcesMgr.getString(".Public.Credential.") + object.toString() + ResourcesMgr.getString("NEWLINE"); 
    } 
    if (paramBoolean)
      synchronized (this.privCredentials) {
        Iterator iterator = this.privCredentials.iterator();
        while (iterator.hasNext()) {
          try {
            Object object = iterator.next();
            str2 = str2 + ResourcesMgr.getString(".Private.Credential.") + object.toString() + ResourcesMgr.getString("NEWLINE");
          } catch (SecurityException securityException) {
            str2 = str2 + ResourcesMgr.getString(".Private.Credential.inaccessible.");
            break;
          } 
        } 
      }  
    return str1 + str2;
  }
  
  public int hashCode() {
    int i = 0;
    synchronized (this.principals) {
      for (Principal principal : this.principals)
        i ^= principal.hashCode(); 
    } 
    synchronized (this.pubCredentials) {
      Iterator iterator = this.pubCredentials.iterator();
      while (iterator.hasNext())
        i ^= getCredHashCode(iterator.next()); 
    } 
    return i;
  }
  
  private int getCredHashCode(Object paramObject) {
    try {
      return paramObject.hashCode();
    } catch (IllegalStateException illegalStateException) {
      return paramObject.getClass().toString().hashCode();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    synchronized (this.principals) {
      paramObjectOutputStream.defaultWriteObject();
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    this.readOnly = getField.get("readOnly", false);
    Set set = (Set)getField.get("principals", null);
    if (set == null)
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.input.s.")); 
    try {
      this.principals = Collections.synchronizedSet(new SecureSet(this, 1, set));
    } catch (NullPointerException nullPointerException) {
      this.principals = Collections.synchronizedSet(new SecureSet(this, 1));
    } 
    this.pubCredentials = Collections.synchronizedSet(new SecureSet(this, 2));
    this.privCredentials = Collections.synchronizedSet(new SecureSet(this, 3));
  }
  
  static class AuthPermissionHolder {
    static final AuthPermission DO_AS_PERMISSION = new AuthPermission("doAs");
    
    static final AuthPermission DO_AS_PRIVILEGED_PERMISSION = new AuthPermission("doAsPrivileged");
    
    static final AuthPermission SET_READ_ONLY_PERMISSION = new AuthPermission("setReadOnly");
    
    static final AuthPermission GET_SUBJECT_PERMISSION = new AuthPermission("getSubject");
    
    static final AuthPermission MODIFY_PRINCIPALS_PERMISSION = new AuthPermission("modifyPrincipals");
    
    static final AuthPermission MODIFY_PUBLIC_CREDENTIALS_PERMISSION = new AuthPermission("modifyPublicCredentials");
    
    static final AuthPermission MODIFY_PRIVATE_CREDENTIALS_PERMISSION = new AuthPermission("modifyPrivateCredentials");
  }
  
  private class ClassSet<T> extends AbstractSet<T> {
    private int which;
    
    private Class<T> c;
    
    private Set<T> set;
    
    ClassSet(int param1Int, Class<T> param1Class) {
      this.which = param1Int;
      this.c = param1Class;
      this.set = new HashSet();
      switch (param1Int) {
        case 1:
          synchronized (Subject.this.principals) {
            populateSet();
          } 
          return;
        case 2:
          synchronized (Subject.this.pubCredentials) {
            populateSet();
          } 
          return;
      } 
      synchronized (Subject.this.privCredentials) {
        populateSet();
      } 
    }
    
    private void populateSet() {
      final Iterator iterator;
      switch (this.which) {
        case 1:
          iterator = Subject.this.principals.iterator();
          break;
        case 2:
          iterator = Subject.this.pubCredentials.iterator();
          break;
        default:
          iterator = Subject.this.privCredentials.iterator();
          break;
      } 
      while (iterator.hasNext()) {
        Object object;
        if (this.which == 3) {
          object = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() { return iterator.next(); }
              });
        } else {
          object = iterator.next();
        } 
        if (this.c.isAssignableFrom(object.getClass())) {
          if (this.which != 3) {
            this.set.add(object);
            continue;
          } 
          SecurityManager securityManager = System.getSecurityManager();
          if (securityManager != null)
            securityManager.checkPermission(new PrivateCredentialPermission(object.getClass().getName(), Subject.this.getPrincipals())); 
          this.set.add(object);
        } 
      } 
    }
    
    public int size() { return this.set.size(); }
    
    public Iterator<T> iterator() { return this.set.iterator(); }
    
    public boolean add(T param1T) {
      if (!param1T.getClass().isAssignableFrom(this.c)) {
        MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("attempting.to.add.an.object.which.is.not.an.instance.of.class"));
        Object[] arrayOfObject = { this.c.toString() };
        throw new SecurityException(messageFormat.format(arrayOfObject));
      } 
      return this.set.add(param1T);
    }
  }
  
  private static class SecureSet<E> extends AbstractSet<E> implements Serializable {
    private static final long serialVersionUID = 7911754171111800359L;
    
    private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("this$0", Subject.class), new ObjectStreamField("elements", LinkedList.class), new ObjectStreamField("which", int.class) };
    
    Subject subject;
    
    LinkedList<E> elements;
    
    private int which;
    
    SecureSet(Subject param1Subject, int param1Int) {
      this.subject = param1Subject;
      this.which = param1Int;
      this.elements = new LinkedList();
    }
    
    SecureSet(Subject param1Subject, int param1Int, Set<? extends E> param1Set) {
      this.subject = param1Subject;
      this.which = param1Int;
      this.elements = new LinkedList(param1Set);
    }
    
    public int size() { return this.elements.size(); }
    
    public Iterator<E> iterator() {
      final LinkedList list = this.elements;
      return new Iterator<E>() {
          ListIterator<E> i = list.listIterator(0);
          
          public boolean hasNext() { return this.i.hasNext(); }
          
          public E next() {
            if (Subject.SecureSet.this.which != 3)
              return (E)this.i.next(); 
            SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null)
              try {
                securityManager.checkPermission(new PrivateCredentialPermission(list.get(this.i.nextIndex()).getClass().getName(), Subject.SecureSet.this.subject.getPrincipals()));
              } catch (SecurityException securityException) {
                this.i.next();
                throw securityException;
              }  
            return (E)this.i.next();
          }
          
          public void remove() {
            if (Subject.SecureSet.this.subject.isReadOnly())
              throw new IllegalStateException(ResourcesMgr.getString("Subject.is.read.only")); 
            SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null)
              switch (Subject.SecureSet.this.which) {
                case 1:
                  securityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRINCIPALS_PERMISSION);
                  break;
                case 2:
                  securityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PUBLIC_CREDENTIALS_PERMISSION);
                  break;
                default:
                  securityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRIVATE_CREDENTIALS_PERMISSION);
                  break;
              }  
            this.i.remove();
          }
        };
    }
    
    public boolean add(E param1E) {
      if (this.subject.isReadOnly())
        throw new IllegalStateException(ResourcesMgr.getString("Subject.is.read.only")); 
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        switch (this.which) {
          case 1:
            securityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRINCIPALS_PERMISSION);
            break;
          case 2:
            securityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PUBLIC_CREDENTIALS_PERMISSION);
            break;
          default:
            securityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRIVATE_CREDENTIALS_PERMISSION);
            break;
        }  
      switch (this.which) {
        case 1:
          if (!(param1E instanceof Principal))
            throw new SecurityException(ResourcesMgr.getString("attempting.to.add.an.object.which.is.not.an.instance.of.java.security.Principal.to.a.Subject.s.Principal.Set")); 
          break;
      } 
      return !this.elements.contains(param1E) ? this.elements.add(param1E) : 0;
    }
    
    public boolean remove(Object param1Object) {
      final Iterator e = iterator();
      while (iterator.hasNext()) {
        Object object;
        if (this.which != 3) {
          object = iterator.next();
        } else {
          object = AccessController.doPrivileged(new PrivilegedAction<E>() {
                public E run() { return (E)e.next(); }
              });
        } 
        if (object == null) {
          if (param1Object == null) {
            iterator.remove();
            return true;
          } 
          continue;
        } 
        if (object.equals(param1Object)) {
          iterator.remove();
          return true;
        } 
      } 
      return false;
    }
    
    public boolean contains(Object param1Object) {
      final Iterator e = iterator();
      while (iterator.hasNext()) {
        Object object;
        if (this.which != 3) {
          object = iterator.next();
        } else {
          SecurityManager securityManager = System.getSecurityManager();
          if (securityManager != null)
            securityManager.checkPermission(new PrivateCredentialPermission(param1Object.getClass().getName(), this.subject.getPrincipals())); 
          object = AccessController.doPrivileged(new PrivilegedAction<E>() {
                public E run() { return (E)e.next(); }
              });
        } 
        if (object == null) {
          if (param1Object == null)
            return true; 
          continue;
        } 
        if (object.equals(param1Object))
          return true; 
      } 
      return false;
    }
    
    public boolean removeAll(Collection<?> param1Collection) {
      Objects.requireNonNull(param1Collection);
      boolean bool = false;
      final Iterator e = iterator();
      while (iterator.hasNext()) {
        Object object;
        if (this.which != 3) {
          object = iterator.next();
        } else {
          object = AccessController.doPrivileged(new PrivilegedAction<E>() {
                public E run() { return (E)e.next(); }
              });
        } 
        for (Object object1 : param1Collection) {
          if (object == null) {
            if (object1 == null) {
              iterator.remove();
              bool = true;
              break;
            } 
            continue;
          } 
          if (object.equals(object1)) {
            iterator.remove();
            bool = true;
          } 
        } 
      } 
      return bool;
    }
    
    public boolean retainAll(Collection<?> param1Collection) {
      Objects.requireNonNull(param1Collection);
      boolean bool = false;
      boolean bool1 = false;
      final Iterator e = iterator();
      while (iterator.hasNext()) {
        Object object;
        bool1 = false;
        if (this.which != 3) {
          object = iterator.next();
        } else {
          object = AccessController.doPrivileged(new PrivilegedAction<E>() {
                public E run() { return (E)e.next(); }
              });
        } 
        for (Object object1 : param1Collection) {
          if (object == null) {
            if (object1 == null) {
              bool1 = true;
              break;
            } 
            continue;
          } 
          if (object.equals(object1)) {
            bool1 = true;
            break;
          } 
        } 
        if (!bool1) {
          iterator.remove();
          bool1 = false;
          bool = true;
        } 
      } 
      return bool;
    }
    
    public void clear() {
      final Iterator e = iterator();
      while (iterator.hasNext()) {
        if (this.which != 3) {
          Object object = iterator.next();
        } else {
          Object object = AccessController.doPrivileged(new PrivilegedAction<E>() {
                public E run() { return (E)e.next(); }
              });
        } 
        iterator.remove();
      } 
    }
    
    private void writeObject(ObjectOutputStream param1ObjectOutputStream) throws IOException {
      if (this.which == 3) {
        Iterator iterator = iterator();
        while (iterator.hasNext())
          iterator.next(); 
      } 
      ObjectOutputStream.PutField putField = param1ObjectOutputStream.putFields();
      putField.put("this$0", this.subject);
      putField.put("elements", this.elements);
      putField.put("which", this.which);
      param1ObjectOutputStream.writeFields();
    }
    
    private void readObject(ObjectInputStream param1ObjectInputStream) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField getField = param1ObjectInputStream.readFields();
      this.subject = (Subject)getField.get("this$0", null);
      this.which = getField.get("which", 0);
      LinkedList linkedList = (LinkedList)getField.get("elements", null);
      if (linkedList.getClass() != LinkedList.class) {
        this.elements = new LinkedList(linkedList);
      } else {
        this.elements = linkedList;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\Subject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */