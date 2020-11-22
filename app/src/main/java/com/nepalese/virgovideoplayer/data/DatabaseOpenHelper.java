package com.nepalese.virgovideoplayer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nepalese.virgovideoplayer.data.db.DaoMaster;
import com.nepalese.virgovideoplayer.data.db.DownloadItemDao;

import org.greenrobot.greendao.database.Database;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author nepalese on 2020/10/27 16:43
 * @usage
 */
public class DatabaseOpenHelper extends DaoMaster.DevOpenHelper {

    public static final String DATABASE_NAME = "VirgoVideo.db";

    private File mDatabasePath;
    private Context mContext;

    public DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
        mContext = context;
        mDatabasePath = context.getDatabasePath(DATABASE_NAME);
    }

    /**
     * Check Database if it exists
     */
    private boolean databaseExists() {
        SQLiteDatabase sqliteDatabase = null;
        try {
            if (mDatabasePath.exists()) {
                sqliteDatabase = SQLiteDatabase.openDatabase(mDatabasePath.getAbsolutePath(), null,
                        SQLiteDatabase.OPEN_READWRITE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sqliteDatabase != null) {
            sqliteDatabase.close();
        }
        return sqliteDatabase != null;
    }

    // 拷贝数据库
    private static int copy(InputStream in, OutputStream out)
            throws IOException {
        int byteCount = 0;
        byte[] buffer = new byte[4096];
        while (true) {
            int read = in.read(buffer);
            if (read == -1) {
                break;
            }
            out.write(buffer, 0, read);
            byteCount += read;
        }
        in.close();
        out.close();
        return byteCount;
    }


    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        if (newVersion == 1){
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " recreate DownloadItem table");
            DownloadItemDao.dropTable(db, true);
            DownloadItemDao.createTable(db, false);
            return;
        }
        super.onUpgrade(db, oldVersion, newVersion);
        //TODO upgrade
    }
}
