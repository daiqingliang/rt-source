package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.TubeFactory;
import com.sun.xml.internal.ws.assembler.dev.TubelineAssemblyContextUpdater;
import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryConfig;

final class TubeCreator {
  private static final Logger LOGGER = Logger.getLogger(TubeCreator.class);
  
  private final TubeFactory factory;
  
  private final String msgDumpPropertyBase;
  
  TubeCreator(TubeFactoryConfig paramTubeFactoryConfig, ClassLoader paramClassLoader) {
    String str = paramTubeFactoryConfig.getClassName();
    try {
      Class clazz;
      if (isJDKInternal(str)) {
        clazz = Class.forName(str, true, null);
      } else {
        clazz = Class.forName(str, true, paramClassLoader);
      } 
      if (TubeFactory.class.isAssignableFrom(clazz)) {
        Class clazz1 = clazz;
        this.factory = (TubeFactory)clazz1.newInstance();
        this.msgDumpPropertyBase = this.factory.getClass().getName() + ".dump";
      } else {
        throw new RuntimeException(TubelineassemblyMessages.MASM_0015_CLASS_DOES_NOT_IMPLEMENT_INTERFACE(clazz.getName(), TubeFactory.class.getName()));
      } 
    } catch (InstantiationException instantiationException) {
      throw (RuntimeException)LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0016_UNABLE_TO_INSTANTIATE_TUBE_FACTORY(str), instantiationException), true);
    } catch (IllegalAccessException illegalAccessException) {
      throw (RuntimeException)LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0016_UNABLE_TO_INSTANTIATE_TUBE_FACTORY(str), illegalAccessException), true);
    } catch (ClassNotFoundException classNotFoundException) {
      throw (RuntimeException)LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0017_UNABLE_TO_LOAD_TUBE_FACTORY_CLASS(str), classNotFoundException), true);
    } 
  }
  
  Tube createTube(DefaultClientTubelineAssemblyContext paramDefaultClientTubelineAssemblyContext) { return this.factory.createTube(paramDefaultClientTubelineAssemblyContext); }
  
  Tube createTube(DefaultServerTubelineAssemblyContext paramDefaultServerTubelineAssemblyContext) { return this.factory.createTube(paramDefaultServerTubelineAssemblyContext); }
  
  void updateContext(ClientTubelineAssemblyContext paramClientTubelineAssemblyContext) {
    if (this.factory instanceof TubelineAssemblyContextUpdater)
      ((TubelineAssemblyContextUpdater)this.factory).prepareContext(paramClientTubelineAssemblyContext); 
  }
  
  void updateContext(DefaultServerTubelineAssemblyContext paramDefaultServerTubelineAssemblyContext) {
    if (this.factory instanceof TubelineAssemblyContextUpdater)
      ((TubelineAssemblyContextUpdater)this.factory).prepareContext(paramDefaultServerTubelineAssemblyContext); 
  }
  
  String getMessageDumpPropertyBase() { return this.msgDumpPropertyBase; }
  
  private boolean isJDKInternal(String paramString) { return paramString.startsWith("com.sun.xml.internal.ws"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\assembler\TubeCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */