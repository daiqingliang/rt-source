package jdk.internal.org.objectweb.asm.tree.analysis;

import java.util.List;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;

public abstract class Interpreter<V extends Value> extends Object {
  protected final int api;
  
  protected Interpreter(int paramInt) { this.api = paramInt; }
  
  public abstract V newValue(Type paramType);
  
  public abstract V newOperation(AbstractInsnNode paramAbstractInsnNode) throws AnalyzerException;
  
  public abstract V copyOperation(AbstractInsnNode paramAbstractInsnNode, V paramV) throws AnalyzerException;
  
  public abstract V unaryOperation(AbstractInsnNode paramAbstractInsnNode, V paramV) throws AnalyzerException;
  
  public abstract V binaryOperation(AbstractInsnNode paramAbstractInsnNode, V paramV1, V paramV2) throws AnalyzerException;
  
  public abstract V ternaryOperation(AbstractInsnNode paramAbstractInsnNode, V paramV1, V paramV2, V paramV3) throws AnalyzerException;
  
  public abstract V naryOperation(AbstractInsnNode paramAbstractInsnNode, List<? extends V> paramList) throws AnalyzerException;
  
  public abstract void returnOperation(AbstractInsnNode paramAbstractInsnNode, V paramV1, V paramV2) throws AnalyzerException;
  
  public abstract V merge(V paramV1, V paramV2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\Interpreter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */