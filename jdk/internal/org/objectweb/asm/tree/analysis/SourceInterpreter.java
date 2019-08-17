package jdk.internal.org.objectweb.asm.tree.analysis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.InvokeDynamicInsnNode;
import jdk.internal.org.objectweb.asm.tree.LdcInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode;

public class SourceInterpreter extends Interpreter<SourceValue> implements Opcodes {
  public SourceInterpreter() { super(327680); }
  
  protected SourceInterpreter(int paramInt) { super(paramInt); }
  
  public SourceValue newValue(Type paramType) { return (paramType == Type.VOID_TYPE) ? null : new SourceValue((paramType == null) ? 1 : paramType.getSize()); }
  
  public SourceValue newOperation(AbstractInsnNode paramAbstractInsnNode) {
    Object object;
    switch (paramAbstractInsnNode.getOpcode()) {
      case 9:
      case 10:
      case 14:
      case 15:
        i = 2;
        return new SourceValue(i, paramAbstractInsnNode);
      case 18:
        object = ((LdcInsnNode)paramAbstractInsnNode).cst;
        i = (object instanceof Long || object instanceof Double) ? 2 : 1;
        return new SourceValue(i, paramAbstractInsnNode);
      case 178:
        i = Type.getType(((FieldInsnNode)paramAbstractInsnNode).desc).getSize();
        return new SourceValue(i, paramAbstractInsnNode);
    } 
    int i = 1;
    return new SourceValue(i, paramAbstractInsnNode);
  }
  
  public SourceValue copyOperation(AbstractInsnNode paramAbstractInsnNode, SourceValue paramSourceValue) { return new SourceValue(paramSourceValue.getSize(), paramAbstractInsnNode); }
  
  public SourceValue unaryOperation(AbstractInsnNode paramAbstractInsnNode, SourceValue paramSourceValue) {
    switch (paramAbstractInsnNode.getOpcode()) {
      case 117:
      case 119:
      case 133:
      case 135:
      case 138:
      case 140:
      case 141:
      case 143:
        i = 2;
        return new SourceValue(i, paramAbstractInsnNode);
      case 180:
        i = Type.getType(((FieldInsnNode)paramAbstractInsnNode).desc).getSize();
        return new SourceValue(i, paramAbstractInsnNode);
    } 
    int i = 1;
    return new SourceValue(i, paramAbstractInsnNode);
  }
  
  public SourceValue binaryOperation(AbstractInsnNode paramAbstractInsnNode, SourceValue paramSourceValue1, SourceValue paramSourceValue2) {
    switch (paramAbstractInsnNode.getOpcode()) {
      case 47:
      case 49:
      case 97:
      case 99:
      case 101:
      case 103:
      case 105:
      case 107:
      case 109:
      case 111:
      case 113:
      case 115:
      case 121:
      case 123:
      case 125:
      case 127:
      case 129:
      case 131:
        b = 2;
        return new SourceValue(b, paramAbstractInsnNode);
    } 
    byte b = 1;
    return new SourceValue(b, paramAbstractInsnNode);
  }
  
  public SourceValue ternaryOperation(AbstractInsnNode paramAbstractInsnNode, SourceValue paramSourceValue1, SourceValue paramSourceValue2, SourceValue paramSourceValue3) { return new SourceValue(1, paramAbstractInsnNode); }
  
  public SourceValue naryOperation(AbstractInsnNode paramAbstractInsnNode, List<? extends SourceValue> paramList) {
    int i;
    int j = paramAbstractInsnNode.getOpcode();
    if (j == 197) {
      i = 1;
    } else {
      String str = (j == 186) ? ((InvokeDynamicInsnNode)paramAbstractInsnNode).desc : ((MethodInsnNode)paramAbstractInsnNode).desc;
      i = Type.getReturnType(str).getSize();
    } 
    return new SourceValue(i, paramAbstractInsnNode);
  }
  
  public void returnOperation(AbstractInsnNode paramAbstractInsnNode, SourceValue paramSourceValue1, SourceValue paramSourceValue2) {}
  
  public SourceValue merge(SourceValue paramSourceValue1, SourceValue paramSourceValue2) {
    if (paramSourceValue1.insns instanceof SmallSet && paramSourceValue2.insns instanceof SmallSet) {
      Set set = ((SmallSet)paramSourceValue1.insns).union((SmallSet)paramSourceValue2.insns);
      return (set == paramSourceValue1.insns && paramSourceValue1.size == paramSourceValue2.size) ? paramSourceValue1 : new SourceValue(Math.min(paramSourceValue1.size, paramSourceValue2.size), set);
    } 
    if (paramSourceValue1.size != paramSourceValue2.size || !paramSourceValue1.insns.containsAll(paramSourceValue2.insns)) {
      HashSet hashSet = new HashSet();
      hashSet.addAll(paramSourceValue1.insns);
      hashSet.addAll(paramSourceValue2.insns);
      return new SourceValue(Math.min(paramSourceValue1.size, paramSourceValue2.size), hashSet);
    } 
    return paramSourceValue1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\SourceInterpreter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */