package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SimpleElementVisitor6<R, P> extends AbstractElementVisitor6<R, P> {
  protected final R DEFAULT_VALUE = null;
  
  protected SimpleElementVisitor6() {}
  
  protected SimpleElementVisitor6(R paramR) {}
  
  protected R defaultAction(Element paramElement, P paramP) { return (R)this.DEFAULT_VALUE; }
  
  public R visitPackage(PackageElement paramPackageElement, P paramP) { return (R)defaultAction(paramPackageElement, paramP); }
  
  public R visitType(TypeElement paramTypeElement, P paramP) { return (R)defaultAction(paramTypeElement, paramP); }
  
  public R visitVariable(VariableElement paramVariableElement, P paramP) { return (paramVariableElement.getKind() != ElementKind.RESOURCE_VARIABLE) ? (R)defaultAction(paramVariableElement, paramP) : (R)visitUnknown(paramVariableElement, paramP); }
  
  public R visitExecutable(ExecutableElement paramExecutableElement, P paramP) { return (R)defaultAction(paramExecutableElement, paramP); }
  
  public R visitTypeParameter(TypeParameterElement paramTypeParameterElement, P paramP) { return (R)defaultAction(paramTypeParameterElement, paramP); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\mode\\util\SimpleElementVisitor6.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */