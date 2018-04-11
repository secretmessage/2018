import java.util.*;

@javax.jdo.annotations.PersistenceCapable

public class Department extends Office
{
	Professor chairPerson;

	HashSet<Course> offer = new HashSet<Course>();
	  // Implements offer: Department --> Course  1..*, *
	
	
	public Department(String officeName, String roomNum, String building, 
				      String phoneNum)
	{
		this.officeName = officeName;
		this.roomNum = roomNum;
		this.building = building;
		this.phoneNum = phoneNum;
	}
	
	public String toString()
	{
		return officeName+" "+roomNum+" "+building+" "+phoneNum;
	}
}

