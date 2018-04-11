import java.util.*;

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
}
