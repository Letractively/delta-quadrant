package com.k42b3.zubat;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class BodyPanel extends JTabbedPane
{
	private Zubat zubat;

	public BodyPanel(Zubat zubatInstance)
	{
		this.zubat = zubatInstance;

		this.addTab("View", null);
		this.addTab("Create", null);
		this.addTab("Update", null);
		this.addTab("Delete", null);

		this.setEnabledAt(2, false);
		this.setEnabledAt(3, false);

		this.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				switch(getSelectedIndex())
				{
					case 3:

						zubat.loadForm(zubat.getSelectedService().getUri() + "/form?method=delete");

						break;

					case 2:

						zubat.loadForm(zubat.getSelectedService().getUri() + "/form?method=update");

						break;

					case 1:

						zubat.loadForm(zubat.getSelectedService().getUri() + "/form?method=create");

						break;

					default:
					case 0:

						zubat.loadService(zubat.getSelectedService(), zubat.getSelectedFields());

						break;
				}
			}

		});
	}
}
