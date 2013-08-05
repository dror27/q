package com.nightox.q.api.cmd;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import com.nightox.q.api.ApiConst;
import com.nightox.q.api.ApiContext;
import com.nightox.q.api.ApiException;
import com.nightox.q.api.cmd.base.CmdBase;
import com.nightox.q.db.Database;
import com.nightox.q.model.User;

public class CmdLogin extends CmdBase {

	public void doCommand(ApiContext context) throws ApiException
	{
		Criteria			crit;
		SimpleExpression	fieldExpr = null;
		
		if ( context.getApiRequest().hasField("username") )
		{
			crit = Database.getSession().createCriteria(User.class)
										.add(fieldExpr = Restrictions.eq("username", context.getApiRequest().getField("username")))
										.add(Restrictions.eq("password", context.getApiRequest().getField("password")));
		}
		else
			throw new ApiException(ApiConst.API_ERR_MISSING_PARAM, "rememberToken|username|email");
		
		User				user = (User)crit.uniqueResult();
		
		context.getApiSession().setUser(user);
		
		if ( user == null )
			throw new ApiException(ApiConst.API_ERR_NO_SUCH, fieldExpr.toString());
	}

}
