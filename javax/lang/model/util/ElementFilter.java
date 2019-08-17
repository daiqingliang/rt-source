package javax.lang.model.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class ElementFilter {
  private static final Set<ElementKind> CONSTRUCTOR_KIND = Collections.unmodifiableSet(EnumSet.of(ElementKind.CONSTRUCTOR));
  
  private static final Set<ElementKind> FIELD_KINDS = Collections.unmodifiableSet(EnumSet.of(ElementKind.FIELD, ElementKind.ENUM_CONSTANT));
  
  private static final Set<ElementKind> METHOD_KIND = Collections.unmodifiableSet(EnumSet.of(ElementKind.METHOD));
  
  private static final Set<ElementKind> PACKAGE_KIND = Collections.unmodifiableSet(EnumSet.of(ElementKind.PACKAGE));
  
  private static final Set<ElementKind> TYPE_KINDS = Collections.unmodifiableSet(EnumSet.of(ElementKind.CLASS, ElementKind.ENUM, ElementKind.INTERFACE, ElementKind.ANNOTATION_TYPE));
  
  public static List<VariableElement> fieldsIn(Iterable<? extends Element> paramIterable) { return listFilter(paramIterable, FIELD_KINDS, VariableElement.class); }
  
  public static Set<VariableElement> fieldsIn(Set<? extends Element> paramSet) { return setFilter(paramSet, FIELD_KINDS, VariableElement.class); }
  
  public static List<ExecutableElement> constructorsIn(Iterable<? extends Element> paramIterable) { return listFilter(paramIterable, CONSTRUCTOR_KIND, ExecutableElement.class); }
  
  public static Set<ExecutableElement> constructorsIn(Set<? extends Element> paramSet) { return setFilter(paramSet, CONSTRUCTOR_KIND, ExecutableElement.class); }
  
  public static List<ExecutableElement> methodsIn(Iterable<? extends Element> paramIterable) { return listFilter(paramIterable, METHOD_KIND, ExecutableElement.class); }
  
  public static Set<ExecutableElement> methodsIn(Set<? extends Element> paramSet) { return setFilter(paramSet, METHOD_KIND, ExecutableElement.class); }
  
  public static List<TypeElement> typesIn(Iterable<? extends Element> paramIterable) { return listFilter(paramIterable, TYPE_KINDS, TypeElement.class); }
  
  public static Set<TypeElement> typesIn(Set<? extends Element> paramSet) { return setFilter(paramSet, TYPE_KINDS, TypeElement.class); }
  
  public static List<PackageElement> packagesIn(Iterable<? extends Element> paramIterable) { return listFilter(paramIterable, PACKAGE_KIND, PackageElement.class); }
  
  public static Set<PackageElement> packagesIn(Set<? extends Element> paramSet) { return setFilter(paramSet, PACKAGE_KIND, PackageElement.class); }
  
  private static <E extends Element> List<E> listFilter(Iterable<? extends Element> paramIterable, Set<ElementKind> paramSet, Class<E> paramClass) {
    ArrayList arrayList = new ArrayList();
    for (Element element : paramIterable) {
      if (paramSet.contains(element.getKind()))
        arrayList.add(paramClass.cast(element)); 
    } 
    return arrayList;
  }
  
  private static <E extends Element> Set<E> setFilter(Set<? extends Element> paramSet1, Set<ElementKind> paramSet2, Class<E> paramClass) {
    LinkedHashSet linkedHashSet = new LinkedHashSet();
    for (Element element : paramSet1) {
      if (paramSet2.contains(element.getKind()))
        linkedHashSet.add(paramClass.cast(element)); 
    } 
    return linkedHashSet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\mode\\util\ElementFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */