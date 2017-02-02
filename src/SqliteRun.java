import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.sqlite.JDBC;//if use sqlite,it must exist.

public class SqliteRun {

	private static DataSource ds;
	
	public static void main(String[] args) throws ClassNotFoundException {
		
		long point=System.nanoTime();			
		File tmpFile =new  File("sqlite.db");		
		DataSource dataSource = setupDataSource("jdbc:sqlite::memory:");//jdbc:sqlite::memory: jdbc:sqlite:sqlite.db
		

		
		boolean first=true;
		
		 Connection conn = null;
	     Statement stmt = null;
	     ResultSet rset = null;
	     for(int a=0;a<10;a++)
	     try {	  
	    	 	point=System.nanoTime();
	            conn = dataSource.getConnection();
	            
	            System.out.println("T:"+(System.nanoTime()-point));
	    		point=System.nanoTime();
	            
	            stmt = conn.createStatement();
	           
	            System.out.println(System.nanoTime()-point);
	    		point=System.nanoTime();
	            
	    		if(first){
	    			stmt.execute("restore from " + tmpFile.getAbsolutePath());
	            
	    			System.out.println(System.nanoTime()-point);
	    			point=System.nanoTime();
	    			first=false;
	    		}
	            
	            rset = stmt.executeQuery("select * from alert_value");
	            
	            System.out.println(System.nanoTime()-point);
	    		point=System.nanoTime();
	            
	            System.out.println("Results:");
	            int numcols = rset.getMetaData().getColumnCount();
	            while(rset.next()) {
	                for(int i=1;i<=numcols;i++) {
	                	if(i==1){
	                		System.out.print(" " + rset.getString(i));
	                	}else{
	                		System.out.print(" " + rset.getFloat(i));
	                	}
	                   
	                }
	                System.out.println("");
	            }
	            
	            System.out.println(System.nanoTime()-point);
	    		point=System.nanoTime();
	            
	        } catch(SQLException e) {
	            e.printStackTrace();
	        } finally {
	            try { if (rset != null) rset.close(); } catch(Exception e) { }
	            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
	            try { if (conn != null) conn.close(); } catch(Exception e) { }
	        }
		
		/*Class.forName("org.sqlite.JDBC"); //simple case
		Connection connection = null;
		 try
	        {
			 
	          // create a database connection
	          connection = DriverManager.getConnection("jdbc:sqlite:sqlite.db");
	          Statement statement = connection.createStatement();
	          statement.setQueryTimeout(30);  // set timeout to 30 sec.
	          
	          statement.executeUpdate("drop table if exists person");
	          statement.executeUpdate("create table person (id integer, name string)");
	          statement.executeUpdate("insert into person values(1, 'leo')");
	          statement.executeUpdate("insert into person values(2, 'yui')");
	          ResultSet rs = statement.executeQuery("select * from person");
	          while(rs.next())
	          {
	            // read the result set
	            System.out.println("name = " + rs.getString("name"));
	            System.out.println("id = " + rs.getInt("id"));
	          }
	        }
	        catch(SQLException e)
	        {
	          // if the error message is "out of memory", 
	          // it probably means no database file is found
	          System.err.println(e.getMessage());
	        }
	        finally
	        {
	          try
	          {
	            if(connection != null)
	              connection.close();
	          }
	          catch(SQLException e)
	          {
	            // connection close failed.
	            System.err.println(e);
	          }
	        }*/
	      }

	  public static DataSource setupDataSource(String connectURI) {
	       if(ds!=null)
	    	   return ds;
	       ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, null);
	       PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
	       GenericObjectPoolConfig config=new GenericObjectPoolConfig();	     
	       config.setMaxIdle(1);
	       config.setMaxTotal(1);	      
	       ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory,config);	     	       
	       poolableConnectionFactory.setPool(connectionPool);	       
	       ds = new PoolingDataSource<>(connectionPool);        
	        return ds;
	    }
}
