package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;

public class AnnotationNode extends AnnotationVisitor {
  public String desc;
  
  public List<Object> values;
  
  public AnnotationNode(String paramString) {
    this(327680, paramString);
    if (getClass() != AnnotationNode.class)
      throw new IllegalStateException(); 
  }
  
  public AnnotationNode(int paramInt, String paramString) {
    super(paramInt);
    this.desc = paramString;
  }
  
  AnnotationNode(List<Object> paramList) {
    super(327680);
    this.values = paramList;
  }
  
  public void visit(String paramString, Object paramObject) {
    if (this.values == null)
      this.values = new ArrayList((this.desc != null) ? 2 : 1); 
    if (this.desc != null)
      this.values.add(paramString); 
    this.values.add(paramObject);
  }
  
  public void visitEnum(String paramString1, String paramString2, String paramString3) {
    if (this.values == null)
      this.values = new ArrayList((this.desc != null) ? 2 : 1); 
    if (this.desc != null)
      this.values.add(paramString1); 
    this.values.add(new String[] { paramString2, paramString3 });
  }
  
  public AnnotationVisitor visitAnnotation(String paramString1, String paramString2) {
    if (this.values == null)
      this.values = new ArrayList((this.desc != null) ? 2 : 1); 
    if (this.desc != null)
      this.values.add(paramString1); 
    AnnotationNode annotationNode = new AnnotationNode(paramString2);
    this.values.add(annotationNode);
    return annotationNode;
  }
  
  public AnnotationVisitor visitArray(String paramString) {
    if (this.values == null)
      this.values = new ArrayList((this.desc != null) ? 2 : 1); 
    if (this.desc != null)
      this.values.add(paramString); 
    ArrayList arrayList = new ArrayList();
    this.values.add(arrayList);
    return new AnnotationNode(arrayList);
  }
  
  public void visitEnd() {}
  
  public void check(int paramInt) {}
  
  public void accept(AnnotationVisitor paramAnnotationVisitor) {
    if (paramAnnotationVisitor != null) {
      if (this.values != null)
        for (byte b = 0; b < this.values.size(); b += 2) {
          String str = (String)this.values.get(b);
          Object object = this.values.get(b + 1);
          accept(paramAnnotationVisitor, str, object);
        }  
      paramAnnotationVisitor.visitEnd();
    } 
  }
  
  static void accept(AnnotationVisitor paramAnnotationVisitor, String paramString, Object paramObject) {
    if (paramAnnotationVisitor != null)
      if (paramObject instanceof String[]) {
        String[] arrayOfString = (String[])paramObject;
        paramAnnotationVisitor.visitEnum(paramString, arrayOfString[0], arrayOfString[1]);
      } else if (paramObject instanceof AnnotationNode) {
        AnnotationNode annotationNode = (AnnotationNode)paramObject;
        annotationNode.accept(paramAnnotationVisitor.visitAnnotation(paramString, annotationNode.desc));
      } else if (paramObject instanceof List) {
        AnnotationVisitor annotationVisitor = paramAnnotationVisitor.visitArray(paramString);
        if (annotationVisitor != null) {
          List list = (List)paramObject;
          for (byte b = 0; b < list.size(); b++)
            accept(annotationVisitor, null, list.get(b)); 
          annotationVisitor.visitEnd();
        } 
      } else {
        paramAnnotationVisitor.visit(paramString, paramObject);
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\AnnotationNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */