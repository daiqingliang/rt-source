package jdk.internal.org.objectweb.asm.tree.analysis;

import java.util.ArrayList;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.IincInsnNode;
import jdk.internal.org.objectweb.asm.tree.InvokeDynamicInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode;
import jdk.internal.org.objectweb.asm.tree.MultiANewArrayInsnNode;
import jdk.internal.org.objectweb.asm.tree.VarInsnNode;

public class Frame<V extends Value> extends Object {
  private V returnValue;
  
  private V[] values;
  
  private int locals;
  
  private int top;
  
  public Frame(int paramInt1, int paramInt2) {
    this.values = (Value[])new Value[paramInt1 + paramInt2];
    this.locals = paramInt1;
  }
  
  public Frame(Frame<? extends V> paramFrame) {
    this(paramFrame.locals, paramFrame.values.length - paramFrame.locals);
    init(paramFrame);
  }
  
  public Frame<V> init(Frame<? extends V> paramFrame) {
    this.returnValue = paramFrame.returnValue;
    System.arraycopy(paramFrame.values, 0, this.values, 0, this.values.length);
    this.top = paramFrame.top;
    return this;
  }
  
  public void setReturn(V paramV) { this.returnValue = paramV; }
  
  public int getLocals() { return this.locals; }
  
  public int getMaxStackSize() { return this.values.length - this.locals; }
  
  public V getLocal(int paramInt) throws IndexOutOfBoundsException {
    if (paramInt >= this.locals)
      throw new IndexOutOfBoundsException("Trying to access an inexistant local variable"); 
    return (V)this.values[paramInt];
  }
  
  public void setLocal(int paramInt, V paramV) throws IndexOutOfBoundsException {
    if (paramInt >= this.locals)
      throw new IndexOutOfBoundsException("Trying to access an inexistant local variable " + paramInt); 
    this.values[paramInt] = paramV;
  }
  
  public int getStackSize() { return this.top; }
  
  public V getStack(int paramInt) throws IndexOutOfBoundsException { return (V)this.values[paramInt + this.locals]; }
  
  public void clearStack() { this.top = 0; }
  
  public V pop() throws IndexOutOfBoundsException {
    if (this.top == 0)
      throw new IndexOutOfBoundsException("Cannot pop operand off an empty stack."); 
    return (V)this.values[--this.top + this.locals];
  }
  
  public void push(V paramV) {
    if (this.top + this.locals >= this.values.length)
      throw new IndexOutOfBoundsException("Insufficient maximum stack size."); 
    this.values[this.top++ + this.locals] = paramV;
  }
  
