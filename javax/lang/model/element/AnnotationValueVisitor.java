package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.TypeMirror;

public interface AnnotationValueVisitor<R, P> {
  R visit(AnnotationValue paramAnnotationValue, P paramP);
  
  R visit(AnnotationValue paramAnnotationValue);
  
  R visitBoolean(boolean paramBoolean, P paramP);
  
  R visitByte(byte paramByte, P paramP);
  
  R visitChar(char paramChar, P paramP);
  
  R visitDouble(double paramDouble, P paramP);
  
  R visitFloat(float paramFloat, P paramP);
  
  R visitInt(int paramInt, P paramP);
  
  R visitLong(long paramLong, P paramP);
  
  R visitShort(short paramShort, P paramP);
  
  R visitString(String paramString, P paramP);
  
  R visitType(TypeMirror paramTypeMirror, P paramP);
  
  R visitEnumConstant(VariableElement paramVariableElement, P paramP);
  
  R visitAnnotation(AnnotationMirror paramAnnotationMirror, P paramP);
  
  R visitArray(List<? extends AnnotationValue> paramList, P paramP);
  
  R visitUnknown(AnnotationValue paramAnnotationValue, P paramP);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\element\AnnotationValueVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */