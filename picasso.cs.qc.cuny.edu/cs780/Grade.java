import java.util.*;

@javax.jdo.annotations.PersistenceCapable

public class Grade
{
	String originalGrade;
	Date originalGradeDate;
	String updatedGrade;
	Date updatedGradeDate;


	String getGrade()
	{
		if ( updatedGrade == null )
			return originalGrade;
		else
			return updatedGrade;
	}
}
