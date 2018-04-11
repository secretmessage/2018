import java.util.*;
import javax.jdo.*;
import com.objectdb.Utilities;

@javax.jdo.annotations.PersistenceCapable

public class CourseSection implements Comparable<CourseSection>
{
	String sectionCode;
	String registrationCode;
	int semester; /* 1 = winter session, 2 = spring, 3 = summer session I,
                         4 = summer session II, 5 = fall */
	int year;

	Course course;
	  // Implements the inverse of 
	  // courseSection: Course --> CourseSection  1, *
 
	HashSet<Take> take = new HashSet<Take>(); 
	  // Implements the inverse of 
	  // courseSection: Take --> CourseSection  *, 1

	public String semesterString()
	{
		if ( semester == 1 )
			return "Winter";
		else if ( semester == 2 )
			return "Spring";
		else if ( semester == 3 )
			return "Summer I";
		else if ( semester == 4 )
			return "Summer II";
		else
			return "Fall";
	}

	public String toString()
	{
		return course.number+" "+course.title+" "+registrationCode+" "+
		       semesterString()+" "+year; 
	}

	public int compareTo(CourseSection that)

	/* Compared by (year, semester) in chronological order.
	   In case "this" and "that" have the same (year, semester),
	   the ordering is resolved by registrationCode to be consistent with "equals". 

	   this.compareTo(that) returns -1, 0, 1 according as this object is <, =, > that object. */

	{
		if ( this.year > that.year )
			return 1;
		else if ( this.year < that.year )
			return -1;
		else if ( this.semester > that.semester )
			return 1;
		else if ( this.semester < that.semester )
			return -1;
		else
			return this.registrationCode.compareTo(that.registrationCode);
	}

	public static CourseSection find(String registrationCode,
	                            int semester,
	                            int yearOffered,
	                            PersistenceManager pm)

	/* Returns the course section object that has the three parameter values.
	   If no such object is found, null is returned. The three corresponding
	   attributes are assumed to form a key for the CourseSection class.
	   The function is applied to the database held by the persistence manager pm. */

	{
	       	Query q = pm.newQuery(CourseSection.class);
		q.declareParameters("String regCode, int sem, int yr");
		q.setFilter("this.registrationCode == regCode &&"+
		            "this.semester == sem &&"+
		            "this.year == yr");

        	Collection<CourseSection> cc = 
		  (Collection<CourseSection>) q.execute(registrationCode,
		                                new Integer(semester),
		                                new Integer(yearOffered));
		
		CourseSection c = Utility.extract(cc);

		q.close(cc);

		return c;
	}

	public Collection<Department> isOfferedBy(Query q)

	/* Returns the collection of all departments that offered or are offering
	   the target course section. */

	{
	       	q.setClass(Department.class);
		q.declareParameters("CourseSection c");
		q.setFilter("this.offer.contains(c.course)");

        	return (Collection<Department>) q.execute(this);
	}

	public Collection<Professor> isTaughtBy(Query q)

	/* Returns the collection of all professors that taught or are teaching
	   the target course section. Represents the inverse relation of Professor.teach. */

	{
	       	q.setClass(Professor.class);
		q.declareParameters("CourseSection c");
		q.setFilter("this.teach.contains(c)");

        	return (Collection<Professor>) q.execute(this);
	}

	public static boolean deleteCourseSection(String registrationCode,
	                                          int semester,
	                                          int yearOffered,
	                                          PersistenceManager pm)

	 
	/* Deletes the course section object that has the three parameter values and
	   returns true. The course section object is also deleted from the teach set of
	   each professor that is assigned to teach it, and from the associated Course
	   object. The Take relation is also appropriately adjusted.
	   If no object with the given parameter values is found, returns false. */
 	

	{
		CourseSection c = CourseSection.find(registrationCode, semester, yearOffered, pm);

		if ( c == null )
			return false;
		else
		{
			for (Take t: c.take)
				t.deleteTake(pm);

			Query q = pm.newQuery();
			Collection<Professor> pp = c.isTaughtBy(q);

			for (Professor p: pp)
				p.teach.remove(c);

			q.close(pp);

			c.course.courseSection.remove(c);

			pm.deletePersistent(c);
			return true;
		}
	}

	public static void main(String argv[])
	{
        	PersistenceManager pm = Utilities.getPersistenceManager("odb6.odb");

		pm.currentTransaction().begin();
			boolean b = CourseSection.deleteCourseSection("19084", 5, 2002, pm);
		pm.currentTransaction().commit();
		System.out.println(b);
		System.out.println();

		CourseSection c = CourseSection.find("1432", 2, 2003, pm);
		if ( c != null )
		{
			System.out.println(c);
			Query q = pm.newQuery();
			Utility.printCollection(c.isTaughtBy(q));
			q.closeAll();
			System.out.println();
		}

        	Query q = pm.newQuery(CourseSection.class);
		q.setOrdering("this.year ascending, this.semester ascending");
        	Collection<CourseSection> cc = (Collection<CourseSection>) q.execute();
		Utility.printCollection(cc);
		q.close(cc);

       		if ( !pm.isClosed() )
       			pm.close();
	} // end main
}
