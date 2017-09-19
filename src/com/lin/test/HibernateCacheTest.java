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
	 * ʹ���ݱ��еļ�¼��session�����еĶ���״̬����һ��,Ϊ��һ��,���ܷ��Ͷ�Ӧ��sql���  
	 * 1.�����������  �ύ commit()������ �ȵ���session��flush()����,���ύ����
	 * 2.flush() ���ܻᷢ��sql��䵫�����ύ����
	 * 3. ע��:δ�ύ�������ʽ����session.flush()����ʱҲ�п��ܽ���flush()����
	 * 	1).ִ��HQL��QBC��ѯ,���Ƚ���flush����
	 * 	2).�����ݵ�Id �� �ײ����ݿ� ʹ�������ķ�ʽ����,�ڵ���save()�ķ����� ����������insert���;
	 * 			��Ϊ save ������,���뱣֤�����id�Ǵ��ڵ�;
	 * 	3)
	 */
	@Test
	public void testSessionFlush(){
		News news =(News) session.get(News.class, 1);
		news.setDate(new Date());
//		session.flush();//��ʽflush
		
//		News news2=(News) session.createCriteria(News.class).add(Restrictions.eq("id", 1)).uniqueResult();
//		System.out.println(news2);//��ʱ�����Ѿ�ˢ��,���ǻ�û���ύ ���ݿ�û�з����仯
		
		News news3=new News("test1", "Xxx", new Date());
		session.save(news3);
		
	}
	
	/**
	 * refresh :��ǿ�Ʒ���select ��� ʹsession����״̬�����ݿ��е�����һ��   
	 * 	
	 */
	@Test
	public void testSessionRefresh(){
		
		News news=(News) session.get(News.class, 1);
		
		System.out.println(news);
		//��ִ��refresh֮ǰ,�޸����ݿ�����
		session.refresh(news);//�����ݿ�������session�������ͬ��,ˢ�»���
		
		System.out.println(news);//����޸ĺ������,  �����ݿ�ĸ��뼶��Ӱ��,�����뼶��Ϊ ���ظ���ʱ ,����������������޸�, refresh��  ��session�����ݶ��ǵ�һ�ζ�ȡʱ�Ľ�� 
	}

	@Test
	public void testClear(){
		News news =(News) session.get(News.class, 1);
		session.clear();
		News news1=(News) session.get(News.class, 1);
	}
}
