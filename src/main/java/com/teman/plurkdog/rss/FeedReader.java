package com.teman.plurkdog.rss;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.teman.plurkdog.PlurkFilter;
import com.teman.plurkdog.PlurkFilter.Predicate;


public class FeedReader {
	public static Log logger = LogFactory.getLog(FeedReader.class);
	
    /**
     * 取得新的文章
     * @param url
     * @param lastMin
     * @return
     */
    public static List<SyndEntry> getNewEntry(String url, int lastMin) {
        List<SyndEntry> list = null;
        List<SyndEntry> filterList = new ArrayList<SyndEntry>();
        try {
            URL feedUrl = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));
            logger.info(feed.getTitle());  
            list =  feed.getEntries();
            filterList = getLatestPost(list,lastMin);                     
        } catch (Exception ex) {           
        	logger.error(ex,ex);
        }
        return filterList;
    }
    
    /**
     * 過濾時間
     * @param list
     * @param min
     * @return
     */
    public static  List<SyndEntry> getLatestPost(List<SyndEntry> list,int min){
		final int minute = min;
		Predicate<SyndEntry> predicate = new Predicate<SyndEntry>() {
			
			public boolean apply(SyndEntry entry) {
				Date now = new Date();
				if(entry.getPublishedDate()==null){//避免時間是空的
					return false;
				}
				boolean isApply = entry.getPublishedDate().getTime() 
					> (now.getTime() - minute * 60 * 1000);
				isApply = isApply && entry.getPublishedDate().getTime() < now.getTime();
				return isApply;
			}
		}; 		
		return (List<SyndEntry>) PlurkFilter.filter(list, predicate);
	}
    
    public static void main(String[] args) {        
        List<SyndEntry> list = FeedReader.getNewEntry("http://api.flickr.com/services/feeds/groups_pool.gne?id=21475196@N00&lang=zh-hk&format=rss_200", 60*24);
        for( SyndEntry entry : list){
        	logger.debug(entry.getPublishedDate());
        	logger.debug(entry.getTitle());
            logger.debug(entry.getAuthor());
            logger.debug(entry.getLink());
                    
        }
    }

}
