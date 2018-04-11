@javax.jdo.annotations.PersistenceCapable

public class Name
{
	String firstName;
	String lastName;
	String midInitial;

	public Name()
	{
	}

	public Name(String firstName, String lastName, String midInitial)
	{
	        this.firstName = firstName;
		this.lastName = lastName;
		this.midInitial = midInitial;
	}

}
