package com.sun.xml.internal.messaging.saaj.util.transform;

import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import com.sun.xml.internal.messaging.saaj.util.XMLDeclarationParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;

public class EfficientStreamingTransformer extends Transformer {
  private final TransformerFactory transformerFactory = TransformerFactory.newInstance();
  
  private Transformer m_realTransformer = null;
  
  private Object m_fiDOMDocumentParser = null;
  
  private Object m_fiDOMDocumentSerializer = null;
  
  private void materialize() {
    if (this.m_realTransformer == null)
      this.m_realTransformer = this.transformerFactory.newTransformer(); 
  }
  
  public void clearParameters() {
    if (this.m_realTransformer != null)
      this.m_realTransformer.clearParameters(); 
  }
  
  public ErrorListener getErrorListener() {
    try {
      materialize();
      return this.m_realTransformer.getErrorListener();
    } catch (TransformerException transformerException) {
      return null;
    } 
  }
  
  public Properties getOutputProperties() {
    try {
      materialize();
      return this.m_realTransformer.getOutputProperties();
    } catch (TransformerException transformerException) {
      return null;
    } 
  }
  
  public String getOutputProperty(String paramString) throws IllegalArgumentException {
    try {
      materialize();
      return this.m_realTransformer.getOutputProperty(paramString);
    } catch (TransformerException transformerException) {
      return null;
    } 
  }
  
  public Object getParameter(String paramString) {
    try {
      materialize();
      return this.m_realTransformer.getParameter(paramString);
    } catch (TransformerException transformerException) {
      return null;
    } 
  }
  
  public URIResolver getURIResolver() {
    try {
      materialize();
      return this.m_realTransformer.getURIResolver();
    } catch (TransformerException transformerException) {
      return null;
    } 
  }
  
  public void setErrorListener(ErrorListener paramErrorListener) throws IllegalArgumentException {
    try {
      materialize();
      this.m_realTransformer.setErrorListener(paramErrorListener);
    } catch (TransformerException transformerException) {}
  }
  
  public void setOutputProperties(Properties paramProperties) throws IllegalArgumentException {
    try {
      materialize();
      this.m_realTransformer.setOutputProperties(paramProperties);
    } catch (TransformerException transformerException) {}
  }
  
  public void setOutputProperty(String paramString1, String paramString2) throws IllegalArgumentException {
    try {
      materialize();
      this.m_realTransformer.setOutputProperty(paramString1, paramString2);
    } catch (TransformerException transformerException) {}
  }
  
  public void setParameter(String paramString, Object paramObject) {
    try {
      materialize();
      this.m_realTransformer.setParameter(paramString, paramObject);
    } catch (TransformerException transformerException) {}
  }
  
  public void setURIResolver(URIResolver paramURIResolver) {
    try {
      materialize();
      this.m_realTransformer.setURIResolver(paramURIResolver);
    } catch (TransformerException transformerException) {}
  }
  
  private InputStream getInputStreamFromSource(StreamSource paramStreamSource) throws TransformerException {
    InputStream inputStream = paramStreamSource.getInputStream();
    if (inputStream != null)
      return inputStream; 
    if (paramStreamSource.getReader() != null)
      return null; 
    String str = paramStreamSource.getSystemId();
    if (str != null)
      try {
        String str1 = str;
        if (str.startsWith("file:///")) {
          String str2 = str.substring(7);
          boolean bool = (str2.indexOf(":") > 0) ? 1 : 0;
          if (bool) {
            String str3 = str2.substring(1);
            str1 = str3;
          } else {
            str1 = str2;
          } 
        } 
        try {
          return new FileInputStream(new File(new URI(str1)));
        } catch (URISyntaxException uRISyntaxException) {
          throw new TransformerException(uRISyntaxException);
        } 
      } catch (IOException iOException) {
        throw new TransformerException(iOException.toString());
      }  
    throw new TransformerException("Unexpected StreamSource object");
  }
  
  public void transform(Source paramSource, Result paramResult) throws TransformerException {
    if (paramSource instanceof StreamSource && paramResult instanceof StreamResult) {
      try {
        StreamSource streamSource = (StreamSource)paramSource;
        InputStream inputStream = getInputStreamFromSource(streamSource);
        OutputStream outputStream = ((StreamResult)paramResult).getOutputStream();
        if (outputStream == null)
          throw new TransformerException("Unexpected StreamResult object contains null OutputStream"); 
        if (inputStream != null) {
          if (inputStream.markSupported())
            inputStream.mark(2147483647); 
          byte[] arrayOfByte = new byte[8192];
          int i;
          while ((i = inputStream.read(arrayOfByte)) != -1)
            outputStream.write(arrayOfByte, 0, i); 
          if (inputStream.markSupported())
            inputStream.reset(); 
          return;
        } 
        Reader reader = streamSource.getReader();
        if (reader != null) {
          if (reader.markSupported())
            reader.mark(2147483647); 
          PushbackReader pushbackReader = new PushbackReader(reader, 4096);
          XMLDeclarationParser xMLDeclarationParser = new XMLDeclarationParser(pushbackReader);
          try {
            xMLDeclarationParser.parse();
          } catch (Exception exception) {
            throw new TransformerException("Unable to run the JAXP transformer on a stream " + exception.getMessage());
          } 
          OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
          xMLDeclarationParser.writeTo(outputStreamWriter);
          char[] arrayOfChar = new char[8192];
          int i;
          while ((i = pushbackReader.read(arrayOfChar)) != -1)
            outputStreamWriter.write(arrayOfChar, 0, i); 
          outputStreamWriter.flush();
          if (reader.markSupported())
            reader.reset(); 
          return;
        } 
      } catch (IOException iOException) {
        iOException.printStackTrace();
        throw new TransformerException(iOException.toString());
      } 
      throw new TransformerException("Unexpected StreamSource object");
    } 
    if (FastInfosetReflection.isFastInfosetSource(paramSource) && paramResult instanceof DOMResult)
      try {
        if (this.m_fiDOMDocumentParser == null)
          this.m_fiDOMDocumentParser = FastInfosetReflection.DOMDocumentParser_new(); 
        FastInfosetReflection.DOMDocumentParser_parse(this.m_fiDOMDocumentParser, (Document)((DOMResult)paramResult).getNode(), FastInfosetReflection.FastInfosetSource_getInputStream(paramSource));
        return;
      } catch (Exception exception) {
        throw new TransformerException(exception);
      }  
    if (paramSource instanceof DOMSource && FastInfosetReflection.isFastInfosetResult(paramResult))
      try {
        if (this.m_fiDOMDocumentSerializer == null)
          this.m_fiDOMDocumentSerializer = FastInfosetReflection.DOMDocumentSerializer_new(); 
        FastInfosetReflection.DOMDocumentSerializer_setOutputStream(this.m_fiDOMDocumentSerializer, FastInfosetReflection.FastInfosetResult_getOutputStream(paramResult));
        FastInfosetReflection.DOMDocumentSerializer_serialize(this.m_fiDOMDocumentSerializer, ((DOMSource)paramSource).getNode());
        return;
      } catch (Exception exception) {
        throw new TransformerException(exception);
      }  
    materialize();
    this.m_realTransformer.transform(paramSource, paramResult);
  }
  
  public static Transformer newTransformer() { return new EfficientStreamingTransformer(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saa\\util\transform\EfficientStreamingTransformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */