package com.teman.plurkdog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.json.JSONException;

import com.teman.plurkdog.bean.PlurkBase;

public class PlurkFilter {

	public interface Predicate<T> { boolean apply(T type); }
	
	public static <T> Collection<T> filter(Collection<T> target, Predicate<T> predicate) {
	    Collection<T> result = new ArrayList<T>();
	    for (T element: target) {
	        if (predicate.apply(element)) {
	            result.add(element);
	        }
	    }
	    return result;
	}

	
	


}
