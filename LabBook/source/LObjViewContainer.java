package org.concord.LabBook;

import org.concord.waba.extra.ui.*;

public interface LObjViewContainer
{
    public void addMenu(LabObjectView source, Menu menu);

    public void delMenu(LabObjectView source, Menu menu);

    public void done(LabObjectView source);

    public void reload(LabObjectView source);

    public LObjDictionary getDict();
}
