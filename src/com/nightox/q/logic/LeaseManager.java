package com.nightox.q.logic;

import java.util.Calendar;

import com.nightox.q.model.m.Q;

public class LeaseManager {

	private int			defaultLeaseMinutes = 60;
	private int			maxLeaseMinutes = 1440;
	
	private String		unleasedText;
	private String		leasedToHolderText;
	private String		leasedToOther;
	
	public boolean lease(Q q, String holder, int period)
	{
		// must not be leased
		if ( isLeased(q) )
			return false;
		
		// setup lease start
		Calendar	now = Calendar.getInstance();
		q.setLeaseHolder(holder);
		q.setLeaseStartedAt(now.getTime());
		
		// adjust period
		if ( period <= 0 )
			period = defaultLeaseMinutes;
		period = Math.min(period, maxLeaseMinutes);
		
		// setup lease end
		now.add(Calendar.MINUTE, period);
		q.setLeaseEndsAt(now.getTime());
		
		return true;
	}
	
	public boolean isLeased(Q q)
	{
		// no holder?
		if ( q.getLeaseHolder() == null )
			return false;
		
		// no end date?
		if ( q.getLeaseEndsAt() == null )
			return false;
		
		// end date passed
		if ( Calendar.getInstance().getTime().after(q.getLeaseEndsAt()) )
			return false;
		
		// otherwise, leased
		return true;
	}
	
	public boolean isLeaseOwner(Q q, String holder)
	{
		return isLeased(q) && holder.equals(q.getLeaseHolder());
	}
	
	public int secondsToLeaseEnd(Q q)
	{
		if ( !isLeased(q) )
			return 0;
		
		return (int)(q.getLeaseEndsAt().getTime() - Calendar.getInstance().getTime().getTime()) / 1000;
		
	}

	public int getDefaultLeaseMinutes() {
		return defaultLeaseMinutes;
	}

	public void setDefaultLeaseMinutes(int defaultLeaseMinutes) {
		this.defaultLeaseMinutes = defaultLeaseMinutes;
	}

	public String getUnleasedText() {
		return unleasedText;
	}

	public void setUnleasedText(String unleasedText) {
		this.unleasedText = unleasedText;
	}

	public String getLeasedToHolderText() {
		return leasedToHolderText;
	}

	public void setLeasedToHolderText(String leasedToHolderText) {
		this.leasedToHolderText = leasedToHolderText;
	}

	public String getLeasedToOther() {
		return leasedToOther;
	}

	public void setLeasedToOther(String leasedToOther) {
		this.leasedToOther = leasedToOther;
	}

	public int getMaxLeaseMinutes() {
		return maxLeaseMinutes;
	}

	public void setMaxLeaseMinutes(int maxLeaseMinutes) {
		this.maxLeaseMinutes = maxLeaseMinutes;
	}
	
}
