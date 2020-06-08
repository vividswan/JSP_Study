package com.bookService.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.bookService.bean.BookBean;

public class BookDAO {
	
	private static BookDAO dao = new BookDAO();
	private BookDAO() {}
	public static BookDAO getInstance() {
		return dao;
	}
	public Connection connect() {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/webdb?serverTimezone=UTC","root","1234567890");
		} catch (Exception e) {
			System.out.println("오류 발생 : "+e);
		}
		return conn;
	}
	
	public void close(Connection conn, PreparedStatement ps, ResultSet rs) {
		if(rs!=null) {
			try {
				rs.close();
			}catch (Exception e) {
				System.out.println("오류 발생 : "+e);
			}
		}
		close(conn,ps);
	}
	
	public void close(Connection conn, PreparedStatement ps) {
		if(ps!=null) {
			try {
				ps.close();
			} catch (Exception e) {
				System.out.println("오류 발생 : "+e);
			}
		}
		if(conn !=null) {
			try {
				conn.close();
			} catch (Exception e) {
				System.out.println("오류 발생 : "+e);
			}
		}
	}
	
	public boolean duplicatationCheck(BookBean book) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean DuplicateFlag = false;
		try {
			conn=connect();
			pstmt = conn.prepareStatement("select * from book where id=?;");
			pstmt.setInt(1, book.getID());
			rs =  pstmt.executeQuery();
			if(rs.next()) {
				DuplicateFlag = true;
			}
		}catch (Exception e) {
			System.out.println("오류 발생 : "+e);
		}finally {
			close(conn,pstmt,rs);
		}
		return DuplicateFlag;
	}
	
	public void bookInsert(BookBean book) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn=connect();
			pstmt = conn.prepareStatement("insert into book values(?,?,?,?,?,?)");
			pstmt.setInt(1, book.getID());
			pstmt.setString(2,book.getName());
			pstmt.setString(3, book.getAuthor());
			pstmt.setInt(4, book.getPrice());
			pstmt.setBoolean(5, false);
			pstmt.setInt(6, 0);
			pstmt.executeUpdate();
		}catch (Exception e) {
			System.out.println("오류 발생 : "+e);
		}finally {
			close(conn,pstmt);
		}
	}
	
	public boolean bookUpdate(int ID) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean isBorrowed = false;
		int borrowCnt = 0;
		try {
			conn=connect();
			pstmt = conn.prepareStatement("select * from book where id=?;");
			pstmt.setInt(1, ID);
			ResultSet rs =  pstmt.executeQuery();
			if(rs.next()) {
				isBorrowed = rs.getBoolean(5);
				borrowCnt = rs.getInt(6);
			}
		}catch (Exception e) {
			System.out.println("오류 발생 : "+e);
		}finally {
			close(conn,pstmt);
		}
		if(!isBorrowed) {
			try {
				conn=connect();
				pstmt = conn.prepareStatement("update book set isBorrowed=? , borrowCnt=? where id=?;");
				pstmt.setBoolean(1, true);
				pstmt.setInt(2, borrowCnt+1);
				pstmt.setInt(3, ID);
				pstmt.executeUpdate();				
			}catch (Exception e) {
				System.out.println("오류 발생 : "+e);
			}finally {
				close(conn,pstmt);
			}
		}
		return isBorrowed;
	}
	
	public ArrayList<BookBean> bookList() {
		ArrayList<BookBean> list = new ArrayList<BookBean>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		BookBean book = null;
		try {
			conn = connect();
			pstmt = conn.prepareStatement("select * from book");
			rs = pstmt.executeQuery();
			while(rs.next()) {
				book = new BookBean();
				book.setID(rs.getInt(1));
				book.setName(rs.getString(2));
				book.setAuthor(rs.getString(3));
				book.setPrice(rs.getInt(4));
				book.setIsBorrowed(rs.getBoolean(5));
				book.setBorrowCnt(rs.getInt(6));
				list.add(book);
			}
		}catch (Exception e) {
			System.out.println("오류 발생 : "+e);
		}finally {
			close(conn,pstmt,rs);
		}
		return list;
	}
	
	public void bookReturn(int ID){
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean isBorrowed = false;
		int borrowCnt = 0;
		try {
			conn=connect();
			pstmt = conn.prepareStatement("select * from book where id=?;");
			pstmt.setInt(1, ID);
			ResultSet rs =  pstmt.executeQuery();
			if(rs.next()) {
				isBorrowed = rs.getBoolean(5);
				borrowCnt = rs.getInt(6);
			}
		}catch (Exception e) {
			System.out.println("오류 발생 : "+e);
		}finally {
			close(conn,pstmt);
		}
		if(!isBorrowed) {
			try {
				conn=connect();
				pstmt = conn.prepareStatement("update book set isBorrowed=? where id=?;");
				pstmt.setBoolean(1, false);
				pstmt.setInt(2, ID);
				pstmt.executeUpdate();				
			}catch (Exception e) {
				System.out.println("오류 발생 : "+e);
			}finally {
				close(conn,pstmt);
			}
		}
	}
	
}
