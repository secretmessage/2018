import java.util.*;
import javax.jdo.*;

@javax.jdo.annotations.PersistenceCapable

public class Company
{
	String name; // key

	HashSet<Laptop> makeLaptops = new HashSet<Laptop>(); 
	  // The set of laptops this company makes

	HashSet<Processor> makeProcessors = new HashSet<Processor>(); 
	  // The set of processors this company makes


	public Company(String s)
	{
		name = s;
	}

	public String toString()
	{
		return name;
	}
	
	public static Collection<Company> memoryProcessor(float c, int s, Query q)

	/* Returns the collection of all companies that make laptops that
	   have a processor clock speed of at least "c" GHz and a memory size of
	   at least "s" GB. Sort the result by name. */

	{

	}

	public static Collection<Company> differentCompanyProcessor(Query q)

	/* Returns the collection of all companies that make at least two laptops 
	   preinstalled with processors made by different companies. Sort the result by name. */

	{
	
	}
}
