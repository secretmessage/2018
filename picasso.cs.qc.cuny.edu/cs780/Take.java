import java.util.*;
import javax.jdo.*;

@javax.jdo.annotations.PersistenceCapable

public class Take implements Comparable<Take>

/* Implements the 3-way relation 
   take(Student, CourseSection, Grade). */

{
	Student student;
	CourseSection courseSection;
	Grade grade;


	public String toString()
	{
		return student.toString()+" "+
		       courseSection.toString()+" "+
		       grade.getGrade();
	}

	public int compareTo(Take that)

	/* Compared by the associated course sections in chronological order.
	   In case "this" and "that" have the same course section,
	   the ordering is resolved by student.IDNum to be consistent with "equals".

	   this.compareTo(that) returns -1, 0, 1 according as this object is <, =, > that object. */

	{
		int i = this.courseSection.compareTo(that.courseSection);
		if ( i != 0 )
			return i;
		else
			return this.student.IDNum.compareTo(that.student.IDNum);
	}

	public void deleteTake(PersistenceManager pm)
	{
		this.student.take.remove(this);
		this.courseSection.take.remove(this);
		pm.deletePersistent(this.grade);
		pm.deletePersistent(this);
	}
}
