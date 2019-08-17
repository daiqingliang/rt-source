package javax.xml.transform.sax;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class SAXSource implements Source {
  public static final String FEATURE = "http://javax.xml.transform.sax.SAXSource/feature";
  
  private XMLReader reader;
  
  private InputSource inputSource;
  
  public SAXSource() {}
  
  public SAXSource(XMLReader paramXMLReader, InputSource paramInputSource) {
    this.reader = paramXMLReader;
    this.inputSource = paramInputSource;
  }
  
  public SAXSource(InputSource paramInputSource) { this.inputSource = paramInputSource; }
  
  public void setXMLReader(XMLReader paramXMLReader) { this.reader = paramXMLReader; }
  
  public XMLReader getXMLReader() { return this.reader; }
  
  public void setInputSource(InputSource paramInputSource) { this.inputSource = paramInputSource; }
  
  public InputSource getInputSource() { return this.inputSource; }
  
  public void setSystemId(String paramString) {
    if (null == this.inputSource) {
      this.inputSource = new InputSource(paramString);
    } else {
      this.inputSource.setSystemId(paramString);
    } 
  }
  
  public String getSystemId() { return (this.inputSource == null) ? null : this.inputSource.getSystemId(); }
  
  public static InputSource sourceToInputSource(Source paramSource) {
    if (paramSource instanceof SAXSource)
      return ((SAXSource)paramSource).getInputSource(); 
    if (paramSource instanceof StreamSource) {
      StreamSource streamSource = (StreamSource)paramSource;
      InputSource inputSource1 = new InputSource(streamSource.getSystemId());
      inputSource1.setByteStream(streamSource.getInputStream());
      inputSource1.setCharacterStream(streamSource.getReader());
      inputSource1.setPublicId(streamSource.getPublicId());
      return inputSource1;
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\sax\SAXSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */