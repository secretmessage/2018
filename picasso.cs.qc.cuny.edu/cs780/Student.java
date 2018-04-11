import java.util.*;
import javax.jdo.*;
import com.objectdb.Utilities;

@javax.jdo.annotations.PersistenceCapable

public class Student extends Person
{
	String major;
	char program; // 'U' = undergraduate, 'M' = Master's
                      // 'P' = PhD
	boolean hasGraduated;

	TreeSet<Take> take = new TreeSet<Take>();
	  // Implements the inverse of
	  // student: Take --> Student  *, 1
	  // Sorted in chronological order of Take


	public String toString()
	{
		return IDNum+" "+name.firstName+" "+name.lastName+
		       " Major: "+major;
	}

	public static Student find(String ID, PersistenceManager pm)

	/* Given a student ID as parameter, the function returns the Student
           object with that ID; if no such Student object exists, null is
           returned. The function is applied to the database held by 
	   the persistence manager pm. */

	{
	       	Query q = pm.newQuery(Student.class);
		q.declareParameters("String studentID");
		q.setFilter("this.IDNum == studentID");

        	Collection<Student> ss = (Collection<Student>) q.execute(ID);

		Student s = Utility.extract(ss);

		q.close(ss);

		return s;
	}

	public static int enrolledInAtLeast(String ID, int x, PersistenceManager pm)

	/* Given a student ID and an integer x, the function checks to see
           if the Student object with that ID took or is taking at least
           x course sections; if no such Student object exists, -1 is returned. */

	{
		Student s = Student.find(ID, pm);
		if ( s == null )
			return -1;
		else if ( s.take.size() >= x )
			return 1;
		else
			return 0;
	}

	public static Collection<Student> sameLastName1(Query q)

	/* Returns the collection of all students each of whom has at least
	   one other student with the same last name. */

	{
	       	q.setClass(Student.class);
		q.declareVariables("Student s1");
		q.setFilter("this.name.lastName == s1.name.lastName &&"+
		            "this != s1");

        	return (Collection<Student>) q.execute();
	}

	public static Collection<Student> sameLastName2(Query q)

	/* Returns the collection of all students each of whom has at least
	   two other students with the same last name. */

	{
	       	q.setClass(Student.class);
		q.declareVariables("Student s1, s2");
		q.setFilter("this.name.lastName == s1.name.lastName &&"+
		            "s1.name.lastName == s2.name.lastName &&"+
		            "this != s1 && s1 != s2 && s2 != this");

        	return (Collection<Student>) q.execute();
	}

	public Collection<CourseSection> getCourseSections(Query q)

	/* Returns the collection of all course sections the target student
	   took or is taking. The collection will be sorted by (year, semester)
	   in ascending order. The implementation of the query in the code
	   will likely inspect all course section objects and therefore be
	   less efficient than the alternative code below. */

	{
		q.setClass(CourseSection.class);
		q.declareParameters("Student s");
		q.declareVariables("Take t");
		q.setFilter("this.take.contains(t) &&"+
		            "t.student == s");
		q.setOrdering("this.year ascending, this.semester ascending");

        	return (Collection<CourseSection>) q.execute(this);
	}

	public TreeSet<CourseSection> getCourseSections()

	/* Returns the treeSet of all course sections the target student
	   took or is taking. The treeSet will be sorted by (year, semester)
	   in ascending order. Much more efficient than the above version. */

	{
		TreeSet<CourseSection> cc = new TreeSet<CourseSection>();

		for ( Take t: this.take )
			cc.add(t.courseSection);

		return cc;
	}

	public Take getTake(CourseSection c, PersistenceManager pm)

	/* Returns the Take object associated with the target student
	   object and the parameter course section object c; returns null if no such
	   Take object exists. */
 
	{
		Collection<Take> takes = this.take;

		Query q = pm.newQuery(Take.class, takes);
		q.declareParameters("CourseSection cs");
		q.setFilter("this.courseSection == cs");
	
		Collection<Take> tt = (Collection<Take>) q.execute(c);

		Take t = Utility.extract(tt);

		q.close(tt);

		return t;
	}

	public Take getTake(CourseSection c)

	/* Alternative implementation of "getTake". */
 
	{
		TreeSet<Take> tt = (TreeSet<Take>) this.take.clone();
		tt.retainAll( c.take );

		// tt = the intersection of this.take and c.take

		return Utility.extract(tt);
	}

	public boolean addToTake(CourseSection c)

	/* Creates new Grade and Take objects, and make this Take object
	   represent a new relation instance among the target student,
	   the given course section c, and the new grade object. */

	{
		if ( getCourseSections().contains(c) )
			return false;
		else
		{
			Grade g = new Grade();
			Take t = new Take();

			t.student = this;
			t.courseSection = c;
			t.grade = g;
			this.take.add(t);
			c.take.add(t);

			return true;
		}
	}

	public boolean removeFromTake(CourseSection c, PersistenceManager pm)

	/* Removes the Take and Grade objects associated with the target
	   student object and the given course section object c. */

	{
		Take t = getTake(c, pm);

		if ( t == null )
			return false;
		else
		{
			this.take.remove(t);
			c.take.remove(t);
			pm.deletePersistent(t.grade);
			pm.deletePersistent(t);

			return true;
		}
	}

	public static void main(String argv[])
	{
		PersistenceManager pm = Utilities.getPersistenceManager("odb6.odb");

		Student s = Student.find(argv[0], pm);
		if ( s == null )
		{
			System.out.println("Student with given ID does not exist: "+argv[0]);
			if ( !pm.isClosed() )
       				pm.close();
			return;
		}
		int i = Student.enrolledInAtLeast(argv[0], 10, pm);
		System.out.println(s);
		System.out.println(i);
		System.out.println();

		Query q = pm.newQuery();
		Utility.printCollection(s.getCourseSections(q));
		q.closeAll();
		System.out.println();

		CourseSection c = CourseSection.find("123456", 2, 2003, pm);
		if ( c != null )
		{
			System.out.println(c);

			pm.currentTransaction().begin();
				boolean b = s.addToTake(c);
			pm.currentTransaction().commit();
			System.out.println(b);
			Utility.printCollection(s.getCourseSections());
			pm.currentTransaction().begin();
				System.out.println(s.getTake(c));
			pm.currentTransaction().commit();
			System.out.println();

			if ( b == true )
			{
				pm.currentTransaction().begin();
					b = s.removeFromTake(c, pm);
				pm.currentTransaction().commit();
				System.out.println(b);
				Utility.printCollection(s.getCourseSections());
				System.out.println();
			}
		}
		
		q = pm.newQuery();
		Utility.printCollection(Student.sameLastName1(q));
		q.closeAll();
		System.out.println();

		q = pm.newQuery();
		Utility.printCollection(Student.sameLastName2(q));
		q.closeAll();

		if ( !pm.isClosed() )
       			pm.close();

	}  // end main
}
