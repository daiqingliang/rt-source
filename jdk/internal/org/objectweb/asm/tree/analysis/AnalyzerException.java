package jdk.internal.org.objectweb.asm.tree.analysis;

import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;

public class AnalyzerException extends Exception {
  public final AbstractInsnNode node;
  
  public AnalyzerException(AbstractInsnNode paramAbstractInsnNode, String paramString) {
    super(paramString);
    this.node = paramAbstractInsnNode;
  }
  
  public AnalyzerException(AbstractInsnNode paramAbstractInsnNode, String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
    this.node = paramAbstractInsnNode;
  }
  
  public AnalyzerException(AbstractInsnNode paramAbstractInsnNode, String paramString, Object paramObject, Value paramValue) {
    super(((paramString == null) ? "Expected " : (paramString + ": expected ")) + paramObject + ", but found " + paramValue);
    this.node = paramAbstractInsnNode;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\AnalyzerException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */