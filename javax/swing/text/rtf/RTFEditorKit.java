package javax.swing.text.rtf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

public class RTFEditorKit extends StyledEditorKit {
  public String getContentType() { return "text/rtf"; }
  
  public void read(InputStream paramInputStream, Document paramDocument, int paramInt) throws IOException, BadLocationException {
    if (paramDocument instanceof StyledDocument) {
      RTFReader rTFReader = new RTFReader((StyledDocument)paramDocument);
      rTFReader.readFromStream(paramInputStream);
      rTFReader.close();
    } else {
      super.read(paramInputStream, paramDocument, paramInt);
    } 
  }
  
  public void write(OutputStream paramOutputStream, Document paramDocument, int paramInt1, int paramInt2) throws IOException, BadLocationException { RTFGenerator.writeDocument(paramDocument, paramOutputStream); }
  
  public void read(Reader paramReader, Document paramDocument, int paramInt) throws IOException, BadLocationException {
    if (paramDocument instanceof StyledDocument) {
      RTFReader rTFReader = new RTFReader((StyledDocument)paramDocument);
      rTFReader.readFromReader(paramReader);
      rTFReader.close();
    } else {
      super.read(paramReader, paramDocument, paramInt);
    } 
  }
  
  public void write(Writer paramWriter, Document paramDocument, int paramInt1, int paramInt2) throws IOException, BadLocationException { throw new IOException("RTF is an 8-bit format"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\rtf\RTFEditorKit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */