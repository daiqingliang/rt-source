package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import java.io.InputStream;

public final class FastInfosetStreamReaderRecyclable extends StAXDocumentParser implements XMLStreamReaderFactory.RecycleAware {
  private static final FastInfosetStreamReaderFactory READER_FACTORY = FastInfosetStreamReaderFactory.getInstance();
  
  public FastInfosetStreamReaderRecyclable() {}
  
  public FastInfosetStreamReaderRecyclable(InputStream paramInputStream) { super(paramInputStream); }
  
  public void onRecycled() { READER_FACTORY.doRecycle(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\fastinfoset\FastInfosetStreamReaderRecyclable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */