package com.nightox.q.model.base;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.criterion.Restrictions;

import com.nightox.q.api.ApiException;
import com.nightox.q.api.cmd.base.CmdCRUDBase;
import com.nightox.q.db.Database;
import com.nightox.q.model.Prop;
import com.nightox.q.utils.DateUtils;
import com.nightox.q.utils.records.Record;

public abstract class DbObject implements IMapRepresetation {

	int			id;

	static public DbObject get(Class<? extends DbObject> clazz, Integer id) throws ApiException
	{
		DbObject		obj = (DbObject)Database.getInstance().getSessionManager().peekThreadSession().getSession().get(clazz, id);
		
		if ( obj == null )
			throw new ApiException(new ObjectNotFoundException(id, clazz.getName()), "id=" + id);
		
		return obj;
	}
	
	static public DbObject get(Class<? extends DbObject> clazz, String key) throws ApiException
	{
		int				index = key.indexOf(":");
		if ( index < 0 )
			return get(clazz, new Integer(key));
		else
			return getByProperty(clazz, key.substring(0, index), key.substring(index + 1));
	}

	static public DbObject getByProperty(Class<? extends DbObject> clazz, String propertyName, String propertyValue) throws ApiException
	{
		return getByProperty(clazz, propertyName, propertyValue, true);
	}

	@SuppressWarnings("unchecked")
	static public DbObject getByProperty(Class<? extends DbObject> clazz, String propertyName, String propertyValue, boolean throwOnNotFound) throws ApiException
	{	
		Criteria		crit = Database.getSession().createCriteria(clazz)
									.setMaxResults(1);
		
		@SuppressWarnings("rawtypes")
		Class		fieldClass = CmdCRUDBase.getInstanceFieldClass(clazz, propertyName);
		
		if ( DbObject.class.isAssignableFrom(fieldClass) )
		{
			DbObject	fieldValue = DbObject.get(fieldClass, propertyValue);

			crit = crit.add(Restrictions.eq(propertyName, fieldValue));
		}
		else if ( Integer.class.isAssignableFrom(fieldClass) || fieldClass.getName().equals("int") )
			crit = crit.add(Restrictions.eq(propertyName, Integer.parseInt(propertyValue)));
		else if ( Double.class.isAssignableFrom(fieldClass) || fieldClass.getName().equals("double") )
			crit = crit.add(Restrictions.eq(propertyName, Double.parseDouble(propertyValue)));
		else 
			crit = crit.add(Restrictions.eq(propertyName, propertyValue));
		
		DbObject		obj = (DbObject)crit.uniqueResult();
		
		if ( obj == null && throwOnNotFound )
		{
			String			fieldExpr = propertyName + "=" + propertyValue;
			throw new ApiException(new ObjectNotFoundException(fieldExpr, clazz.getName()), fieldExpr);
		}
		
		return obj;
	}
	
	static public DbObject get(Class<? extends DbObject> clazz) throws ApiException
	{
		DbObject		obj = (DbObject)Database.getInstance().getSessionManager().peekThreadSession().getSession()
									.createCriteria(clazz).setMaxResults(1).uniqueResult();
		
		if ( obj == null )
			throw new ApiException(new ObjectNotFoundException(-1, clazz.getName()));
		
		return obj;
	}
	
	static public List<DbObject> getList(Class<? extends DbObject> clazz, String keyList) throws ApiException
	{
		List<DbObject>	objList = new LinkedList<DbObject>();
		
		for ( String key : keyList.split(",") )
			objList.add(get(clazz, key));
		
		return objList;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean equals(Object obj) 
	{
		if ( obj == null )
			return false;
		if ( !(obj instanceof DbObject) )
			return false;
		
		if ( !getAbbrClassName().equals(((DbObject)obj).getAbbrClassName()) )
			return false;
		
		return getId() == ((DbObject)obj).getId();
	}
	
	public Map<String, Object> getMapRepresentation(int level) 
	{
		Map<String, Object>			map = new LinkedHashMap<String, Object>();
		
		map.put("id", getId());
		
		return map;
	}
	
	public String getAbbrClassName()
	{	
		return getAbbrClassName(getClass());
	}
	
	static public String getAbbrClassName(Class<? extends DbObject> clazz)
	{
		return clazz.getSimpleName().toLowerCase();
	}

	public void setProp(String name, int value)
	{
		setProp(name, Integer.toString(value));
	}

	public void setProp(String name, String value)
	{
		// find existing if present
		Criteria	crit = Database.getSession().createCriteria(Prop.class)
								.add(Restrictions.eq("name", name))
								.add(Restrictions.eq("objectId", getId()))
								.add(Restrictions.eq("objectClass", getAbbrClassName()));
		
		Prop		prop = (Prop)crit.uniqueResult();
		
		if ( value != null )
		{
			if ( prop != null )
				prop.setValue(value);
			else
			{
				prop = new Prop();
				prop.setName(name);
				prop.setValue(value);
				prop.setObjectId(getId());
				prop.setObjectClass(getAbbrClassName());
			}
			
			Database.getSession().saveOrUpdate(prop);		
		}
		else
		{
			if ( prop != null )
				Database.getSession().delete(prop);
		}
	}
	
	public String getProp(String name, String defaultValue)
	{
		Criteria	crit = Database.getSession().createCriteria(Prop.class)
		.add(Restrictions.eq("name", name))
		.add(Restrictions.eq("objectId", getId()))
		.add(Restrictions.eq("objectClass", getAbbrClassName()));
		
		Prop		prop = (Prop)crit.uniqueResult();
		if ( prop != null )
			return prop.getValue();
		else
			return defaultValue;

	}
	
	@SuppressWarnings("unchecked")
	public static List<Prop> listProps(String propName, String propValue, Class<? extends DbObject> clazz)
	{
		Criteria		crit = Database.getSession().createCriteria(Prop.class)
							.add(Restrictions.eq("name", propName))
							.add(Restrictions.eq("value", propValue))
							.add(Restrictions.eq("objectClass", DbObject.getAbbrClassName(clazz)));
		
		return (List<Prop>)crit.list();
	}

	public void save() 
	{
		Database.getSession().save(this);	
	}
	
	public void update() 
	{
		Database.getSession().update(this);	
	}
	
	protected void mapProp(Map<String, Object> map, String name, Object value)
	{
		if ( value != null )
			map.put(name, value.toString());
			
	}
	
	protected void mapProp(Map<String, Object> map, String name, int value)
	{
		map.put(name, value);			
	}

	protected void mapProp(Map<String, Object> map, String name, double value)
	{
		map.put(name, value);			
	}

	protected void mapProp(Map<String, Object> map, String name, boolean value)
	{
		map.put(name, value);			
	}

	protected void mapProp(Map<String, Object> map, String name, Date value)
	{
		if ( value != null )
			map.put(name, DateUtils.formatDate(value));
	}

	protected void mapProp(Map<String, Object> map, String name, byte[] value)
	{
		if ( value != null )
			map.put(name + "Size", value.length);
	}

	protected void mapProp(Map<String, Object> map, String name, DbObject value, int level)
	{
		if ( value == null )
			return;
		
		if ( level == IMapRepresetation.LEVEL_SHALOW )
			map.put(name + "Id", value.getId());
		else
			map.put(name, value.getMapRepresentation(level - 1));
	}
	
	protected void mapProp(Map<String, Object> map, String name, Collection<? extends DbObject> values, int level)
	{
		if ( values == null )
			return;
		
		if ( level == IMapRepresetation.LEVEL_SHALOW )
			map.put(name + "Count", values.size());
		else
		{
			List<Map<String, Object>>	list = new LinkedList<Map<String,Object>>();
			
			for ( DbObject value : values )
				list.add(value.getMapRepresentation(level - 1));
			
			map.put(name, list);
		}
	}
	
	protected void mapProp(Record map, String name, Object value)
	{
		if ( value != null )
			map.put(name, value.toString());
			
	}

}
