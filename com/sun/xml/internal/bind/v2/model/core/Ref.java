package com.sun.xml.internal.bind.v2.model.core;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.impl.ModelBuilderI;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public final class Ref<T, C> extends Object {
  public final T type;
  
  public final Adapter<T, C> adapter;
  
  public final boolean valueList;
  
  public Ref(T paramT) { this(paramT, null, false); }
  
  public Ref(T paramT, Adapter<T, C> paramAdapter, boolean paramBoolean) {
    this.adapter = paramAdapter;
    if (paramAdapter != null)
      paramT = (T)paramAdapter.defaultType; 
    this.type = paramT;
    this.valueList = paramBoolean;
  }
  
  public Ref(ModelBuilderI<T, C, ?, ?> paramModelBuilderI, T paramT, XmlJavaTypeAdapter paramXmlJavaTypeAdapter, XmlList paramXmlList) { this(paramModelBuilderI.getReader(), paramModelBuilderI.getNavigator(), paramT, paramXmlJavaTypeAdapter, paramXmlList); }
  
  public Ref(AnnotationReader<T, C, ?, ?> paramAnnotationReader, Navigator<T, C, ?, ?> paramNavigator, T paramT, XmlJavaTypeAdapter paramXmlJavaTypeAdapter, XmlList paramXmlList) {
    Adapter adapter1 = null;
    if (paramXmlJavaTypeAdapter != null) {
      adapter1 = new Adapter(paramXmlJavaTypeAdapter, paramAnnotationReader, paramNavigator);
      paramT = (T)adapter1.defaultType;
    } 
    this.type = paramT;
    this.adapter = adapter1;
    this.valueList = (paramXmlList != null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\core\Ref.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */