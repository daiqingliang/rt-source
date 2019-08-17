package jdk.internal.org.objectweb.asm.tree.analysis;

import java.util.Set;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;

public class SourceValue implements Value {
  public final int size;
  
  public final Set<AbstractInsnNode> insns;
  
  public SourceValue(int paramInt) { this(paramInt, SmallSet.emptySet()); }
  
  public SourceValue(int paramInt, AbstractInsnNode paramAbstractInsnNode) {
    this.size = paramInt;
    this.insns = new SmallSet(paramAbstractInsnNode, null);
  }
  
  public SourceValue(int paramInt, Set<AbstractInsnNode> paramSet) {
    this.size = paramInt;
    this.insns = paramSet;
  }
  
  public int getSize() { return this.size; }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof SourceValue))
      return false; 
    SourceValue sourceValue = (SourceValue)paramObject;
    return (this.size == sourceValue.size && this.insns.equals(sourceValue.insns));
  }
  
  public int hashCode() { return this.insns.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\SourceValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */