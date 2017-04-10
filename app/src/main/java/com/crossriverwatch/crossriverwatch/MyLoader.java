package com.crossriverwatch.crossriverwatch;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.crossriverwatch.crossriverwatch.database.NewsContract;

/**
 * Created by ESIDEM jnr on 3/8/2017.
 */

public class MyLoader extends CursorLoader {

    public static MyLoader newAllArticlesInstance(Context context) {
        return new MyLoader(context, NewsContract.Entry.buildDirUri());
    }

    public static MyLoader newInstanceForItemId(Context context, long itemId) {
        return new MyLoader(context, NewsContract.Entry.buildItemUri(itemId));
    }


    private MyLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, NewsContract.Entry.COLUMN_NAME_PUBLISHED + " DESC");
    }

    public interface Query {
        String[] PROJECTION = {
                NewsContract.Entry._ID,
                NewsContract.Entry.COLUMN_NAME_TITLE,
                NewsContract.Entry.COLUMN_NAME_PUBLISHED,
                NewsContract.Entry.COLUMN_NAME_LINK,
                NewsContract.Entry.COLUMN_NAME_DESCRIPTION,
                NewsContract.Entry.COLUMN_NAME_IMAGE_URL,
                NewsContract.Entry.COLUMN_NAME_FAV,

        };

        int COLUMN_ID = 0;
        int COLUMN_TITLE = 1;
        int COLUMN_PUB_DATE = 2;
        int COLUMN_LINK = 3 ;
        int COLUMN_DESC = 4;
        int COLUMN_PHOTO_URL = 5;
        int COLUMN_FAV = 6;

    }
}
