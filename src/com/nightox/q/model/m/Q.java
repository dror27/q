package com.nightox.q.model.m;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.freebss.sprout.banner.util.StreamUtils;
import com.nightox.q.model.base.DbObject;

public class Q extends DbObject {

	static final int		Q_BINARY_DATA_SIZE = (16777216-1);
	static final int		Q_TEXT_DATA_SIZE = (65536-1);
	static final int		Q_CONTENT_TYPE_SIZE = 64;
	
	String		q;
	
	String		dataType;
	byte[]		binaryData;
	String		contentType;
	String		textData;
	
	Double		latitude;
	Double		longitude;
	Double		altitude;
	
	public void cleanData()
	{
		setDataType(null);
		setBinaryData((byte[])null);
		setContentType(null);
		setTextData(null);
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
	public byte[] getBinaryData() {
		return binaryData;
	}
	
	public void setBinaryData(byte[] binaryData) 
	{
		if ( binaryData != null && binaryData.length > Q_BINARY_DATA_SIZE )
		{
			byte[]		data = new byte[Q_BINARY_DATA_SIZE];
			
			System.arraycopy(binaryData, 0, data, 0, Q_BINARY_DATA_SIZE);
			binaryData = data;
		}
		this.binaryData = binaryData;
	}
	
	public void setBinaryData(InputStream is) throws IOException
	{
		if ( is != null )
		{
			ByteArrayOutputStream			os = new ByteArrayOutputStream();
			StreamUtils.copy(is, os);
			
			setBinaryData(os.toByteArray());
		}
		else
			this.binaryData = null;
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
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) 
	{
		if ( contentType != null && contentType.length() > Q_CONTENT_TYPE_SIZE )
			contentType = contentType.substring(0, Q_CONTENT_TYPE_SIZE);
		
		this.contentType = contentType;
	}

	public String getBinaryDataAsString() 
	{
		if ( binaryData == null )
			return null;
		
		try {
			return new String(binaryData, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
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
	
}
