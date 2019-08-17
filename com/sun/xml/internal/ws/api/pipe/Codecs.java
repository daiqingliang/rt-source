package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.encoding.SOAPBindingCodec;
import com.sun.xml.internal.ws.encoding.StreamSOAPCodec;
import com.sun.xml.internal.ws.encoding.XMLHTTPBindingCodec;

public abstract class Codecs {
  @NotNull
  public static SOAPBindingCodec createSOAPBindingCodec(WSFeatureList paramWSFeatureList) { return new SOAPBindingCodec(paramWSFeatureList); }
  
  @NotNull
  public static Codec createXMLCodec(WSFeatureList paramWSFeatureList) { return new XMLHTTPBindingCodec(paramWSFeatureList); }
  
  @NotNull
  public static SOAPBindingCodec createSOAPBindingCodec(WSBinding paramWSBinding, StreamSOAPCodec paramStreamSOAPCodec) { return new SOAPBindingCodec(paramWSBinding.getFeatures(), paramStreamSOAPCodec); }
  
  @NotNull
  public static StreamSOAPCodec createSOAPEnvelopeXmlCodec(@NotNull SOAPVersion paramSOAPVersion) { return StreamSOAPCodec.create(paramSOAPVersion); }
  
  @NotNull
  public static StreamSOAPCodec createSOAPEnvelopeXmlCodec(@NotNull WSBinding paramWSBinding) { return StreamSOAPCodec.create(paramWSBinding); }
  
  @NotNull
  public static StreamSOAPCodec createSOAPEnvelopeXmlCodec(@NotNull WSFeatureList paramWSFeatureList) { return StreamSOAPCodec.create(paramWSFeatureList); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\Codecs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */