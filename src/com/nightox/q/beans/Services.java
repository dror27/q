package com.nightox.q.beans;

import com.nightox.q.types.QManager;
import com.nightox.q.types.QTypeManager;

public class Services {
	
	private QTypeManager			qTypeManager;	
	private QManager				qManager = new QManager();
	
	public Services()
	{
	}

	public QTypeManager getqTypeManager() {
		return qTypeManager;
	}

	public void setqTypeManager(QTypeManager qTypeManager) {
		this.qTypeManager = qTypeManager;
	}

	public QManager getqManager() {
		return qManager;
	}

	public void setqManager(QManager qManager) {
		this.qManager = qManager;
	}
	
}
