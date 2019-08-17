package java.awt;

import sun.awt.AppContext;

abstract class ModalEventFilter implements EventFilter {
  protected Dialog modalDialog;
  
  protected boolean disabled;
  
  protected ModalEventFilter(Dialog paramDialog) {
    this.modalDialog = paramDialog;
    this.disabled = false;
  }
  
  Dialog getModalDialog() { return this.modalDialog; }
  
  public EventFilter.FilterAction acceptEvent(AWTEvent paramAWTEvent) {
    if (this.disabled || !this.modalDialog.isVisible())
      return EventFilter.FilterAction.ACCEPT; 
    int i = paramAWTEvent.getID();
    if ((i >= 500 && i <= 507) || (i >= 1001 && i <= 1001) || i == 201) {
      Object object = paramAWTEvent.getSource();
      if (!(object instanceof sun.awt.ModalExclude) && object instanceof Component) {
        Component component;
        for (component = (Component)object; component != null && !(component instanceof Window); component = component.getParent_NoClientCode());
        if (component != null)
          return acceptWindow((Window)component); 
      } 
    } 
    return EventFilter.FilterAction.ACCEPT;
  }
  
  protected abstract EventFilter.FilterAction acceptWindow(Window paramWindow);
  
  void disable() { this.disabled = true; }
  
  int compareTo(ModalEventFilter paramModalEventFilter) {
    Dialog dialog1 = paramModalEventFilter.getModalDialog();
    Dialog dialog2 = this.modalDialog;
    while (dialog2 != null) {
      if (dialog2 == dialog1)
        return 1; 
      Container container = dialog2.getParent_NoClientCode();
    } 
    dialog2 = dialog1;
    while (dialog2 != null) {
      if (dialog2 == this.modalDialog)
        return -1; 
      Container container = dialog2.getParent_NoClientCode();
    } 
    Dialog dialog3;
    for (dialog3 = this.modalDialog.getModalBlocker(); dialog3 != null; dialog3 = dialog3.getModalBlocker()) {
      if (dialog3 == dialog1)
        return -1; 
    } 
    for (dialog3 = dialog1.getModalBlocker(); dialog3 != null; dialog3 = dialog3.getModalBlocker()) {
      if (dialog3 == this.modalDialog)
        return 1; 
    } 
    return this.modalDialog.getModalityType().compareTo(dialog1.getModalityType());
  }
  
  static ModalEventFilter createFilterForDialog(Dialog paramDialog) {
    switch (paramDialog.getModalityType()) {
      case DOCUMENT_MODAL:
        return new DocumentModalEventFilter(paramDialog);
      case APPLICATION_MODAL:
        return new ApplicationModalEventFilter(paramDialog);
      case TOOLKIT_MODAL:
        return new ToolkitModalEventFilter(paramDialog);
    } 
    return null;
  }
  
  private static class ApplicationModalEventFilter extends ModalEventFilter {
    private AppContext appContext;
    
    ApplicationModalEventFilter(Dialog param1Dialog) {
      super(param1Dialog);
      this.appContext = param1Dialog.appContext;
    }
    
    protected EventFilter.FilterAction acceptWindow(Window param1Window) {
      if (param1Window.isModalExcluded(Dialog.ModalExclusionType.APPLICATION_EXCLUDE))
        return EventFilter.FilterAction.ACCEPT; 
      if (param1Window.appContext == this.appContext) {
        while (param1Window != null) {
          if (param1Window == this.modalDialog)
            return EventFilter.FilterAction.ACCEPT_IMMEDIATELY; 
          param1Window = param1Window.getOwner();
        } 
        return EventFilter.FilterAction.REJECT;
      } 
      return EventFilter.FilterAction.ACCEPT;
    }
  }
  
  private static class DocumentModalEventFilter extends ModalEventFilter {
    private Window documentRoot;
    
    DocumentModalEventFilter(Dialog param1Dialog) {
      super(param1Dialog);
      this.documentRoot = param1Dialog.getDocumentRoot();
    }
    
    protected EventFilter.FilterAction acceptWindow(Window param1Window) {
      if (param1Window.isModalExcluded(Dialog.ModalExclusionType.APPLICATION_EXCLUDE)) {
        for (Window window = this.modalDialog.getOwner(); window != null; window = window.getOwner()) {
          if (window == param1Window)
            return EventFilter.FilterAction.REJECT; 
        } 
        return EventFilter.FilterAction.ACCEPT;
      } 
      while (param1Window != null) {
        if (param1Window == this.modalDialog)
          return EventFilter.FilterAction.ACCEPT_IMMEDIATELY; 
        if (param1Window == this.documentRoot)
          return EventFilter.FilterAction.REJECT; 
        param1Window = param1Window.getOwner();
      } 
      return EventFilter.FilterAction.ACCEPT;
    }
  }
  
  private static class ToolkitModalEventFilter extends ModalEventFilter {
    private AppContext appContext;
    
    ToolkitModalEventFilter(Dialog param1Dialog) {
      super(param1Dialog);
      this.appContext = param1Dialog.appContext;
    }
    
    protected EventFilter.FilterAction acceptWindow(Window param1Window) {
      if (param1Window.isModalExcluded(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE))
        return EventFilter.FilterAction.ACCEPT; 
      if (param1Window.appContext != this.appContext)
        return EventFilter.FilterAction.REJECT; 
      while (param1Window != null) {
        if (param1Window == this.modalDialog)
          return EventFilter.FilterAction.ACCEPT_IMMEDIATELY; 
        param1Window = param1Window.getOwner();
      } 
      return EventFilter.FilterAction.REJECT;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\ModalEventFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */