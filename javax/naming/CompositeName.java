package javax.naming;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Enumeration;

public class CompositeName implements Name {
  private NameImpl impl;
  
  private static final long serialVersionUID = 1667768148915813118L;
  
  protected CompositeName(Enumeration<String> paramEnumeration) { this.impl = new NameImpl(null, paramEnumeration); }
  
  public CompositeName(String paramString) throws InvalidNameException { this.impl = new NameImpl(null, paramString); }
  
  public CompositeName() { this.impl = new NameImpl(null); }
  
  public String toString() { return this.impl.toString(); }
  
  public boolean equals(Object paramObject) { return (paramObject != null && paramObject instanceof CompositeName && this.impl.equals(((CompositeName)paramObject).impl)); }
  
  public int hashCode() { return this.impl.hashCode(); }
  
  public int compareTo(Object paramObject) {
    if (!(paramObject instanceof CompositeName))
      throw new ClassCastException("Not a CompositeName"); 
    return this.impl.compareTo(((CompositeName)paramObject).impl);
  }
  
  public Object clone() { return new CompositeName(getAll()); }
  
  public int size() { return this.impl.size(); }
  
  public boolean isEmpty() { return this.impl.isEmpty(); }
  
  public Enumeration<String> getAll() { return this.impl.getAll(); }
  
  public String get(int paramInt) { return this.impl.get(paramInt); }
  
  public Name getPrefix(int paramInt) {
    Enumeration enumeration = this.impl.getPrefix(paramInt);
    return new CompositeName(enumeration);
  }
  
  public Name getSuffix(int paramInt) {
    Enumeration enumeration = this.impl.getSuffix(paramInt);
    return new CompositeName(enumeration);
  }
  
  public boolean startsWith(Name paramName) { return (paramName instanceof CompositeName) ? this.impl.startsWith(paramName.size(), paramName.getAll()) : 0; }
  
  public boolean endsWith(Name paramName) { return (paramName instanceof CompositeName) ? this.impl.endsWith(paramName.size(), paramName.getAll()) : 0; }
  
  public Name addAll(Name paramName) throws InvalidNameException {
    if (paramName instanceof CompositeName) {
      this.impl.addAll(paramName.getAll());
      return this;
    } 
    throw new InvalidNameException("Not a composite name: " + paramName.toString());
  }
  
  public Name addAll(int paramInt, Name paramName) throws InvalidNameException {
    if (paramName instanceof CompositeName) {
      this.impl.addAll(paramInt, paramName.getAll());
      return this;
    } 
    throw new InvalidNameException("Not a composite name: " + paramName.toString());
  }
  
  public Name add(String paramString) throws InvalidNameException {
    this.impl.add(paramString);
    return this;
  }
  
  public Name add(int paramInt, String paramString) throws InvalidNameException {
    this.impl.add(paramInt, paramString);
    return this;
  }
  
  public Object remove(int paramInt) throws InvalidNameException { return this.impl.remove(paramInt); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.writeInt(size());
    Enumeration enumeration = getAll();
    while (enumeration.hasMoreElements())
      paramObjectOutputStream.writeObject(enumeration.nextElement()); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.impl = new NameImpl(null);
    int i = paramObjectInputStream.readInt();
    try {
      while (--i >= 0)
        add((String)paramObjectInputStream.readObject()); 
    } catch (InvalidNameException invalidNameException) {
      throw new StreamCorruptedException("Invalid name");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\CompositeName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */