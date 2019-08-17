package jdk.internal.org.objectweb.asm.util;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.TypePath;

public abstract class Printer {
  public static final String[] OPCODES;
  
  public static final String[] TYPES;
  
  public static final String[] HANDLE_TAG;
  
  protected final int api;
  
  protected final StringBuffer buf;
  
  public final List<Object> text;
  
  protected Printer(int paramInt) {
    this.api = paramInt;
    this.buf = new StringBuffer();
    this.text = new ArrayList();
  }
  
  public abstract void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString);
  
  public abstract void visitSource(String paramString1, String paramString2);
  
  public abstract void visitOuterClass(String paramString1, String paramString2, String paramString3);
  
  public abstract Printer visitClassAnnotation(String paramString, boolean paramBoolean);
  
  public Printer visitClassTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) { throw new RuntimeException("Must be overriden"); }
  
  public abstract void visitClassAttribute(Attribute paramAttribute);
  
  public abstract void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt);
  
  public abstract Printer visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject);
  
  public abstract Printer visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString);
  
  public abstract void visitClassEnd();
  
  public abstract void visit(String paramString, Object paramObject);
  
  public abstract void visitEnum(String paramString1, String paramString2, String paramString3);
  
  public abstract Printer visitAnnotation(String paramString1, String paramString2);
  
  public abstract Printer visitArray(String paramString);
  
  public abstract void visitAnnotationEnd();
  
  public abstract Printer visitFieldAnnotation(String paramString, boolean paramBoolean);
  
  public Printer visitFieldTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) { throw new RuntimeException("Must be overriden"); }
  
  public abstract void visitFieldAttribute(Attribute paramAttribute);
  
  public abstract void visitFieldEnd();
  
  public void visitParameter(String paramString, int paramInt) { throw new RuntimeException("Must be overriden"); }
  
  public abstract Printer visitAnnotationDefault();
  
  public abstract Printer visitMethodAnnotation(String paramString, boolean paramBoolean);
  
  public Printer visitMethodTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) { throw new RuntimeException("Must be overriden"); }
  
  public abstract Printer visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean);
  
  public abstract void visitMethodAttribute(Attribute paramAttribute);
  
  public abstract void visitCode();
  
  public abstract void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2);
  
  public abstract void visitInsn(int paramInt);
  
  public abstract void visitIntInsn(int paramInt1, int paramInt2);
  
  public abstract void visitVarInsn(int paramInt1, int paramInt2);
  
  public abstract void visitTypeInsn(int paramInt, String paramString);
  
  public abstract void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3);
  
  @Deprecated
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    if (this.api >= 327680) {
      boolean bool = (paramInt == 185);
      visitMethodInsn(paramInt, paramString1, paramString2, paramString3, bool);
      return;
    } 
    throw new RuntimeException("Must be overriden");
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    if (this.api < 327680) {
      if (paramBoolean != ((paramInt == 185)))
        throw new IllegalArgumentException("INVOKESPECIAL/STATIC on interfaces require ASM 5"); 
      visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
      return;
    } 
    throw new RuntimeException("Must be overriden");
  }
  
  public abstract void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs);
  
  public abstract void visitJumpInsn(int paramInt, Label paramLabel);
  
  public abstract void visitLabel(Label paramLabel);
  
  public abstract void visitLdcInsn(Object paramObject);
  
  public abstract void visitIincInsn(int paramInt1, int paramInt2);
  
  public abstract void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs);
  
  public abstract void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel);
  
  public abstract void visitMultiANewArrayInsn(String paramString, int paramInt);
  
  public Printer visitInsnAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) { throw new RuntimeException("Must be overriden"); }
  
  public abstract void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString);
  
  public Printer visitTryCatchAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) { throw new RuntimeException("Must be overriden"); }
  
  public abstract void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt);
  
  public Printer visitLocalVariableAnnotation(int paramInt, TypePath paramTypePath, Label[] paramArrayOfLabel1, Label[] paramArrayOfLabel2, int[] paramArrayOfInt, String paramString, boolean paramBoolean) { throw new RuntimeException("Must be overriden"); }
  
  public abstract void visitLineNumber(int paramInt, Label paramLabel);
  
  public abstract void visitMaxs(int paramInt1, int paramInt2);
  
  public abstract void visitMethodEnd();
  
  public List<Object> getText() { return this.text; }
  
  public void print(PrintWriter paramPrintWriter) { printList(paramPrintWriter, this.text); }
  
  public static void appendString(StringBuffer paramStringBuffer, String paramString) {
    paramStringBuffer.append('"');
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c == '\n') {
        paramStringBuffer.append("\\n");
      } else if (c == '\r') {
        paramStringBuffer.append("\\r");
      } else if (c == '\\') {
        paramStringBuffer.append("\\\\");
      } else if (c == '"') {
        paramStringBuffer.append("\\\"");
      } else if (c < ' ' || c > '') {
        paramStringBuffer.append("\\u");
        if (c < '\020') {
          paramStringBuffer.append("000");
        } else if (c < 'Ā') {
          paramStringBuffer.append("00");
        } else if (c < 'က') {
          paramStringBuffer.append('0');
        } 
        paramStringBuffer.append(Integer.toString(c, 16));
      } else {
        paramStringBuffer.append(c);
      } 
    } 
    paramStringBuffer.append('"');
  }
  
  static void printList(PrintWriter paramPrintWriter, List<?> paramList) {
    for (byte b = 0; b < paramList.size(); b++) {
      Object object = paramList.get(b);
      if (object instanceof List) {
        printList(paramPrintWriter, (List)object);
      } else {
        paramPrintWriter.print(object.toString());
      } 
    } 
  }
  
  static  {
    String str = "NOP,ACONST_NULL,ICONST_M1,ICONST_0,ICONST_1,ICONST_2,ICONST_3,ICONST_4,ICONST_5,LCONST_0,LCONST_1,FCONST_0,FCONST_1,FCONST_2,DCONST_0,DCONST_1,BIPUSH,SIPUSH,LDC,,,ILOAD,LLOAD,FLOAD,DLOAD,ALOAD,,,,,,,,,,,,,,,,,,,,,IALOAD,LALOAD,FALOAD,DALOAD,AALOAD,BALOAD,CALOAD,SALOAD,ISTORE,LSTORE,FSTORE,DSTORE,ASTORE,,,,,,,,,,,,,,,,,,,,,IASTORE,LASTORE,FASTORE,DASTORE,AASTORE,BASTORE,CASTORE,SASTORE,POP,POP2,DUP,DUP_X1,DUP_X2,DUP2,DUP2_X1,DUP2_X2,SWAP,IADD,LADD,FADD,DADD,ISUB,LSUB,FSUB,DSUB,IMUL,LMUL,FMUL,DMUL,IDIV,LDIV,FDIV,DDIV,IREM,LREM,FREM,DREM,INEG,LNEG,FNEG,DNEG,ISHL,LSHL,ISHR,LSHR,IUSHR,LUSHR,IAND,LAND,IOR,LOR,IXOR,LXOR,IINC,I2L,I2F,I2D,L2I,L2F,L2D,F2I,F2L,F2D,D2I,D2L,D2F,I2B,I2C,I2S,LCMP,FCMPL,FCMPG,DCMPL,DCMPG,IFEQ,IFNE,IFLT,IFGE,IFGT,IFLE,IF_ICMPEQ,IF_ICMPNE,IF_ICMPLT,IF_ICMPGE,IF_ICMPGT,IF_ICMPLE,IF_ACMPEQ,IF_ACMPNE,GOTO,JSR,RET,TABLESWITCH,LOOKUPSWITCH,IRETURN,LRETURN,FRETURN,DRETURN,ARETURN,RETURN,GETSTATIC,PUTSTATIC,GETFIELD,PUTFIELD,INVOKEVIRTUAL,INVOKESPECIAL,INVOKESTATIC,INVOKEINTERFACE,INVOKEDYNAMIC,NEW,NEWARRAY,ANEWARRAY,ARRAYLENGTH,ATHROW,CHECKCAST,INSTANCEOF,MONITORENTER,MONITOREXIT,,MULTIANEWARRAY,IFNULL,IFNONNULL,";
    OPCODES = new String[200];
    byte b = 0;
    int i;
    int j;
    for (i = 0; (j = str.indexOf(',', i)) > 0; i = j + 1)
      OPCODES[b++] = (i + 1 == j) ? null : str.substring(i, j); 
    str = "T_BOOLEAN,T_CHAR,T_FLOAT,T_DOUBLE,T_BYTE,T_SHORT,T_INT,T_LONG,";
    TYPES = new String[12];
    i = 0;
    b = 4;
    while ((j = str.indexOf(',', i)) > 0) {
      TYPES[b++] = str.substring(i, j);
      i = j + 1;
    } 
    str = "H_GETFIELD,H_GETSTATIC,H_PUTFIELD,H_PUTSTATIC,H_INVOKEVIRTUAL,H_INVOKESTATIC,H_INVOKESPECIAL,H_NEWINVOKESPECIAL,H_INVOKEINTERFACE,";
    HANDLE_TAG = new String[10];
    i = 0;
    b = 1;
    while ((j = str.indexOf(',', i)) > 0) {
      HANDLE_TAG[b++] = str.substring(i, j);
      i = j + 1;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\as\\util\Printer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */