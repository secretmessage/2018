import java.util.*;
import javax.jdo.*;
import com.objectdb.Utilities;

public abstract class CreateData
{
	public static void main(String argv[])
	{
		Student s1 = new Student("John", "Brown", "K", "200", "86 9th Ave",
		                         "212 123 7654", 'm', "Physics", 'U', false);
		Student s2 = new Student("John", "Brown", "K", "201", "876 7th Ave",
		                         "212 908 2657", 'm', "Mathematics", 'U', false);
		Student s3 = new Student("Mario", "Brown", "", "202", "76 Brewster St",
		                         "516 876 3715", 'm', "Computer Science", 'U', false);	
		Student s4 = new Student("Mary", "Miller", "J", "203", "867 Main St, Flushing",
		                         "718 876 2444", 'f', "Computer Science", 'U', true);

		Professor p1 = new Professor("Linda", "Smith", "E", "100", "50 Flamingo Rd",
		                         "718 873 1098", 'f', "Associate Professor", true);
		Professor p2 = new Professor("Jennifer", "Mendenhall", "F", "101", "15 Westhill Rd",
		                         "631 870 7766", 'f', "Professor", false);
		Professor p3 = new Professor("James", "Friedman", "A", "102", "254 Ogden Rd",
		                         "614 352 8712", 'm', "Assistant Professor", true);
		Professor p4 = new Professor("Kevin", "Chen", "", "103", "65 Hillside Ave",
		                         "718 865 8163", 'm', "Professor", true);

		Department d1 = new Department("Computer Science", "A202", "Science Building", "718 997 3500");
		Department d2 = new Department("Mathematics", "237", "Kiely Hall", "718 997 5800");

		PersistenceManager pm = Utilities.getPersistenceManager("test.odb");

		pm.currentTransaction().begin();
		pm.makePersistent(s1);
		pm.makePersistent(s2);
		pm.makePersistent(s3);
		pm.makePersistent(s4);
		pm.makePersistent(p1);
		pm.makePersistent(p2);
		pm.makePersistent(p3);
		pm.makePersistent(p4);
		pm.makePersistent(d1);
		pm.makePersistent(d2);
		pm.currentTransaction().commit();

		Course c1 = new Course("CS 316", "Principles of Programming Languages", 3, 3, "");
		Course c2 = new Course("CS 220", "Discrete Mathematics", 3, 3, "");
		Course c3 = new Course("Math 221", "Linear Algebra", 3, 3, "");
		Course c4 = new Course("Math 334", "Topology", 3, 3, "");

		pm.currentTransaction().begin();
		pm.makePersistent(c1);
		pm.makePersistent(c2);
		pm.makePersistent(c3);
		pm.makePersistent(c4);
		pm.currentTransaction().commit();

		CourseSection cc1 = new CourseSection("8U7T5", "1000", 5, 2004, c1);
		CourseSection cc2 = new CourseSection("6T5R3", "1432", 2, 2003, c2);
		CourseSection cc3 = new CourseSection("8M9Y3", "1234", 2, 2003, c2);
		CourseSection cc4 = new CourseSection("9U7Y5", "9057", 5, 2004, c3);
		CourseSection cc5 = new CourseSection("D5T6P", "2064", 5, 2002, c4);

		pm.currentTransaction().begin();
		pm.makePersistent(cc1);
		pm.makePersistent(cc2);
		pm.makePersistent(cc3);
		pm.makePersistent(cc4);
		pm.makePersistent(cc5);
		c1.courseSection.add(cc1);
		c2.courseSection.add(cc2);
		c2.courseSection.add(cc3);
		c3.courseSection.add(cc4);
		c4.courseSection.add(cc5);
		pm.currentTransaction().commit();

		pm.currentTransaction().begin();
		p1.teach.add(cc5);
		p2.teach.add(cc1);
		p2.teach.add(cc2);
		p3.teach.add(cc4);
		p4.teach.add(cc3);
		pm.currentTransaction().commit();

		pm.currentTransaction().begin();
		d1.chairPerson = p4;
		d2.chairPerson = p1;
		d1.offer.add(c1);
		d1.offer.add(c2);
		d2.offer.add(c3);
		d2.offer.add(c4);
		pm.currentTransaction().commit();

		Grade g1 = new Grade("B+", "12/28/2004", null, null);
		Grade g2 = new Grade("B", "05/25/2003", "B+", "07/14/2003");
		Grade g3 = new Grade("C+", "05/29/2003", null, null);
		Grade g4 = new Grade("B", "12/21/2002", null, null);
		Grade g5 = new Grade("B", "12/23/2004", null, null);
		Grade g6 = new Grade("A-", "12/21/2002", null, null);
		Grade g7 = new Grade("A", "06/01/2003", null, null);
		Grade g8 = new Grade("A-", "05/27/2003", null, null);
		Grade g9 = new Grade("C", "12/30/2004", null, null);
		Grade g10 = new Grade("B-", "12/25/2002", null, null);

		Take t1 = new Take(s1, cc1, g1);
		Take t2 = new Take(s1, cc2, g2);
		Take t8 = new Take(s2, cc3, g8);
		Take t9 = new Take(s1, cc4, g9);
		Take t10 = new Take(s1, cc5, g10);
		Take t3 = new Take(s3, cc2, g3);
		Take t4 = new Take(s3, cc5, g4);
		Take t5 = new Take(s2, cc4, g5);
		Take t6 = new Take(s2, cc5, g6);
		Take t7 = new Take(s4, cc3, g7);

		pm.currentTransaction().begin();
		s1.take.add(t1);
		s1.take.add(t2);
		s1.take.add(t9);
		s1.take.add(t10);
		s3.take.add(t3);
		s3.take.add(t4);
		s2.take.add(t5);
		s2.take.add(t6);
		s2.take.add(t8);
		s4.take.add(t7);

		cc1.take.add(t1);
		cc2.take.add(t2);
		cc2.take.add(t3);
		cc5.take.add(t4);
		cc4.take.add(t5);
		cc5.take.add(t6);
		cc3.take.add(t7);
		cc3.take.add(t8);
		cc4.take.add(t9);
		cc5.take.add(t10);
		pm.currentTransaction().commit();

		System.out.println( "-- print all professors in lexicographic order of (last name, first name, IDNum) --\n" );
		Query q = pm.newQuery(
		        "select "+
			"from Professor "+
		        "order by name.lastName, name.firstName, IDNum");
		Collection<Professor> pp = (Collection<Professor>) q.execute();
		Utility.printCollection( pp );
		q.closeAll();
		System.out.println();

		System.out.println( "-- print all students in lexicographic order of (last name, first name, IDNum) --\n" );
		q = pm.newQuery(
		  "select "+
                  "from Student "+
		  "order by name.lastName, name.firstName, IDNum");
		Collection<Student> ss = (Collection<Student>) q.execute();
		Utility.printCollection( ss );
		q.closeAll();
		System.out.println();

		System.out.println( "-- print all departments in lexicograhic order of office name --\n" );
		q = pm.newQuery(
		  "select "+
                  "from Department "+
		  "order by officeName");
		Collection<Department> dd = (Collection<Department>) q.execute();
		Utility.printCollection( dd );
		q.closeAll();
		System.out.println();

		if ( !pm.isClosed() )
       			pm.close();
	}
}

