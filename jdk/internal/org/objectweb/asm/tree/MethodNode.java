package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.TypePath;

public class MethodNode extends MethodVisitor {
  public int access;
  
  public String name;
  
  public String desc;
  
  public String signature;
  
  public List<String> exceptions;
  
  public List<ParameterNode> parameters;
  
  public List<AnnotationNode> visibleAnnotations;
  
  public List<AnnotationNode> invisibleAnnotations;
  
  public List<TypeAnnotationNode> visibleTypeAnnotations;
  
  public List<TypeAnnotationNode> invisibleTypeAnnotations;
  
  public List<Attribute> attrs;
  
  public Object annotationDefault;
  
  public List<AnnotationNode>[] visibleParameterAnnotations;
  
  public List<AnnotationNode>[] invisibleParameterAnnotations;
  
  public InsnList instructions;
  
  public List<TryCatchBlockNode> tryCatchBlocks;
  
  public int maxStack;
  
  public int maxLocals;
  
  public List<LocalVariableNode> localVariables;
  
  public List<LocalVariableAnnotationNode> visibleLocalVariableAnnotations;
  
  public List<LocalVariableAnnotationNode> invisibleLocalVariableAnnotations;
  
  private boolean visited;
  
  public MethodNode() {
    this(327680);
    if (getClass() != MethodNode.class)
      throw new IllegalStateException(); 
  }
  
  public MethodNode(int paramInt) {
    super(paramInt);
    this.instructions = new InsnList();
  }
  
  public MethodNode(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    this(327680, paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    if (getClass() != MethodNode.class)
      throw new IllegalStateException(); 
  }
  
  public MethodNode(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    super(paramInt1);
    this.access = paramInt2;
    this.name = paramString1;
    this.desc = paramString2;
    this.signature = paramString3;
    this.exceptions = new ArrayList((paramArrayOfString == null) ? 0 : paramArrayOfString.length);
    boolean bool = ((paramInt2 & 0x400) != 0) ? 1 : 0;
    if (!bool)
      this.localVariables = new ArrayList(5); 
    this.tryCatchBlocks = new ArrayList();
    if (paramArrayOfString != null)
      this.exceptions.addAll(Arrays.asList(paramArrayOfString)); 
    this.instructions = new InsnList();
  }
  
  public void visitParameter(String paramString, int paramInt) {
    if (this.parameters == null)
      this.parameters = new ArrayList(5); 
    this.parameters.add(new ParameterNode(paramString, paramInt));
  }
  
  public AnnotationVisitor visitAnnotationDefault() { return new AnnotationNode(new ArrayList<Object>(this, 0) {
          public boolean add(Object param1Object) {
            MethodNode.this.annotationDefault = param1Object;
            return super.add(param1Object);
          }
        }); }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean) {
    AnnotationNode annotationNode = new AnnotationNode(paramString);
    if (paramBoolean) {
      if (this.visibleAnnotations == null)
        this.visibleAnnotations = new ArrayList(1); 
      this.visibleAnnotations.add(annotationNode);
    } else {
      if (this.invisibleAnnotations == null)
        this.invisibleAnnotations = new ArrayList(1); 
      this.invisibleAnnotations.add(annotationNode);
    } 
    return annotationNode;
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    TypeAnnotationNode typeAnnotationNode = new TypeAnnotationNode(paramInt, paramTypePath, paramString);
    if (paramBoolean) {
      if (this.visibleTypeAnnotations == null)
        this.visibleTypeAnnotations = new ArrayList(1); 
      this.visibleTypeAnnotations.add(typeAnnotationNode);
    } else {
      if (this.invisibleTypeAnnotations == null)
        this.invisibleTypeAnnotations = new ArrayList(1); 
      this.invisibleTypeAnnotations.add(typeAnnotationNode);
    } 
    return typeAnnotationNode;
  }
  
  public AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean) {
    AnnotationNode annotationNode = new AnnotationNode(paramString);
    if (paramBoolean) {
      if (this.visibleParameterAnnotations == null) {
        int i = Type.getArgumentTypes(this.desc).length;
        this.visibleParameterAnnotations = (List[])new List[i];
      } 
      if (this.visibleParameterAnnotations[paramInt] == null)
        this.visibleParameterAnnotations[paramInt] = new ArrayList(1); 
      this.visibleParameterAnnotations[paramInt].add(annotationNode);
    } else {
      if (this.invisibleParameterAnnotations == null) {
        int i = Type.getArgumentTypes(this.desc).length;
        this.invisibleParameterAnnotations = (List[])new List[i];
      } 
      if (this.invisibleParameterAnnotations[paramInt] == null)
        this.invisibleParameterAnnotations[paramInt] = new ArrayList(1); 
      this.invisibleParameterAnnotations[paramInt].add(annotationNode);
    } 
    return annotationNode;
  }
  
  public void visitAttribute(Attribute paramAttribute) {
    if (this.attrs == null)
      this.attrs = new ArrayList(1); 
    this.attrs.add(paramAttribute);
  }
  
  public void visitCode() {}
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2) { this.instructions.add(new FrameNode(paramInt1, paramInt2, (paramArrayOfObject1 == null) ? null : getLabelNodes(paramArrayOfObject1), paramInt3, (paramArrayOfObject2 == null) ? null : getLabelNodes(paramArrayOfObject2))); }
  
  public void visitInsn(int paramInt) { this.instructions.add(new InsnNode(paramInt)); }
  
  public void visitIntInsn(int paramInt1, int paramInt2) { this.instructions.add(new IntInsnNode(paramInt1, paramInt2)); }
  
  public void visitVarInsn(int paramInt1, int paramInt2) { this.instructions.add(new VarInsnNode(paramInt1, paramInt2)); }
  
  public void visitTypeInsn(int paramInt, String paramString) { this.instructions.add(new TypeInsnNode(paramInt, paramString)); }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3) { this.instructions.add(new FieldInsnNode(paramInt, paramString1, paramString2, paramString3)); }
  
  @Deprecated
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    if (this.api >= 327680) {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
      return;
    } 
    this.instructions.add(new MethodInsnNode(paramInt, paramString1, paramString2, paramString3));
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    if (this.api < 327680) {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
      return;
    } 
    this.instructions.add(new MethodInsnNode(paramInt, paramString1, paramString2, paramString3, paramBoolean));
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs) { this.instructions.add(new InvokeDynamicInsnNode(paramString1, paramString2, paramHandle, paramVarArgs)); }
  
  public void visitJumpInsn(int paramInt, Label paramLabel) { this.instructions.add(new JumpInsnNode(paramInt, getLabelNode(paramLabel))); }
  
  public void visitLabel(Label paramLabel) { this.instructions.add(getLabelNode(paramLabel)); }
  
  public void visitLdcInsn(Object paramObject) { this.instructions.add(new LdcInsnNode(paramObject)); }
  
  public void visitIincInsn(int paramInt1, int paramInt2) { this.instructions.add(new IincInsnNode(paramInt1, paramInt2)); }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs) { this.instructions.add(new TableSwitchInsnNode(paramInt1, paramInt2, getLabelNode(paramLabel), getLabelNodes(paramVarArgs))); }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel) { this.instructions.add(new LookupSwitchInsnNode(getLabelNode(paramLabel), paramArrayOfInt, getLabelNodes(paramArrayOfLabel))); }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt) { this.instructions.add(new MultiANewArrayInsnNode(paramString, paramInt)); }
  
  public AnnotationVisitor visitInsnAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    AbstractInsnNode abstractInsnNode;
    for (abstractInsnNode = this.instructions.getLast(); abstractInsnNode.getOpcode() == -1; abstractInsnNode = abstractInsnNode.getPrevious());
    TypeAnnotationNode typeAnnotationNode = new TypeAnnotationNode(paramInt, paramTypePath, paramString);
    if (paramBoolean) {
      if (abstractInsnNode.visibleTypeAnnotations == null)
        abstractInsnNode.visibleTypeAnnotations = new ArrayList(1); 
      abstractInsnNode.visibleTypeAnnotations.add(typeAnnotationNode);
    } else {
      if (abstractInsnNode.invisibleTypeAnnotations == null)
        abstractInsnNode.invisibleTypeAnnotations = new ArrayList(1); 
      abstractInsnNode.invisibleTypeAnnotations.add(typeAnnotationNode);
    } 
    return typeAnnotationNode;
  }
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString) { this.tryCatchBlocks.add(new TryCatchBlockNode(getLabelNode(paramLabel1), getLabelNode(paramLabel2), getLabelNode(paramLabel3), paramString)); }
  
  public AnnotationVisitor visitTryCatchAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    TryCatchBlockNode tryCatchBlockNode = (TryCatchBlockNode)this.tryCatchBlocks.get((paramInt & 0xFFFF00) >> 8);
    TypeAnnotationNode typeAnnotationNode = new TypeAnnotationNode(paramInt, paramTypePath, paramString);
    if (paramBoolean) {
      if (tryCatchBlockNode.visibleTypeAnnotations == null)
        tryCatchBlockNode.visibleTypeAnnotations = new ArrayList(1); 
      tryCatchBlockNode.visibleTypeAnnotations.add(typeAnnotationNode);
    } else {
      if (tryCatchBlockNode.invisibleTypeAnnotations == null)
        tryCatchBlockNode.invisibleTypeAnnotations = new ArrayList(1); 
      tryCatchBlockNode.invisibleTypeAnnotations.add(typeAnnotationNode);
    } 
    return typeAnnotationNode;
  }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt) { this.localVariables.add(new LocalVariableNode(paramString1, paramString2, paramString3, getLabelNode(paramLabel1), getLabelNode(paramLabel2), paramInt)); }
  
  public AnnotationVisitor visitLocalVariableAnnotation(int paramInt, TypePath paramTypePath, Label[] paramArrayOfLabel1, Label[] paramArrayOfLabel2, int[] paramArrayOfInt, String paramString, boolean paramBoolean) {
    LocalVariableAnnotationNode localVariableAnnotationNode = new LocalVariableAnnotationNode(paramInt, paramTypePath, getLabelNodes(paramArrayOfLabel1), getLabelNodes(paramArrayOfLabel2), paramArrayOfInt, paramString);
    if (paramBoolean) {
      if (this.visibleLocalVariableAnnotations == null)
        this.visibleLocalVariableAnnotations = new ArrayList(1); 
      this.visibleLocalVariableAnnotations.add(localVariableAnnotationNode);
    } else {
      if (this.invisibleLocalVariableAnnotations == null)
        this.invisibleLocalVariableAnnotations = new ArrayList(1); 
      this.invisibleLocalVariableAnnotations.add(localVariableAnnotationNode);
    } 
    return localVariableAnnotationNode;
  }
  
  public void visitLineNumber(int paramInt, Label paramLabel) { this.instructions.add(new LineNumberNode(paramInt, getLabelNode(paramLabel))); }
  
  public void visitMaxs(int paramInt1, int paramInt2) {
    this.maxStack = paramInt1;
    this.maxLocals = paramInt2;
  }
  
  public void visitEnd() {}
  
  protected LabelNode getLabelNode(Label paramLabel) {
    if (!(paramLabel.info instanceof LabelNode))
      paramLabel.info = new LabelNode(); 
    return (LabelNode)paramLabel.info;
  }
  
  private LabelNode[] getLabelNodes(Label[] paramArrayOfLabel) {
    LabelNode[] arrayOfLabelNode = new LabelNode[paramArrayOfLabel.length];
    for (byte b = 0; b < paramArrayOfLabel.length; b++)
      arrayOfLabelNode[b] = getLabelNode(paramArrayOfLabel[b]); 
    return arrayOfLabelNode;
  }
  
  private Object[] getLabelNodes(Object[] paramArrayOfObject) {
    Object[] arrayOfObject = new Object[paramArrayOfObject.length];
    for (byte b = 0; b < paramArrayOfObject.length; b++) {
      Object object = paramArrayOfObject[b];
      if (object instanceof Label)
        object = getLabelNode((Label)object); 
      arrayOfObject[b] = object;
    } 
    return arrayOfObject;
  }
  
  public void check(int paramInt) {
    if (paramInt == 262144) {
      if (this.visibleTypeAnnotations != null && this.visibleTypeAnnotations.size() > 0)
        throw new RuntimeException(); 
      if (this.invisibleTypeAnnotations != null && this.invisibleTypeAnnotations.size() > 0)
        throw new RuntimeException(); 
      boolean bool = (this.tryCatchBlocks == null) ? 0 : this.tryCatchBlocks.size();
      byte b;
      for (b = 0; b < bool; b++) {
        TryCatchBlockNode tryCatchBlockNode = (TryCatchBlockNode)this.tryCatchBlocks.get(b);
        if (tryCatchBlockNode.visibleTypeAnnotations != null && tryCatchBlockNode.visibleTypeAnnotations.size() > 0)
          throw new RuntimeException(); 
        if (tryCatchBlockNode.invisibleTypeAnnotations != null && tryCatchBlockNode.invisibleTypeAnnotations.size() > 0)
          throw new RuntimeException(); 
      } 
      for (b = 0; b < this.instructions.size(); b++) {
        AbstractInsnNode abstractInsnNode = this.instructions.get(b);
        if (abstractInsnNode.visibleTypeAnnotations != null && abstractInsnNode.visibleTypeAnnotations.size() > 0)
          throw new RuntimeException(); 
        if (abstractInsnNode.invisibleTypeAnnotations != null && abstractInsnNode.invisibleTypeAnnotations.size() > 0)
          throw new RuntimeException(); 
        if (abstractInsnNode instanceof MethodInsnNode) {
          boolean bool1 = ((MethodInsnNode)abstractInsnNode).itf;
          if (bool1 != ((abstractInsnNode.opcode == 185)))
            throw new RuntimeException(); 
        } 
      } 
      if (this.visibleLocalVariableAnnotations != null && this.visibleLocalVariableAnnotations.size() > 0)
        throw new RuntimeException(); 
      if (this.invisibleLocalVariableAnnotations != null && this.invisibleLocalVariableAnnotations.size() > 0)
        throw new RuntimeException(); 
    } 
  }
  
  public void accept(ClassVisitor paramClassVisitor) {
    String[] arrayOfString = new String[this.exceptions.size()];
    this.exceptions.toArray(arrayOfString);
    MethodVisitor methodVisitor = paramClassVisitor.visitMethod(this.access, this.name, this.desc, this.signature, arrayOfString);
    if (methodVisitor != null)
      accept(methodVisitor); 
  }
  
  public void accept(MethodVisitor paramMethodVisitor) {
    boolean bool = (this.parameters == null) ? 0 : this.parameters.size();
    byte b;
    for (b = 0; b < bool; b++) {
      ParameterNode parameterNode = (ParameterNode)this.parameters.get(b);
      paramMethodVisitor.visitParameter(parameterNode.name, parameterNode.access);
    } 
    if (this.annotationDefault != null) {
      AnnotationVisitor annotationVisitor = paramMethodVisitor.visitAnnotationDefault();
      AnnotationNode.accept(annotationVisitor, null, this.annotationDefault);
      if (annotationVisitor != null)
        annotationVisitor.visitEnd(); 
    } 
    bool = (this.visibleAnnotations == null) ? 0 : this.visibleAnnotations.size();
    for (b = 0; b < bool; b++) {
      AnnotationNode annotationNode = (AnnotationNode)this.visibleAnnotations.get(b);
      annotationNode.accept(paramMethodVisitor.visitAnnotation(annotationNode.desc, true));
    } 
    bool = (this.invisibleAnnotations == null) ? 0 : this.invisibleAnnotations.size();
    for (b = 0; b < bool; b++) {
      AnnotationNode annotationNode = (AnnotationNode)this.invisibleAnnotations.get(b);
      annotationNode.accept(paramMethodVisitor.visitAnnotation(annotationNode.desc, false));
    } 
    bool = (this.visibleTypeAnnotations == null) ? 0 : this.visibleTypeAnnotations.size();
    for (b = 0; b < bool; b++) {
      TypeAnnotationNode typeAnnotationNode = (TypeAnnotationNode)this.visibleTypeAnnotations.get(b);
      typeAnnotationNode.accept(paramMethodVisitor.visitTypeAnnotation(typeAnnotationNode.typeRef, typeAnnotationNode.typePath, typeAnnotationNode.desc, true));
    } 
    bool = (this.invisibleTypeAnnotations == null) ? 0 : this.invisibleTypeAnnotations.size();
    for (b = 0; b < bool; b++) {
      TypeAnnotationNode typeAnnotationNode = (TypeAnnotationNode)this.invisibleTypeAnnotations.get(b);
      typeAnnotationNode.accept(paramMethodVisitor.visitTypeAnnotation(typeAnnotationNode.typeRef, typeAnnotationNode.typePath, typeAnnotationNode.desc, false));
    } 
    bool = (this.visibleParameterAnnotations == null) ? 0 : this.visibleParameterAnnotations.length;
    for (b = 0; b < bool; b++) {
      List list = this.visibleParameterAnnotations[b];
      if (list != null)
        for (byte b1 = 0; b1 < list.size(); b1++) {
          AnnotationNode annotationNode = (AnnotationNode)list.get(b1);
          annotationNode.accept(paramMethodVisitor.visitParameterAnnotation(b, annotationNode.desc, true));
        }  
    } 
    bool = (this.invisibleParameterAnnotations == null) ? 0 : this.invisibleParameterAnnotations.length;
    for (b = 0; b < bool; b++) {
      List list = this.invisibleParameterAnnotations[b];
      if (list != null)
        for (byte b1 = 0; b1 < list.size(); b1++) {
          AnnotationNode annotationNode = (AnnotationNode)list.get(b1);
          annotationNode.accept(paramMethodVisitor.visitParameterAnnotation(b, annotationNode.desc, false));
        }  
    } 
    if (this.visited)
      this.instructions.resetLabels(); 
    bool = (this.attrs == null) ? 0 : this.attrs.size();
    for (b = 0; b < bool; b++)
      paramMethodVisitor.visitAttribute((Attribute)this.attrs.get(b)); 
    if (this.instructions.size() > 0) {
      paramMethodVisitor.visitCode();
      bool = (this.tryCatchBlocks == null) ? 0 : this.tryCatchBlocks.size();
      for (b = 0; b < bool; b++) {
        ((TryCatchBlockNode)this.tryCatchBlocks.get(b)).updateIndex(b);
        ((TryCatchBlockNode)this.tryCatchBlocks.get(b)).accept(paramMethodVisitor);
      } 
      this.instructions.accept(paramMethodVisitor);
      bool = (this.localVariables == null) ? 0 : this.localVariables.size();
      for (b = 0; b < bool; b++)
        ((LocalVariableNode)this.localVariables.get(b)).accept(paramMethodVisitor); 
      bool = (this.visibleLocalVariableAnnotations == null) ? 0 : this.visibleLocalVariableAnnotations.size();
      for (b = 0; b < bool; b++)
        ((LocalVariableAnnotationNode)this.visibleLocalVariableAnnotations.get(b)).accept(paramMethodVisitor, true); 
      bool = (this.invisibleLocalVariableAnnotations == null) ? 0 : this.invisibleLocalVariableAnnotations.size();
      for (b = 0; b < bool; b++)
        ((LocalVariableAnnotationNode)this.invisibleLocalVariableAnnotations.get(b)).accept(paramMethodVisitor, false); 
      paramMethodVisitor.visitMaxs(this.maxStack, this.maxLocals);
      this.visited = true;
    } 
    paramMethodVisitor.visitEnd();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\MethodNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */