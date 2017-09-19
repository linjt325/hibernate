package com.lin.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.hibernate.Hibernate;
import org.hibernate.jdbc.Work;
import org.junit.Test;

import com.lin.base.BaseConnection;
import com.lin.pojo.News;

public class HibernateSessionMethodTest extends BaseConnection {

//	Logger l=Logger.getLogger("timePrint");
	/**
	 * save()
	 * 1.是一个临时对象变为持久化对象
	 * 2.为对象分配ID
	 * 3.在flush 缓存时会发送一条insert 语句
	 * 4.在save方法之前的id 是无效的
	 * 5.持久化对象的ID是不能被修改的
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	public void testSave() throws IOException, SQLException{
	 	News news=new News();
		news.setAuthor("XxX");
		news.setDate(new Date());
		news.setTitle("去你妈");
		news.setContent("12123");
		InputStream st=new FileInputStream("D:\\Linjt\\11.广州\\shuichang2.png");
		Blob image=Hibernate.getLobCreator(session).createBlob(st,st.available());
		news.setImage(image);
//		news.setId(100);//设置id无效
		System.out.println(news);//没有id
		session.save(news);
//		news.setId(111);
//		session.flush();//报错
		System.out.println(news); 
		System.out.println(session.get(News.class, 1)); 
	}
	
	
	/**
	 * persist 也会执行insert操作
	 * 在调用persist方法之前,若对象已经有id了,则不会执行insert,抛出异常
	 */
	@Test
	public void testPersist(){
		News news=new News();
		news.setAuthor("XxX");
		news.setDate(new Date());
//		news.setId(111);
		news.setTitle("不玩了");
		session.persist(news);
	}
	
	/**
	 * 
	* load和get 区别:
		* 加载:
			* 执行get:会立即加载对象
			* 执行load 若不使用该对象,则不会立即执行查询操作.而返回一个代理对象;(支持懒加载)

		* 若数据表中没有对应的记录.且session没有被关闭. 同时需要使用对象时
			* get: 返回null
			* load :若不是用该对象的任何属性,不报错;若需要初始化时,抛出异常
		* load 方法可能会抛出懒加载异常,LazyInitializationException :在需要初始化代理对象之前已经关闭了session
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Test
	public void testGet() throws SQLException, IOException{
		News news=(News) session.get(News.class, 1);
		Blob b=news.getImage();
		InputStream in=b.getBinaryStream();
		System.out.println(in.available());
		System.out.println(news);
	}
	
	@Test
	public void testLoad(){
		News news=(News) session.load(News.class,1);
		System.out.println(news);
	}
	
	
	/**
	 * 1.若更新一个持久化对象,不需要显式地调用update()方法.因为 commit时会调用flush  将修改持久化到数据库  
	 * 2.更新一个游离对象时 需要显式调用update 方法.  可以吧一个游离对象变为持久化对象
	 * 需要注意:
	 * 	1.无论要更新的游离对象和数据表的记录是否一致,都需要执行update 语句
	 * 	 			如果要update 方法 在执行时 先判断 游离对象与持久化对象是否有修改 ,只有当发生修改,才执行update 语句 
	 * 				将hbm.xml文件的class节点设置 select-before-update =true(默认为false) 当通常不需要设置该属性
	 * 	2.若数据表没有对应的记录,但还调用了update方法 会抛出异常
	 * 3.当update方法关联一个游离对象时, 
	 * 	若果在session的缓存中已经存在相同的OID 的持久化对象,会抛出异常.因为session缓存中不能有两个OID相同的对象
	 */
	@Test
	public void testUpdate(){
		News news=(News) session.get(News.class,1);
//		news.setAuthor("lin");//commit时会调用flush  将修改持久化到数据库   ,不需要显式调用update方法
		
		//使news 变为游离对象
		transaction.commit();
		session.close();
		session=sessionFactory.openSession();
		transaction=session.beginTransaction();
		
		news.setAuthor("lin");
		
		News news2=(News) session.get(News.class,1);//注意3 
		session.update(news);
		
	}
	
	
	/**
	 * 1.若OID 不为null; 当数据表没有和其对应的记录,会抛出异常
	 * 2.了解: 如果OID值等于id的unsaved-value 属性值的对象,也被认为是一个游离对象,执行save 
	 */
	@Test
	public void testSaveOrUpdate(){
		News news1 =(News) session.get(News.class	,1);
		news1.setDate(new Date());
		session.saveOrUpdate(news1);//update
		
		News news2 =new News("垃圾游戏", "Xxx", new Date());
		session.saveOrUpdate(news2);//save
	}
	
	/**
	 * delete: 执行删除操作,只要OID 和数据表中的一条记录对应,就会准备执行delete 操作
	 * 若OID 在数据表中没有对应的记录,这抛出异常
	 *  可以通过设置hibernate 配置文件hibernate.use_identifier_rollback属性设为true; 是删除对象后,把其OID置为null;
	 */
	@Test
	public void testDelete(){
//		News news =new News(); //游离对象
//		news.setId(32778);
		
		News news1=(News) session.get(News.class, 32788);//持久化对象
//		session.delete(news);
		session.delete(news1);//执行delete方法后 news1的OID 仍然保留 , 
		
		System.out.println(news1);
		session.save(news1);
		System.out.println(news1);
	}
	
	/**
	 * 把持久化对象从session缓存中移除, 这样就没有办法执行update 语句
	 */
	@Test
	public void testEvict(){
		News news1=(News) session.get(News.class, 32789);//持久化对象
		News news2=(News) session.get(News.class, 32790);//持久化对象
		news1.setAuthor("111");
		news2.setAuthor("2222");
		session.evict(news1);//只执行了两个select 和一个update 语句
	}
	
	
	@Test
	public void testDoWork(){
		session.doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {

				System.out.println(connection);
			}
		});
	}
	
	@Test
	public void testDynamicUpdate(){
		News news=(News) session.get(News.class, 1);
		news.setAuthor("xx");
	}
}
