package jdk.internal.org.objectweb.asm.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;

public class AnalyzerAdapter extends MethodVisitor {
  public List<Object> locals;
  
  public List<Object> stack;
  
  private List<Label> labels;
  
  public Map<Object, Object> uninitializedTypes;
  
  private int maxStack;
  
  private int maxLocals;
  
  private String owner;
  
  public AnalyzerAdapter(String paramString1, int paramInt, String paramString2, String paramString3, MethodVisitor paramMethodVisitor) {
    this(327680, paramString1, paramInt, paramString2, paramString3, paramMethodVisitor);
    if (getClass() != AnalyzerAdapter.class)
      throw new IllegalStateException(); 
  }
  
  protected AnalyzerAdapter(int paramInt1, String paramString1, int paramInt2, String paramString2, String paramString3, MethodVisitor paramMethodVisitor) {
    super(paramInt1, paramMethodVisitor);
    this.owner = paramString1;
    this.locals = new ArrayList();
    this.stack = new ArrayList();
    this.uninitializedTypes = new HashMap();
    if ((paramInt2 & 0x8) == 0)
      if ("<init>".equals(paramString2)) {
        this.locals.add(Opcodes.UNINITIALIZED_THIS);
      } else {
        this.locals.add(paramString1);
      }  
    Type[] arrayOfType = Type.getArgumentTypes(paramString3);
    for (byte b = 0; b < arrayOfType.length; b++) {
      Type type = arrayOfType[b];
      switch (type.getSort()) {
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
          this.locals.add(Opcodes.INTEGER);
          break;
        case 6:
          this.locals.add(Opcodes.FLOAT);
          break;
        case 7:
          this.locals.add(Opcodes.LONG);
          this.locals.add(Opcodes.TOP);
          break;
        case 8:
          this.locals.add(Opcodes.DOUBLE);
          this.locals.add(Opcodes.TOP);
          break;
        case 9:
          this.locals.add(arrayOfType[b].getDescriptor());
          break;
        default:
          this.locals.add(arrayOfType[b].getInternalName());
          break;
      } 
    } 
    this.maxLocals = this.locals.size();
  }
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2) {
    if (paramInt1 != -1)
      throw new IllegalStateException("ClassReader.accept() should be called with EXPAND_FRAMES flag"); 
    if (this.mv != null)
      this.mv.visitFrame(paramInt1, paramInt2, paramArrayOfObject1, paramInt3, paramArrayOfObject2); 
    if (this.locals != null) {
      this.locals.clear();
      this.stack.clear();
    } else {
      this.locals = new ArrayList();
      this.stack = new ArrayList();
    } 
    visitFrameTypes(paramInt2, paramArrayOfObject1, this.locals);
    visitFrameTypes(paramInt3, paramArrayOfObject2, this.stack);
    this.maxStack = Math.max(this.maxStack, this.stack.size());
  }
  
  private static void visitFrameTypes(int paramInt, Object[] paramArrayOfObject, List<Object> paramList) {
    for (byte b = 0; b < paramInt; b++) {
      Object object = paramArrayOfObject[b];
      paramList.add(object);
      if (object == Opcodes.LONG || object == Opcodes.DOUBLE)
        paramList.add(Opcodes.TOP); 
    } 
  }
  
  public void visitInsn(int paramInt) {
    if (this.mv != null)
      this.mv.visitInsn(paramInt); 
    execute(paramInt, 0, null);
    if ((paramInt >= 172 && paramInt <= 177) || paramInt == 191) {
      this.locals = null;
      this.stack = null;
    } 
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2) {
    if (this.mv != null)
      this.mv.visitIntInsn(paramInt1, paramInt2); 
    execute(paramInt1, paramInt2, null);
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2) {
    if (this.mv != null)
      this.mv.visitVarInsn(paramInt1, paramInt2); 
    execute(paramInt1, paramInt2, null);
  }
  
  public void visitTypeInsn(int paramInt, String paramString) {
    if (paramInt == 187) {
      if (this.labels == null) {
        Label label = new Label();
        this.labels = new ArrayList(3);
        this.labels.add(label);
        if (this.mv != null)
          this.mv.visitLabel(label); 
      } 
      for (byte b = 0; b < this.labels.size(); b++)
        this.uninitializedTypes.put(this.labels.get(b), paramString); 
    } 
    if (this.mv != null)
      this.mv.visitTypeInsn(paramInt, paramString); 
    execute(paramInt, 0, paramString);
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    if (this.mv != null)
      this.mv.visitFieldInsn(paramInt, paramString1, paramString2, paramString3); 
    execute(paramInt, 0, paramString3);
  }
  
  @Deprecated
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    if (this.api >= 327680) {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
      return;
    } 
    doVisitMethodInsn(paramInt, paramString1, paramString2, paramString3, (paramInt == 185));
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    if (this.api < 327680) {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
      return;
    } 
    doVisitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
  }
  
  private void doVisitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    if (this.mv != null)
      this.mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean); 
    if (this.locals == null) {
      this.labels = null;
      return;
    } 
    pop(paramString3);
    if (paramInt != 184) {
      Object object = pop();
      if (paramInt == 183 && paramString2.charAt(0) == '<') {
        Object object1;
        if (object == Opcodes.UNINITIALIZED_THIS) {
          object1 = this.owner;
        } else {
          object1 = this.uninitializedTypes.get(object);
        } 
        byte b;
        for (b = 0; b < this.locals.size(); b++) {
          if (this.locals.get(b) == object)
            this.locals.set(b, object1); 
        } 
        for (b = 0; b < this.stack.size(); b++) {
          if (this.stack.get(b) == object)
            this.stack.set(b, object1); 
        } 
      } 
    } 
    pushDesc(paramString3);
    this.labels = null;
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs) {
    if (this.mv != null)
      this.mv.visitInvokeDynamicInsn(paramString1, paramString2, paramHandle, paramVarArgs); 
    if (this.locals == null) {
      this.labels = null;
      return;
    } 
    pop(paramString2);
    pushDesc(paramString2);
    this.labels = null;
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel) {
    if (this.mv != null)
      this.mv.visitJumpInsn(paramInt, paramLabel); 
    execute(paramInt, 0, null);
    if (paramInt == 167) {
      this.locals = null;
      this.stack = null;
    } 
  }
  
  public void visitLabel(Label paramLabel) {
    if (this.mv != null)
      this.mv.visitLabel(paramLabel); 
    if (this.labels == null)
      this.labels = new ArrayList(3); 
    this.labels.add(paramLabel);
  }
  
  public void visitLdcInsn(Object paramObject) {
    if (this.mv != null)
      this.mv.visitLdcInsn(paramObject); 
    if (this.locals == null) {
      this.labels = null;
      return;
    } 
    if (paramObject instanceof Integer) {
      push(Opcodes.INTEGER);
    } else if (paramObject instanceof Long) {
      push(Opcodes.LONG);
      push(Opcodes.TOP);
    } else if (paramObject instanceof Float) {
      push(Opcodes.FLOAT);
    } else if (paramObject instanceof Double) {
      push(Opcodes.DOUBLE);
      push(Opcodes.TOP);
    } else if (paramObject instanceof String) {
      push("java/lang/String");
    } else if (paramObject instanceof Type) {
      int i = ((Type)paramObject).getSort();
      if (i == 10 || i == 9) {
        push("java/lang/Class");
      } else if (i == 11) {
        push("java/lang/invoke/MethodType");
      } else {
        throw new IllegalArgumentException();
      } 
    } else if (paramObject instanceof Handle) {
      push("java/lang/invoke/MethodHandle");
    } else {
      throw new IllegalArgumentException();
    } 
    this.labels = null;
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2) {
    if (this.mv != null)
      this.mv.visitIincInsn(paramInt1, paramInt2); 
    execute(132, paramInt1, null);
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs) {
    if (this.mv != null)
      this.mv.visitTableSwitchInsn(paramInt1, paramInt2, paramLabel, paramVarArgs); 
    execute(170, 0, null);
    this.locals = null;
    this.stack = null;
  }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel) {
    if (this.mv != null)
      this.mv.visitLookupSwitchInsn(paramLabel, paramArrayOfInt, paramArrayOfLabel); 
    execute(171, 0, null);
    this.locals = null;
    this.stack = null;
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt) {
    if (this.mv != null)
      this.mv.visitMultiANewArrayInsn(paramString, paramInt); 
    execute(197, paramInt, paramString);
  }
  
  public void visitMaxs(int paramInt1, int paramInt2) {
    if (this.mv != null) {
      this.maxStack = Math.max(this.maxStack, paramInt1);
      this.maxLocals = Math.max(this.maxLocals, paramInt2);
      this.mv.visitMaxs(this.maxStack, this.maxLocals);
    } 
  }
  
  private Object get(int paramInt) {
    this.maxLocals = Math.max(this.maxLocals, paramInt + 1);
    return (paramInt < this.locals.size()) ? this.locals.get(paramInt) : Opcodes.TOP;
  }
  
  private void set(int paramInt, Object paramObject) {
    this.maxLocals = Math.max(this.maxLocals, paramInt + 1);
    while (paramInt >= this.locals.size())
      this.locals.add(Opcodes.TOP); 
    this.locals.set(paramInt, paramObject);
  }
  
  private void push(Object paramObject) {
    this.stack.add(paramObject);
    this.maxStack = Math.max(this.maxStack, this.stack.size());
  }
  
  private void pushDesc(String paramString) {
    int i = (paramString.charAt(0) == '(') ? (paramString.indexOf(')') + 1) : 0;
    switch (paramString.charAt(i)) {
      case 'V':
        return;
      case 'B':
      case 'C':
      case 'I':
      case 'S':
      case 'Z':
        push(Opcodes.INTEGER);
        return;
      case 'F':
        push(Opcodes.FLOAT);
        return;
      case 'J':
        push(Opcodes.LONG);
        push(Opcodes.TOP);
        return;
      case 'D':
        push(Opcodes.DOUBLE);
        push(Opcodes.TOP);
        return;
      case '[':
        if (i == 0) {
          push(paramString);
        } else {
          push(paramString.substring(i, paramString.length()));
        } 
        return;
    } 
    if (i == 0) {
      push(paramString.substring(1, paramString.length() - 1));
    } else {
      push(paramString.substring(i + 1, paramString.length() - 1));
    } 
  }
  
  private Object pop() { return this.stack.remove(this.stack.size() - 1); }
  
  private void pop(int paramInt) {
    int i = this.stack.size();
    int j = i - paramInt;
    for (int k = i - 1; k >= j; k--)
      this.stack.remove(k); 
  }
  
  private void pop(String paramString) {
    char c = paramString.charAt(0);
    if (c == '(') {
      int i = 0;
      Type[] arrayOfType = Type.getArgumentTypes(paramString);
      for (byte b = 0; b < arrayOfType.length; b++)
        i += arrayOfType[b].getSize(); 
      pop(i);
    } else if (c == 'J' || c == 'D') {
      pop(2);
    } else {
      pop(1);
    } 
  }
  
  private void execute(int paramInt1, int paramInt2, String paramString) {
    Object object4;
    Object object3;
    Object object2;
    Object object1;
    if (this.locals == null) {
      this.labels = null;
      return;
    } 
    switch (paramInt1) {
      case 0:
      case 116:
      case 117:
      case 118:
      case 119:
      case 145:
      case 146:
      case 147:
      case 167:
      case 177:
        break;
      case 1:
        push(Opcodes.NULL);
        break;
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 16:
      case 17:
        push(Opcodes.INTEGER);
        break;
      case 9:
      case 10:
        push(Opcodes.LONG);
        push(Opcodes.TOP);
        break;
      case 11:
      case 12:
      case 13:
        push(Opcodes.FLOAT);
        break;
      case 14:
      case 15:
        push(Opcodes.DOUBLE);
        push(Opcodes.TOP);
        break;
      case 21:
      case 23:
      case 25:
        push(get(paramInt2));
        break;
      case 22:
      case 24:
        push(get(paramInt2));
        push(Opcodes.TOP);
        break;
      case 46:
      case 51:
      case 52:
      case 53:
        pop(2);
        push(Opcodes.INTEGER);
        break;
      case 47:
      case 143:
        pop(2);
        push(Opcodes.LONG);
        push(Opcodes.TOP);
        break;
      case 48:
        pop(2);
        push(Opcodes.FLOAT);
        break;
      case 49:
      case 138:
        pop(2);
        push(Opcodes.DOUBLE);
        push(Opcodes.TOP);
        break;
      case 50:
        pop(1);
        object1 = pop();
        if (object1 instanceof String) {
          pushDesc(((String)object1).substring(1));
          break;
        } 
        push("java/lang/Object");
        break;
      case 54:
      case 56:
      case 58:
        object1 = pop();
        set(paramInt2, object1);
        if (paramInt2 > 0) {
          Object object = get(paramInt2 - 1);
          if (object == Opcodes.LONG || object == Opcodes.DOUBLE)
            set(paramInt2 - 1, Opcodes.TOP); 
        } 
        break;
      case 55:
      case 57:
        pop(1);
        object1 = pop();
        set(paramInt2, object1);
        set(paramInt2 + 1, Opcodes.TOP);
        if (paramInt2 > 0) {
          Object object = get(paramInt2 - 1);
          if (object == Opcodes.LONG || object == Opcodes.DOUBLE)
            set(paramInt2 - 1, Opcodes.TOP); 
        } 
        break;
      case 79:
      case 81:
      case 83:
      case 84:
      case 85:
      case 86:
        pop(3);
        break;
      case 80:
      case 82:
        pop(4);
        break;
      case 87:
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
      case 170:
      case 171:
      case 172:
      case 174:
      case 176:
      case 191:
      case 194:
      case 195:
      case 198:
      case 199:
        pop(1);
        break;
      case 88:
      case 159:
      case 160:
      case 161:
      case 162:
      case 163:
      case 164:
      case 165:
      case 166:
      case 173:
      case 175:
        pop(2);
        break;
      case 89:
        object1 = pop();
        push(object1);
        push(object1);
        break;
      case 90:
        object1 = pop();
        object2 = pop();
        push(object1);
        push(object2);
        push(object1);
        break;
      case 91:
        object1 = pop();
        object2 = pop();
        object3 = pop();
        push(object1);
        push(object3);
        push(object2);
        push(object1);
        break;
      case 92:
        object1 = pop();
        object2 = pop();
        push(object2);
        push(object1);
        push(object2);
        push(object1);
        break;
      case 93:
        object1 = pop();
        object2 = pop();
        object3 = pop();
        push(object2);
        push(object1);
        push(object3);
        push(object2);
        push(object1);
        break;
      case 94:
        object1 = pop();
        object2 = pop();
        object3 = pop();
        object4 = pop();
        push(object2);
        push(object1);
        push(object4);
        push(object3);
        push(object2);
        push(object1);
        break;
      case 95:
        object1 = pop();
        object2 = pop();
        push(object1);
        push(object2);
        break;
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
      case 136:
      case 142:
      case 149:
      case 150:
        pop(2);
        push(Opcodes.INTEGER);
        break;
      case 97:
      case 101:
      case 105:
      case 109:
      case 113:
      case 127:
      case 129:
      case 131:
        pop(4);
        push(Opcodes.LONG);
        push(Opcodes.TOP);
        break;
      case 98:
      case 102:
      case 106:
      case 110:
      case 114:
      case 137:
      case 144:
        pop(2);
        push(Opcodes.FLOAT);
        break;
      case 99:
      case 103:
      case 107:
      case 111:
      case 115:
        pop(4);
        push(Opcodes.DOUBLE);
        push(Opcodes.TOP);
        break;
      case 121:
      case 123:
      case 125:
        pop(3);
        push(Opcodes.LONG);
        push(Opcodes.TOP);
        break;
      case 132:
        set(paramInt2, Opcodes.INTEGER);
        break;
      case 133:
      case 140:
        pop(1);
        push(Opcodes.LONG);
        push(Opcodes.TOP);
        break;
      case 134:
        pop(1);
        push(Opcodes.FLOAT);
        break;
      case 135:
      case 141:
        pop(1);
        push(Opcodes.DOUBLE);
        push(Opcodes.TOP);
        break;
      case 139:
      case 190:
      case 193:
        pop(1);
        push(Opcodes.INTEGER);
        break;
      case 148:
      case 151:
      case 152:
        pop(4);
        push(Opcodes.INTEGER);
        break;
      case 168:
      case 169:
        throw new RuntimeException("JSR/RET are not supported");
      case 178:
        pushDesc(paramString);
        break;
      case 179:
        pop(paramString);
        break;
      case 180:
        pop(1);
        pushDesc(paramString);
        break;
      case 181:
        pop(paramString);
        pop();
        break;
      case 187:
        push(this.labels.get(0));
        break;
      case 188:
        pop();
        switch (paramInt2) {
          case 4:
            pushDesc("[Z");
            break;
          case 5:
            pushDesc("[C");
            break;
          case 8:
            pushDesc("[B");
            break;
          case 9:
            pushDesc("[S");
            break;
          case 10:
            pushDesc("[I");
            break;
          case 6:
            pushDesc("[F");
            break;
          case 7:
            pushDesc("[D");
            break;
        } 
        pushDesc("[J");
        break;
      case 189:
        pop();
        pushDesc("[" + Type.getObjectType(paramString));
        break;
      case 192:
        pop();
        pushDesc(Type.getObjectType(paramString).getDescriptor());
        break;
      default:
        pop(paramInt2);
        pushDesc(paramString);
        break;
    } 
    this.labels = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\commons\AnalyzerAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */