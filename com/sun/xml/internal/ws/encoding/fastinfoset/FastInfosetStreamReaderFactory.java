package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.XMLStreamReader;

public final class FastInfosetStreamReaderFactory extends XMLStreamReaderFactory {
  private static final FastInfosetStreamReaderFactory factory = new FastInfosetStreamReaderFactory();
  
  private ThreadLocal<StAXDocumentParser> pool = new ThreadLocal();
  
  public static FastInfosetStreamReaderFactory getInstance() { return factory; }
  
  public XMLStreamReader doCreate(String paramString, InputStream paramInputStream, boolean paramBoolean) {
    StAXDocumentParser stAXDocumentParser = fetch();
    if (stAXDocumentParser == null)
      return FastInfosetCodec.createNewStreamReaderRecyclable(paramInputStream, false); 
    stAXDocumentParser.setInputStream(paramInputStream);
    return stAXDocumentParser;
  }
  
  public XMLStreamReader doCreate(String paramString, Reader paramReader, boolean paramBoolean) { throw new UnsupportedOperationException(); }
  
  private StAXDocumentParser fetch() {
    StAXDocumentParser stAXDocumentParser = (StAXDocumentParser)this.pool.get();
    this.pool.set(null);
    return stAXDocumentParser;
  }
  
  public void doRecycle(XMLStreamReader paramXMLStreamReader) {
    if (paramXMLStreamReader instanceof StAXDocumentParser)
      this.pool.set((StAXDocumentParser)paramXMLStreamReader); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\fastinfoset\FastInfosetStreamReaderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */