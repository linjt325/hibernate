package com.lin.test;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lin.base.BaseConnection;
import com.lin.pojo.News;

public class HibernateCacheTest extends BaseConnection{
	
	 
	public HibernateCacheTest (){
		super();
	}
//	@Test
//	public void test(){
//		
//		News ne=new News("1", "xxx", new Date());
//		session.save(ne);
//		
//	}
	
	@Test
	public void testSessionCache(){
		
		News news =(News) session.get(News.class, 1);
		System.out.println(news);
		

		News news1 =(News) session.get(News.class, 1);
		System.out.println(news1);
	}
	
	/**
	 * 使数据表中的记录和session缓存中的对象状态保持一致,为了一致,可能发送对应的sql语句  
	 * 1.在事务管理器  提交 commit()方法中 先调用session的flush()方法,再提交事务
	 * 2.flush() 可能会发送sql语句但不会提交事务
	 * 3. 注意:未提交事务或显式调用session.flush()方法时也有可能进行flush()操作
	 * 	1).执行HQL或QBC查询,会先进行flush操作
	 * 	2).若数据的Id 由 底层数据库 使用自增的方式生成,在调用save()的方法后 会立即发送insert语句;
	 * 			因为 save 方法后,必须保证对象的id是存在的;
	 * 	3)
	 */
	@Test
	public void testSessionFlush(){
		News news =(News) session.get(News.class, 1);
		news.setDate(new Date());
//		session.flush();//显式flush
		
//		News news2=(News) session.createCriteria(News.class).add(Restrictions.eq("id", 1)).uniqueResult();
//		System.out.println(news2);//此时数据已经刷新,但是还没有提交 数据库没有发生变化
		
		News news3=new News("test1", "Xxx", new Date());
		session.save(news3);
		
	}
	
	/**
	 * refresh :会强制发送select 语句 使session缓存状态和数据库中的数据一致   
	 * 	
	 */
	@Test
	public void testSessionRefresh(){
		
		News news=(News) session.get(News.class, 1);
		
		System.out.println(news);
		//在执行refresh之前,修改数据库数据
		session.refresh(news);//将数据库数据与session缓存进行同步,刷新缓存
		
		System.out.println(news);//输出修改后的数据,  受数据库的隔离级别影响,当隔离级别为 可重复读时 ,数据无论数据如何修改, refresh后  该session的数据都是第一次读取时的结果 
	}

	@Test
	public void testClear(){
		News news =(News) session.get(News.class, 1);
		session.clear();
		News news1=(News) session.get(News.class, 1);
	}
}
