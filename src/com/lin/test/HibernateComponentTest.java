package com.lin.test;

import org.junit.Test;

import com.lin.base.BaseConnection;
import com.lin.pojo.Pay;
import com.lin.pojo.Worker;

public class HibernateComponentTest extends BaseConnection{
	
	@Test
	public void test(){
		Worker work=new Worker();
		Pay x=new Pay();
		x.setSalary(10000);
		work.setPay(x);
		work.setName("xx");
		
		session.save(work);
	}

}
