package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.IDResolver;
import java.util.HashMap;
import java.util.concurrent.Callable;
import javax.xml.bind.ValidationEventHandler;
import org.xml.sax.SAXException;

final class DefaultIDResolver extends IDResolver {
  private HashMap<String, Object> idmap = null;
  
  public void startDocument(ValidationEventHandler paramValidationEventHandler) throws SAXException {
    if (this.idmap != null)
      this.idmap.clear(); 
  }
  
  public void bind(String paramString, Object paramObject) {
    if (this.idmap == null)
      this.idmap = new HashMap(); 
    this.idmap.put(paramString, paramObject);
  }
  
  public Callable resolve(final String id, Class paramClass) { return new Callable() {
        public Object call() throws Exception { return (DefaultIDResolver.this.idmap == null) ? null : DefaultIDResolver.this.idmap.get(id); }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\DefaultIDResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */