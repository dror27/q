package com.nightox.q.beans;

import com.nightox.q.html.HtmlRenderer;
import com.nightox.q.logic.DeviceManager;
import com.nightox.q.logic.LeaseManager;
import com.nightox.q.types.QManager;

public class Services {
	
	private QManager				qManager = new QManager();
	private HtmlRenderer			htmlRenderer = new HtmlRenderer();
	private LeaseManager			leaseManager = new LeaseManager();
	private DeviceManager			deviceManager = new DeviceManager();
	
	public Services()
	{
	}

	public QManager getqManager() {
		return qManager;
	}

	public void setqManager(QManager qManager) {
		this.qManager = qManager;
	}

	public HtmlRenderer getHtmlRenderer() {
		return htmlRenderer;
	}

	public void setHtmlRenderer(HtmlRenderer htmlRenderer) {
		this.htmlRenderer = htmlRenderer;
	}

	public LeaseManager getLeaseManager() {
		return leaseManager;
	}

	public void setLeaseManager(LeaseManager leaseManager) {
		this.leaseManager = leaseManager;
	}

	public DeviceManager getDeviceManager() {
		return deviceManager;
	}

	public void setDeviceManager(DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}
	
}
