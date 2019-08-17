package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.util.JAXMStreamSource;
import com.sun.xml.internal.messaging.saaj.util.ParserPool;
import com.sun.xml.internal.messaging.saaj.util.RejectDoctypeSaxFilter;
import com.sun.xml.internal.messaging.saaj.util.transform.EfficientStreamingTransformer;
import java.io.IOException;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;

public class EnvelopeFactory {
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
  
  private static ContextClassloaderLocal<ParserPool> parserPool = new ContextClassloaderLocal<ParserPool>() {
      protected ParserPool initialValue() throws Exception { return new ParserPool(5); }
    };
  
  public static Envelope createEnvelope(Source paramSource, SOAPPartImpl paramSOAPPartImpl) throws SOAPException {
    sAXParser = null;
    if (paramSource instanceof javax.xml.transform.stream.StreamSource) {
      RejectDoctypeSaxFilter rejectDoctypeSaxFilter;
      if (paramSource instanceof JAXMStreamSource)
        try {
          if (!SOAPPartImpl.lazyContentLength)
            ((JAXMStreamSource)paramSource).reset(); 
        } catch (IOException iOException) {
          log.severe("SAAJ0515.source.reset.exception");
          throw new SOAPExceptionImpl(iOException);
        }  
      try {
        sAXParser = ((ParserPool)parserPool.get()).get();
      } catch (Exception exception) {
        log.severe("SAAJ0601.util.newSAXParser.exception");
        throw new SOAPExceptionImpl("Couldn't get a SAX parser while constructing a envelope", exception);
      } 
      InputSource inputSource = SAXSource.sourceToInputSource(paramSource);
      if (inputSource.getEncoding() == null && paramSOAPPartImpl.getSourceCharsetEncoding() != null)
        inputSource.setEncoding(paramSOAPPartImpl.getSourceCharsetEncoding()); 
      try {
        rejectDoctypeSaxFilter = new RejectDoctypeSaxFilter(sAXParser);
      } catch (Exception exception) {
        log.severe("SAAJ0510.soap.cannot.create.envelope");
        throw new SOAPExceptionImpl("Unable to create envelope from given source: ", exception);
      } 
      paramSource = new SAXSource(rejectDoctypeSaxFilter, inputSource);
    } 
    try {
      Transformer transformer = EfficientStreamingTransformer.newTransformer();
      DOMResult dOMResult = new DOMResult(paramSOAPPartImpl);
      transformer.transform(paramSource, dOMResult);
      Envelope envelope = (Envelope)paramSOAPPartImpl.getEnvelope();
      return envelope;
    } catch (Exception exception) {
      if (exception instanceof SOAPVersionMismatchException)
        throw (SOAPVersionMismatchException)exception; 
      log.severe("SAAJ0511.soap.cannot.create.envelope");
      throw new SOAPExceptionImpl("Unable to create envelope from given source: ", exception);
    } finally {
      if (sAXParser != null)
        ((ParserPool)parserPool.get()).returnParser(sAXParser); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\EnvelopeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */