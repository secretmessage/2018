import java.util.*;
import javax.jdo.*;

@javax.jdo.annotations.PersistenceCapable

public class Flight
{
	String airlineCompanyName;
	String flightNum; // { airlineCompanyName, flightNum } is a key
	Airport origin; 
	Airport destination;
	Time departTime;
	Time arriveTime;


	public String toString()
	{
		return airlineCompanyName+" "+flightNum+" "+
		       origin.name+" "+departTime.hour+":"+departTime.minute+" "+
		       destination.name+" "+arriveTime.hour+":"+arriveTime.minute ;
	}

	public static Flight find(String airlineCompanyName, String flightNum, PersistenceManager pm)

	/* Returns the flight that has the two parameter values; returns null if no such flight exists.
           { airlineCompanyName, flightNum } is assumed to be a key for Flight class.
	   The function is applied to the database held by the persistence manager "pm". */

	{

	}

	public static Collection<Flight> getFlights(String a1, String a2, Query q)
	
	/* Given airport names a1 and a2, the function returns the collection of
	   all flights departing from a1 and arriving to a2.
	   Sort the result by (airlineCompanyName, flightNum). */

	{

	}

	public static Collection<Flight> getFlightsForCities(String c1, String c2, Query q)

	/* Given city names c1 and c2, the function returns the collection of all flights 
	   departing from an airport close to c1 and arriving to an airport close to c2. 
	   Sort the result by (airlineCompanyName, flightNum). */

	{

	}

	public static Collection<Flight> getFlightsDepartTime(
		  String a1, String a2, int h1, int m1, int h2, int m2, Query q)

	/* Given airport names a1 and a2 and times h1:m1 and h2:m2,
	   the function returns the collection of all flights departing from a1 and arriving to a2
	   satisfying the condition that the departure time is h1:m1 at earliest and h2:m2 at latest.
	   Note that the time interval from h1:m1 to h2:m2 may include midnight.
	   Sort the result by (airlineCompanyName, flightNum). */

	{

	}

	public static Collection<Object[]> getFlightsConnection(
		  String a1, String a2, int h1, int m1, int h2, int m2,
		  int connectionAtLeast, int connectionAtMost, Query q)

	/* Given airport names a1 and a2, times h1:m1 and h2:m2, and connection time lower and upper bounds in minutes,
	   connectionAtLeast and connectionAtMost, the function returns the pairs of all flights f and f1 satisfying
	   the following conditions:

	   1. f departs from a1 and arrives to a connecting airport "ca" different from a2; and
	   2. The departure time of f is h1:m1 at earliest and h2:m2 at latest; and
	   3. There is a second flight f1 from "ca" to a2; and
	   4. The connecting time, i.e. the time interval in minutes between 
	      the arrival time of f and the departure time of f1, is at least connectionAtLeast
	      and at most connectionAtMost. 

	   Note again that the relevant time intervals may include midnight.
	   Sort the result by (f.airlineCompanyName, f.flightNum, f1.airlineCompanyName, f1.flightNum). */

	{

	}

	public static Collection<Object[]> groupByCompany(Query q)

	/* Group the flights by their airline company names.
	   Then return the set of 2-tuples <airlineCompanyName: String, num: int> where:

	   num = the total number of flights operated by airlineCompanyName

	   Sort the result by airlineCompanyName. */

	{

	}
}
