package getdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dhruv on 6/12/14.
 *
 * This class takes response text as the input and
 * returns the announcement data contained in the response
 * text. It returns a List of HashMaps with the
 * following structure :-
 *      title -     title of the announcement
 *      date -      date of the post
 *      time -      time of posting
 *      message -   the body of the announcement
 *
 */
public class GetAnnouncementData {

    private String responseText;

    public GetAnnouncementData(String responseText) {
        this.responseText = responseText;
    }

    public List<HashMap<String, String>> getDataList() {
        List<HashMap<String, String>> announcementList = new ArrayList<HashMap<String, String>>();
        String[] lines = responseText.split("\n");
        for(int i = 0; i < lines.length; i++) {
            /*
            "contentTextTitle2" is the class name of the tr tags which contain the
            announcement titles and dates. The line following the tr tag with this
            class name contains title of the announcement
            */
            if(lines[i].contains("contentTextTitle2") && lines[i + 1].contains("strong")) {
                HashMap<String, String> announcement = new HashMap<String, String>();

                //  for fetching the title of the announcement
                int index_strong = lines[i + 1].indexOf("strong");
                int index_strong_close = lines[i + 1].indexOf("</strong>");
                int title_index = index_strong + 7;
                if(lines[i + 1].charAt(72) == '<') {  //  some titles have header tags, a jump of 4 skips those tags
                    title_index += 4;
                }
                if(lines[i + 1].charAt(index_strong_close - 1) == '>')
                    index_strong_close -= 5;
                String title = lines[i + 1].substring(title_index, index_strong_close);
                announcement.put("title", title);

                //  for fetching the date and time of posting of the announcement
                int timestampIndex = lines[i + 2].indexOf("style1") + 8;
                String timestamp = lines[i + 2].substring(timestampIndex, lines[i + 2].length() - 10);
                String date = timestamp.split("\n")[0];
                String time = timestamp.split("\n")[1];
                announcement.put("date", date);
                announcement.put("time", time);

                //  for fetching the body of the announcement
                String beforeMessage = "<td height=\"27\" colspan=\"2\"><span class=\"standardText\">";
                String messageRaw = lines[i + 5];
                messageRaw = messageRaw.substring(beforeMessage.length() - 1);
                messageRaw = messageRaw.replaceAll("</?h[0-9]>", "");   //  remove all opening and closing header tags
                messageRaw = messageRaw.replaceAll("<br>", "\n");   //  replacing all br tags with end line character

                announcement.put("message", messageRaw);
                announcementList.add(announcement);
            }
        }
        return announcementList;
    }
}
