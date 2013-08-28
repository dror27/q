package com.nightox.q.model.m;

import java.util.Date;

import com.nightox.q.model.base.DbObject;

public class QNoBlobs extends DbObject {

	static final int		Q_CONTENT_TYPE_SIZE = 64;
	static final int		Q_TEXT_DATA_SIZE = (65536-1);
	
	String		q;
	
	String		dataType;
	String		contentType;
	String		textData;
	
	Double		latitude;
	Double		longitude;
	Double		altitude;
	
	Integer		version;
	String		leaseHolder;
	Date		leaseStartedAt;
	Date		leaseEndsAt;

	public QNoBlobs()
	{
		super();
	}
	
	public QNoBlobs(String qid)
	{
		super();
		
		this.q = qid;
	}
	
	public QNoBlobs(QNoBlobs other)
	{
		super();
		this.q = other.getQ();
		this.latitude = other.getLatitude();
		this.longitude = other.getLongitude();
		this.altitude = other.getAltitude();
	}
	
	public void cleanData()
	{
		setDataType(null);
		setContentType(null);
		setTextData(null);
	}
	
	public void cleanLease()
	{
		setLeaseHolder(null);
		setLeaseStartedAt(null);
		setLeaseEndsAt(null);
	}
	
	public String getQ() {
		return q;
	}
	public void setQ(String q) {
		this.q = q;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) 
	{
		if ( contentType != null && contentType.length() > Q_CONTENT_TYPE_SIZE )
			contentType = contentType.substring(0, Q_CONTENT_TYPE_SIZE);
		
		this.contentType = contentType;
	}

	public boolean hasPosition()
	{
		return latitude != null && longitude != null;
	}
	
	public void setPosition(double latitude, double longitude, double altitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}
	
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public String getLeaseHolder() {
		return leaseHolder;
	}

	public void setLeaseHolder(String leaseHolder) {
		this.leaseHolder = leaseHolder;
	}

	public Date getLeaseStartedAt() {
		return leaseStartedAt;
	}

	public void setLeaseStartedAt(Date leaseStartedAt) {
		this.leaseStartedAt = leaseStartedAt;
	}

	public Date getLeaseEndsAt() {
		return leaseEndsAt;
	}

	public void setLeaseEndsAt(Date leaseEndsAt) {
		this.leaseEndsAt = leaseEndsAt;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public void setAutoVersion()
	{
		// since ids are generated in an sequential fashion, they can serve as (sparse)
		this.version = getId();
	}
	
	public String getTextData() {
		return textData;
	}
	
	public void setTextData(String textData) 
	{
		if ( textData != null && textData.length() > Q_TEXT_DATA_SIZE )
			textData = textData.substring(0, Q_TEXT_DATA_SIZE);
		
		this.textData = textData;
	}

}
