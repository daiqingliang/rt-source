package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSerializer;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public interface FastInfosetWriter extends ContentHandler, LexicalHandler, EncodingAlgorithmContentHandler, PrimitiveTypeContentHandler, RestrictedAlphabetContentHandler, ExtendedContentHandler, FastInfosetSerializer {}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\sax\FastInfosetWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */