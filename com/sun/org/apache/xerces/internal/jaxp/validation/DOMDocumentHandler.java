package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

interface DOMDocumentHandler extends XMLDocumentHandler {
  void setDOMResult(DOMResult paramDOMResult);
  
  void doctypeDecl(DocumentType paramDocumentType) throws XNIException;
  
  void characters(Text paramText) throws XNIException;
  
  void cdata(CDATASection paramCDATASection) throws XNIException;
  
  void comment(Comment paramComment) throws XNIException;
  
  void processingInstruction(ProcessingInstruction paramProcessingInstruction) throws XNIException;
  
  void setIgnoringCharacters(boolean paramBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\DOMDocumentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */