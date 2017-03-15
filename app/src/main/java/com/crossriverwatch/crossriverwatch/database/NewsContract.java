package com.crossriverwatch.crossriverwatch.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.crossriverwatch.crossriverwatch.database.NewsContract.Entry.COLUMN_NAME_PUBLISHED;

/**
 * Created by ESIDEM jnr on 3/8/2017.
 */

public class NewsContract {

    public static final String CONTENT_AUTHORITY = "com.crossriverwatch.crossriverwatch";

    public static final Uri BASE_URI = Uri.parse("content://com.crossriverwatch.crossriverwatch");

    /**
     * Path component for "entry"-type resources..
     */
    private static final String PATH_ENTRIES = "entries";


    /**
     * Columns supported by "entries" records.
     */
    public static class Entry implements BaseColumns {
        /**
         * MIME type for lists of entries.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.crossriverwatch.entries";
        /**
         * MIME type for individual entries.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.crossriverwatch.entry";

        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(PATH_ENTRIES).build();



        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "entry";


        public static final String COLUMN_NAME_TITLE = "title";


        public static final String COLUMN_NAME_LINK = "link";
        /**
         * Date article was published.
         */
        public static final String COLUMN_NAME_PUBLISHED = "published";

        public static final String COLUMN_NAME_FAV = "fav";

        public static final String COLUMN_NAME_DESCRIPTION = "description";

        public static final String COLUMN_NAME_IMAGE_URL = "image_url";




        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath("entries").build();
        }

        /**
         * Matches: /items/[_id]/
         */
        public static Uri buildItemUri(long _id) {
            return BASE_URI.buildUpon().appendPath("entries").appendPath(Long.toString(_id)).build();
        }

        /**
         * Read item ID item detail URI.
         */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }

    }


    private NewsContract() {
    }
}
