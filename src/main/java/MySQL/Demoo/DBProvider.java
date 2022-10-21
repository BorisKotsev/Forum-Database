package MySQL.Demoo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class DBProvider 
{
	private Connection connection;
	
	private static DBProvider instance = null;
	
	public static DBProvider getInstance()
	{
		if(instance == null)
		{
			instance = new DBProvider();
		}
		
		return instance;
	}
	
	private DBProvider()
	{
		try 
		{
			connection = DriverManager.getConnection("jdbc:mysql://localhost/forumdb", "root", "Bobiko2319!");
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public String getCryptoByUsername(String username)
	{
		try
		{
			PreparedStatement statement = connection.prepareStatement("select crypto from users where username = ?;");
		
			statement.setString(1, username);
			
			ResultSet result = statement.executeQuery();
			
			result.next();
			
			return result.getString(1);		
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getPasswordByUsername(String username)
	{
		try
		{
			PreparedStatement statement = connection.prepareStatement("select password from users where username = ?;");
		
			statement.setString(1, username);
			
			ResultSet result = statement.executeQuery();
			
			result.next();
			
			return result.getString(1);	
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void executeInsertQuery(String query, String ...args)
	{
		try 
		{
			PreparedStatement statement = connection.prepareStatement(query);
			
			int index = 1;
			
			for(String arg : args)
			{
				statement.setString(index ++, arg);
			}
			
			statement.executeUpdate();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public int getThemeIDByTitle(String title)
	{
		try 
		{
			PreparedStatement statement = connection.prepareStatement("select id from themes where title = ?");
			
			statement.setString(1, title.trim());
			
			ResultSet result = statement.executeQuery();
			
			if(result.next())
			{
				return result.getInt("id");
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}

		return -1;
	}
	
	public int getPostIDByTitle(String title)
	{
		try 
		{
			PreparedStatement statement = connection.prepareStatement("select id from posts where title = ?");
			
			statement.setString(1, title);
			
			ResultSet result = statement.executeQuery();
			
			if(result.next())
			{
				return result.getInt("id");
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}

		return -1;
	}
	
	public int getUserIDByUsername(String username)
	{
		try
		{
			PreparedStatement statement = connection.prepareStatement("select id from users where username = ?;");
		
			statement.setString(1, username);
			
			ResultSet result = statement.executeQuery();
			
			result.next();
			
			return result.getInt(1);	
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public List<String> getThemes()
	{
		try
		{
			List<String> m_allThemes = new LinkedList<>();
			
			PreparedStatement statement = connection.prepareStatement("select title from themes;");	
			
			ResultSet result = statement.executeQuery();
			
			while(result.next())
			{
				m_allThemes.add(result.getString(1));
			}
			
			return m_allThemes;	
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<String> getPostByThemeID(int themeID)
	{
		try
		{
			List<String> m_allPosts = new LinkedList<>();
			
			PreparedStatement statement = connection.prepareStatement("select title from posts where themeid = ?;");
			
			statement.setInt(1, themeID);
			
			ResultSet result = statement.executeQuery();
			
			while(result.next())
			{
				m_allPosts.add(result.getString(1));
			}
			
			return m_allPosts;	
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<String> getPostByThemeIDAndUserID(int themeID, int userID)
	{
		try
		{
			List<String> m_allPosts = new LinkedList<>();
			
			PreparedStatement statement = connection.prepareStatement("select title from posts where themeid = ? and userID = ?;");
			
			statement.setInt(1, themeID);
			statement.setInt(2, userID);
			
			ResultSet result = statement.executeQuery();
			
			while(result.next())
			{
				m_allPosts.add(result.getString(1));
			}
			
			return m_allPosts;	
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean isUserAdminByUserID(int userID)
	{
		try
		{
			PreparedStatement statement = connection.prepareStatement("select userrole from users where id = ?;");
		
			statement.setInt(1, userID);
			
			ResultSet result = statement.executeQuery();
			
			result.next();
			
			if(result.getInt(1) == 1)
			{
				return true;
			}
			else
			{
				return false;
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return false;
	}
}
