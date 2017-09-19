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
	 * 1.��һ����ʱ�����Ϊ�־û�����
	 * 2.Ϊ�������ID
	 * 3.��flush ����ʱ�ᷢ��һ��insert ���
	 * 4.��save����֮ǰ��id ����Ч��
	 * 5.�־û������ID�ǲ��ܱ��޸ĵ�
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@Test
	public void testSave() throws IOException, SQLException{
	 	News news=new News();
		news.setAuthor("XxX");
		news.setDate(new Date());
		news.setTitle("ȥ����");
		news.setContent("12123");
		InputStream st=new FileInputStream("D:\\Linjt\\11.����\\shuichang2.png");
		Blob image=Hibernate.getLobCreator(session).createBlob(st,st.available());
		news.setImage(image);
//		news.setId(100);//����id��Ч
		System.out.println(news);//û��id
		session.save(news);
//		news.setId(111);
//		session.flush();//����
		System.out.println(news); 
		System.out.println(session.get(News.class, 1)); 
	}
	
	
	/**
	 * persist Ҳ��ִ��insert����
	 * �ڵ���persist����֮ǰ,�������Ѿ���id��,�򲻻�ִ��insert,�׳��쳣
	 */
	@Test
	public void testPersist(){
		News news=new News();
		news.setAuthor("XxX");
		news.setDate(new Date());
//		news.setId(111);
		news.setTitle("������");
		session.persist(news);
	}
	
	/**
	 * 
	* load��get ����:
		* ����:
			* ִ��get:���������ض���
			* ִ��load ����ʹ�øö���,�򲻻�����ִ�в�ѯ����.������һ���������;(֧��������)

		* �����ݱ���û�ж�Ӧ�ļ�¼.��sessionû�б��ر�. ͬʱ��Ҫʹ�ö���ʱ
			* get: ����null
			* load :�������øö�����κ�����,������;����Ҫ��ʼ��ʱ,�׳��쳣
		* load �������ܻ��׳��������쳣,LazyInitializationException :����Ҫ��ʼ���������֮ǰ�Ѿ��ر���session
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
	 * 1.������һ���־û�����,����Ҫ��ʽ�ص���update()����.��Ϊ commitʱ�����flush  ���޸ĳ־û������ݿ�  
	 * 2.����һ���������ʱ ��Ҫ��ʽ����update ����.  ���԰�һ����������Ϊ�־û�����
	 * ��Ҫע��:
	 * 	1.����Ҫ���µ������������ݱ�ļ�¼�Ƿ�һ��,����Ҫִ��update ���
	 * 	 			���Ҫupdate ���� ��ִ��ʱ ���ж� ���������־û������Ƿ����޸� ,ֻ�е������޸�,��ִ��update ��� 
	 * 				��hbm.xml�ļ���class�ڵ����� select-before-update =true(Ĭ��Ϊfalse) ��ͨ������Ҫ���ø�����
	 * 	2.�����ݱ�û�ж�Ӧ�ļ�¼,����������update���� ���׳��쳣
	 * 3.��update��������һ���������ʱ, 
	 * 	������session�Ļ������Ѿ�������ͬ��OID �ĳ־û�����,���׳��쳣.��Ϊsession�����в���������OID��ͬ�Ķ���
	 */
	@Test
	public void testUpdate(){
		News news=(News) session.get(News.class,1);
//		news.setAuthor("lin");//commitʱ�����flush  ���޸ĳ־û������ݿ�   ,����Ҫ��ʽ����update����
		
		//ʹnews ��Ϊ�������
		transaction.commit();
		session.close();
		session=sessionFactory.openSession();
		transaction=session.beginTransaction();
		
		news.setAuthor("lin");
		
		News news2=(News) session.get(News.class,1);//ע��3 
		session.update(news);
		
	}
	
	
	/**
	 * 1.��OID ��Ϊnull; �����ݱ�û�к����Ӧ�ļ�¼,���׳��쳣
	 * 2.�˽�: ���OIDֵ����id��unsaved-value ����ֵ�Ķ���,Ҳ����Ϊ��һ���������,ִ��save 
	 */
	@Test
	public void testSaveOrUpdate(){
		News news1 =(News) session.get(News.class	,1);
		news1.setDate(new Date());
		session.saveOrUpdate(news1);//update
		
		News news2 =new News("������Ϸ", "Xxx", new Date());
		session.saveOrUpdate(news2);//save
	}
	
	/**
	 * delete: ִ��ɾ������,ֻҪOID �����ݱ��е�һ����¼��Ӧ,�ͻ�׼��ִ��delete ����
	 * ��OID �����ݱ���û�ж�Ӧ�ļ�¼,���׳��쳣
	 *  ����ͨ������hibernate �����ļ�hibernate.use_identifier_rollback������Ϊtrue; ��ɾ�������,����OID��Ϊnull;
	 */
	@Test
	public void testDelete(){
//		News news =new News(); //�������
//		news.setId(32778);
		
		News news1=(News) session.get(News.class, 32788);//�־û�����
//		session.delete(news);
		session.delete(news1);//ִ��delete������ news1��OID ��Ȼ���� , 
		
		System.out.println(news1);
		session.save(news1);
		System.out.println(news1);
	}
	
	/**
	 * �ѳ־û������session�������Ƴ�, ������û�а취ִ��update ���
	 */
	@Test
	public void testEvict(){
		News news1=(News) session.get(News.class, 32789);//�־û�����
		News news2=(News) session.get(News.class, 32790);//�־û�����
		news1.setAuthor("111");
		news2.setAuthor("2222");
		session.evict(news1);//ִֻ��������select ��һ��update ���
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
