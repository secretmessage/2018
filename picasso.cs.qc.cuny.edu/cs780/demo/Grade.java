import java.util.*;

@javax.jdo.annotations.PersistenceCapable

public class Grade
{
	String originalGrade;
	String originalGradeDate;
	String updatedGrade;
	String updatedGradeDate;


	public Grade()
	{
	}

	public Grade(String originalGrade, String originalGradeDate,
		     String updatedGrade, String updatedGradeDate)
	{
		this.originalGrade = originalGrade;
		this.originalGradeDate = originalGradeDate;
		this.updatedGrade = updatedGrade;
		this.updatedGradeDate = updatedGradeDate;
	}

	String getGrade()
	{
		if ( updatedGrade == null )
			return originalGrade;
		else
			return updatedGrade;
	}

	public String toString()
	{
		return getGrade();
	}
}
