package javax.lang.model.util;

import java.util.List;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SimpleAnnotationValueVisitor6<R, P> extends AbstractAnnotationValueVisitor6<R, P> {
  protected final R DEFAULT_VALUE = null;
  
  protected SimpleAnnotationValueVisitor6() {}
  
  protected SimpleAnnotationValueVisitor6(R paramR) {}
  
  protected R defaultAction(Object paramObject, P paramP) { return (R)this.DEFAULT_VALUE; }
  
  public R visitBoolean(boolean paramBoolean, P paramP) { return (R)defaultAction(Boolean.valueOf(paramBoolean), paramP); }
  
  public R visitByte(byte paramByte, P paramP) { return (R)defaultAction(Byte.valueOf(paramByte), paramP); }
  
  public R visitChar(char paramChar, P paramP) { return (R)defaultAction(Character.valueOf(paramChar), paramP); }
  
  public R visitDouble(double paramDouble, P paramP) { return (R)defaultAction(Double.valueOf(paramDouble), paramP); }
  
  public R visitFloat(float paramFloat, P paramP) { return (R)defaultAction(Float.valueOf(paramFloat), paramP); }
  
  public R visitInt(int paramInt, P paramP) { return (R)defaultAction(Integer.valueOf(paramInt), paramP); }
  
  public R visitLong(long paramLong, P paramP) { return (R)defaultAction(Long.valueOf(paramLong), paramP); }
  
  public R visitShort(short paramShort, P paramP) { return (R)defaultAction(Short.valueOf(paramShort), paramP); }
  
  public R visitString(String paramString, P paramP) { return (R)defaultAction(paramString, paramP); }
  
  public R visitType(TypeMirror paramTypeMirror, P paramP) { return (R)defaultAction(paramTypeMirror, paramP); }
  
  public R visitEnumConstant(VariableElement paramVariableElement, P paramP) { return (R)defaultAction(paramVariableElement, paramP); }
  
  public R visitAnnotation(AnnotationMirror paramAnnotationMirror, P paramP) { return (R)defaultAction(paramAnnotationMirror, paramP); }
  
  public R visitArray(List<? extends AnnotationValue> paramList, P paramP) { return (R)defaultAction(paramList, paramP); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\mode\\util\SimpleAnnotationValueVisitor6.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */