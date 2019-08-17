package jdk.internal.org.objectweb.asm.tree.analysis;

import java.util.List;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.IntInsnNode;
import jdk.internal.org.objectweb.asm.tree.InvokeDynamicInsnNode;
import jdk.internal.org.objectweb.asm.tree.LdcInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode;
import jdk.internal.org.objectweb.asm.tree.MultiANewArrayInsnNode;
import jdk.internal.org.objectweb.asm.tree.TypeInsnNode;

public class BasicInterpreter extends Interpreter<BasicValue> implements Opcodes {
  public BasicInterpreter() { super(327680); }
  
  protected BasicInterpreter(int paramInt) { super(paramInt); }
  
  public BasicValue newValue(Type paramType) {
    if (paramType == null)
      return BasicValue.UNINITIALIZED_VALUE; 
    switch (paramType.getSort()) {
      case 0:
        return null;
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
        return BasicValue.INT_VALUE;
      case 6:
        return BasicValue.FLOAT_VALUE;
      case 7:
        return BasicValue.LONG_VALUE;
      case 8:
        return BasicValue.DOUBLE_VALUE;
      case 9:
      case 10:
        return BasicValue.REFERENCE_VALUE;
    } 
    throw new Error("Internal error");
  }
  
  public BasicValue newOperation(AbstractInsnNode paramAbstractInsnNode) throws AnalyzerException {
    Object object;
    switch (paramAbstractInsnNode.getOpcode()) {
      case 1:
        return newValue(Type.getObjectType("null"));
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
        return BasicValue.INT_VALUE;
      case 9:
      case 10:
        return BasicValue.LONG_VALUE;
      case 11:
      case 12:
      case 13:
        return BasicValue.FLOAT_VALUE;
      case 14:
      case 15:
        return BasicValue.DOUBLE_VALUE;
      case 16:
      case 17:
        return BasicValue.INT_VALUE;
      case 18:
        object = ((LdcInsnNode)paramAbstractInsnNode).cst;
        if (object instanceof Integer)
          return BasicValue.INT_VALUE; 
        if (object instanceof Float)
          return BasicValue.FLOAT_VALUE; 
        if (object instanceof Long)
          return BasicValue.LONG_VALUE; 
        if (object instanceof Double)
          return BasicValue.DOUBLE_VALUE; 
        if (object instanceof String)
          return newValue(Type.getObjectType("java/lang/String")); 
        if (object instanceof Type) {
          int i = ((Type)object).getSort();
          if (i == 10 || i == 9)
            return newValue(Type.getObjectType("java/lang/Class")); 
          if (i == 11)
            return newValue(Type.getObjectType("java/lang/invoke/MethodType")); 
          throw new IllegalArgumentException("Illegal LDC constant " + object);
        } 
        if (object instanceof jdk.internal.org.objectweb.asm.Handle)
          return newValue(Type.getObjectType("java/lang/invoke/MethodHandle")); 
        throw new IllegalArgumentException("Illegal LDC constant " + object);
      case 168:
        return BasicValue.RETURNADDRESS_VALUE;
      case 178:
        return newValue(Type.getType(((FieldInsnNode)paramAbstractInsnNode).desc));
      case 187:
        return newValue(Type.getObjectType(((TypeInsnNode)paramAbstractInsnNode).desc));
    } 
    throw new Error("Internal error.");
  }
  
  public BasicValue copyOperation(AbstractInsnNode paramAbstractInsnNode, BasicValue paramBasicValue) throws AnalyzerException { return paramBasicValue; }
  
