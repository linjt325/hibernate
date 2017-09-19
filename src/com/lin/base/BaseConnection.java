package com.lin.base;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;

public class BaseConnection {

	protected Logger log=Logger.getLogger(this.getClass());
	protected SessionFactory sessionFactory;
	
	protected Session session;
	
	protected Transaction transaction;
	
	@Before
	public void init (){
		//configuration  规则注册, 生成sessionFactory -->生成seesion --->开启事务
		Configuration configuration=new Configuration().configure();
		ServiceRegistry registry=new ServiceRegistryBuilder()
									.applySettings(configuration.getProperties())
									.buildServiceRegistry();
		
		sessionFactory=configuration.buildSessionFactory(registry);
		
		//使用getCurrentSession  session在事务提交后自动关闭
		session=sessionFactory.getCurrentSession();
//		session=sessionFactory.openSession();
		transaction=session.beginTransaction();
		
	}
	
	@After
	public void destory(){
		
		transaction.commit();
//		session.close();
		sessionFactory.close();
	}
}
