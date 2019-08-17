package com.sun.org.apache.xalan.internal.xsltc.runtime.output;

import com.sun.org.apache.xalan.internal.xsltc.trax.SAX2DOM;
import com.sun.org.apache.xalan.internal.xsltc.trax.SAX2StAXEventWriter;
import com.sun.org.apache.xalan.internal.xsltc.trax.SAX2StAXStreamWriter;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.serializer.ToHTMLSAXHandler;
import com.sun.org.apache.xml.internal.serializer.ToHTMLStream;
import com.sun.org.apache.xml.internal.serializer.ToTextSAXHandler;
import com.sun.org.apache.xml.internal.serializer.ToTextStream;
import com.sun.org.apache.xml.internal.serializer.ToUnknownStream;
import com.sun.org.apache.xml.internal.serializer.ToXMLSAXHandler;
import com.sun.org.apache.xml.internal.serializer.ToXMLStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public class TransletOutputHandlerFactory {
  public static final int STREAM = 0;
  
  public static final int SAX = 1;
  
  public static final int DOM = 2;
  
  public static final int STAX = 3;
  
  private String _encoding = "utf-8";
  
  private String _method = null;
  
  private int _outputType = 0;
  
  private OutputStream _ostream = System.out;
  
  private Writer _writer = null;
  
  private Node _node = null;
  
  private Node _nextSibling = null;
  
  private XMLEventWriter _xmlStAXEventWriter = null;
  
  private XMLStreamWriter _xmlStAXStreamWriter = null;
  
  private int _indentNumber = -1;
  
  private ContentHandler _handler = null;
  
  private LexicalHandler _lexHandler = null;
  
  private boolean _overrideDefaultParser;
  
  public static TransletOutputHandlerFactory newInstance() { return new TransletOutputHandlerFactory(true); }
  
  public static TransletOutputHandlerFactory newInstance(boolean paramBoolean) { return new TransletOutputHandlerFactory(paramBoolean); }
  
  public TransletOutputHandlerFactory(boolean paramBoolean) { this._overrideDefaultParser = paramBoolean; }
  
  public void setOutputType(int paramInt) { this._outputType = paramInt; }
  
  public void setEncoding(String paramString) {
    if (paramString != null)
      this._encoding = paramString; 
  }
  
  public void setOutputMethod(String paramString) { this._method = paramString; }
  
  public void setOutputStream(OutputStream paramOutputStream) { this._ostream = paramOutputStream; }
  
  public void setWriter(Writer paramWriter) { this._writer = paramWriter; }
  
  public void setHandler(ContentHandler paramContentHandler) { this._handler = paramContentHandler; }
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler) { this._lexHandler = paramLexicalHandler; }
  
  public void setNode(Node paramNode) { this._node = paramNode; }
  
  public Node getNode() { return (this._handler instanceof SAX2DOM) ? ((SAX2DOM)this._handler).getDOM() : null; }
  
  public void setNextSibling(Node paramNode) { this._nextSibling = paramNode; }
  
  public XMLEventWriter getXMLEventWriter() { return (this._handler instanceof SAX2StAXEventWriter) ? ((SAX2StAXEventWriter)this._handler).getEventWriter() : null; }
  
  public void setXMLEventWriter(XMLEventWriter paramXMLEventWriter) { this._xmlStAXEventWriter = paramXMLEventWriter; }
  
  public XMLStreamWriter getXMLStreamWriter() { return (this._handler instanceof SAX2StAXStreamWriter) ? ((SAX2StAXStreamWriter)this._handler).getStreamWriter() : null; }
  
  public void setXMLStreamWriter(XMLStreamWriter paramXMLStreamWriter) { this._xmlStAXStreamWriter = paramXMLStreamWriter; }
  
  public void setIndentNumber(int paramInt) { this._indentNumber = paramInt; }
  
  public SerializationHandler getSerializationHandler() throws IOException, ParserConfigurationException {
    ToTextSAXHandler toTextSAXHandler = null;
    switch (this._outputType) {
      case 0:
        if (this._method == null) {
          toTextSAXHandler = new ToUnknownStream();
        } else if (this._method.equalsIgnoreCase("xml")) {
          ToXMLStream toXMLStream = new ToXMLStream();
        } else if (this._method.equalsIgnoreCase("html")) {
          ToHTMLStream toHTMLStream = new ToHTMLStream();
        } else if (this._method.equalsIgnoreCase("text")) {
          toTextSAXHandler = new ToTextStream();
        } 
        if (toTextSAXHandler != null && this._indentNumber >= 0)
          toTextSAXHandler.setIndentAmount(this._indentNumber); 
        toTextSAXHandler.setEncoding(this._encoding);
        if (this._writer != null) {
          toTextSAXHandler.setWriter(this._writer);
        } else {
          toTextSAXHandler.setOutputStream(this._ostream);
        } 
        return toTextSAXHandler;
      case 2:
        this._handler = (this._node != null) ? new SAX2DOM(this._node, this._nextSibling, this._overrideDefaultParser) : new SAX2DOM(this._overrideDefaultParser);
        this._lexHandler = (LexicalHandler)this._handler;
      case 3:
        if (this._xmlStAXEventWriter != null) {
          this._handler = new SAX2StAXEventWriter(this._xmlStAXEventWriter);
        } else if (this._xmlStAXStreamWriter != null) {
          this._handler = new SAX2StAXStreamWriter(this._xmlStAXStreamWriter);
        } 
        this._lexHandler = (LexicalHandler)this._handler;
      case 1:
        if (this._method == null)
          this._method = "xml"; 
        if (this._method.equalsIgnoreCase("xml")) {
          if (this._lexHandler == null) {
            ToXMLSAXHandler toXMLSAXHandler = new ToXMLSAXHandler(this._handler, this._encoding);
          } else {
            ToXMLSAXHandler toXMLSAXHandler = new ToXMLSAXHandler(this._handler, this._lexHandler, this._encoding);
          } 
        } else if (this._method.equalsIgnoreCase("html")) {
          if (this._lexHandler == null) {
            ToHTMLSAXHandler toHTMLSAXHandler = new ToHTMLSAXHandler(this._handler, this._encoding);
          } else {
            ToHTMLSAXHandler toHTMLSAXHandler = new ToHTMLSAXHandler(this._handler, this._lexHandler, this._encoding);
          } 
        } else if (this._method.equalsIgnoreCase("text")) {
          if (this._lexHandler == null) {
            ToTextSAXHandler toTextSAXHandler1 = new ToTextSAXHandler(this._handler, this._encoding);
          } else {
            toTextSAXHandler = new ToTextSAXHandler(this._handler, this._lexHandler, this._encoding);
          } 
        } 
        return toTextSAXHandler;
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\output\TransletOutputHandlerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */