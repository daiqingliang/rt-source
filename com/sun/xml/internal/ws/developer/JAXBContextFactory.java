package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.api.model.SEIModel;
import java.util.List;
import javax.xml.bind.JAXBException;

public interface JAXBContextFactory {
  public static final JAXBContextFactory DEFAULT = new JAXBContextFactory() {
      @NotNull
      public JAXBRIContext createJAXBContext(@NotNull SEIModel param1SEIModel, @NotNull List<Class> param1List1, @NotNull List<TypeReference> param1List2) throws JAXBException { return JAXBRIContext.newInstance((Class[])param1List1.toArray(new Class[param1List1.size()]), param1List2, null, param1SEIModel.getTargetNamespace(), false, null); }
    };
  
  @NotNull
  JAXBRIContext createJAXBContext(@NotNull SEIModel paramSEIModel, @NotNull List<Class> paramList1, @NotNull List<TypeReference> paramList2) throws JAXBException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\developer\JAXBContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */