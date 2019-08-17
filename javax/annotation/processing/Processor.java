package javax.annotation.processing;

import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public interface Processor {
  Set<String> getSupportedOptions();
  
  Set<String> getSupportedAnnotationTypes();
  
  SourceVersion getSupportedSourceVersion();
  
  void init(ProcessingEnvironment paramProcessingEnvironment);
  
  boolean process(Set<? extends TypeElement> paramSet, RoundEnvironment paramRoundEnvironment);
  
  Iterable<? extends Completion> getCompletions(Element paramElement, AnnotationMirror paramAnnotationMirror, ExecutableElement paramExecutableElement, String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\annotation\processing\Processor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */