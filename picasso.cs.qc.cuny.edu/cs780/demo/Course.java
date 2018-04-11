import java.util.*;
import javax.jdo.*;

@javax.jdo.annotations.PersistenceCapable

public class Course
{
	String number;
	String title;
	int credits;
	int hours;
	String description;

	TreeSet<CourseSection> courseSection = new TreeSet<CourseSection>(); 
	  // Implements courseSection: Course --> CourseSection  1, *
          // Sorted by (year, semester) of CourseSection in chronological order 

	public Course(String number, String title, int credits, int hours, String description)
	{
		this.number = number;
		this.title = title;
		this.credits = credits;
		this.hours = hours;
		this.description = description;
	}
	
	public static Collection<Student> getStudents(String cNum, Query q)
	
	/* Returns the collection of all students who took or are taking the course with number cNum.
	   The collection will be sorted by (last name, first name) in ascending order. */
	
	{
		q.setClass(Student.class);
		q.declareParameters("String cNum");
		q.declareVariables("Course c; CourseSection cs; Take t");
		q.setFilter("c.number == cNum &&"+
		            "c.courseSection.contains(cs) &&"+
			    "cs.take.contains(t) &&"+
		            "t.student == this");
		q.setOrdering("this.name.lastName ascending, this.name.firstName ascending");
		
		return (Collection<Student>) q.execute(cNum);
	}
}

