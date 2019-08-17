package com.oracle.webservices.internal.impl.encoding;

import com.oracle.webservices.internal.impl.internalspi.encoding.StreamDecoder;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.encoding.StreamSOAPCodec;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamReader;

public class StreamDecoderImpl implements StreamDecoder {
  public Message decode(InputStream paramInputStream, String paramString, AttachmentSet paramAttachmentSet, SOAPVersion paramSOAPVersion) throws IOException {
    XMLStreamReader xMLStreamReader = XMLStreamReaderFactory.create(null, paramInputStream, paramString, true);
    xMLStreamReader = new TidyXMLStreamReader(xMLStreamReader, paramInputStream);
    return StreamSOAPCodec.decode(paramSOAPVersion, xMLStreamReader, paramAttachmentSet);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\webservices\internal\impl\encoding\StreamDecoderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */