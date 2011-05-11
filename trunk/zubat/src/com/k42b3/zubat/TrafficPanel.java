package com.k42b3.zubat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class TrafficPanel extends JPanel
{
	private JTable trafficTable;
	private TrafficDetail trafficDetailFrame;
	private TrafficTableModel trafficTm;

	public TrafficPanel(TrafficTableModel trafficTmModel)
	{
		this.setLayout(new BorderLayout());


		this.trafficTm = trafficTmModel;

		trafficTable = new JTable(trafficTm);

		trafficTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		trafficTable.getColumnModel().getColumn(0).setMinWidth(60); 
		trafficTable.getColumnModel().getColumn(0).setMaxWidth(60); 
		trafficTable.getColumnModel().getColumn(1).setMinWidth(120); 
		trafficTable.getColumnModel().getColumn(1).setMaxWidth(120); 
		trafficTable.getColumnModel().getColumn(2).setMinWidth(600); 

		trafficTable.addMouseListener(new MouseListener() {

			public void mouseReleased(MouseEvent e) 
			{
				TrafficItem item = trafficTm.getRow(trafficTable.getSelectedRow());

				if(item != null)
				{
					if(trafficDetailFrame == null)
					{
						trafficDetailFrame = new TrafficDetail();
					}

					trafficDetailFrame.setItem(item);

					trafficDetailFrame.setVisible(true);

					trafficDetailFrame.toFront();
				}
			}

			public void mousePressed(MouseEvent e) 
			{
			}

			public void mouseExited(MouseEvent e) 
			{
			}

			public void mouseEntered(MouseEvent e) 
			{
			}

			public void mouseClicked(MouseEvent e) 
			{
			}

		});

		JScrollPane trafficPane = new JScrollPane(trafficTable);
		trafficPane.setPreferredSize(new Dimension(600, 200));


		this.add(trafficPane, BorderLayout.CENTER);
	}
}
