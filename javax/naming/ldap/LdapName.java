package javax.naming.ldap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.naming.InvalidNameException;
import javax.naming.Name;

public class LdapName implements Name {
  private List<Rdn> rdns;
  
  private String unparsed;
  
  private static final long serialVersionUID = -1595520034788997356L;
  
  public LdapName(String paramString) throws InvalidNameException {
    this.unparsed = paramString;
    parse();
  }
  
  public LdapName(List<Rdn> paramList) {
    this.rdns = new ArrayList(paramList.size());
    for (byte b = 0; b < paramList.size(); b++) {
      Object object = paramList.get(b);
      if (!(object instanceof Rdn))
        throw new IllegalArgumentException("Entry:" + object + "  not a valid type;list entries must be of type Rdn"); 
      this.rdns.add((Rdn)object);
    } 
  }
  
  private LdapName(String paramString, List<Rdn> paramList, int paramInt1, int paramInt2) {
    this.unparsed = paramString;
    List list = paramList.subList(paramInt1, paramInt2);
    this.rdns = new ArrayList(list);
  }
  
  public int size() { return this.rdns.size(); }
  
  public boolean isEmpty() { return this.rdns.isEmpty(); }
  
  public Enumeration<String> getAll() {
    final Iterator iter = this.rdns.iterator();
    return new Enumeration<String>() {
        public boolean hasMoreElements() { return iter.hasNext(); }
        
        public String nextElement() { return ((Rdn)iter.next()).toString(); }
      };
  }
  
  public String get(int paramInt) { return ((Rdn)this.rdns.get(paramInt)).toString(); }
  
  public Rdn getRdn(int paramInt) { return (Rdn)this.rdns.get(paramInt); }
  
  public Name getPrefix(int paramInt) {
    try {
      return new LdapName(null, this.rdns, 0, paramInt);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IndexOutOfBoundsException("Posn: " + paramInt + ", Size: " + this.rdns.size());
    } 
  }
  
  public Name getSuffix(int paramInt) {
    try {
      return new LdapName(null, this.rdns, paramInt, this.rdns.size());
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IndexOutOfBoundsException("Posn: " + paramInt + ", Size: " + this.rdns.size());
    } 
  }
  
  public boolean startsWith(Name paramName) {
    if (paramName == null)
      return false; 
    int i = this.rdns.size();
    int j = paramName.size();
    return (i >= j && matches(0, j, paramName));
  }
  
  public boolean startsWith(List<Rdn> paramList) {
    if (paramList == null)
      return false; 
    int i = this.rdns.size();
    int j = paramList.size();
    return (i >= j && doesListMatch(0, j, paramList));
  }
  
  public boolean endsWith(Name paramName) {
    if (paramName == null)
      return false; 
    int i = this.rdns.size();
    int j = paramName.size();
    return (i >= j && matches(i - j, i, paramName));
  }
  
  public boolean endsWith(List<Rdn> paramList) {
    if (paramList == null)
      return false; 
    int i = this.rdns.size();
    int j = paramList.size();
    return (i >= j && doesListMatch(i - j, i, paramList));
  }
  
  private boolean doesListMatch(int paramInt1, int paramInt2, List<Rdn> paramList) {
    for (int i = paramInt1; i < paramInt2; i++) {
      if (!((Rdn)this.rdns.get(i)).equals(paramList.get(i - paramInt1)))
        return false; 
    } 
    return true;
  }
  
  private boolean matches(int paramInt1, int paramInt2, Name paramName) {
    if (paramName instanceof LdapName) {
      LdapName ldapName = (LdapName)paramName;
      return doesListMatch(paramInt1, paramInt2, ldapName.rdns);
    } 
    for (int i = paramInt1; i < paramInt2; i++) {
      Rdn rdn;
      String str = paramName.get(i - paramInt1);
      try {
        rdn = (new Rfc2253Parser(str)).parseRdn();
      } catch (InvalidNameException invalidNameException) {
        return false;
      } 
      if (!rdn.equals(this.rdns.get(i)))
        return false; 
    } 
    return true;
  }
  
  public Name addAll(Name paramName) throws InvalidNameException { return addAll(size(), paramName); }
  
  public Name addAll(List<Rdn> paramList) { return addAll(size(), paramList); }
  
