package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public class UTF8XmlOutput extends XmlOutputAbstractImpl {
  protected final OutputStream out;
  
  private Encoded[] prefixes = new Encoded[8];
  
  private int prefixCount;
  
  private final Encoded[] localNames;
  
  private final Encoded textBuffer = new Encoded();
  
  protected final byte[] octetBuffer = new byte[1024];
  
  protected int octetBufferIndex;
  
  protected boolean closeStartTagPending = false;
  
  private String header;
  
  private CharacterEscapeHandler escapeHandler = null;
  
  private final byte[] XMLNS_EQUALS = (byte[])_XMLNS_EQUALS.clone();
  
  private final byte[] XMLNS_COLON = (byte[])_XMLNS_COLON.clone();
  
  private final byte[] EQUALS = (byte[])_EQUALS.clone();
  
  private final byte[] CLOSE_TAG = (byte[])_CLOSE_TAG.clone();
  
  private final byte[] EMPTY_TAG = (byte[])_EMPTY_TAG.clone();
  
  private final byte[] XML_DECL = (byte[])_XML_DECL.clone();
  
  private static final byte[] _XMLNS_EQUALS = toBytes(" xmlns=\"");
  
  private static final byte[] _XMLNS_COLON = toBytes(" xmlns:");
  
  private static final byte[] _EQUALS = toBytes("=\"");
  
  private static final byte[] _CLOSE_TAG = toBytes("</");
  
  private static final byte[] _EMPTY_TAG = toBytes("/>");
  
  private static final byte[] _XML_DECL = toBytes("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
  
  private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  
  public UTF8XmlOutput(OutputStream paramOutputStream, Encoded[] paramArrayOfEncoded, CharacterEscapeHandler paramCharacterEscapeHandler) {
    this.out = paramOutputStream;
    this.localNames = paramArrayOfEncoded;
    for (byte b = 0; b < this.prefixes.length; b++)
      this.prefixes[b] = new Encoded(); 
    this.escapeHandler = paramCharacterEscapeHandler;
  }
  
  public void setHeader(String paramString) { this.header = paramString; }
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl) throws IOException, SAXException, XMLStreamException {
    super.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
    this.octetBufferIndex = 0;
    if (!paramBoolean)
      write(this.XML_DECL); 
    if (this.header != null) {
      this.textBuffer.set(this.header);
      this.textBuffer.write(this);
    } 
  }
  
  public void endDocument(boolean paramBoolean) throws IOException, SAXException, XMLStreamException {
    flushBuffer();
    super.endDocument(paramBoolean);
  }
  
  protected final void closeStartTag() throws IOException {
    if (this.closeStartTagPending) {
      write(62);
      this.closeStartTagPending = false;
    } 
  }
  
  public void beginStartTag(int paramInt, String paramString) throws IOException {
    closeStartTag();
    int i = pushNsDecls();
    write(60);
    writeName(paramInt, paramString);
    writeNsDecls(i);
  }
  
  public void beginStartTag(Name paramName) throws IOException {
    closeStartTag();
    int i = pushNsDecls();
    write(60);
    writeName(paramName);
    writeNsDecls(i);
  }
  
  private int pushNsDecls() {
    int i = this.nsContext.count();
    NamespaceContextImpl.Element element = this.nsContext.getCurrent();
    if (i > this.prefixes.length) {
      int n = Math.max(i, this.prefixes.length * 2);
      Encoded[] arrayOfEncoded = new Encoded[n];
      System.arraycopy(this.prefixes, 0, arrayOfEncoded, 0, this.prefixes.length);
      for (int i1 = this.prefixes.length; i1 < arrayOfEncoded.length; i1++)
        arrayOfEncoded[i1] = new Encoded(); 
      this.prefixes = arrayOfEncoded;
    } 
    int j = Math.min(this.prefixCount, element.getBase());
    int k = this.nsContext.count();
    for (int m = j; m < k; m++) {
      String str = this.nsContext.getPrefix(m);
      Encoded encoded = this.prefixes[m];
      if (str.length() == 0) {
        encoded.buf = EMPTY_BYTE_ARRAY;
        encoded.len = 0;
      } else {
        encoded.set(str);
        encoded.append(':');
      } 
    } 
    this.prefixCount = k;
    return j;
  }
  
  protected void writeNsDecls(int paramInt) throws IOException {
    NamespaceContextImpl.Element element = this.nsContext.getCurrent();
    int i = this.nsContext.count();
    for (int j = element.getBase(); j < i; j++)
      writeNsDecl(j); 
  }
  
  protected final void writeNsDecl(int paramInt) throws IOException {
    String str = this.nsContext.getPrefix(paramInt);
    if (str.length() == 0) {
      if (this.nsContext.getCurrent().isRootElement() && this.nsContext.getNamespaceURI(paramInt).length() == 0)
        return; 
      write(this.XMLNS_EQUALS);
    } else {
      Encoded encoded = this.prefixes[paramInt];
      write(this.XMLNS_COLON);
      write(encoded.buf, 0, encoded.len - 1);
      write(this.EQUALS);
    } 
    doText(this.nsContext.getNamespaceURI(paramInt), true);
    write(34);
  }
  
  private void writePrefix(int paramInt) throws IOException { this.prefixes[paramInt].write(this); }
  
  private void writeName(Name paramName) throws IOException {
    writePrefix(this.nsUriIndex2prefixIndex[paramName.nsUriIndex]);
    this.localNames[paramName.localNameIndex].write(this);
  }
  
  private void writeName(int paramInt, String paramString) throws IOException {
    writePrefix(paramInt);
    this.textBuffer.set(paramString);
    this.textBuffer.write(this);
  }
  
  public void attribute(Name paramName, String paramString) throws IOException {
    write(32);
    if (paramName.nsUriIndex == -1) {
      this.localNames[paramName.localNameIndex].write(this);
    } else {
      writeName(paramName);
    } 
    write(this.EQUALS);
    doText(paramString, true);
    write(34);
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2) throws IOException {
    write(32);
    if (paramInt == -1) {
      this.textBuffer.set(paramString1);
      this.textBuffer.write(this);
    } else {
      writeName(paramInt, paramString1);
    } 
    write(this.EQUALS);
    doText(paramString2, true);
    write(34);
  }
  
  public void endStartTag() throws IOException { this.closeStartTagPending = true; }
  
  public void endTag(Name paramName) throws IOException {
    if (this.closeStartTagPending) {
      write(this.EMPTY_TAG);
      this.closeStartTagPending = false;
    } else {
      write(this.CLOSE_TAG);
      writeName(paramName);
      write(62);
    } 
  }
  
  public void endTag(int paramInt, String paramString) throws IOException {
    if (this.closeStartTagPending) {
      write(this.EMPTY_TAG);
      this.closeStartTagPending = false;
    } else {
      write(this.CLOSE_TAG);
      writeName(paramInt, paramString);
      write(62);
    } 
  }
  
  public void text(String paramString, boolean paramBoolean) throws IOException {
    closeStartTag();
    if (paramBoolean)
      write(32); 
    doText(paramString, false);
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean) throws IOException {
    closeStartTag();
    if (paramBoolean)
      write(32); 
    paramPcdata.writeTo(this);
  }
  
  private void doText(String paramString, boolean paramBoolean) throws IOException {
    if (this.escapeHandler != null) {
      StringWriter stringWriter = new StringWriter();
      this.escapeHandler.escape(paramString.toCharArray(), 0, paramString.length(), paramBoolean, stringWriter);
      this.textBuffer.set(stringWriter.toString());
    } else {
      this.textBuffer.setEscape(paramString, paramBoolean);
    } 
    this.textBuffer.write(this);
  }
  
  public final void text(int paramInt) throws IOException {
    closeStartTag();
    boolean bool = (paramInt < 0) ? 1 : 0;
    this.textBuffer.ensureSize(11);
    byte[] arrayOfByte = this.textBuffer.buf;
    byte b = 11;
    do {
      int i = paramInt % 10;
      if (i < 0)
        i = -i; 
      arrayOfByte[--b] = (byte)(0x30 | i);
      paramInt /= 10;
    } while (paramInt != 0);
    if (bool)
      arrayOfByte[--b] = 45; 
    write(arrayOfByte, b, 11 - b);
  }
  
  public void text(byte[] paramArrayOfByte, int paramInt) throws IOException {
    closeStartTag();
    int i = 0;
    while (paramInt > 0) {
      int j = Math.min((this.octetBuffer.length - this.octetBufferIndex) / 4 * 3, paramInt);
      this.octetBufferIndex = DatatypeConverterImpl._printBase64Binary(paramArrayOfByte, i, j, this.octetBuffer, this.octetBufferIndex);
      if (j < paramInt)
        flushBuffer(); 
      i += j;
      paramInt -= j;
    } 
  }
  
  public final void write(int paramInt) throws IOException {
    if (this.octetBufferIndex < this.octetBuffer.length) {
      this.octetBuffer[this.octetBufferIndex++] = (byte)paramInt;
    } else {
      this.out.write(this.octetBuffer);
      this.octetBufferIndex = 1;
      this.octetBuffer[0] = (byte)paramInt;
    } 
  }
  
  protected final void write(byte[] paramArrayOfByte) throws IOException { write(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  protected final void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this.octetBufferIndex + paramInt2 < this.octetBuffer.length) {
      System.arraycopy(paramArrayOfByte, paramInt1, this.octetBuffer, this.octetBufferIndex, paramInt2);
      this.octetBufferIndex += paramInt2;
    } else {
      this.out.write(this.octetBuffer, 0, this.octetBufferIndex);
      this.out.write(paramArrayOfByte, paramInt1, paramInt2);
      this.octetBufferIndex = 0;
    } 
  }
  
  protected final void flushBuffer() throws IOException {
    this.out.write(this.octetBuffer, 0, this.octetBufferIndex);
    this.octetBufferIndex = 0;
  }
  
  static byte[] toBytes(String paramString) {
    byte[] arrayOfByte = new byte[paramString.length()];
    for (int i = paramString.length() - 1; i >= 0; i--)
      arrayOfByte[i] = (byte)paramString.charAt(i); 
    return arrayOfByte;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\UTF8XmlOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */