package javax.management.relation;

import com.sun.jmx.mbeanserver.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoleList extends ArrayList<Object> {
  private boolean typeSafe;
  
  private boolean tainted;
  
  private static final long serialVersionUID = 5568344346499649313L;
  
  public RoleList() {}
  
  public RoleList(int paramInt) { super(paramInt); }
  
  public RoleList(List<Role> paramList) throws IllegalArgumentException {
    if (paramList == null)
      throw new IllegalArgumentException("Null parameter"); 
    checkTypeSafe(paramList);
    super.addAll(paramList);
  }
  
  public List<Role> asList() {
    if (!this.typeSafe) {
      if (this.tainted)
        checkTypeSafe(this); 
      this.typeSafe = true;
    } 
    return (List)Util.cast(this);
  }
  
  public void add(Role paramRole) throws IllegalArgumentException {
    if (paramRole == null) {
      String str = "Invalid parameter";
      throw new IllegalArgumentException(str);
    } 
    super.add(paramRole);
  }
  
  public void add(int paramInt, Role paramRole) throws IllegalArgumentException, IndexOutOfBoundsException {
    if (paramRole == null) {
      String str = "Invalid parameter";
      throw new IllegalArgumentException(str);
    } 
    super.add(paramInt, paramRole);
  }
  
  public void set(int paramInt, Role paramRole) throws IllegalArgumentException, IndexOutOfBoundsException {
    if (paramRole == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    super.set(paramInt, paramRole);
  }
  
  public boolean addAll(RoleList paramRoleList) throws IndexOutOfBoundsException { return (paramRoleList == null) ? true : super.addAll(paramRoleList); }
  
  public boolean addAll(int paramInt, RoleList paramRoleList) throws IllegalArgumentException, IndexOutOfBoundsException {
    if (paramRoleList == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    return super.addAll(paramInt, paramRoleList);
  }
  
  public boolean add(Object paramObject) {
    if (!this.tainted)
      this.tainted = isTainted(paramObject); 
    if (this.typeSafe)
      checkTypeSafe(paramObject); 
    return super.add(paramObject);
  }
  
  public void add(int paramInt, Object paramObject) {
    if (!this.tainted)
      this.tainted = isTainted(paramObject); 
    if (this.typeSafe)
      checkTypeSafe(paramObject); 
    super.add(paramInt, paramObject);
  }
  
  public boolean addAll(Collection<?> paramCollection) {
    if (!this.tainted)
      this.tainted = isTainted(paramCollection); 
    if (this.typeSafe)
      checkTypeSafe(paramCollection); 
    return super.addAll(paramCollection);
  }
  
  public boolean addAll(int paramInt, Collection<?> paramCollection) {
    if (!this.tainted)
      this.tainted = isTainted(paramCollection); 
    if (this.typeSafe)
      checkTypeSafe(paramCollection); 
    return super.addAll(paramInt, paramCollection);
  }
  
  public Object set(int paramInt, Object paramObject) {
    if (!this.tainted)
      this.tainted = isTainted(paramObject); 
    if (this.typeSafe)
      checkTypeSafe(paramObject); 
    return super.set(paramInt, paramObject);
  }
  
  private static void checkTypeSafe(Object paramObject) {
    try {
      paramObject = (Role)paramObject;
    } catch (ClassCastException classCastException) {
      throw new IllegalArgumentException(classCastException);
    } 
  }
  
  private static void checkTypeSafe(Collection<?> paramCollection) {
    try {
      for (Object object : paramCollection)
        Role role = (Role)object; 
    } catch (ClassCastException classCastException) {
      throw new IllegalArgumentException(classCastException);
    } 
  }
  
  private static boolean isTainted(Object paramObject) {
    try {
      checkTypeSafe(paramObject);
    } catch (IllegalArgumentException illegalArgumentException) {
      return true;
    } 
    return false;
  }
  
  private static boolean isTainted(Collection<?> paramCollection) {
    try {
      checkTypeSafe(paramCollection);
    } catch (IllegalArgumentException illegalArgumentException) {
      return true;
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\relation\RoleList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */