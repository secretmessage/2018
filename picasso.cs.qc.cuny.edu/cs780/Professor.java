import java.util.*;

@javax.jdo.annotations.PersistenceCapable

public class Professor extends Employee
{
	String rank;
	boolean nowEmployed;

	HashSet<CourseSection> teach = new HashSet<CourseSection>();
	  // Implements teach: Professor --> CourseSection  1..*, *

	public String toString()
	{
		return IDNum+" "+name.firstName+" "+name.lastName+" "+
		       " Rank: "+rank+" "+" now employed? "+nowEmployed ; 
	}
}