  public void execute(AbstractInsnNode paramAbstractInsnNode, Interpreter<V> paramInterpreter) throws AnalyzerException {
    int k;
    int j;
    String str;
    int i;
    ArrayList arrayList;
    Value value3;
    Value value2;
    Value value1;
    switch (paramAbstractInsnNode.getOpcode()) {
      case 0:
        return;
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
        push(paramInterpreter.newOperation(paramAbstractInsnNode));
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
        push(paramInterpreter.copyOperation(paramAbstractInsnNode, getLocal(((VarInsnNode)paramAbstractInsnNode).var)));
      case 46:
      case 47:
      case 48:
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
        value2 = pop();
        value1 = pop();
        push(paramInterpreter.binaryOperation(paramAbstractInsnNode, value1, value2));
      case 54:
      case 55:
      case 56:
      case 57:
      case 58:
        value1 = paramInterpreter.copyOperation(paramAbstractInsnNode, pop());
        i = ((VarInsnNode)paramAbstractInsnNode).var;
        setLocal(i, value1);
        if (value1.getSize() == 2)
          setLocal(i + 1, paramInterpreter.newValue(null)); 
        if (i > 0) {
          Value value = getLocal(i - 1);
          if (value != null && value.getSize() == 2)
            setLocal(i - 1, paramInterpreter.newValue(null)); 
        } 
      case 79:
      case 80:
      case 81:
      case 82:
      case 83:
      case 84:
      case 85:
      case 86:
        value3 = pop();
        value2 = pop();
        value1 = pop();
        paramInterpreter.ternaryOperation(paramAbstractInsnNode, value1, value2, value3);
      case 87:
        if (pop().getSize() == 2)
          throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of POP"); 
      case 88:
        if (pop().getSize() == 1 && pop().getSize() != 1)
          throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of POP2"); 
      case 89:
        value1 = pop();
        if (value1.getSize() != 1)
          throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP"); 
        push(value1);
        push(paramInterpreter.copyOperation(paramAbstractInsnNode, value1));
      case 90:
        value1 = pop();
        value2 = pop();
        if (value1.getSize() != 1 || value2.getSize() != 1)
          throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP_X1"); 
        push(paramInterpreter.copyOperation(paramAbstractInsnNode, value1));
        push(value2);
        push(value1);
      case 91:
        value1 = pop();
        if (value1.getSize() == 1) {
          value2 = pop();
          if (value2.getSize() == 1) {
            value3 = pop();
            if (value3.getSize() == 1) {
              push(paramInterpreter.copyOperation(paramAbstractInsnNode, value1));
              push(value3);
              push(value2);
              push(value1);
            } else {
              throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP_X2");
            } 
          } else {
            push(paramInterpreter.copyOperation(paramAbstractInsnNode, value1));
            push(value2);
            push(value1);
          } 
        } else {
          throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP_X2");
        } 
      case 92:
        value1 = pop();
        if (value1.getSize() == 1) {
          value2 = pop();
          if (value2.getSize() == 1) {
            push(value2);
            push(value1);
            push(paramInterpreter.copyOperation(paramAbstractInsnNode, value2));
            push(paramInterpreter.copyOperation(paramAbstractInsnNode, value1));
          } else {
            throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP2");
          } 
        } else {
          push(value1);
          push(paramInterpreter.copyOperation(paramAbstractInsnNode, value1));
        } 
      case 93:
        value1 = pop();
        if (value1.getSize() == 1) {
          value2 = pop();
          if (value2.getSize() == 1) {
            value3 = pop();
            if (value3.getSize() == 1) {
              push(paramInterpreter.copyOperation(paramAbstractInsnNode, value2));
              push(paramInterpreter.copyOperation(paramAbstractInsnNode, value1));
              push(value3);
              push(value2);
              push(value1);
            } else {
              throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP2_X1");
            } 
          } else {
            throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP2_X1");
          } 
        } else {
          value2 = pop();
          if (value2.getSize() == 1) {
            push(paramInterpreter.copyOperation(paramAbstractInsnNode, value1));
            push(value2);
            push(value1);
          } else {
            throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP2_X1");
          } 
        } 
      case 94:
        value1 = pop();
        if (value1.getSize() == 1) {
          value2 = pop();
          if (value2.getSize() == 1) {
            value3 = pop();
            if (value3.getSize() == 1) {
              Value value = pop();
              if (value.getSize() == 1) {
                push(paramInterpreter.copyOperation(paramAbstractInsnNode, value2));
                push(paramInterpreter.copyOperation(paramAbstractInsnNode, value1));
                push(value);
                push(value3);
                push(value2);
                push(value1);
              } else {
                throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP2_X2");
              } 
            } else {
              push(paramInterpreter.copyOperation(paramAbstractInsnNode, value2));
              push(paramInterpreter.copyOperation(paramAbstractInsnNode, value1));
              push(value3);
              push(value2);
              push(value1);
            } 
          } else {
            throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP2_X2");
          } 
        } else {
          value2 = pop();
          if (value2.getSize() == 1) {
            value3 = pop();
            if (value3.getSize() == 1) {
              push(paramInterpreter.copyOperation(paramAbstractInsnNode, value1));
              push(value3);
              push(value2);
              push(value1);
            } else {
              throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP2_X2");
            } 
          } else {
            push(paramInterpreter.copyOperation(paramAbstractInsnNode, value1));
            push(value2);
            push(value1);
          } 
        } 
      case 95:
        value2 = pop();
        value1 = pop();
        if (value1.getSize() != 1 || value2.getSize() != 1)
          throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of SWAP"); 
        push(paramInterpreter.copyOperation(paramAbstractInsnNode, value2));
        push(paramInterpreter.copyOperation(paramAbstractInsnNode, value1));
      case 96:
      case 97:
      case 98:
      case 99:
      case 100:
      case 101:
      case 102:
      case 103:
      case 104:
      case 105:
      case 106:
      case 107:
      case 108:
      case 109:
      case 110:
      case 111:
      case 112:
      case 113:
      case 114:
      case 115:
        value2 = pop();
        value1 = pop();
        push(paramInterpreter.binaryOperation(paramAbstractInsnNode, value1, value2));
      case 116:
      case 117:
      case 118:
      case 119:
        push(paramInterpreter.unaryOperation(paramAbstractInsnNode, pop()));
      case 120:
      case 121:
      case 122:
      case 123:
      case 124:
      case 125:
      case 126:
      case 127:
      case 128:
      case 129:
      case 130:
      case 131:
        value2 = pop();
        value1 = pop();
        push(paramInterpreter.binaryOperation(paramAbstractInsnNode, value1, value2));
      case 132:
        i = ((IincInsnNode)paramAbstractInsnNode).var;
        setLocal(i, paramInterpreter.unaryOperation(paramAbstractInsnNode, getLocal(i)));
      case 133:
      case 134:
      case 135:
      case 136:
      case 137:
      case 138:
      case 139:
      case 140:
      case 141:
      case 142:
      case 143:
      case 144:
      case 145:
      case 146:
      case 147:
        push(paramInterpreter.unaryOperation(paramAbstractInsnNode, pop()));
      case 148:
      case 149:
      case 150:
      case 151:
      case 152:
        value2 = pop();
        value1 = pop();
        push(paramInterpreter.binaryOperation(paramAbstractInsnNode, value1, value2));
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
        paramInterpreter.unaryOperation(paramAbstractInsnNode, pop());
      case 159:
      case 160:
      case 161:
      case 162:
      case 163:
      case 164:
      case 165:
      case 166:
        value2 = pop();
        value1 = pop();
        paramInterpreter.binaryOperation(paramAbstractInsnNode, value1, value2);
      case 167:
        return;
      case 168:
        push(paramInterpreter.newOperation(paramAbstractInsnNode));
      case 169:
        return;
      case 170:
      case 171:
        paramInterpreter.unaryOperation(paramAbstractInsnNode, pop());
      case 172:
      case 173:
      case 174:
      case 175:
      case 176:
        value1 = pop();
        paramInterpreter.unaryOperation(paramAbstractInsnNode, value1);
        paramInterpreter.returnOperation(paramAbstractInsnNode, value1, this.returnValue);
      case 177:
        if (this.returnValue != null)
          throw new AnalyzerException(paramAbstractInsnNode, "Incompatible return type"); 
      case 178:
        push(paramInterpreter.newOperation(paramAbstractInsnNode));
      case 179:
        paramInterpreter.unaryOperation(paramAbstractInsnNode, pop());
      case 180:
        push(paramInterpreter.unaryOperation(paramAbstractInsnNode, pop()));
      case 181:
        value2 = pop();
        value1 = pop();
        paramInterpreter.binaryOperation(paramAbstractInsnNode, value1, value2);
      case 182:
      case 183:
      case 184:
      case 185:
        arrayList = new ArrayList();
        str = ((MethodInsnNode)paramAbstractInsnNode).desc;
        for (k = Type.getArgumentTypes(str).length; k > 0; k--)
          arrayList.add(0, pop()); 
        if (paramAbstractInsnNode.getOpcode() != 184)
          arrayList.add(0, pop()); 
        if (Type.getReturnType(str) == Type.VOID_TYPE) {
          paramInterpreter.naryOperation(paramAbstractInsnNode, arrayList);
        } else {
          push(paramInterpreter.naryOperation(paramAbstractInsnNode, arrayList));
        } 
      case 186:
        arrayList = new ArrayList();
        str = ((InvokeDynamicInsnNode)paramAbstractInsnNode).desc;
        for (k = Type.getArgumentTypes(str).length; k > 0; k--)
          arrayList.add(0, pop()); 
        if (Type.getReturnType(str) == Type.VOID_TYPE) {
          paramInterpreter.naryOperation(paramAbstractInsnNode, arrayList);
        } else {
          push(paramInterpreter.naryOperation(paramAbstractInsnNode, arrayList));
        } 
      case 187:
        push(paramInterpreter.newOperation(paramAbstractInsnNode));
      case 188:
      case 189:
      case 190:
        push(paramInterpreter.unaryOperation(paramAbstractInsnNode, pop()));
      case 191:
        paramInterpreter.unaryOperation(paramAbstractInsnNode, pop());
      case 192:
      case 193:
        push(paramInterpreter.unaryOperation(paramAbstractInsnNode, pop()));
      case 194:
      case 195:
        paramInterpreter.unaryOperation(paramAbstractInsnNode, pop());
      case 197:
        arrayList = new ArrayList();
        for (j = ((MultiANewArrayInsnNode)paramAbstractInsnNode).dims; j > 0; j--)
          arrayList.add(0, pop()); 
        push(paramInterpreter.naryOperation(paramAbstractInsnNode, arrayList));
      case 198:
      case 199:
        paramInterpreter.unaryOperation(paramAbstractInsnNode, pop());
    } 
    throw new RuntimeException("Illegal opcode " + paramAbstractInsnNode.getOpcode());
  }
  
  public boolean merge(Frame<? extends V> paramFrame, Interpreter<V> paramInterpreter) throws AnalyzerException {
    if (this.top != paramFrame.top)
      throw new AnalyzerException(null, "Incompatible stack heights"); 
    boolean bool = false;
    for (byte b = 0; b < this.locals + this.top; b++) {
      Value value = paramInterpreter.merge(this.values[b], paramFrame.values[b]);
      if (!value.equals(this.values[b])) {
        this.values[b] = value;
        bool = true;
      } 
    } 
    return bool;
  }
  
  public boolean merge(Frame<? extends V> paramFrame, boolean[] paramArrayOfBoolean) {
    boolean bool = false;
    for (byte b = 0; b < this.locals; b++) {
      if (!paramArrayOfBoolean[b] && !this.values[b].equals(paramFrame.values[b])) {
        this.values[b] = paramFrame.values[b];
        bool = true;
      } 
    } 
    return bool;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    byte b;
    for (b = 0; b < getLocals(); b++)
      stringBuilder.append(getLocal(b)); 
    stringBuilder.append(' ');
    for (b = 0; b < getStackSize(); b++)
      stringBuilder.append(getStack(b).toString()); 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\Frame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */