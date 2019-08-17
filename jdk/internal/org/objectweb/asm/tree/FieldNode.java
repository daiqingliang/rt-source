package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public class FieldNode extends FieldVisitor {
  public int access;
  
  public String name;
  
  public String desc;
  
  public String signature;
  
  public Object value;
  
  public List<AnnotationNode> visibleAnnotations;
  
  public List<AnnotationNode> invisibleAnnotations;
  
  public List<TypeAnnotationNode> visibleTypeAnnotations;
  
  public List<TypeAnnotationNode> invisibleTypeAnnotations;
  
  public List<Attribute> attrs;
  
  public FieldNode(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject) {
    this(327680, paramInt, paramString1, paramString2, paramString3, paramObject);
    if (getClass() != FieldNode.class)
      throw new IllegalStateException(); 
  }
  
  public FieldNode(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, Object paramObject) {
    super(paramInt1);
    this.access = paramInt2;
    this.name = paramString1;
    this.desc = paramString2;
    this.signature = paramString3;
    this.value = paramObject;
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
  
  public void visitEnd() {}
  
  public void check(int paramInt) {
    if (paramInt == 262144) {
      if (this.visibleTypeAnnotations != null && this.visibleTypeAnnotations.size() > 0)
        throw new RuntimeException(); 
      if (this.invisibleTypeAnnotations != null && this.invisibleTypeAnnotations.size() > 0)
        throw new RuntimeException(); 
    } 
  }
  
  public void accept(ClassVisitor paramClassVisitor) {
    FieldVisitor fieldVisitor = paramClassVisitor.visitField(this.access, this.name, this.desc, this.signature, this.value);
    if (fieldVisitor == null)
      return; 
    boolean bool = (this.visibleAnnotations == null) ? 0 : this.visibleAnnotations.size();
    byte b;
    for (b = 0; b < bool; b++) {
      AnnotationNode annotationNode = (AnnotationNode)this.visibleAnnotations.get(b);
      annotationNode.accept(fieldVisitor.visitAnnotation(annotationNode.desc, true));
    } 
    bool = (this.invisibleAnnotations == null) ? 0 : this.invisibleAnnotations.size();
    for (b = 0; b < bool; b++) {
      AnnotationNode annotationNode = (AnnotationNode)this.invisibleAnnotations.get(b);
      annotationNode.accept(fieldVisitor.visitAnnotation(annotationNode.desc, false));
    } 
    bool = (this.visibleTypeAnnotations == null) ? 0 : this.visibleTypeAnnotations.size();
    for (b = 0; b < bool; b++) {
      TypeAnnotationNode typeAnnotationNode = (TypeAnnotationNode)this.visibleTypeAnnotations.get(b);
      typeAnnotationNode.accept(fieldVisitor.visitTypeAnnotation(typeAnnotationNode.typeRef, typeAnnotationNode.typePath, typeAnnotationNode.desc, true));
    } 
    bool = (this.invisibleTypeAnnotations == null) ? 0 : this.invisibleTypeAnnotations.size();
    for (b = 0; b < bool; b++) {
      TypeAnnotationNode typeAnnotationNode = (TypeAnnotationNode)this.invisibleTypeAnnotations.get(b);
      typeAnnotationNode.accept(fieldVisitor.visitTypeAnnotation(typeAnnotationNode.typeRef, typeAnnotationNode.typePath, typeAnnotationNode.desc, false));
    } 
    bool = (this.attrs == null) ? 0 : this.attrs.size();
    for (b = 0; b < bool; b++)
      fieldVisitor.visitAttribute((Attribute)this.attrs.get(b)); 
    fieldVisitor.visitEnd();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\FieldNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */