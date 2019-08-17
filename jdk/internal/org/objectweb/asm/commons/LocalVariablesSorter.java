package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.TypePath;

public class LocalVariablesSorter extends MethodVisitor {
  private static final Type OBJECT_TYPE = Type.getObjectType("java/lang/Object");
  
  private int[] mapping = new int[40];
  
  private Object[] newLocals = new Object[20];
  
  protected final int firstLocal;
  
  protected int nextLocal;
  
  private boolean changed;
  
  public LocalVariablesSorter(int paramInt, String paramString, MethodVisitor paramMethodVisitor) {
    this(327680, paramInt, paramString, paramMethodVisitor);
    if (getClass() != LocalVariablesSorter.class)
      throw new IllegalStateException(); 
  }
  
  protected LocalVariablesSorter(int paramInt1, int paramInt2, String paramString, MethodVisitor paramMethodVisitor) {
    super(paramInt1, paramMethodVisitor);
    Type[] arrayOfType = Type.getArgumentTypes(paramString);
    this.nextLocal = ((0x8 & paramInt2) == 0) ? 1 : 0;
    for (byte b = 0; b < arrayOfType.length; b++)
      this.nextLocal += arrayOfType[b].getSize(); 
    this.firstLocal = this.nextLocal;
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2) {
    Type type;
    switch (paramInt1) {
      case 22:
      case 55:
        type = Type.LONG_TYPE;
        break;
      case 24:
      case 57:
        type = Type.DOUBLE_TYPE;
        break;
      case 23:
      case 56:
        type = Type.FLOAT_TYPE;
        break;
      case 21:
      case 54:
        type = Type.INT_TYPE;
        break;
      default:
        type = OBJECT_TYPE;
        break;
    } 
    this.mv.visitVarInsn(paramInt1, remap(paramInt2, type));
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2) { this.mv.visitIincInsn(remap(paramInt1, Type.INT_TYPE), paramInt2); }
  
  public void visitMaxs(int paramInt1, int paramInt2) { this.mv.visitMaxs(paramInt1, this.nextLocal); }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt) {
    int i = remap(paramInt, Type.getType(paramString2));
    this.mv.visitLocalVariable(paramString1, paramString2, paramString3, paramLabel1, paramLabel2, i);
  }
  
  public AnnotationVisitor visitLocalVariableAnnotation(int paramInt, TypePath paramTypePath, Label[] paramArrayOfLabel1, Label[] paramArrayOfLabel2, int[] paramArrayOfInt, String paramString, boolean paramBoolean) {
    Type type = Type.getType(paramString);
    int[] arrayOfInt = new int[paramArrayOfInt.length];
    for (byte b = 0; b < arrayOfInt.length; b++)
      arrayOfInt[b] = remap(paramArrayOfInt[b], type); 
    return this.mv.visitLocalVariableAnnotation(paramInt, paramTypePath, paramArrayOfLabel1, paramArrayOfLabel2, arrayOfInt, paramString, paramBoolean);
  }
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2) {
    if (paramInt1 != -1)
      throw new IllegalStateException("ClassReader.accept() should be called with EXPAND_FRAMES flag"); 
    if (!this.changed) {
      this.mv.visitFrame(paramInt1, paramInt2, paramArrayOfObject1, paramInt3, paramArrayOfObject2);
      return;
    } 
    Object[] arrayOfObject = new Object[this.newLocals.length];
    System.arraycopy(this.newLocals, 0, arrayOfObject, 0, arrayOfObject.length);
    updateNewLocals(this.newLocals);
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < paramInt2; b2++) {
      Object object = paramArrayOfObject1[b2];
      byte b = (object == Opcodes.LONG || object == Opcodes.DOUBLE) ? 2 : 1;
      if (object != Opcodes.TOP) {
        Type type = OBJECT_TYPE;
        if (object == Opcodes.INTEGER) {
          type = Type.INT_TYPE;
        } else if (object == Opcodes.FLOAT) {
          type = Type.FLOAT_TYPE;
        } else if (object == Opcodes.LONG) {
          type = Type.LONG_TYPE;
        } else if (object == Opcodes.DOUBLE) {
          type = Type.DOUBLE_TYPE;
        } else if (object instanceof String) {
          type = Type.getObjectType((String)object);
        } 
        setFrameLocal(remap(b1, type), object);
      } 
      b1 += b;
    } 
    b1 = 0;
    b2 = 0;
    for (byte b3 = 0; b1 < this.newLocals.length; b3++) {
      Object object = this.newLocals[b1++];
      if (object != null && object != Opcodes.TOP) {
        this.newLocals[b3] = object;
        b2 = b3 + true;
        if (object == Opcodes.LONG || object == Opcodes.DOUBLE)
          b1++; 
      } else {
        this.newLocals[b3] = Opcodes.TOP;
      } 
    } 
    this.mv.visitFrame(paramInt1, b2, this.newLocals, paramInt3, paramArrayOfObject2);
    this.newLocals = arrayOfObject;
  }
  
  public int newLocal(Type paramType) {
    Integer integer;
    switch (paramType.getSort()) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
        integer = Opcodes.INTEGER;
        i = newLocalMapping(paramType);
        setLocalType(i, paramType);
        setFrameLocal(i, integer);
        this.changed = true;
        return i;
      case 6:
        integer = Opcodes.FLOAT;
        i = newLocalMapping(paramType);
        setLocalType(i, paramType);
        setFrameLocal(i, integer);
        this.changed = true;
        return i;
      case 7:
        integer = Opcodes.LONG;
        i = newLocalMapping(paramType);
        setLocalType(i, paramType);
        setFrameLocal(i, integer);
        this.changed = true;
        return i;
      case 8:
        integer = Opcodes.DOUBLE;
        i = newLocalMapping(paramType);
        setLocalType(i, paramType);
        setFrameLocal(i, integer);
        this.changed = true;
        return i;
      case 9:
        str = paramType.getDescriptor();
        i = newLocalMapping(paramType);
        setLocalType(i, paramType);
        setFrameLocal(i, str);
        this.changed = true;
        return i;
    } 
    String str = paramType.getInternalName();
    int i = newLocalMapping(paramType);
    setLocalType(i, paramType);
    setFrameLocal(i, str);
    this.changed = true;
    return i;
  }
  
  protected void updateNewLocals(Object[] paramArrayOfObject) {}
  
  protected void setLocalType(int paramInt, Type paramType) {}
  
  private void setFrameLocal(int paramInt, Object paramObject) {
    int i = this.newLocals.length;
    if (paramInt >= i) {
      Object[] arrayOfObject = new Object[Math.max(2 * i, paramInt + 1)];
      System.arraycopy(this.newLocals, 0, arrayOfObject, 0, i);
      this.newLocals = arrayOfObject;
    } 
    this.newLocals[paramInt] = paramObject;
  }
  
  private int remap(int paramInt, Type paramType) {
    if (paramInt + paramType.getSize() <= this.firstLocal)
      return paramInt; 
    int i = 2 * paramInt + paramType.getSize() - 1;
    int j = this.mapping.length;
    if (i >= j) {
      int[] arrayOfInt = new int[Math.max(2 * j, i + 1)];
      System.arraycopy(this.mapping, 0, arrayOfInt, 0, j);
      this.mapping = arrayOfInt;
    } 
    int k = this.mapping[i];
    if (k == 0) {
      k = newLocalMapping(paramType);
      setLocalType(k, paramType);
      this.mapping[i] = k + 1;
    } else {
      k--;
    } 
    if (k != paramInt)
      this.changed = true; 
    return k;
  }
  
  protected int newLocalMapping(Type paramType) {
    int i = this.nextLocal;
    this.nextLocal += paramType.getSize();
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\commons\LocalVariablesSorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */