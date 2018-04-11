import java.util.*;

@javax.jdo.annotations.PersistenceCapable

public class Department extends Office
{
	Professor chairPerson;

	HashSet<Course> offer = new HashSet<Course>();
	  // Implements offer: Department --> Course  1..*, *
}
