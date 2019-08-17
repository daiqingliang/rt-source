package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import org.xml.sax.SAXException;

public class LeafPropertyLoader extends Loader {
  private final TransducedAccessor xacc;
  
  public LeafPropertyLoader(TransducedAccessor paramTransducedAccessor) {
    super(true);
    this.xacc = paramTransducedAccessor;
  }
  
  public void text(UnmarshallingContext.State paramState, CharSequence paramCharSequence) throws SAXException {
    try {
      this.xacc.parse(paramState.getPrev().getTarget(), paramCharSequence);
    } catch (AccessorException accessorException) {
      handleGenericException(accessorException, true);
    } catch (RuntimeException runtimeException) {
      handleParseConversionException(paramState, runtimeException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\LeafPropertyLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */