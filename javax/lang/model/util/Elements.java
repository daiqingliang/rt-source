package javax.lang.model.util;

import java.io.Writer;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public interface Elements {
  PackageElement getPackageElement(CharSequence paramCharSequence);
  
  TypeElement getTypeElement(CharSequence paramCharSequence);
  
  Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror paramAnnotationMirror);
  
  String getDocComment(Element paramElement);
  
  boolean isDeprecated(Element paramElement);
  
  Name getBinaryName(TypeElement paramTypeElement);
  
  PackageElement getPackageOf(Element paramElement);
  
  List<? extends Element> getAllMembers(TypeElement paramTypeElement);
  
  List<? extends AnnotationMirror> getAllAnnotationMirrors(Element paramElement);
  
  boolean hides(Element paramElement1, Element paramElement2);
  
  boolean overrides(ExecutableElement paramExecutableElement1, ExecutableElement paramExecutableElement2, TypeElement paramTypeElement);
  
  String getConstantExpression(Object paramObject);
  
  void printElements(Writer paramWriter, Element... paramVarArgs);
  
  Name getName(CharSequence paramCharSequence);
  
  boolean isFunctionalInterface(TypeElement paramTypeElement);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\mode\\util\Elements.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */