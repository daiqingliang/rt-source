package jdk.internal.org.objectweb.asm;

public final class Handle {
  final int tag;
  
  final String owner;
  
  final String name;
  
  final String desc;
  
  public Handle(int paramInt, String paramString1, String paramString2, String paramString3) {
    this.tag = paramInt;
    this.owner = paramString1;
    this.name = paramString2;
    this.desc = paramString3;
  }
  
  public int getTag() { return this.tag; }
  
  public String getOwner() { return this.owner; }
  
  public String getName() { return this.name; }
  
  public String getDesc() { return this.desc; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof Handle))
      return false; 
    Handle handle = (Handle)paramObject;
    return (this.tag == handle.tag && this.owner.equals(handle.owner) && this.name.equals(handle.name) && this.desc.equals(handle.desc));
  }
  
  public int hashCode() { return this.tag + this.owner.hashCode() * this.name.hashCode() * this.desc.hashCode(); }
  
  public String toString() { return this.owner + '.' + this.name + this.desc + " (" + this.tag + ')'; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\Handle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */