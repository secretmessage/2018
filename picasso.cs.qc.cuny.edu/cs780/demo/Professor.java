import java.util.*;

@javax.jdo.annotations.PersistenceCapable

public class Professor extends Person
{
	String rank;
	boolean nowEmployed;

	HashSet<CourseSection> teach = new HashSet<CourseSection>();
	  // Implements teach: Professor --> CourseSection  1..*, *
	
	public Professor(String firstName, String lastName, String midInitial, 
				     String IDNum, String address, String homePhone, char gender,
				     String rank, boolean nowEmployed)
	{
		this.name = new Name(firstName, lastName, midInitial);
		this.IDNum = IDNum;
		this.address = address;
		this.homePhone = homePhone;
		this.gender = gender;
		this.rank = rank;
		this.nowEmployed = nowEmployed;
	}
	
	public String toString()
	{
		return IDNum+" "+name.firstName+" "+name.lastName+" "+
		       " Rank: "+rank+" "+" now employed? "+nowEmployed ; 
	}
}

