package com.guest.dao;

import com.guest.jdbc.JdbcUtil;
import com.guest.model.Message;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;




public class MessageDao {
	private static MessageDao messageDao = new MessageDao();
	
	public static MessageDao getInstance() {
		return messageDao;
	}
	
	private MessageDao() {}
	
	public int insert(Connection conn, Message message) throws SQLException {
		// 1. 클래스 로딩 : listener 에서 이미 로딩됨
		// 2. 연결 생성 : 파라미터로 받음
		// 3. statement 메소드내
		// 4. 쿼리 실행 : 
		// 5. 결과 처리 : 호출 한 곳
		// 6. 자원 닫기 : 
		
		PreparedStatement pstmt = null;
		try {
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:guestbook");
			pstmt = conn.prepareStatement(
					"insert into guestbook_message" +
					"(guest_name, password, message) values (?, ?, ?)");
			pstmt.setString(1, message.getGuestName());
			pstmt.setString(2, message.getPassword());
			pstmt.setString(3, message.getMessage());
					
			return pstmt.executeUpdate();
		} finally {			
			JdbcUtil.close(pstmt);
		}
	}

	public Message select(Connection conn, int messageId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(
					"select*from guestbook_message where message_id = ?");
			pstmt.setInt(1, messageId);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return makeMessageFromResultSet(rs);
			} else {
				return null;
			}
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
			
		}
	}
	private Message makeMessageFromResultSet(ResultSet rs) throws SQLException {
	// TODO Auto-generated method stub
		Message message = new Message();
		message.setId(rs.getInt("message_id"));
		message.setGuestName(rs.getString("guest_name"));
		message.setPassword(rs.getString("password"));
		message.setMessage(rs.getString("message"));
		return message;
	}
	
	public int selectCount(Connection conn) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt=conn.createStatement();
			rs=stmt.executeQuery("select count(*) from "
					+ "guestbook_message");
			rs.next();
			return rs.getInt(1);
		} finally {
			JdbcUtil.close();
		}
	}
	
	public List<Message> selectList(Connection conn, int firstRow, int endRow)
	 throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement("select * from "
					+ " guestbook_message" 
					+ " order by message_id "
					+ " desc limit ?, ?");
			
			pstmt.setInt(1, firstRow -1);
			pstmt.setInt(2, endRow = firstRow + 1);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				List<Message> messageList = new ArrayList<Message>();
				
			do {
				messageList.add(makeMessageFromResultSet(rs));
				
			} while (rs.next());
			
			return messageList;
			
			} else {
				
				return Collections.emptyList();
			}
			
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	public int delete(Connection conn, int messageId) throws SQLException {
		PreparedStatement pstmt = null;
		
		try {
			pstmt=conn.prepareStatement(
				"delete from "
				+ "guestbook_message where message_id=?");			
			pstmt.setInt(1, messageId);
			
			return pstmt.executeUpdate();		
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}






