package com.nightox.q.api.cmd.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import com.nightox.q.api.ApiConst;
import com.nightox.q.api.ApiContext;
import com.nightox.q.api.ApiException;
import com.nightox.q.api.ApiPacket;
import com.nightox.q.api.IApiObjectCommand;
import com.nightox.q.api.notify.DbObjectNotifier;
import com.nightox.q.db.Database;
import com.nightox.q.model.ElementAnnotation;
import com.nightox.q.model.Prop;
import com.nightox.q.model.User;
import com.nightox.q.model.base.DbObject;
import com.nightox.q.model.base.IHasAccount;
import com.nightox.q.model.base.IMapRepresetation;

public class CmdCRUDBase extends CmdBase {

	private static Log						log = LogFactory.getLog(CmdCRUDBase.class);
	
	protected List<String>					readFields = new LinkedList<String>();
	protected List<String>					createFields = new LinkedList<String>();
	protected List<String>					updateFields;
	protected Class<? extends DbObject>		crudClass;
	protected Map<String, IApiObjectCommand> updateCommands = new LinkedHashMap<String, IApiObjectCommand>();
	protected Map<String, IApiObjectCommand> globalCommands = new LinkedHashMap<String, IApiObjectCommand>();
	
	protected String						createMinRole = "user";
	protected int							createMinRoleIndex;
	
	static {
		Converter		c = new DateConverter(null);
		ConvertUtils.register(c, Date.class);
	}
	
	@SuppressWarnings("unchecked")
	public void doCommand(ApiContext context) throws ApiException
	{
		super.doCommand(context);

		// get level
		int			level = IMapRepresetation.LEVEL_SHALOW;
		if ( context.getApiRequest().hasField("_level") )
			level = context.getApiRequest().getIntegerField("_level");
		log.debug("level: " + level);

		// branch on update verb
		String			pathInfo = context.getRequestPath();
		log.debug("pathInfo: " + pathInfo);
		for ( String verb : updateCommands.keySet() )
			if ( pathInfo.endsWith("/" + verb) )
			{
				log.debug("updateCommand found: " + verb);
				
				// assumes an "update" like command with a difference body
				DbObject				obj = load(context);
				
				updateCommands.get(verb).doCommand(obj, context);
				
				Database.getSession().save(obj);
				
				if ( !context.getApiResponse().hasField(verb) )
					context.getApiResponse().setField(verb, obj.getMapRepresentation(level));
				
				return;
			}
		
		// branch of global command
		for ( String verb : globalCommands.keySet() )
			if ( pathInfo.endsWith("/" + verb) )
			{
				log.debug("globalCommands found: " + verb);

				globalCommands.get(verb).doCommand(null, context);
				
				return;
			}
		
		if ( pathInfo.endsWith("/list") )
		{		
			List<Map<String, Object>>	list = new LinkedList<Map<String,Object>>();
			Criteria					criteria = fillCRUDCriteria(Database.getSession().createCriteria(getCrudClass()), context);
						
			for ( DbObject obj : (List<DbObject>)criteria.list() )
				list.add(obj.getMapRepresentation(level));
			
			if ( context.getApiRequest().hasField("_props") )
				addPropsToResponseEntities(list);
			
			context.getApiResponse().setField("list", list);
		}
		else if ( pathInfo.endsWith("/count") )
		{		
			Criteria					criteria = fillCRUDCriteria(Database.getSession().createCriteria(getCrudClass()), context);

			criteria = criteria.setProjection(Projections.rowCount());
			
			context.getApiResponse().setField("count", criteria.uniqueResult());
		}
		else if ( pathInfo.endsWith("/create") )
		{
			if ( getRoleIndex(context.getApiSession().getUserType()) < createMinRoleIndex )
				throwNotAuthorized();
			
			DbObject				obj = getCRUDNewInstance(context);
			
			obj.save();
					
			context.getApiResponse().setField("create", obj.getMapRepresentation(level));			
		}
		else if ( pathInfo.endsWith("/read") )
		{
			Map<String, Object>			read = load(context).getMapRepresentation(level);
	
			if ( context.getApiRequest().hasField("_props") )
				addPropsToResponseEntity(read);

			context.getApiResponse().setField("read", read);			
		}
		else if ( pathInfo.endsWith("/update") )
		{
			DbObject				obj = load(context);
			
			fillCRUDEditInstance(obj, context);
			
			Database.getSession().save(obj);
			
			Map<String, Object>			update = obj.getMapRepresentation(level);
			
			if ( context.getApiRequest().hasField("_props") )
				addPropsToResponseEntity(update);
			
			context.getApiResponse().setField("update", update);			
		}
		else if ( pathInfo.endsWith("/delete") )
		{
			DbObject				obj = load(context);
			
			
			// delete all props
			Criteria				crit = Database.getSession().createCriteria(Prop.class)
											.add(Restrictions.eq("objectId", obj.getId()))
											.add(Restrictions.eq("objectClass", obj.getAbbrClassName()));
			for ( Prop prop : (List<Prop>)crit.list() )
				Database.getSession().delete(prop);

			Database.getSession().delete(obj);
			
		}
		else if ( pathInfo.endsWith("/props") )
		{
			DbObject				obj = load(context);
			
			// create props?
			String					name = context.getApiRequest().getField("name", null);
			if ( name != null )
			{
				String				value = context.getApiRequest().getField("value", null);

				obj.setProp(name, value);
			}
			
			// list props
			Map<String, String>		props = new LinkedHashMap<String, String>();
			Criteria				crit = Database.getSession().createCriteria(Prop.class)
												.add(Restrictions.eq("objectId", obj.getId()))
												.add(Restrictions.eq("objectClass", obj.getAbbrClassName()));
			for ( Prop prop : (List<Prop>)crit.list() )
				props.put(prop.getName(), prop.getValue());
			
			context.getApiResponse().setField("props", props);			
		}
		else if ( pathInfo.endsWith("/describe") )
		{		
			Map<String, Object>			describe = new LinkedHashMap<String, Object>();
	
			if ( getCreateFields() != null && getCreateFields().size() > 0 )
				describe.put("createFields", getCreateFields());
			if ( getReadFields() != null && getReadFields().size() > 0 )
				describe.put("getReadFields", getReadFields());
			if ( getUpdateFields() != null && getUpdateFields().size() > 0 )
				describe.put("getUpdateFields", getUpdateFields());
			
			if ( globalCommands != null && globalCommands.size() > 0 )
				describe.put("globalCommands", new LinkedList<String>(globalCommands.keySet()));
			if ( updateCommands != null && updateCommands.size() > 0 )
				describe.put("updateCommands", new LinkedList<String>(updateCommands.keySet()));
			
			context.getApiResponse().setField("describe", describe);
		}
		else if ( pathInfo.endsWith("/notify") )
		{
			DbObject				obj = load(context);
			String					operation = context.getApiRequest().getField("operation", null);
			String					message = context.getApiRequest().getField("message", null);
			
			context.getApiResponse().setField("notify", DbObjectNotifier.notify(obj, operation, message));
		}
		else
		{
			log.debug("no such command: " + pathInfo);
			
			throw new ApiException(ApiConst.API_ERR_BAD_CMD, pathInfo);
		}
	}
	
	private void addPropsToResponseEntity(Map<String, Object> map) 
	{
		List<Map<String, Object>>	list = new LinkedList<Map<String,Object>>();
		list.add(map);
		
		addPropsToResponseEntities(list);
	}

