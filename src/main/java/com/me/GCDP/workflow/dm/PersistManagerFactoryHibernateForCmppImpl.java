package com.me.GCDP.workflow.dm;

import org.hibernate.SessionFactory;
import org.w3c.dom.Element;

import com.me.GCDP.util.SpringContextUtil;
import com.ifeng.common.conf.ConfigRoot;
import com.ifeng.common.conf.Configurable;
import com.ifeng.common.dm.persist.ThreadLocalPersistManager;
import com.ifeng.common.dm.persist.hibernate.HibernateConfig;
import com.ifeng.common.dm.persist.hibernate.PersistManagerHibernate;
import com.ifeng.common.dm.persist.intf.PersistManager;
import com.ifeng.common.dm.persist.intf.PersistManagerFactory;

/**
 * <p>Title: 该类用于创建PersistManager实例，并向HibernateConfig中注入Spring实例化的SessionFactory</p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2012-8-8              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class PersistManagerFactoryHibernateForCmppImpl implements
		PersistManagerFactory, Configurable {

	private static final PersistManagerHibernate _innerInstance 
	    = new PersistManagerHibernate();
	private static final PersistManager _instance = new ThreadLocalPersistManager(
	        _innerInstance);
	
	private SessionFactory sessionFactory = null;
	
	/**
	 * 创建PersistManager实例
	 * @return PersistManager
	 */
	public PersistManager getInstance() {
	    return _instance;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory){
		this.sessionFactory = sessionFactory;
		HibernateConfig.setSessionFactory(sessionFactory);
	}
	
	public Object config(ConfigRoot configRoot, Object parent, Element configEle) {
	    //_innerInstance.config(configRoot, parent, configEle);
		if(sessionFactory == null){
			sessionFactory = (SessionFactory)SpringContextUtil.getBean("sessionFactory");
		}
		HibernateConfig.setSessionFactory(sessionFactory);
	    return this;
	}

}
