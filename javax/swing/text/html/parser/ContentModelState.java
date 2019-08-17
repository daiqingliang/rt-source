package javax.swing.text.html.parser;

class ContentModelState {
  ContentModel model;
  
  long value;
  
  ContentModelState next;
  
  public ContentModelState(ContentModel paramContentModel) { this(paramContentModel, null, 0L); }
  
  ContentModelState(Object paramObject, ContentModelState paramContentModelState) { this(paramObject, paramContentModelState, 0L); }
  
  ContentModelState(Object paramObject, ContentModelState paramContentModelState, long paramLong) {
    this.model = (ContentModel)paramObject;
    this.next = paramContentModelState;
    this.value = paramLong;
  }
  
  public ContentModel getModel() {
    ContentModel contentModel = this.model;
    for (byte b = 0; b < this.value; b++) {
      if (contentModel.next != null) {
        contentModel = contentModel.next;
      } else {
        return null;
      } 
    } 
    return contentModel;
  }
  
  public boolean terminate() {
    byte b;
    ContentModel contentModel;
    switch (this.model.type) {
      case 43:
        if (this.value == 0L && !this.model.empty())
          return false; 
      case 42:
      case 63:
        return (this.next == null || this.next.terminate());
      case 124:
        for (contentModel = (ContentModel)this.model.content; contentModel != null; contentModel = contentModel.next) {
          if (contentModel.empty())
            return (this.next == null || this.next.terminate()); 
        } 
        return false;
      case 38:
        contentModel = (ContentModel)this.model.content;
        b = 0;
        while (contentModel != null) {
          if ((this.value & 1L << b) == 0L && !contentModel.empty())
            return false; 
          b++;
          contentModel = contentModel.next;
        } 
        return (this.next == null || this.next.terminate());
      case 44:
        contentModel = (ContentModel)this.model.content;
        b = 0;
        while (b < this.value) {
          b++;
          contentModel = contentModel.next;
        } 
        while (contentModel != null && contentModel.empty())
          contentModel = contentModel.next; 
        return (contentModel != null) ? false : ((this.next == null || this.next.terminate()));
    } 
    return false;
  }
  
  public Element first() {
    byte b;
    ContentModel contentModel;
    switch (this.model.type) {
      case 38:
      case 42:
      case 63:
      case 124:
        return null;
      case 43:
        return this.model.first();
      case 44:
        contentModel = (ContentModel)this.model.content;
        b = 0;
        while (b < this.value) {
          b++;
          contentModel = contentModel.next;
        } 
        return contentModel.first();
    } 
    return this.model.first();
  }
  
  public ContentModelState advance(Object paramObject) {
    byte b2;
    byte b1;
    ContentModel contentModel;
    switch (this.model.type) {
      case 43:
        return this.model.first(paramObject) ? (new ContentModelState(this.model.content, new ContentModelState(this.model, this.next, this.value + 1L))).advance(paramObject) : ((this.value != 0L) ? ((this.next != null) ? this.next.advance(paramObject) : null) : null);
      case 42:
        return this.model.first(paramObject) ? (new ContentModelState(this.model.content, this)).advance(paramObject) : ((this.next != null) ? this.next.advance(paramObject) : null);
      case 63:
        return this.model.first(paramObject) ? (new ContentModelState(this.model.content, this.next)).advance(paramObject) : ((this.next != null) ? this.next.advance(paramObject) : null);
      case 124:
        for (contentModel = (ContentModel)this.model.content; contentModel != null; contentModel = contentModel.next) {
          if (contentModel.first(paramObject))
            return (new ContentModelState(contentModel, this.next)).advance(paramObject); 
        } 
        return null;
      case 44:
        contentModel = (ContentModel)this.model.content;
        b1 = 0;
        while (b1 < this.value) {
          b1++;
          contentModel = contentModel.next;
        } 
        return (contentModel.first(paramObject) || contentModel.empty()) ? ((contentModel.next == null) ? (new ContentModelState(contentModel, this.next)).advance(paramObject) : (new ContentModelState(contentModel, new ContentModelState(this.model, this.next, this.value + 1L))).advance(paramObject)) : null;
      case 38:
        contentModel = (ContentModel)this.model.content;
        b1 = 1;
        b2 = 0;
        while (contentModel != null) {
          if ((this.value & 1L << b2) == 0L) {
            if (contentModel.first(paramObject))
              return (new ContentModelState(contentModel, new ContentModelState(this.model, this.next, this.value | 1L << b2))).advance(paramObject); 
            if (!contentModel.empty())
              b1 = 0; 
          } 
          b2++;
          contentModel = contentModel.next;
        } 
        return (b1 != 0) ? ((this.next != null) ? this.next.advance(paramObject) : null) : null;
    } 
    return (this.model.content == paramObject) ? ((this.next == null && paramObject instanceof Element && ((Element)paramObject).content != null) ? new ContentModelState(((Element)paramObject).content) : this.next) : null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\parser\ContentModelState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */