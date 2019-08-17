package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubelineAssembler;
import com.sun.xml.internal.ws.assembler.dev.TubelineAssemblyDecorator;
import com.sun.xml.internal.ws.dump.LoggingDumpTube;
import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.Collection;
import java.util.logging.Level;

public class MetroTubelineAssembler implements TubelineAssembler {
  private static final String COMMON_MESSAGE_DUMP_SYSTEM_PROPERTY_BASE = "com.sun.metro.soap.dump";
  
  public static final MetroConfigNameImpl JAXWS_TUBES_CONFIG_NAMES = new MetroConfigNameImpl("jaxws-tubes-default.xml", "jaxws-tubes.xml");
  
  private static final Logger LOGGER = Logger.getLogger(MetroTubelineAssembler.class);
  
  private final BindingID bindingId;
  
  private final TubelineAssemblyController tubelineAssemblyController;
  
  public MetroTubelineAssembler(BindingID paramBindingID, MetroConfigName paramMetroConfigName) {
    this.bindingId = paramBindingID;
    this.tubelineAssemblyController = new TubelineAssemblyController(paramMetroConfigName);
  }
  
  TubelineAssemblyController getTubelineAssemblyController() { return this.tubelineAssemblyController; }
  
  @NotNull
  public Tube createClient(@NotNull ClientTubeAssemblerContext paramClientTubeAssemblerContext) {
    if (LOGGER.isLoggable(Level.FINER))
      LOGGER.finer("Assembling client-side tubeline for WS endpoint: " + paramClientTubeAssemblerContext.getAddress().getURI().toString()); 
    DefaultClientTubelineAssemblyContext defaultClientTubelineAssemblyContext = createClientContext(paramClientTubeAssemblerContext);
    Collection collection = this.tubelineAssemblyController.getTubeCreators(defaultClientTubelineAssemblyContext);
    for (TubeCreator tubeCreator : collection)
      tubeCreator.updateContext(defaultClientTubelineAssemblyContext); 
    TubelineAssemblyDecorator tubelineAssemblyDecorator = TubelineAssemblyDecorator.composite(ServiceFinder.find(TubelineAssemblyDecorator.class, defaultClientTubelineAssemblyContext.getContainer()));
    boolean bool = true;
    for (TubeCreator tubeCreator : collection) {
      MessageDumpingInfo messageDumpingInfo = setupMessageDumping(tubeCreator.getMessageDumpPropertyBase(), Side.Client);
      Tube tube = defaultClientTubelineAssemblyContext.getTubelineHead();
      LoggingDumpTube loggingDumpTube = null;
      if (messageDumpingInfo.dumpAfter) {
        loggingDumpTube = new LoggingDumpTube(messageDumpingInfo.logLevel, LoggingDumpTube.Position.After, defaultClientTubelineAssemblyContext.getTubelineHead());
        defaultClientTubelineAssemblyContext.setTubelineHead(loggingDumpTube);
      } 
      if (!defaultClientTubelineAssemblyContext.setTubelineHead(tubelineAssemblyDecorator.decorateClient(tubeCreator.createTube(defaultClientTubelineAssemblyContext), defaultClientTubelineAssemblyContext))) {
        if (loggingDumpTube != null)
          defaultClientTubelineAssemblyContext.setTubelineHead(tube); 
      } else {
        String str = defaultClientTubelineAssemblyContext.getTubelineHead().getClass().getName();
        if (loggingDumpTube != null)
          loggingDumpTube.setLoggedTubeName(str); 
        if (messageDumpingInfo.dumpBefore) {
          LoggingDumpTube loggingDumpTube1 = new LoggingDumpTube(messageDumpingInfo.logLevel, LoggingDumpTube.Position.Before, defaultClientTubelineAssemblyContext.getTubelineHead());
          loggingDumpTube1.setLoggedTubeName(str);
          defaultClientTubelineAssemblyContext.setTubelineHead(loggingDumpTube1);
        } 
      } 
      if (bool) {
        defaultClientTubelineAssemblyContext.setTubelineHead(tubelineAssemblyDecorator.decorateClientTail(defaultClientTubelineAssemblyContext.getTubelineHead(), defaultClientTubelineAssemblyContext));
        bool = false;
      } 
    } 
    return tubelineAssemblyDecorator.decorateClientHead(defaultClientTubelineAssemblyContext.getTubelineHead(), defaultClientTubelineAssemblyContext);
  }
  
  @NotNull
  public Tube createServer(@NotNull ServerTubeAssemblerContext paramServerTubeAssemblerContext) {
    if (LOGGER.isLoggable(Level.FINER))
      LOGGER.finer("Assembling endpoint tubeline for WS endpoint: " + paramServerTubeAssemblerContext.getEndpoint().getServiceName() + "::" + paramServerTubeAssemblerContext.getEndpoint().getPortName()); 
    DefaultServerTubelineAssemblyContext defaultServerTubelineAssemblyContext = createServerContext(paramServerTubeAssemblerContext);
    Collection collection = this.tubelineAssemblyController.getTubeCreators(defaultServerTubelineAssemblyContext);
    for (TubeCreator tubeCreator : collection)
      tubeCreator.updateContext(defaultServerTubelineAssemblyContext); 
    TubelineAssemblyDecorator tubelineAssemblyDecorator = TubelineAssemblyDecorator.composite(ServiceFinder.find(TubelineAssemblyDecorator.class, defaultServerTubelineAssemblyContext.getEndpoint().getContainer()));
    boolean bool = true;
    for (TubeCreator tubeCreator : collection) {
      MessageDumpingInfo messageDumpingInfo = setupMessageDumping(tubeCreator.getMessageDumpPropertyBase(), Side.Endpoint);
      Tube tube = defaultServerTubelineAssemblyContext.getTubelineHead();
      LoggingDumpTube loggingDumpTube = null;
      if (messageDumpingInfo.dumpAfter) {
        loggingDumpTube = new LoggingDumpTube(messageDumpingInfo.logLevel, LoggingDumpTube.Position.After, defaultServerTubelineAssemblyContext.getTubelineHead());
        defaultServerTubelineAssemblyContext.setTubelineHead(loggingDumpTube);
      } 
      if (!defaultServerTubelineAssemblyContext.setTubelineHead(tubelineAssemblyDecorator.decorateServer(tubeCreator.createTube(defaultServerTubelineAssemblyContext), defaultServerTubelineAssemblyContext))) {
        if (loggingDumpTube != null)
          defaultServerTubelineAssemblyContext.setTubelineHead(tube); 
      } else {
        String str = defaultServerTubelineAssemblyContext.getTubelineHead().getClass().getName();
        if (loggingDumpTube != null)
          loggingDumpTube.setLoggedTubeName(str); 
        if (messageDumpingInfo.dumpBefore) {
          LoggingDumpTube loggingDumpTube1 = new LoggingDumpTube(messageDumpingInfo.logLevel, LoggingDumpTube.Position.Before, defaultServerTubelineAssemblyContext.getTubelineHead());
          loggingDumpTube1.setLoggedTubeName(str);
          defaultServerTubelineAssemblyContext.setTubelineHead(loggingDumpTube1);
        } 
      } 
      if (bool) {
        defaultServerTubelineAssemblyContext.setTubelineHead(tubelineAssemblyDecorator.decorateServerTail(defaultServerTubelineAssemblyContext.getTubelineHead(), defaultServerTubelineAssemblyContext));
        bool = false;
      } 
    } 
    return tubelineAssemblyDecorator.decorateServerHead(defaultServerTubelineAssemblyContext.getTubelineHead(), defaultServerTubelineAssemblyContext);
  }
  
  private MessageDumpingInfo setupMessageDumping(String paramString, Side paramSide) {
    boolean bool1 = false;
    boolean bool2 = false;
    Level level1 = Level.INFO;
    Boolean bool = getBooleanValue("com.sun.metro.soap.dump");
    if (bool != null) {
      bool1 = bool.booleanValue();
      bool2 = bool.booleanValue();
    } 
    bool = getBooleanValue("com.sun.metro.soap.dump.before");
    bool1 = (bool != null) ? bool.booleanValue() : bool1;
    bool = getBooleanValue("com.sun.metro.soap.dump.after");
    bool2 = (bool != null) ? bool.booleanValue() : bool2;
    Level level2 = getLevelValue("com.sun.metro.soap.dump.level");
    if (level2 != null)
      level1 = level2; 
    bool = getBooleanValue("com.sun.metro.soap.dump." + paramSide.toString());
    if (bool != null) {
      bool1 = bool.booleanValue();
      bool2 = bool.booleanValue();
    } 
    bool = getBooleanValue("com.sun.metro.soap.dump." + paramSide.toString() + ".before");
    bool1 = (bool != null) ? bool.booleanValue() : bool1;
    bool = getBooleanValue("com.sun.metro.soap.dump." + paramSide.toString() + ".after");
    bool2 = (bool != null) ? bool.booleanValue() : bool2;
    level2 = getLevelValue("com.sun.metro.soap.dump." + paramSide.toString() + ".level");
    if (level2 != null)
      level1 = level2; 
    bool = getBooleanValue(paramString);
    if (bool != null) {
      bool1 = bool.booleanValue();
      bool2 = bool.booleanValue();
    } 
    bool = getBooleanValue(paramString + ".before");
    bool1 = (bool != null) ? bool.booleanValue() : bool1;
    bool = getBooleanValue(paramString + ".after");
    bool2 = (bool != null) ? bool.booleanValue() : bool2;
    level2 = getLevelValue(paramString + ".level");
    if (level2 != null)
      level1 = level2; 
    paramString = paramString + "." + paramSide.toString();
    bool = getBooleanValue(paramString);
    if (bool != null) {
      bool1 = bool.booleanValue();
      bool2 = bool.booleanValue();
    } 
    bool = getBooleanValue(paramString + ".before");
    bool1 = (bool != null) ? bool.booleanValue() : bool1;
    bool = getBooleanValue(paramString + ".after");
    bool2 = (bool != null) ? bool.booleanValue() : bool2;
    level2 = getLevelValue(paramString + ".level");
    if (level2 != null)
      level1 = level2; 
    return new MessageDumpingInfo(bool1, bool2, level1);
  }
  
  private Boolean getBooleanValue(String paramString) {
    Boolean bool = null;
    String str = System.getProperty(paramString);
    if (str != null) {
      bool = Boolean.valueOf(str);
      LOGGER.fine(TubelineassemblyMessages.MASM_0018_MSG_LOGGING_SYSTEM_PROPERTY_SET_TO_VALUE(paramString, bool));
    } 
    return bool;
  }
  
  private Level getLevelValue(String paramString) {
    Level level = null;
    String str = System.getProperty(paramString);
    if (str != null) {
      LOGGER.fine(TubelineassemblyMessages.MASM_0018_MSG_LOGGING_SYSTEM_PROPERTY_SET_TO_VALUE(paramString, str));
      try {
        level = Level.parse(str);
      } catch (IllegalArgumentException illegalArgumentException) {
        LOGGER.warning(TubelineassemblyMessages.MASM_0019_MSG_LOGGING_SYSTEM_PROPERTY_ILLEGAL_VALUE(paramString, str), illegalArgumentException);
      } 
    } 
    return level;
  }
  
  protected DefaultServerTubelineAssemblyContext createServerContext(ServerTubeAssemblerContext paramServerTubeAssemblerContext) { return new DefaultServerTubelineAssemblyContext(paramServerTubeAssemblerContext); }
  
  protected DefaultClientTubelineAssemblyContext createClientContext(ClientTubeAssemblerContext paramClientTubeAssemblerContext) { return new DefaultClientTubelineAssemblyContext(paramClientTubeAssemblerContext); }
  
  private static class MessageDumpingInfo {
    final boolean dumpBefore;
    
    final boolean dumpAfter;
    
    final Level logLevel;
    
    MessageDumpingInfo(boolean param1Boolean1, boolean param1Boolean2, Level param1Level) {
      this.dumpBefore = param1Boolean1;
      this.dumpAfter = param1Boolean2;
      this.logLevel = param1Level;
    }
  }
  
  private enum Side {
    Client("client"),
    Endpoint("endpoint");
    
    private final String name;
    
    Side(String param1String1) { this.name = param1String1; }
    
    public String toString() { return this.name; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\assembler\MetroTubelineAssembler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */