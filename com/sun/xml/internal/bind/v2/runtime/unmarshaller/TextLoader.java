package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import org.xml.sax.SAXException;

public class TextLoader extends Loader {
  private final Transducer xducer;
  
  public TextLoader(Transducer paramTransducer) {
    super(true);
    this.xducer = paramTransducer;
  }
  
  public void text(UnmarshallingContext.State paramState, CharSequence paramCharSequence) throws SAXException {
    try {
      paramState.setTarget(this.xducer.parse(paramCharSequence));
    } catch (AccessorException accessorException) {
      handleGenericException(accessorException, true);
    } catch (RuntimeException runtimeException) {
      handleParseConversionException(paramState, runtimeException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\TextLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */