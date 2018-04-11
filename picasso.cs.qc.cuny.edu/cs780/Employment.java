import java.util.*;

@javax.jdo.annotations.PersistenceCapable

public class Employment

/* Implements the ternary relation
   employment(Employee, Office, JobDescription).
   Since the employment relation instances and the JobDescription objects
   have 1:1 correspondence, the JobDescription class is 
   dispensed with and its attributes are incorporated into this class. */

{
	String positionTitle;
	Date startDate;
	Date endDate;

	Employee employee;
	Office office;
}
