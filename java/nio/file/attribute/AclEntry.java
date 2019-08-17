package java.nio.file.attribute;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public final class AclEntry {
  private final AclEntryType type;
  
  private final UserPrincipal who;
  
  private final Set<AclEntryPermission> perms;
  
  private final Set<AclEntryFlag> flags;
  
  private AclEntry(AclEntryType paramAclEntryType, UserPrincipal paramUserPrincipal, Set<AclEntryPermission> paramSet1, Set<AclEntryFlag> paramSet2) {
    this.type = paramAclEntryType;
    this.who = paramUserPrincipal;
    this.perms = paramSet1;
    this.flags = paramSet2;
  }
  
  public static Builder newBuilder() {
    Set set1 = Collections.emptySet();
    Set set2 = Collections.emptySet();
    return new Builder(null, null, set1, set2, null);
  }
  
  public static Builder newBuilder(AclEntry paramAclEntry) { return new Builder(paramAclEntry.type, paramAclEntry.who, paramAclEntry.perms, paramAclEntry.flags, null); }
  
  public AclEntryType type() { return this.type; }
  
  public UserPrincipal principal() { return this.who; }
  
  public Set<AclEntryPermission> permissions() { return new HashSet(this.perms); }
  
  public Set<AclEntryFlag> flags() { return new HashSet(this.flags); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject == null || !(paramObject instanceof AclEntry))
      return false; 
    AclEntry aclEntry = (AclEntry)paramObject;
    return (this.type != aclEntry.type) ? false : (!this.who.equals(aclEntry.who) ? false : (!this.perms.equals(aclEntry.perms) ? false : (!!this.flags.equals(aclEntry.flags))));
  }
  
  private static int hash(int paramInt, Object paramObject) { return paramInt * 127 + paramObject.hashCode(); }
  
  public int hashCode() {
    if (this.hash != 0)
      return this.hash; 
    int i = this.type.hashCode();
    i = hash(i, this.who);
    i = hash(i, this.perms);
    i = hash(i, this.flags);
    this.hash = i;
    return this.hash;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(this.who.getName());
    stringBuilder.append(':');
    for (AclEntryPermission aclEntryPermission : this.perms) {
      stringBuilder.append(aclEntryPermission.name());
      stringBuilder.append('/');
    } 
    stringBuilder.setLength(stringBuilder.length() - 1);
    stringBuilder.append(':');
    if (!this.flags.isEmpty()) {
      for (AclEntryFlag aclEntryFlag : this.flags) {
        stringBuilder.append(aclEntryFlag.name());
        stringBuilder.append('/');
      } 
      stringBuilder.setLength(stringBuilder.length() - 1);
      stringBuilder.append(':');
    } 
    stringBuilder.append(this.type.name());
    return stringBuilder.toString();
  }
  
  public static final class Builder {
    private AclEntryType type;
    
    private UserPrincipal who;
    
    private Set<AclEntryPermission> perms;
    
    private Set<AclEntryFlag> flags;
    
    private Builder(AclEntryType param1AclEntryType, UserPrincipal param1UserPrincipal, Set<AclEntryPermission> param1Set1, Set<AclEntryFlag> param1Set2) {
      assert param1Set1 != null && param1Set2 != null;
      this.type = param1AclEntryType;
      this.who = param1UserPrincipal;
      this.perms = param1Set1;
      this.flags = param1Set2;
    }
    
    public AclEntry build() {
      if (this.type == null)
        throw new IllegalStateException("Missing type component"); 
      if (this.who == null)
        throw new IllegalStateException("Missing who component"); 
      return new AclEntry(this.type, this.who, this.perms, this.flags, null);
    }
    
    public Builder setType(AclEntryType param1AclEntryType) {
      if (param1AclEntryType == null)
        throw new NullPointerException(); 
      this.type = param1AclEntryType;
      return this;
    }
    
    public Builder setPrincipal(UserPrincipal param1UserPrincipal) {
      if (param1UserPrincipal == null)
        throw new NullPointerException(); 
      this.who = param1UserPrincipal;
      return this;
    }
    
    private static void checkSet(Set<?> param1Set, Class<?> param1Class) {
      for (Object object : param1Set) {
        if (object == null)
          throw new NullPointerException(); 
        param1Class.cast(object);
      } 
    }
    
    public Builder setPermissions(Set<AclEntryPermission> param1Set) {
      if (param1Set.isEmpty()) {
        param1Set = Collections.emptySet();
      } else {
        param1Set = EnumSet.copyOf(param1Set);
        checkSet(param1Set, AclEntryPermission.class);
      } 
      this.perms = param1Set;
      return this;
    }
    
    public Builder setPermissions(AclEntryPermission... param1VarArgs) {
      EnumSet enumSet = EnumSet.noneOf(AclEntryPermission.class);
      for (AclEntryPermission aclEntryPermission : param1VarArgs) {
        if (aclEntryPermission == null)
          throw new NullPointerException(); 
        enumSet.add(aclEntryPermission);
      } 
      this.perms = enumSet;
      return this;
    }
    
    public Builder setFlags(Set<AclEntryFlag> param1Set) {
      if (param1Set.isEmpty()) {
        param1Set = Collections.emptySet();
      } else {
        param1Set = EnumSet.copyOf(param1Set);
        checkSet(param1Set, AclEntryFlag.class);
      } 
      this.flags = param1Set;
      return this;
    }
    
    public Builder setFlags(AclEntryFlag... param1VarArgs) {
      EnumSet enumSet = EnumSet.noneOf(AclEntryFlag.class);
      for (AclEntryFlag aclEntryFlag : param1VarArgs) {
        if (aclEntryFlag == null)
          throw new NullPointerException(); 
        enumSet.add(aclEntryFlag);
      } 
      this.flags = enumSet;
      return this;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\attribute\AclEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */