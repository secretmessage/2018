import java.util.*;

@javax.jdo.annotations.PersistenceCapable

public abstract class Employee extends Person
{
	OfficeInfo office;

	HashSet<Employment> employment = new HashSet<Employment>();
	  // Implements the inverse of
	  // employee: Employment --> Employee  1..*,  1
}