  public Name addAll(int paramInt, Name paramName) throws InvalidNameException {
    this.unparsed = null;
    if (paramName instanceof LdapName) {
      LdapName ldapName = (LdapName)paramName;
      this.rdns.addAll(paramInt, ldapName.rdns);
    } else {
      Enumeration enumeration = paramName.getAll();
      while (enumeration.hasMoreElements())
        this.rdns.add(paramInt++, (new Rfc2253Parser((String)enumeration.nextElement())).parseRdn()); 
    } 
    return this;
  }
  
  public Name addAll(int paramInt, List<Rdn> paramList) {
    this.unparsed = null;
    for (int i = 0; i < paramList.size(); i++) {
      Object object = paramList.get(i);
      if (!(object instanceof Rdn))
        throw new IllegalArgumentException("Entry:" + object + "  not a valid type;suffix list entries must be of type Rdn"); 
      this.rdns.add(i + paramInt, (Rdn)object);
    } 
    return this;
  }
  
  public Name add(String paramString) throws InvalidNameException { return add(size(), paramString); }
  
  public Name add(Rdn paramRdn) { return add(size(), paramRdn); }
  
  public Name add(int paramInt, String paramString) throws InvalidNameException {
    Rdn rdn = (new Rfc2253Parser(paramString)).parseRdn();
    this.rdns.add(paramInt, rdn);
    this.unparsed = null;
    return this;
  }
  
  public Name add(int paramInt, Rdn paramRdn) {
    if (paramRdn == null)
      throw new NullPointerException("Cannot set comp to null"); 
    this.rdns.add(paramInt, paramRdn);
    this.unparsed = null;
    return this;
  }
  
  public Object remove(int paramInt) throws InvalidNameException {
    this.unparsed = null;
    return ((Rdn)this.rdns.remove(paramInt)).toString();
  }
  
  public List<Rdn> getRdns() { return Collections.unmodifiableList(this.rdns); }
  
  public Object clone() { return new LdapName(this.unparsed, this.rdns, 0, this.rdns.size()); }
  
  public String toString() {
    if (this.unparsed != null)
      return this.unparsed; 
    StringBuilder stringBuilder = new StringBuilder();
    int i = this.rdns.size();
    if (i - 1 >= 0)
      stringBuilder.append(this.rdns.get(i - 1)); 
    for (int j = i - 2; j >= 0; j--) {
      stringBuilder.append(',');
      stringBuilder.append(this.rdns.get(j));
    } 
    this.unparsed = stringBuilder.toString();
    return this.unparsed;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof LdapName))
      return false; 
    LdapName ldapName = (LdapName)paramObject;
    if (this.rdns.size() != ldapName.rdns.size())
      return false; 
    if (this.unparsed != null && this.unparsed.equalsIgnoreCase(ldapName.unparsed))
      return true; 
    for (byte b = 0; b < this.rdns.size(); b++) {
      Rdn rdn1 = (Rdn)this.rdns.get(b);
      Rdn rdn2 = (Rdn)ldapName.rdns.get(b);
      if (!rdn1.equals(rdn2))
        return false; 
    } 
    return true;
  }
  
  public int compareTo(Object paramObject) {
    if (!(paramObject instanceof LdapName))
      throw new ClassCastException("The obj is not a LdapName"); 
    if (paramObject == this)
      return 0; 
    LdapName ldapName = (LdapName)paramObject;
    if (this.unparsed != null && this.unparsed.equalsIgnoreCase(ldapName.unparsed))
      return 0; 
    int i = Math.min(this.rdns.size(), ldapName.rdns.size());
    for (byte b = 0; b < i; b++) {
      Rdn rdn1 = (Rdn)this.rdns.get(b);
      Rdn rdn2 = (Rdn)ldapName.rdns.get(b);
      int j = rdn1.compareTo(rdn2);
      if (j != 0)
        return j; 
    } 
    return this.rdns.size() - ldapName.rdns.size();
  }
  
  public int hashCode() {
    int i = 0;
    for (byte b = 0; b < this.rdns.size(); b++) {
      Rdn rdn = (Rdn)this.rdns.get(b);
      i += rdn.hashCode();
    } 
    return i;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(toString());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.unparsed = (String)paramObjectInputStream.readObject();
    try {
      parse();
    } catch (InvalidNameException invalidNameException) {
      throw new StreamCorruptedException("Invalid name: " + this.unparsed);
    } 
  }
  
  private void parse() throws InvalidNameException { this.rdns = (new Rfc2253Parser(this.unparsed)).parseDn(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\LdapName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */