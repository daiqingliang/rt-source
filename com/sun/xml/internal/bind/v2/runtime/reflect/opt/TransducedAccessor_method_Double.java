package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;
import org.xml.sax.SAXException;

public final class TransducedAccessor_method_Double extends DefaultTransducedAccessor {
  public String print(Object paramObject) { return DatatypeConverterImpl._printDouble(((Bean)paramObject).get_double()); }
  
  public void parse(Object paramObject, CharSequence paramCharSequence) { ((Bean)paramObject).set_double(DatatypeConverterImpl._parseDouble(paramCharSequence)); }
  
  public boolean hasValue(Object paramObject) { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\TransducedAccessor_method_Double.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */