package org.concord.LabBook;

import org.concord.waba.extra.ui.*;

public interface ViewContainer
{
	public MainView getMainView();

    public void done(LabObjectView source);

    public void reload(LabObjectView source);
}
