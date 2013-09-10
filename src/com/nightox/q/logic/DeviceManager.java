package com.nightox.q.logic;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.nightox.q.db.Database;
import com.nightox.q.model.m.Q;
import com.nightox.q.utils.DateUtils;

public class DeviceManager {
	
	private static Log			log = LogFactory.getLog(DeviceManager.class);

	public Q loadDevice(String qid)
	{
		Q			device = (Q)Database.getSession().createCriteria(Q.class)
										.add(Restrictions.eq("q", qid))
										.add(Restrictions.eq("dataType", "device"))
										.addOrder(Order.desc("id"))
										.setMaxResults(1)
										.uniqueResult();
		if ( device == null )
		{
			device = new Q(qid);
			device.setDataType("device");
			device.save();
		}
		
		return device;
	}

	public String getPlan(Q device)
	{
		return device.getProp("plan", null);
	}
	
	public void setPlan(Q device, String plan)
	{
		device.setProp("plan", plan);
	}
	
	public Date getPlanStartedAt(Q device)
	{
		String		prop = device.getProp("planStartedAt", null);
		
		try {
			return prop != null ? DateUtils.parseDate(prop) : null;
		} catch (ParseException e) {
			log.warn("", e);
			return null;
		}
	}
	
	public void setPlanStartedAt(Q device, Date date)
	{
		device.setProp("planStartedAt", DateUtils.formatDate(date));
	}
	
	public Date getPlanEndsAt(Q device)
	{
		String		prop = device.getProp("planEndsAt", null);
		
		try {
			return prop != null ? DateUtils.parseDate(prop) : null;
		} catch (ParseException e) {
			log.warn("", e);
			return null;
		}
	}
	
	public void setPlanEndsAt(Q device, Date date)
	{
		device.setProp("planEndsAt", DateUtils.formatDate(date));
	}

}
