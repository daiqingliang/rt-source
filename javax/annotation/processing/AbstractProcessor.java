package javax.annotation.processing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

public abstract class AbstractProcessor implements Processor {
  protected ProcessingEnvironment processingEnv;
  
  private boolean initialized = false;
  
  public Set<String> getSupportedOptions() {
    SupportedOptions supportedOptions = (SupportedOptions)getClass().getAnnotation(SupportedOptions.class);
    return (supportedOptions == null) ? Collections.emptySet() : arrayToSet(supportedOptions.value());
  }
  
  public Set<String> getSupportedAnnotationTypes() {
    SupportedAnnotationTypes supportedAnnotationTypes = (SupportedAnnotationTypes)getClass().getAnnotation(SupportedAnnotationTypes.class);
    if (supportedAnnotationTypes == null) {
      if (isInitialized())
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "No SupportedAnnotationTypes annotation found on " + getClass().getName() + ", returning an empty set."); 
      return Collections.emptySet();
    } 
    return arrayToSet(supportedAnnotationTypes.value());
  }
  
  public SourceVersion getSupportedSourceVersion() {
    SupportedSourceVersion supportedSourceVersion = (SupportedSourceVersion)getClass().getAnnotation(SupportedSourceVersion.class);
    SourceVersion sourceVersion = null;
    if (supportedSourceVersion == null) {
      sourceVersion = SourceVersion.RELEASE_6;
      if (isInitialized())
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "No SupportedSourceVersion annotation found on " + getClass().getName() + ", returning " + sourceVersion + "."); 
    } else {
      sourceVersion = supportedSourceVersion.value();
    } 
    return sourceVersion;
  }
  
  public void init(ProcessingEnvironment paramProcessingEnvironment) {
    if (this.initialized)
      throw new IllegalStateException("Cannot call init more than once."); 
    Objects.requireNonNull(paramProcessingEnvironment, "Tool provided null ProcessingEnvironment");
    this.processingEnv = paramProcessingEnvironment;
    this.initialized = true;
  }
  
  public abstract boolean process(Set<? extends TypeElement> paramSet, RoundEnvironment paramRoundEnvironment);
  
  public Iterable<? extends Completion> getCompletions(Element paramElement, AnnotationMirror paramAnnotationMirror, ExecutableElement paramExecutableElement, String paramString) { return Collections.emptyList(); }
  
  protected boolean isInitialized() { return this.initialized; }
  
  private static Set<String> arrayToSet(String[] paramArrayOfString) {
    assert paramArrayOfString != null;
    HashSet hashSet = new HashSet(paramArrayOfString.length);
    for (String str : paramArrayOfString)
      hashSet.add(str); 
    return Collections.unmodifiableSet(hashSet);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\annotation\processing\AbstractProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */