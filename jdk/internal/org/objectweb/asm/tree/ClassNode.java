package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public class ClassNode extends ClassVisitor {
  public int version;
  
  public int access;
  
  public String name;
  
  public String signature;
  
  public String superName;
  
  public List<String> interfaces = new ArrayList();
  
  public String sourceFile;
  
  public String sourceDebug;
  
  public String outerClass;
  
  public String outerMethod;
  
  public String outerMethodDesc;
  
  public List<AnnotationNode> visibleAnnotations;
  
  public List<AnnotationNode> invisibleAnnotations;
  
  public List<TypeAnnotationNode> visibleTypeAnnotations;
  
  public List<TypeAnnotationNode> invisibleTypeAnnotations;
  
  public List<Attribute> attrs;
  
  public List<InnerClassNode> innerClasses = new ArrayList();
  
  public List<FieldNode> fields = new ArrayList();
  
  public List<MethodNode> methods = new ArrayList();
  
  public ClassNode() {
    this(327680);
    if (getClass() != ClassNode.class)
      throw new IllegalStateException(); 
  }
  
  public ClassNode(int paramInt) { super(paramInt); }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    this.version = paramInt1;
    this.access = paramInt2;
    this.name = paramString1;
    this.signature = paramString2;
    this.superName = paramString3;
    if (paramArrayOfString != null)
      this.interfaces.addAll(Arrays.asList(paramArrayOfString)); 
  }
  
  public void visitSource(String paramString1, String paramString2) {
    this.sourceFile = paramString1;
    this.sourceDebug = paramString2;
  }
  
  public void visitOuterClass(String paramString1, String paramString2, String paramString3) {
    this.outerClass = paramString1;
    this.outerMethod = paramString2;
    this.outerMethodDesc = paramString3;
  }
  
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
  
  public void visitAttribute(Attribute paramAttribute) {
    if (this.attrs == null)
      this.attrs = new ArrayList(1); 
    this.attrs.add(paramAttribute);
  }
  
  public void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt) {
    InnerClassNode innerClassNode = new InnerClassNode(paramString1, paramString2, paramString3, paramInt);
    this.innerClasses.add(innerClassNode);
  }
  
  public FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject) {
    FieldNode fieldNode = new FieldNode(paramInt, paramString1, paramString2, paramString3, paramObject);
    this.fields.add(fieldNode);
    return fieldNode;
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    MethodNode methodNode = new MethodNode(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    this.methods.add(methodNode);
    return methodNode;
  }
  
  public void visitEnd() {}
  
  public void check(int paramInt) {
    if (paramInt == 262144) {
      if (this.visibleTypeAnnotations != null && this.visibleTypeAnnotations.size() > 0)
        throw new RuntimeException(); 
      if (this.invisibleTypeAnnotations != null && this.invisibleTypeAnnotations.size() > 0)
        throw new RuntimeException(); 
      for (FieldNode fieldNode : this.fields)
        fieldNode.check(paramInt); 
      for (MethodNode methodNode : this.methods)
        methodNode.check(paramInt); 
    } 
  }
  
  public void accept(ClassVisitor paramClassVisitor) {
    String[] arrayOfString = new String[this.interfaces.size()];
    this.interfaces.toArray(arrayOfString);
    paramClassVisitor.visit(this.version, this.access, this.name, this.signature, this.superName, arrayOfString);
    if (this.sourceFile != null || this.sourceDebug != null)
      paramClassVisitor.visitSource(this.sourceFile, this.sourceDebug); 
    if (this.outerClass != null)
      paramClassVisitor.visitOuterClass(this.outerClass, this.outerMethod, this.outerMethodDesc); 
    boolean bool = (this.visibleAnnotations == null) ? 0 : this.visibleAnnotations.size();
    byte b;
    for (b = 0; b < bool; b++) {
      AnnotationNode annotationNode = (AnnotationNode)this.visibleAnnotations.get(b);
      annotationNode.accept(paramClassVisitor.visitAnnotation(annotationNode.desc, true));
    } 
    bool = (this.invisibleAnnotations == null) ? 0 : this.invisibleAnnotations.size();
    for (b = 0; b < bool; b++) {
      AnnotationNode annotationNode = (AnnotationNode)this.invisibleAnnotations.get(b);
      annotationNode.accept(paramClassVisitor.visitAnnotation(annotationNode.desc, false));
    } 
    bool = (this.visibleTypeAnnotations == null) ? 0 : this.visibleTypeAnnotations.size();
    for (b = 0; b < bool; b++) {
      TypeAnnotationNode typeAnnotationNode = (TypeAnnotationNode)this.visibleTypeAnnotations.get(b);
      typeAnnotationNode.accept(paramClassVisitor.visitTypeAnnotation(typeAnnotationNode.typeRef, typeAnnotationNode.typePath, typeAnnotationNode.desc, true));
    } 
    bool = (this.invisibleTypeAnnotations == null) ? 0 : this.invisibleTypeAnnotations.size();
    for (b = 0; b < bool; b++) {
      TypeAnnotationNode typeAnnotationNode = (TypeAnnotationNode)this.invisibleTypeAnnotations.get(b);
      typeAnnotationNode.accept(paramClassVisitor.visitTypeAnnotation(typeAnnotationNode.typeRef, typeAnnotationNode.typePath, typeAnnotationNode.desc, false));
    } 
    bool = (this.attrs == null) ? 0 : this.attrs.size();
    for (b = 0; b < bool; b++)
      paramClassVisitor.visitAttribute((Attribute)this.attrs.get(b)); 
    for (b = 0; b < this.innerClasses.size(); b++)
      ((InnerClassNode)this.innerClasses.get(b)).accept(paramClassVisitor); 
    for (b = 0; b < this.fields.size(); b++)
      ((FieldNode)this.fields.get(b)).accept(paramClassVisitor); 
    for (b = 0; b < this.methods.size(); b++)
      ((MethodNode)this.methods.get(b)).accept(paramClassVisitor); 
    paramClassVisitor.visitEnd();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\ClassNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */