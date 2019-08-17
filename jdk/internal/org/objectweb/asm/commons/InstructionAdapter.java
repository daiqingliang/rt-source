package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;

public class InstructionAdapter extends MethodVisitor {
  public static final Type OBJECT_TYPE = Type.getType("Ljava/lang/Object;");
  
  public InstructionAdapter(MethodVisitor paramMethodVisitor) {
    this(327680, paramMethodVisitor);
    if (getClass() != InstructionAdapter.class)
      throw new IllegalStateException(); 
  }
  
  protected InstructionAdapter(int paramInt, MethodVisitor paramMethodVisitor) { super(paramInt, paramMethodVisitor); }
  
  public void visitInsn(int paramInt) {
    switch (paramInt) {
      case 0:
        nop();
        return;
      case 1:
        aconst(null);
        return;
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
        iconst(paramInt - 3);
        return;
      case 9:
      case 10:
        lconst((paramInt - 9));
        return;
      case 11:
      case 12:
      case 13:
        fconst((paramInt - 11));
        return;
      case 14:
      case 15:
        dconst((paramInt - 14));
        return;
      case 46:
        aload(Type.INT_TYPE);
        return;
      case 47:
        aload(Type.LONG_TYPE);
        return;
      case 48:
        aload(Type.FLOAT_TYPE);
        return;
      case 49:
        aload(Type.DOUBLE_TYPE);
        return;
      case 50:
        aload(OBJECT_TYPE);
        return;
      case 51:
        aload(Type.BYTE_TYPE);
        return;
      case 52:
        aload(Type.CHAR_TYPE);
        return;
      case 53:
        aload(Type.SHORT_TYPE);
        return;
      case 79:
        astore(Type.INT_TYPE);
        return;
      case 80:
        astore(Type.LONG_TYPE);
        return;
      case 81:
        astore(Type.FLOAT_TYPE);
        return;
      case 82:
        astore(Type.DOUBLE_TYPE);
        return;
      case 83:
        astore(OBJECT_TYPE);
        return;
      case 84:
        astore(Type.BYTE_TYPE);
        return;
      case 85:
        astore(Type.CHAR_TYPE);
        return;
      case 86:
        astore(Type.SHORT_TYPE);
        return;
      case 87:
        pop();
        return;
      case 88:
        pop2();
        return;
      case 89:
        dup();
        return;
      case 90:
        dupX1();
        return;
      case 91:
        dupX2();
        return;
      case 92:
        dup2();
        return;
      case 93:
        dup2X1();
        return;
      case 94:
        dup2X2();
        return;
      case 95:
        swap();
        return;
      case 96:
        add(Type.INT_TYPE);
        return;
      case 97:
        add(Type.LONG_TYPE);
        return;
      case 98:
        add(Type.FLOAT_TYPE);
        return;
      case 99:
        add(Type.DOUBLE_TYPE);
        return;
      case 100:
        sub(Type.INT_TYPE);
        return;
      case 101:
        sub(Type.LONG_TYPE);
        return;
      case 102:
        sub(Type.FLOAT_TYPE);
        return;
      case 103:
        sub(Type.DOUBLE_TYPE);
        return;
      case 104:
        mul(Type.INT_TYPE);
        return;
      case 105:
        mul(Type.LONG_TYPE);
        return;
      case 106:
        mul(Type.FLOAT_TYPE);
        return;
      case 107:
        mul(Type.DOUBLE_TYPE);
        return;
      case 108:
        div(Type.INT_TYPE);
        return;
      case 109:
        div(Type.LONG_TYPE);
        return;
      case 110:
        div(Type.FLOAT_TYPE);
        return;
      case 111:
        div(Type.DOUBLE_TYPE);
        return;
      case 112:
        rem(Type.INT_TYPE);
        return;
      case 113:
        rem(Type.LONG_TYPE);
        return;
      case 114:
        rem(Type.FLOAT_TYPE);
        return;
      case 115:
        rem(Type.DOUBLE_TYPE);
        return;
      case 116:
        neg(Type.INT_TYPE);
        return;
      case 117:
        neg(Type.LONG_TYPE);
        return;
      case 118:
        neg(Type.FLOAT_TYPE);
        return;
      case 119:
        neg(Type.DOUBLE_TYPE);
        return;
      case 120:
        shl(Type.INT_TYPE);
        return;
      case 121:
        shl(Type.LONG_TYPE);
        return;
      case 122:
        shr(Type.INT_TYPE);
        return;
      case 123:
        shr(Type.LONG_TYPE);
        return;
      case 124:
        ushr(Type.INT_TYPE);
        return;
      case 125:
        ushr(Type.LONG_TYPE);
        return;
      case 126:
        and(Type.INT_TYPE);
        return;
      case 127:
        and(Type.LONG_TYPE);
        return;
      case 128:
        or(Type.INT_TYPE);
        return;
      case 129:
        or(Type.LONG_TYPE);
        return;
      case 130:
        xor(Type.INT_TYPE);
        return;
      case 131:
        xor(Type.LONG_TYPE);
        return;
      case 133:
        cast(Type.INT_TYPE, Type.LONG_TYPE);
        return;
      case 134:
        cast(Type.INT_TYPE, Type.FLOAT_TYPE);
        return;
      case 135:
        cast(Type.INT_TYPE, Type.DOUBLE_TYPE);
        return;
      case 136:
        cast(Type.LONG_TYPE, Type.INT_TYPE);
        return;
      case 137:
        cast(Type.LONG_TYPE, Type.FLOAT_TYPE);
        return;
      case 138:
        cast(Type.LONG_TYPE, Type.DOUBLE_TYPE);
        return;
      case 139:
        cast(Type.FLOAT_TYPE, Type.INT_TYPE);
        return;
      case 140:
        cast(Type.FLOAT_TYPE, Type.LONG_TYPE);
        return;
      case 141:
        cast(Type.FLOAT_TYPE, Type.DOUBLE_TYPE);
        return;
      case 142:
        cast(Type.DOUBLE_TYPE, Type.INT_TYPE);
        return;
      case 143:
        cast(Type.DOUBLE_TYPE, Type.LONG_TYPE);
        return;
      case 144:
        cast(Type.DOUBLE_TYPE, Type.FLOAT_TYPE);
        return;
      case 145:
        cast(Type.INT_TYPE, Type.BYTE_TYPE);
        return;
      case 146:
        cast(Type.INT_TYPE, Type.CHAR_TYPE);
        return;
      case 147:
        cast(Type.INT_TYPE, Type.SHORT_TYPE);
        return;
      case 148:
        lcmp();
        return;
      case 149:
        cmpl(Type.FLOAT_TYPE);
        return;
      case 150:
        cmpg(Type.FLOAT_TYPE);
        return;
      case 151:
        cmpl(Type.DOUBLE_TYPE);
        return;
      case 152:
        cmpg(Type.DOUBLE_TYPE);
        return;
      case 172:
        areturn(Type.INT_TYPE);
        return;
      case 173:
        areturn(Type.LONG_TYPE);
        return;
      case 174:
        areturn(Type.FLOAT_TYPE);
        return;
      case 175:
        areturn(Type.DOUBLE_TYPE);
        return;
      case 176:
        areturn(OBJECT_TYPE);
        return;
      case 177:
        areturn(Type.VOID_TYPE);
        return;
      case 190:
        arraylength();
        return;
      case 191:
        athrow();
        return;
      case 194:
        monitorenter();
        return;
      case 195:
        monitorexit();
        return;
    } 
    throw new IllegalArgumentException();
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2) {
    switch (paramInt1) {
      case 16:
        iconst(paramInt2);
        return;
      case 17:
        iconst(paramInt2);
        return;
      case 188:
        switch (paramInt2) {
          case 4:
            newarray(Type.BOOLEAN_TYPE);
            return;
          case 5:
            newarray(Type.CHAR_TYPE);
            return;
          case 8:
            newarray(Type.BYTE_TYPE);
            return;
          case 9:
            newarray(Type.SHORT_TYPE);
            return;
          case 10:
            newarray(Type.INT_TYPE);
            return;
          case 6:
            newarray(Type.FLOAT_TYPE);
            return;
          case 11:
            newarray(Type.LONG_TYPE);
            return;
          case 7:
            newarray(Type.DOUBLE_TYPE);
            return;
        } 
        throw new IllegalArgumentException();
    } 
    throw new IllegalArgumentException();
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2) {
    switch (paramInt1) {
      case 21:
        load(paramInt2, Type.INT_TYPE);
        return;
      case 22:
        load(paramInt2, Type.LONG_TYPE);
        return;
      case 23:
        load(paramInt2, Type.FLOAT_TYPE);
        return;
      case 24:
        load(paramInt2, Type.DOUBLE_TYPE);
        return;
      case 25:
        load(paramInt2, OBJECT_TYPE);
        return;
      case 54:
        store(paramInt2, Type.INT_TYPE);
        return;
      case 55:
        store(paramInt2, Type.LONG_TYPE);
        return;
      case 56:
        store(paramInt2, Type.FLOAT_TYPE);
        return;
      case 57:
        store(paramInt2, Type.DOUBLE_TYPE);
        return;
      case 58:
        store(paramInt2, OBJECT_TYPE);
        return;
      case 169:
        ret(paramInt2);
        return;
    } 
    throw new IllegalArgumentException();
  }
  
  public void visitTypeInsn(int paramInt, String paramString) {
    Type type = Type.getObjectType(paramString);
    switch (paramInt) {
      case 187:
        anew(type);
        return;
      case 189:
        newarray(type);
        return;
      case 192:
        checkcast(type);
        return;
      case 193:
        instanceOf(type);
        return;
    } 
    throw new IllegalArgumentException();
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    switch (paramInt) {
      case 178:
        getstatic(paramString1, paramString2, paramString3);
        return;
      case 179:
        putstatic(paramString1, paramString2, paramString3);
        return;
      case 180:
        getfield(paramString1, paramString2, paramString3);
        return;
      case 181:
        putfield(paramString1, paramString2, paramString3);
        return;
    } 
    throw new IllegalArgumentException();
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
    switch (paramInt) {
      case 183:
        invokespecial(paramString1, paramString2, paramString3, paramBoolean);
        return;
      case 182:
        invokevirtual(paramString1, paramString2, paramString3, paramBoolean);
        return;
      case 184:
        invokestatic(paramString1, paramString2, paramString3, paramBoolean);
        return;
      case 185:
        invokeinterface(paramString1, paramString2, paramString3);
        return;
    } 
    throw new IllegalArgumentException();
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs) { invokedynamic(paramString1, paramString2, paramHandle, paramVarArgs); }
  
  public void visitJumpInsn(int paramInt, Label paramLabel) {
    switch (paramInt) {
      case 153:
        ifeq(paramLabel);
        return;
      case 154:
        ifne(paramLabel);
        return;
      case 155:
        iflt(paramLabel);
        return;
      case 156:
        ifge(paramLabel);
        return;
      case 157:
        ifgt(paramLabel);
        return;
      case 158:
        ifle(paramLabel);
        return;
      case 159:
        ificmpeq(paramLabel);
        return;
      case 160:
        ificmpne(paramLabel);
        return;
      case 161:
        ificmplt(paramLabel);
        return;
      case 162:
        ificmpge(paramLabel);
        return;
      case 163:
        ificmpgt(paramLabel);
        return;
      case 164:
        ificmple(paramLabel);
        return;
      case 165:
        ifacmpeq(paramLabel);
        return;
      case 166:
        ifacmpne(paramLabel);
        return;
      case 167:
        goTo(paramLabel);
        return;
      case 168:
        jsr(paramLabel);
        return;
      case 198:
        ifnull(paramLabel);
        return;
      case 199:
        ifnonnull(paramLabel);
        return;
    } 
    throw new IllegalArgumentException();
  }
  
  public void visitLabel(Label paramLabel) { mark(paramLabel); }
  
