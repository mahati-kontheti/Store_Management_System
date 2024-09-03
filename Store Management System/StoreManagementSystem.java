import java.sql.*;
import java.util.*;
import java.io.*;
class ShopException extends Exception{
	public String getMessage(){
		return "not possible";
	}
}
class StoreManagementSystem{
	public static void main(String args[])throws Exception{
		try{
			Scanner sc=new Scanner(System.in);
			ArrayList<Integer> c=new ArrayList<Integer>();
			int x,ts=0,x1=1;
			while(x1==1){
				System.out.println("Enter the name of the customer");
				x=0;
				ts=0;
				String n=sc.next();
				Customer c1=new Customer(n);
				int b=0;
				c1.dis_menu();
				while(b==0){
					System.out.println("Enter the option of choice");
					int ch=sc.nextInt();
					switch(ch){
							case 1:
							service s=new service("x",1);
							s.displays();
							break;
						case 2:
							c1.add_i();
							break;
						case 3:
							c1.dis_c();
							break;
						case 4:
							c1.del_i();
							break;
						case 5:
							x=c1.bill_g();
							b=1;
							break;
						case 6:
							c1.add_ex();
							break;
					}
				}
				c.add(x);//adding into Arraylist c
				System.out.println("Is there another customer?(yes:1/no:0)");
				 x1=sc.nextInt();
			}
			for(Integer i:c){
				ts=ts+i;
			}
			System.out.println("Total sales today is:"+ts);
		}
		catch(ShopException e){
			System.out.println(e.getMessage());
		}
		catch(Exception ex){
			System.out.println("not possible"+ex.getMessage());
		}
	}
}
class Customer{
	String name,n;
	service s;
	int q;
	Customer(String name){
		this.name=name;
	}
	void dis_s()throws Exception{
		try{
		s.displays();
		this.s=s;
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	void add_i()throws Exception{
		try{
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter the Product to be added: ");
		String n=sc.next();
		System.out.println("Enter the quantity you want:");
		int q=sc.nextInt();
		s=new service(n,q);
		s.insert(n,q);
		this.s=s;
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	void dis_c()throws Exception{
		try{
		s.displayc();
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	void del_i()throws Exception{
		try{
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter the item wanted to be removed:");
		String n=sc.next();
		System.out.println("In want quantity to be deleted");
		int q=sc.nextInt();
		s.remove(n,q);
		this.s=s;
		}
		catch(Exception ex){
			 System.out.println(ex.getMessage());
		}
	}
	void add_ex()throws Exception{
		try{
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter the product wanted to update");
		String n=sc.next();
		System.out.println("Enter the new quantity :");
		int ex=sc.nextInt();
		s.add2(n,ex);
		this.s=s;
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	int bill_g()throws Exception{
		int t=0;
		try{
		t=s.bill();
		this.s=s;
		}
		catch(Exception ex){
			System.out.println("no possible");
		}
		return t;	
	}
	void dis_menu(){
		System.out.println("1.Display of items in store ");
		System.out.println("2.Add to cart");
		System.out.println("3.Display the cart items");
		System.out.println("4.Remove the item from cart");
		System.out.println("5.Generate bill ");
		System.out.println("6.Add quantity to the Existing product in the cart");
	}
}
class service{
	String name;
	int quantity;
	private String password="26mahiK!";
	service(String name,int quantity){
		this.name=name;
		this.quantity=quantity;
	}
	void displays()throws SQLException{
	try{
		Connection c=DriverManager.getConnection("jdbc:mysql://localhost:3306/java_project","root",password);
		Statement st=c.createStatement();
		ResultSet rs=st.executeQuery("select name,quantity,price from shop");
		while(rs.next())
			System.out.println(rs.getString(1)+"\t"+rs.getInt(2)+"\t"+rs.getInt(3));
		rs.close(); 
		st.close();
		c.close();
	}
	catch(Exception e){
		System.out.println(e.getMessage());
	}
	}
	void add2(String n,int q)throws SQLException{
	try{
		Connection c=DriverManager.getConnection("jdbc:mysql://localhost:3306/java_project","root",password);
		Statement st=c.createStatement();
		ResultSet r=st.executeQuery("select quantity from shop where name='"+n+"'");
		r.next();
		int w=r.getInt(1);
		r.close();
		if(w>q){
		ResultSet rs1=st.executeQuery("select quantity from customer where name='"+n+"'");
		rs1.next();
		int sum1=rs1.getInt(1);
		rs1.close();
		PreparedStatement ps=c.prepareStatement("update customer set quantity="+q+",amount=(price*quantity) where name='"+n+"'");
		int d=ps.executeUpdate();
		ps.close();
		ResultSet rs=st.executeQuery("select quantity from customer where name='"+n+"'");
		rs.next();
		int o=rs.getInt(1);
		int sum=o-sum1;
		PreparedStatement ps2=c.prepareStatement("update shop set quantity=quantity-"+sum+" where name='"+n+"'");
		int g=ps2.executeUpdate();
		rs.close();
		st.close();
		ps2.close();
		c.close();
		}
		else{
			throw new ShopException();
		}
		
	}
	catch(Exception ex){
		System.out.println(ex.getMessage());
	}	
	}
	void insert(String n,int quantity)throws SQLException{
	try{
		this.quantity=quantity;
		Connection c=DriverManager.getConnection("jdbc:mysql://localhost:3306/java_project","root",password);
		Statement st=c.createStatement();
		ResultSet rs=st.executeQuery("select * from shop where name = '"+n+"'");
		rs.next();
		if(quantity<=rs.getInt("quantity"))
		{
			PreparedStatement ps=c.prepareStatement("insert into customer values(?,?,?,?,?)");
			ps.setInt(1,rs.getInt("id"));
			ps.setString(2,n);
			ps.setInt(3,quantity);
			ps.setInt(4,rs.getInt("price"));
			ps.setInt(5,(rs.getInt(4)*quantity));
			int x=ps.executeUpdate();
			ps.close();
			PreparedStatement ps1=c.prepareStatement("update shop set quantity=quantity-"+quantity+" where name ='"+n+"'");
			int w=ps1.executeUpdate();
			ps1.close();	
		}
		else{
			//System.out.println("insufficient");
			throw new ShopException();
		}
		rs.close();
		st.close();
		c.close();
	}
	catch(ShopException ex){
	System.out.println(ex.getMessage());
	}
	}
	void displayc()throws Exception{
	try{
		Connection c=DriverManager.getConnection("jdbc:mysql://localhost:3306/java_project","root",password);
		Statement st=c.createStatement();
		ResultSet rs=st.executeQuery("select * from customer");
		System.out.println("ID"+"\t"+"Name"+"\t"+"Quantity"+" "+"Price"+"\t"+"Amount"+"\t");
		while(rs.next())
			System.out.println(rs.getInt(1)+"\t"+rs.getString(2)+"\t"+rs.getInt(3)+"\t "+rs.getInt(4)+"\t"+rs.getInt(5)+"\t");
		rs.close();
		st.close();
		c.close();
	}
	catch(Exception ex){
	System.out.println("not possible");
	}
	}
	void remove(String n,int quantity)throws Exception{
	try{
		int r,e;
		Connection c=DriverManager.getConnection("jdbc:mysql://localhost:3306/java_project","root",password);
		Statement st=c.createStatement();
		ResultSet rs=st.executeQuery("select quantity from customer where name='"+n+"'");
		rs.next();
		if(rs.getInt("quantity")==quantity){
			PreparedStatement ps=c.prepareStatement("delete from customer where name='"+n+"'");
			int a=ps.executeUpdate();
			ps.close();
			PreparedStatement ps1=c.prepareStatement("update shop set quantity=quantity+"+quantity+" where name= '"+n+"'");
			int g=ps1.executeUpdate();
			ps1.close();
	
		}
		else if(rs.getInt("quantity")>quantity){
			PreparedStatement ps=c.prepareStatement("update customer set quantity=quantity-"+quantity+",amount=(quantity*price) where name= '"+n+"'");
			int s=ps.executeUpdate();
			ps.close();
			PreparedStatement ps1=c.prepareStatement("update shop set quantity=quantity+"+quantity+" where name='"+n+"'");
			int g=ps1.executeUpdate();
			ps1.close();

		}
		else{
			throw new ShopException();
		}
		rs.close();
		st.close();
		c.close();
	}
	catch(ShopException ex){
	System.out.println(ex.getMessage());
	}
	}
	int bill()throws Exception{
		int total=0;
		try{
		Connection c=DriverManager.getConnection("jdbc:mysql://localhost:3306/java_project","root",password);
		Statement st=c.createStatement();
		ResultSet rs=st.executeQuery("select sum(amount) from customer");
		displayc();
		rs.next();
		total=rs.getInt(1);
		System.out.println("The total bill is :"+total);
		rs.close();
		st.close();
		PreparedStatement ps=c.prepareStatement("delete from customer");
		int d=ps.executeUpdate();
		ps.close();
		c.close();
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		return total;	
	
}
}