	@SuppressWarnings("unchecked")
	private void addPropsToResponseEntities(List<Map<String, Object>> list) 
	{
		// create index and collect ids
		Map<Integer, Map<String, Object>>	index = new LinkedHashMap<Integer, Map<String,Object>>();		
		for ( Map<String, Object> map : list )
		{
			int			id = Integer.parseInt(map.get("id").toString());
			
			index.put(id, map);
		}

		/// query
		Criteria		crit = Database.getSession().createCriteria(Prop.class)
							.add(Restrictions.eq("objectClass", DbObject.getAbbrClassName(getCrudClass())))
							.add(Restrictions.in("objectId", index.keySet()));
		for ( Prop prop : (List<Prop>)crit.list() )
		{
			Map<String, Object>		map = index.get(prop.getObjectId());
			if ( map != null )
			{
				Map<String,String>		props = (Map<String,String>)map.get("props");
				if ( props == null )
					map.put("props", props = new LinkedHashMap<String, String>());
				
				props.put(prop.getName(), prop.getValue());
			}
		}
	}

	protected DbObject load(ApiContext context) throws ApiException
	{
		DbObject		obj;
		
		if ( context.getApiRequest().hasField("id") )
			obj = DbObject.get(getCrudClass(), context.getApiRequest().getField("id"));
		else 
		{
			Criteria			criteria = Database.getSession().createCriteria(getCrudClass());
			
			SimpleExpression	fieldExpr = null;
			
			for ( String field : readFields )
				if ( context.getApiRequest().hasField(field) )
				{
					criteria.add(fieldExpr = Restrictions.eq(field, context.getApiRequest().getField(field)));
					break;
				}
			
			if ( fieldExpr == null )
				throw new ApiException(ApiConst.API_ERR_MISSING_PARAM, StringUtils.join(readFields, "|"));

			obj  = (DbObject)criteria.uniqueResult();
			if ( obj == null )
				throw new ApiException(ApiConst.API_ERR_NO_SUCH, fieldExpr.toString());
			
		}

		
		if ( securityEnabled() )
			if ( obj != null && !context.getApiSession().isUserSuperAtLeast() && (obj instanceof IHasAccount) )
			{
				User			user = context.getApiSession().getUser();
			
				if ( user == null || !user.getAccount().equals(((IHasAccount)obj).getAccount()) )
				throw new ApiException(ApiConst.API_ERR_NO_SUCH);
			}
		
		return obj;
	}

	protected DbObject getCRUDNewInstance(ApiContext context) throws ApiException
	{
		try
		{
			DbObject			obj = getCrudClass().newInstance();
			
			for ( String field : createFields )
			{
				String		fieldName = field.split(":")[0];
				
				if ( !context.getApiRequest().hasField(fieldName) )
				{
					if ( field.endsWith("::") )
						continue;
					else
						throw new ApiException(ApiConst.API_ERR_MISSING_PARAM, field);
				}

				setInstanceField(obj, context, fieldName);
			}
			
			if ( securityEnabled() )
				if ( !context.getApiSession().isUserSuperAtLeast() && (obj instanceof IHasAccount) )
				{
					((IHasAccount)obj).setAccount(context.getApiSession().getUser().getAccount());
				}
			
			return obj;
		}
		catch (ApiException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ApiException(e);
		}
	}

	protected void fillCRUDEditInstance(DbObject obj, ApiContext context) throws ApiException 
	{
		try
		{
			for ( String field : ((updateFields != null) ? updateFields : createFields) )
			{
				String		fieldName = field.split(":")[0];
				
				if ( context.getApiRequest().hasField(fieldName) )
					setInstanceField(obj, context, fieldName);
			}

			if ( securityEnabled() )
				if ( !context.getApiSession().isUserSuperAtLeast() && (obj instanceof IHasAccount) )
				{
					((IHasAccount)obj).setAccount(context.getApiSession().getUser().getAccount());
				}
		}
		catch (ApiException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ApiException(e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Criteria fillCRUDCriteria(Criteria criteria, ApiContext context) throws ApiException
	{
		ApiPacket			req = context.getApiRequest();
		
		// add fields
		for ( String field : ((updateFields != null) ? updateFields : createFields) )
		{
			if ( field.endsWith("::") )
				field = field.substring(0, field.length() - 2);
			
			if ( req.hasField(field) )
			{
				Class		fieldClass = getInstanceFieldClass(getCrudClass(), field);
				
				if ( DbObject.class.isAssignableFrom(fieldClass) )
				{
					DbObject	fieldValue = req.getDbObjectField(field, null, (Class<? extends DbObject>)fieldClass); 

					criteria = criteria.add(Restrictions.eq(field, fieldValue));
				}
				else if ( Integer.class.isAssignableFrom(fieldClass) || fieldClass.getName().equals("int") )
					criteria = criteria.add(Restrictions.eq(field, req.getIntegerField(field)));
				else if ( Double.class.isAssignableFrom(fieldClass) || fieldClass.getName().equals("double") )
					criteria = criteria.add(Restrictions.eq(field, req.getDoubleField(field)));
				else if ( Set.class.isAssignableFrom(fieldClass) )
				{
					String				fieldValue = req.getField(field);
					List<Integer>		values = new LinkedList<Integer>();
					
					for ( String id : fieldValue.split(",") )
						values.add(Integer.parseInt(id));
					
					String				alias = "MyAlias_" + field;
					criteria = criteria.createAlias(field, alias);
					criteria = criteria.add(Restrictions.in(alias + ".id", values));
				}
				else 
					criteria = criteria.add(Restrictions.eq(field, req.getField(field)));
			}
		}
		
		// add include/exclude
		if ( req.hasField("_include") )
			criteria = criteria.add(Restrictions.in("id", parseIdList(req.getField("_include"))));
		if ( req.hasField("_exclude") )
			criteria = criteria.add(Restrictions.not(
									Restrictions.in("id", parseIdList(req.getField("_exclude")))));
		
		// add pagination
		int			pageSize = req.getIntegerField("_pageSize", 10);
		int			page = req.getIntegerField("_page", -1);
		if ( page >= 0 )
			criteria = criteria.setFirstResult(page * pageSize).setMaxResults(pageSize);
		
		if ( securityEnabled() )
			if ( !context.getApiSession().isUserSuperAtLeast() && IHasAccount.class.isAssignableFrom(getCrudClass()) )
			{
				criteria = criteria.add(Restrictions.eq("account", context.getApiSession().getUser().getAccount()));
			}

		return criteria;
	}

	private List<Integer> parseIdList(String text)
	{
		List<Integer>		result = new LinkedList<Integer>();
		
		for ( String elem : text.split(",") )
			result.add(Integer.parseInt(elem));
		
		return result;
	}
	
	static public String setterName(String field)
	{
		return "set" + Character.toUpperCase(field.charAt(0)) + field.substring(1);
	}
	
	static public String getterName(String field)
	{
		return "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);
	}
	
	@SuppressWarnings("rawtypes")
	static public Method getActualMethod(Class clazz, String name) throws SecurityException, NoSuchMethodException
	{
		for ( Method method : clazz.getMethods() )
			if ( method.getName().equals(name) )
				return method;
		
		if ( clazz.getSuperclass() != null )
			return getActualMethod(clazz.getSuperclass(), name);
		else
			throw new NoSuchMethodException(name);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setInstanceField(DbObject obj, ApiContext context, String field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ApiException
	{
		Class			fieldClass = getInstanceFieldClass(obj.getClass(), field);
		Object			fieldValue;
		
		if ( context.getApiRequest().hasField(field) && context.getApiRequest().getField(field).length() > 0 && context.getApiRequest().getField(field).charAt(0) == '\0' )
			fieldValue = null;
		else
		{
			if ( DbObject.class.isAssignableFrom(fieldClass) )
				fieldValue = context.getApiRequest().getDbObjectField(field, null, (Class<? extends DbObject>)fieldClass); 
			else if ( Date.class.isAssignableFrom(fieldClass) )
				fieldValue = context.getApiRequest().getDateField(field, context.getApiSession().getTimeZone());
			else if ( Set.class.isAssignableFrom(fieldClass) )
			{
				Set<DbObject>	set = (Set<DbObject>)getInstanceField(obj, context, field);
				Class			clazz = getInstanceFieldElementClass(obj, context, field);
				String			fieldValueList = context.getApiRequest().getField(field);
				
				boolean			doAdd = fieldValueList.length() > 0 && fieldValueList.charAt(0) == '+';
				boolean			doRemove = fieldValueList.length() > 0 && fieldValueList.charAt(0) == '-';
				if ( doAdd || doRemove )
					fieldValueList = fieldValueList.substring(1);
				else
					set.clear();
		
				for ( String id : fieldValueList.split(",") )
				{
					id = id.trim();
					if ( id.length() != 0 )
					{
						DbObject		obj1 = DbObject.get(clazz, id);
						
						if ( doRemove )
							set.remove(obj1);
						else
							set.add(obj1);
					}
				}
				
				return;
			}
			else
				fieldValue = context.getApiRequest().getField(field);
		}
					
		if ( fieldValue != null )
		{
			BeanUtils.setProperty(obj, field, fieldValue); 
		}
		else
		{
			Method		setter = getActualMethod(getCrudClass(), setterName(field));
			Object[]	args = {null};
			
			setter.invoke(obj, args);
		}
	}
	
	private Object getInstanceField(DbObject obj, ApiContext context, String field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		Method	getter = getActualMethod(obj.getClass(), getterName(field));
		
		return getter.invoke(obj);
	}

	@SuppressWarnings("rawtypes")
	private Class getInstanceFieldElementClass(DbObject obj, ApiContext context, String field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		return getInstanceFieldElementClass(obj.getClass(), context, field);
	}

	@SuppressWarnings("rawtypes")
	private Class getInstanceFieldElementClass(Class clazz, ApiContext context, String field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		Method	getter = getActualMethod(clazz, getterName(field));
		
		ElementAnnotation		annotation = getter.getAnnotation(ElementAnnotation.class);
		
		return annotation.value();
	}

	@SuppressWarnings("rawtypes")
	public static Class getInstanceFieldClass(Class clazz, String field) throws ApiException
	{
		Method setter;
		try {
			setter = getActualMethod(clazz, setterName(field));
		} catch (NoSuchMethodException e) {
			throw new ApiException(e);
		}
		
		return setter.getParameterTypes()[0];
	}

	public Class<? extends DbObject> getCrudClass() {
		return crudClass;
	}

	public void setCrudClass(Class<? extends DbObject> crudClass) {
		this.crudClass = crudClass;
	}

	public List<String> getReadFields() {
		return readFields;
	}

	public void setReadFields(List<String> readFields) {
		this.readFields = readFields;
	}

	public List<String> getCreateFields() {
		return createFields;
	}

	public void setCreateFields(List<String> createFields) {
		this.createFields = createFields;
	}

	public void setCreateFieldsText(String createFieldsText) {
		this.createFields = new ArrayList<String>(Arrays.asList(createFieldsText.split(","))); 
	}

	public List<String> getUpdateFields() {
		return updateFields;
	}

	public void setUpdateFields(List<String> updateFields) {
		this.updateFields = updateFields;
	}

	public void setUpdateFieldsText(String updateFieldsText) {
		this.updateFields = new ArrayList<String>(Arrays.asList(updateFieldsText.split(","))); 
	}

	public Map<String, IApiObjectCommand> getUpdateCommands() {
		return updateCommands;
	}

	public void setUpdateCommands(Map<String, IApiObjectCommand> updateCommands) {
		this.updateCommands = updateCommands;
	}

	public Map<String, IApiObjectCommand> getGlobalCommands() {
		return globalCommands;
	}

	public void setGlobalCommands(Map<String, IApiObjectCommand> globalCommands) {
		this.globalCommands = globalCommands;
	}

	public String getCreateMinRole() {
		return createMinRole;
	}

	public void setCreateMinRole(String createMinRole) {
		this.createMinRole = createMinRole;
		this.createMinRoleIndex = getRoleIndex(createMinRole);
	}
}