  public void visitLdcInsn(Object paramObject) {
    if (paramObject instanceof Integer) {
      int i = ((Integer)paramObject).intValue();
      iconst(i);
    } else if (paramObject instanceof Byte) {
      int i = ((Byte)paramObject).intValue();
      iconst(i);
    } else if (paramObject instanceof Character) {
      char c = ((Character)paramObject).charValue();
      iconst(c);
    } else if (paramObject instanceof Short) {
      int i = ((Short)paramObject).intValue();
      iconst(i);
    } else if (paramObject instanceof Boolean) {
      byte b = ((Boolean)paramObject).booleanValue() ? 1 : 0;
      iconst(b);
    } else if (paramObject instanceof Float) {
      float f = ((Float)paramObject).floatValue();
      fconst(f);
    } else if (paramObject instanceof Long) {
      long l = ((Long)paramObject).longValue();
      lconst(l);
    } else if (paramObject instanceof Double) {
      double d = ((Double)paramObject).doubleValue();
      dconst(d);
    } else if (paramObject instanceof String) {
      aconst(paramObject);
    } else if (paramObject instanceof Type) {
      tconst((Type)paramObject);
    } else if (paramObject instanceof Handle) {
      hconst((Handle)paramObject);
    } else {
      throw new IllegalArgumentException();
    } 
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2) { iinc(paramInt1, paramInt2); }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs) { tableswitch(paramInt1, paramInt2, paramLabel, paramVarArgs); }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel) { lookupswitch(paramLabel, paramArrayOfInt, paramArrayOfLabel); }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt) { multianewarray(paramString, paramInt); }
  
  public void nop() { this.mv.visitInsn(0); }
  
  public void aconst(Object paramObject) {
    if (paramObject == null) {
      this.mv.visitInsn(1);
    } else {
      this.mv.visitLdcInsn(paramObject);
    } 
  }
  
  public void iconst(int paramInt) {
    if (paramInt >= -1 && paramInt <= 5) {
      this.mv.visitInsn(3 + paramInt);
    } else if (paramInt >= -128 && paramInt <= 127) {
      this.mv.visitIntInsn(16, paramInt);
    } else if (paramInt >= -32768 && paramInt <= 32767) {
      this.mv.visitIntInsn(17, paramInt);
    } else {
      this.mv.visitLdcInsn(Integer.valueOf(paramInt));
    } 
  }
  
  public void lconst(long paramLong) {
    if (paramLong == 0L || paramLong == 1L) {
      this.mv.visitInsn(9 + (int)paramLong);
    } else {
      this.mv.visitLdcInsn(Long.valueOf(paramLong));
    } 
  }
  
  public void fconst(float paramFloat) {
    int i = Float.floatToIntBits(paramFloat);
    if (i == 0L || i == 1065353216 || i == 1073741824) {
      this.mv.visitInsn(11 + (int)paramFloat);
    } else {
      this.mv.visitLdcInsn(Float.valueOf(paramFloat));
    } 
  }
  
  public void dconst(double paramDouble) {
    long l = Double.doubleToLongBits(paramDouble);
    if (l == 0L || l == 4607182418800017408L) {
      this.mv.visitInsn(14 + (int)paramDouble);
    } else {
      this.mv.visitLdcInsn(Double.valueOf(paramDouble));
    } 
  }
  
  public void tconst(Type paramType) { this.mv.visitLdcInsn(paramType); }
  
  public void hconst(Handle paramHandle) { this.mv.visitLdcInsn(paramHandle); }
  
  public void load(int paramInt, Type paramType) { this.mv.visitVarInsn(paramType.getOpcode(21), paramInt); }
  
  public void aload(Type paramType) { this.mv.visitInsn(paramType.getOpcode(46)); }
  
  public void store(int paramInt, Type paramType) { this.mv.visitVarInsn(paramType.getOpcode(54), paramInt); }
  
  public void astore(Type paramType) { this.mv.visitInsn(paramType.getOpcode(79)); }
  
  public void pop() { this.mv.visitInsn(87); }
  
  public void pop2() { this.mv.visitInsn(88); }
  
  public void dup() { this.mv.visitInsn(89); }
  
  public void dup2() { this.mv.visitInsn(92); }
  
  public void dupX1() { this.mv.visitInsn(90); }
  
  public void dupX2() { this.mv.visitInsn(91); }
  
  public void dup2X1() { this.mv.visitInsn(93); }
  
  public void dup2X2() { this.mv.visitInsn(94); }
  
  public void swap() { this.mv.visitInsn(95); }
  
  public void add(Type paramType) { this.mv.visitInsn(paramType.getOpcode(96)); }
  
  public void sub(Type paramType) { this.mv.visitInsn(paramType.getOpcode(100)); }
  
  public void mul(Type paramType) { this.mv.visitInsn(paramType.getOpcode(104)); }
  
  public void div(Type paramType) { this.mv.visitInsn(paramType.getOpcode(108)); }
  
  public void rem(Type paramType) { this.mv.visitInsn(paramType.getOpcode(112)); }
  
  public void neg(Type paramType) { this.mv.visitInsn(paramType.getOpcode(116)); }
  
  public void shl(Type paramType) { this.mv.visitInsn(paramType.getOpcode(120)); }
  
  public void shr(Type paramType) { this.mv.visitInsn(paramType.getOpcode(122)); }
  
  public void ushr(Type paramType) { this.mv.visitInsn(paramType.getOpcode(124)); }
  
  public void and(Type paramType) { this.mv.visitInsn(paramType.getOpcode(126)); }
  
  public void or(Type paramType) { this.mv.visitInsn(paramType.getOpcode(128)); }
  
  public void xor(Type paramType) { this.mv.visitInsn(paramType.getOpcode(130)); }
  
  public void iinc(int paramInt1, int paramInt2) { this.mv.visitIincInsn(paramInt1, paramInt2); }
  
  public void cast(Type paramType1, Type paramType2) {
    if (paramType1 != paramType2)
      if (paramType1 == Type.DOUBLE_TYPE) {
        if (paramType2 == Type.FLOAT_TYPE) {
          this.mv.visitInsn(144);
        } else if (paramType2 == Type.LONG_TYPE) {
          this.mv.visitInsn(143);
        } else {
          this.mv.visitInsn(142);
          cast(Type.INT_TYPE, paramType2);
        } 
      } else if (paramType1 == Type.FLOAT_TYPE) {
        if (paramType2 == Type.DOUBLE_TYPE) {
          this.mv.visitInsn(141);
        } else if (paramType2 == Type.LONG_TYPE) {
          this.mv.visitInsn(140);
        } else {
          this.mv.visitInsn(139);
          cast(Type.INT_TYPE, paramType2);
        } 
      } else if (paramType1 == Type.LONG_TYPE) {
        if (paramType2 == Type.DOUBLE_TYPE) {
          this.mv.visitInsn(138);
        } else if (paramType2 == Type.FLOAT_TYPE) {
          this.mv.visitInsn(137);
        } else {
          this.mv.visitInsn(136);
          cast(Type.INT_TYPE, paramType2);
        } 
      } else if (paramType2 == Type.BYTE_TYPE) {
        this.mv.visitInsn(145);
      } else if (paramType2 == Type.CHAR_TYPE) {
        this.mv.visitInsn(146);
      } else if (paramType2 == Type.DOUBLE_TYPE) {
        this.mv.visitInsn(135);
      } else if (paramType2 == Type.FLOAT_TYPE) {
        this.mv.visitInsn(134);
      } else if (paramType2 == Type.LONG_TYPE) {
        this.mv.visitInsn(133);
      } else if (paramType2 == Type.SHORT_TYPE) {
        this.mv.visitInsn(147);
      }  
  }
  
  public void lcmp() { this.mv.visitInsn(148); }
  
  public void cmpl(Type paramType) { this.mv.visitInsn((paramType == Type.FLOAT_TYPE) ? 149 : 151); }
  
  public void cmpg(Type paramType) { this.mv.visitInsn((paramType == Type.FLOAT_TYPE) ? 150 : 152); }
  
  public void ifeq(Label paramLabel) { this.mv.visitJumpInsn(153, paramLabel); }
  
  public void ifne(Label paramLabel) { this.mv.visitJumpInsn(154, paramLabel); }
  
  public void iflt(Label paramLabel) { this.mv.visitJumpInsn(155, paramLabel); }
  
  public void ifge(Label paramLabel) { this.mv.visitJumpInsn(156, paramLabel); }
  
  public void ifgt(Label paramLabel) { this.mv.visitJumpInsn(157, paramLabel); }
  
  public void ifle(Label paramLabel) { this.mv.visitJumpInsn(158, paramLabel); }
  
  public void ificmpeq(Label paramLabel) { this.mv.visitJumpInsn(159, paramLabel); }
  
  public void ificmpne(Label paramLabel) { this.mv.visitJumpInsn(160, paramLabel); }
  
  public void ificmplt(Label paramLabel) { this.mv.visitJumpInsn(161, paramLabel); }
  
  public void ificmpge(Label paramLabel) { this.mv.visitJumpInsn(162, paramLabel); }
  
  public void ificmpgt(Label paramLabel) { this.mv.visitJumpInsn(163, paramLabel); }
  
  public void ificmple(Label paramLabel) { this.mv.visitJumpInsn(164, paramLabel); }
  
  public void ifacmpeq(Label paramLabel) { this.mv.visitJumpInsn(165, paramLabel); }
  
  public void ifacmpne(Label paramLabel) { this.mv.visitJumpInsn(166, paramLabel); }
  
  public void goTo(Label paramLabel) { this.mv.visitJumpInsn(167, paramLabel); }
  
  public void jsr(Label paramLabel) { this.mv.visitJumpInsn(168, paramLabel); }
  
  public void ret(int paramInt) { this.mv.visitVarInsn(169, paramInt); }
  
  public void tableswitch(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs) { this.mv.visitTableSwitchInsn(paramInt1, paramInt2, paramLabel, paramVarArgs); }
  
  public void lookupswitch(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel) { this.mv.visitLookupSwitchInsn(paramLabel, paramArrayOfInt, paramArrayOfLabel); }
  
  public void areturn(Type paramType) { this.mv.visitInsn(paramType.getOpcode(172)); }
  
  public void getstatic(String paramString1, String paramString2, String paramString3) { this.mv.visitFieldInsn(178, paramString1, paramString2, paramString3); }
  
  public void putstatic(String paramString1, String paramString2, String paramString3) { this.mv.visitFieldInsn(179, paramString1, paramString2, paramString3); }
  
  public void getfield(String paramString1, String paramString2, String paramString3) { this.mv.visitFieldInsn(180, paramString1, paramString2, paramString3); }
  
  public void putfield(String paramString1, String paramString2, String paramString3) { this.mv.visitFieldInsn(181, paramString1, paramString2, paramString3); }
  
  @Deprecated
  public void invokevirtual(String paramString1, String paramString2, String paramString3) {
    if (this.api >= 327680) {
      invokevirtual(paramString1, paramString2, paramString3, false);
      return;
    } 
    this.mv.visitMethodInsn(182, paramString1, paramString2, paramString3);
  }
  
  public void invokevirtual(String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    if (this.api < 327680) {
      if (paramBoolean)
        throw new IllegalArgumentException("INVOKEVIRTUAL on interfaces require ASM 5"); 
      invokevirtual(paramString1, paramString2, paramString3);
      return;
    } 
    this.mv.visitMethodInsn(182, paramString1, paramString2, paramString3, paramBoolean);
  }
  
  @Deprecated
  public void invokespecial(String paramString1, String paramString2, String paramString3) {
    if (this.api >= 327680) {
      invokespecial(paramString1, paramString2, paramString3, false);
      return;
    } 
    this.mv.visitMethodInsn(183, paramString1, paramString2, paramString3, false);
  }
  
  public void invokespecial(String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    if (this.api < 327680) {
      if (paramBoolean)
        throw new IllegalArgumentException("INVOKESPECIAL on interfaces require ASM 5"); 
      invokespecial(paramString1, paramString2, paramString3);
      return;
    } 
    this.mv.visitMethodInsn(183, paramString1, paramString2, paramString3, paramBoolean);
  }
  
  @Deprecated
  public void invokestatic(String paramString1, String paramString2, String paramString3) {
    if (this.api >= 327680) {
      invokestatic(paramString1, paramString2, paramString3, false);
      return;
    } 
    this.mv.visitMethodInsn(184, paramString1, paramString2, paramString3, false);
  }
  
  public void invokestatic(String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    if (this.api < 327680) {
      if (paramBoolean)
        throw new IllegalArgumentException("INVOKESTATIC on interfaces require ASM 5"); 
      invokestatic(paramString1, paramString2, paramString3);
      return;
    } 
    this.mv.visitMethodInsn(184, paramString1, paramString2, paramString3, paramBoolean);
  }
  
  public void invokeinterface(String paramString1, String paramString2, String paramString3) { this.mv.visitMethodInsn(185, paramString1, paramString2, paramString3, true); }
  
  public void invokedynamic(String paramString1, String paramString2, Handle paramHandle, Object[] paramArrayOfObject) { this.mv.visitInvokeDynamicInsn(paramString1, paramString2, paramHandle, paramArrayOfObject); }
  
  public void anew(Type paramType) { this.mv.visitTypeInsn(187, paramType.getInternalName()); }
  
  public void newarray(Type paramType) {
    byte b;
    switch (paramType.getSort()) {
      case 1:
        b = 4;
        break;
      case 2:
        b = 5;
        break;
      case 3:
        b = 8;
        break;
      case 4:
        b = 9;
        break;
      case 5:
        b = 10;
        break;
      case 6:
        b = 6;
        break;
      case 7:
        b = 11;
        break;
      case 8:
        b = 7;
        break;
      default:
        this.mv.visitTypeInsn(189, paramType.getInternalName());
        return;
    } 
    this.mv.visitIntInsn(188, b);
  }
  
  public void arraylength() { this.mv.visitInsn(190); }
  
  public void athrow() { this.mv.visitInsn(191); }
  
  public void checkcast(Type paramType) { this.mv.visitTypeInsn(192, paramType.getInternalName()); }
  
  public void instanceOf(Type paramType) { this.mv.visitTypeInsn(193, paramType.getInternalName()); }
  
  public void monitorenter() { this.mv.visitInsn(194); }
  
  public void monitorexit() { this.mv.visitInsn(195); }
  
  public void multianewarray(String paramString, int paramInt) { this.mv.visitMultiANewArrayInsn(paramString, paramInt); }
  
  public void ifnull(Label paramLabel) { this.mv.visitJumpInsn(198, paramLabel); }
  
  public void ifnonnull(Label paramLabel) { this.mv.visitJumpInsn(199, paramLabel); }
  
  public void mark(Label paramLabel) { this.mv.visitLabel(paramLabel); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\commons\InstructionAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */