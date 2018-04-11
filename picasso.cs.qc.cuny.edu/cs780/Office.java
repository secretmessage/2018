import java.util.*;

@javax.jdo.annotations.PersistenceCapable

public class Office
{
	String officeName;
	OfficeInfo office;

	HashSet<Employment> employment = new HashSet<Employment>();
	  // Implements the inverse of
	  // office: Employment --> Office  *, 1
}
