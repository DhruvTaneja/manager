package getdata;

/**
 * Created by dhruv on 5/12/14.
 *
 * This class is used to fetch the URLs present
 * in the response text. It takes response text
 * as input. The getters available in the class are
 *
 *  getLastLink -   to get the link of the Last page of the
 *                  paginated pages
 *
 *  getPreviousLink to get the link of the Previous page
 *                  of the paginated pages
 *
 */
public class GetLinks {
    private String responseText;
    final public String hostUrl = "http://www.dce.ac.in";
    
    GetLinks(String responseText) {
        this.responseText = responseText;
    }
    
    public String getLastLink() {
        String subString, lastUrl = null;
        String[] lines = responseText.split("\n");
        for(String line : lines) {
            if(line.contains(">Last")) {
                int link_index = line.indexOf("href") + 6;
                int link_index_end = line.indexOf("Last") - 2;
                subString = line.substring(link_index, link_index_end);
                lastUrl = hostUrl + subString;
            }
        }
        return lastUrl;
    }

    public String getPreviousLink() {
        String subString, lastUrl = null;
        String[] lines = responseText.split("\n");
        for(String line : lines) {
            if(line.contains(">Previous")) {
                int link_index = line.indexOf("href") + 6;
                int link_index_end = line.indexOf("Previous") - 2;
                subString = line.substring(link_index, link_index_end);
                lastUrl = hostUrl + subString;
            }
        }
        return lastUrl;
    }
    
}
