package com.crossriverwatch.crossriverwatch.parser;


import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import static android.content.ContentValues.TAG;

/**
 * Created by ESIDEM jnr on 3/8/2017.
 */

public class ReadRss {

    private static String link = "http://crossriverwatch.com/feed/";

    public List<RSSItem> parse() {
        List<RSSItem> itemsList = null;
        XmlPullParser parser = Xml.newPullParser();
        try {
            // auto-detect the encoding from the stream
            parser.setInput(getInputStream(), null);
            int eventType = parser.getEventType();
            RSSItem rssItem = null;
            boolean done = false;
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                String name = null;
                String attributeValue = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        itemsList = new ArrayList<RSSItem>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();

                        if (name.equalsIgnoreCase("item")) {
                            rssItem = new RSSItem();
                        } else if (rssItem != null) {
                            if (name.equalsIgnoreCase("description")) {
                                rssItem.setDescription(parser.nextText().replaceAll("[<](/)?div[^>]*[>]", ""));
                            } else if (name.equalsIgnoreCase("pubDate")) {

                                Date date = new Date(parser.nextText());
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault());
                                rssItem.setPubDate(sdf.format(date));
                            } else if (name.equalsIgnoreCase("link")) {
                                    rssItem.setLink(parser.nextText());
                            } else if (name.equalsIgnoreCase("title")) {
                                rssItem.setTitle(parser.nextText());
                            } else if (name.equalsIgnoreCase("enclosure")) {
                                rssItem.setImageUrl(parser.getAttributeValue(null, "url"));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("item") && rssItem != null) {
                            assert itemsList != null;
                            itemsList.add(rssItem);
                        } else if (name.equalsIgnoreCase("channel")) {
                            done = true;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemsList;
    }

    private InputStream getInputStream() {
        try {
            URL url = new URL(link);
            return url.openConnection().getInputStream();
        }

        catch (IOException e) {

            e.printStackTrace();

            return null;

        } catch(IllegalArgumentException e){ //Replace this with the more specific exception

            throw new IllegalStateException();


        }


    }

}