  public BasicValue unaryOperation(AbstractInsnNode paramAbstractInsnNode, BasicValue paramBasicValue) throws AnalyzerException {
    String str;
    switch (paramAbstractInsnNode.getOpcode()) {
      case 116:
      case 132:
      case 136:
      case 139:
      case 142:
      case 145:
      case 146:
      case 147:
        return BasicValue.INT_VALUE;
      case 118:
      case 134:
      case 137:
      case 144:
        return BasicValue.FLOAT_VALUE;
      case 117:
      case 133:
      case 140:
      case 143:
        return BasicValue.LONG_VALUE;
      case 119:
      case 135:
      case 138:
      case 141:
        return BasicValue.DOUBLE_VALUE;
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
      case 170:
      case 171:
      case 172:
      case 173:
      case 174:
      case 175:
      case 176:
      case 179:
        return null;
      case 180:
        return newValue(Type.getType(((FieldInsnNode)paramAbstractInsnNode).desc));
      case 188:
        switch (((IntInsnNode)paramAbstractInsnNode).operand) {
          case 4:
            return newValue(Type.getType("[Z"));
          case 5:
            return newValue(Type.getType("[C"));
          case 8:
            return newValue(Type.getType("[B"));
          case 9:
            return newValue(Type.getType("[S"));
          case 10:
            return newValue(Type.getType("[I"));
          case 6:
            return newValue(Type.getType("[F"));
          case 7:
            return newValue(Type.getType("[D"));
          case 11:
            return newValue(Type.getType("[J"));
        } 
        throw new AnalyzerException(paramAbstractInsnNode, "Invalid array type");
      case 189:
        str = ((TypeInsnNode)paramAbstractInsnNode).desc;
        return newValue(Type.getType("[" + Type.getObjectType(str)));
      case 190:
        return BasicValue.INT_VALUE;
      case 191:
        return null;
      case 192:
        str = ((TypeInsnNode)paramAbstractInsnNode).desc;
        return newValue(Type.getObjectType(str));
      case 193:
        return BasicValue.INT_VALUE;
      case 194:
      case 195:
      case 198:
      case 199:
        return null;
    } 
    throw new Error("Internal error.");
  }
  
  public BasicValue binaryOperation(AbstractInsnNode paramAbstractInsnNode, BasicValue paramBasicValue1, BasicValue paramBasicValue2) throws AnalyzerException {
    switch (paramAbstractInsnNode.getOpcode()) {
      case 46:
      case 51:
      case 52:
      case 53:
      case 96:
      case 100:
      case 104:
      case 108:
      case 112:
      case 120:
      case 122:
      case 124:
      case 126:
      case 128:
      case 130:
        return BasicValue.INT_VALUE;
      case 48:
      case 98:
      case 102:
      case 106:
      case 110:
      case 114:
        return BasicValue.FLOAT_VALUE;
      case 47:
      case 97:
      case 101:
      case 105:
      case 109:
      case 113:
      case 121:
      case 123:
      case 125:
      case 127:
      case 129:
      case 131:
        return BasicValue.LONG_VALUE;
      case 49:
      case 99:
      case 103:
      case 107:
      case 111:
      case 115:
        return BasicValue.DOUBLE_VALUE;
      case 50:
        return BasicValue.REFERENCE_VALUE;
      case 148:
      case 149:
      case 150:
      case 151:
      case 152:
        return BasicValue.INT_VALUE;
      case 159:
      case 160:
      case 161:
      case 162:
      case 163:
      case 164:
      case 165:
      case 166:
      case 181:
        return null;
    } 
    throw new Error("Internal error.");
  }
  
  public BasicValue ternaryOperation(AbstractInsnNode paramAbstractInsnNode, BasicValue paramBasicValue1, BasicValue paramBasicValue2, BasicValue paramBasicValue3) throws AnalyzerException { return null; }
  
  public BasicValue naryOperation(AbstractInsnNode paramAbstractInsnNode, List<? extends BasicValue> paramList) throws AnalyzerException {
    int i = paramAbstractInsnNode.getOpcode();
    return (i == 197) ? newValue(Type.getType(((MultiANewArrayInsnNode)paramAbstractInsnNode).desc)) : ((i == 186) ? newValue(Type.getReturnType(((InvokeDynamicInsnNode)paramAbstractInsnNode).desc)) : newValue(Type.getReturnType(((MethodInsnNode)paramAbstractInsnNode).desc)));
  }
  
  public void returnOperation(AbstractInsnNode paramAbstractInsnNode, BasicValue paramBasicValue1, BasicValue paramBasicValue2) throws AnalyzerException {}
  
  public BasicValue merge(BasicValue paramBasicValue1, BasicValue paramBasicValue2) { return !paramBasicValue1.equals(paramBasicValue2) ? BasicValue.UNINITIALIZED_VALUE : paramBasicValue1; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\BasicInterpreter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */