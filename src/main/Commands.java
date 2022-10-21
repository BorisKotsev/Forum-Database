package MySQL.Demoo;

import java.util.Random;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

public enum Commands 
{
	REGISTER("register", (args)->{
		Scanner input = new Scanner(System.in);	
		
		System.out.println("Username: ");
		
		String username = input.nextLine();
		
		System.out.println("Password");
		
		String password = input.nextLine();
		
		System.out.println("User role: ");
		
		String role = input.nextLine();
		
		DBProvider db = DBProvider.getInstance();
		
		String cryptedPassword = DigestUtils.sha256Hex(password);
		
		Random rand = new Random();
		
		String crypto = DigestUtils.sha256Hex(Integer.toString(rand.nextInt()));
		
		String result = DigestUtils.sha256Hex(cryptedPassword + crypto);
		
		db.executeInsertQuery("insert into users(username, password, userrole, crypto)"
				+ "values(?,?,?,?)", username, result, "administrator".equals(role) ? "1" : "2", crypto);
		
		CurrentState.user = username;
		CurrentState.userID = db.getUserIDByUsername(username);
	} ),
	
	LOGIN("login", (args)->{
		System.out.println("Username: ");
		
		Scanner input = new Scanner(System.in);
		
		String username = input.nextLine();
		
		System.out.println("Password: ");
		
		String password = input.nextLine();
		
		DBProvider db = DBProvider.getInstance();
		
		String crypto = db.getCryptoByUsername(username);
		
		password = DigestUtils.sha256Hex(password);
		password = DigestUtils.sha256Hex(password + crypto);
		
		String dbPassword = db.getPasswordByUsername(username);
		
		if(password.equals(dbPassword))
		{
			CurrentState.user = username;
			CurrentState.userID = db.getUserIDByUsername(username);
		}
		else
		{
			System.out.println("Wrong username or password");
		}
	}),
	
	CREATE_THEME("create theme", (args)->{
		
		if(CurrentState.user == null || CurrentState.user.isEmpty())
		{
			System.out.println("No user is logged");
			
			return;
		}
		
		Scanner input = new Scanner(System.in);
		
		System.out.println("Enter theme title: ");
		
		String title = input.nextLine();
		
		DBProvider db = DBProvider.getInstance();
		
		db.executeInsertQuery("insert into themes(title) values(?);", title);
	}),
	
	SELECT_THEME("select theme", (args)->{
		
		if(CurrentState.user == null || CurrentState.user.isEmpty())
		{
			System.out.println("No user is logged");
			
			return;
		}
		
		Scanner input = new Scanner(System.in);
		
		System.out.println("Enter theme title to select: ");
		
		String title = input.nextLine();
		
		DBProvider db = DBProvider.getInstance();
		
		int themeID = db.getThemeIDByTitle(title);
		
		if(themeID == -1)
		{
			System.out.println("There is no theme with this title");
			
			return;
		}
		
		CurrentState.selectedThemeID = themeID;
	}),
	
	CREATE_POST("create post", (args)->{
		
		if(CurrentState.user == null || CurrentState.user.isEmpty())
		{
			System.out.println("No user is logged");
			
			return;
		}
		
		if(CurrentState.selectedThemeID == -1)
		{
			System.out.println("No theme is selected");
			
			return;
		}
		
		Scanner input = new Scanner(System.in);
		
		System.out.println("Enter post title: ");
		
		String postTitle = input.nextLine();
		
		System.out.println("Enter post content: ");
		
		String content = input.nextLine();
		
		LocalDate date = LocalDate.now();
				
		DBProvider db = DBProvider.getInstance();
		
		db.executeInsertQuery("insert into posts(title, content, userId, themeId, postedDate) values"
				+ "(?,?,?,?,?);",
				postTitle, content, "" + CurrentState.userID, "" + CurrentState.selectedThemeID, "" + date);
	}),
	
	SHOW_THEMES("show themes", (args)->{		
		System.out.println("All themes title: ");
				
		DBProvider db = DBProvider.getInstance();
		
		List<String> m_allThemes = db.getThemes();
		
		for(String theme : m_allThemes)
		{
			System.out.println(theme);
		}
	}),
	
	SHOW_POSTS("show posts", (args)->{	
		showPosts(args);
	}),
	
	EDIT_POST("edit post", (args) ->{
		
		if(CurrentState.selectedThemeID == -1)
		{
			System.out.println("No theme is selected");
			
			return;
		}
		
		DBProvider db = DBProvider.getInstance();
		
		System.out.println("Posts you can edit are: ");
		
		Scanner input = new Scanner(System.in);

		if(db.isUserAdminByUserID(CurrentState.userID))
		{
			showPosts(args);
			
			selectPost(args);

			System.out.println("Enter new post content: ");
			
			String content = input.nextLine();
			
			LocalDate lastEdit = LocalDate.now();
											
			db.executeInsertQuery("update posts set content = ?, lastedit = ? where id = ?;",
					content, "" + lastEdit, "" + CurrentState.selectedPostID);
		}
		else
		{
			showPosts(CurrentState.userID, args);
			
			selectPost(args);
			
			System.out.println("Enter new post content: ");
			
			String content = input.nextLine();
			
			LocalDate lastEdit = LocalDate.now();
											
			db.executeInsertQuery("update posts set content = ?, lastedit = ? where id = ?;",
					content, "" + lastEdit, "" + CurrentState.selectedPostID);
		}
	}),
	
	SELECT_POST("select post", (args)->{	
		
		selectPost(args);
	});
		
	private String name;
	
	private CommandExecuter executer;
	
	public String getName()
	{
		return name;
	}
	
	private Commands(String name, CommandExecuter executer) 
	{
		this.name = name;
		this.executer = executer;
	}
	
	public void executeCommand(String ...args)
	{
		if(executer != null)
		{
			executer.execute(args);
		}
	}
	
	private static void showPosts(String ...args)
	{
		if(CurrentState.selectedThemeID == -1)
		{
			System.out.println("No theme is selected");
			
			return;
		}
						
		DBProvider db = DBProvider.getInstance();
		
		List<String> m_allPosts = db.getPostByThemeID(CurrentState.selectedThemeID);
		
		for(String post : m_allPosts)
		{
			System.out.println(post);
		}
	}
	
	private static void showPosts(int id, String...args)
	{
		if(id < 0) 
		{
			showPosts(args);
			return;
		}
		else
		{
			if(CurrentState.selectedThemeID == -1)
			{
				System.out.println("No theme is selected");
				
				return;
			}
							
			DBProvider db = DBProvider.getInstance();
			
			List<String> m_allPostsByUser = db.getPostByThemeIDAndUserID(CurrentState.selectedThemeID, CurrentState.userID);
			
			for(String post : m_allPostsByUser)
			{
				System.out.println(post);
			}
		}
	}

	private static void selectPost(String ...args)
	{
		Scanner input = new Scanner(System.in);
		
		System.out.println("Enter post title to select: ");
		
		String title = input.nextLine();
		
		DBProvider db = DBProvider.getInstance();
		
		int postID = db.getPostIDByTitle(title);
		
		if(postID == -1)
		{
			System.out.println("There is no post with this title");
			
			return;
		}
		
		CurrentState.selectedPostID = postID;
	}
}
