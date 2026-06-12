package com.studio.gladetowns.core.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.studio.gladetowns.core.data.db.entity.TownBlobEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TownBlobDao_Impl implements TownBlobDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TownBlobEntity> __insertionAdapterOfTownBlobEntity;

  public TownBlobDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTownBlobEntity = new EntityInsertionAdapter<TownBlobEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `town_blob` (`diorama_id`,`log_relative_path`,`log_crc`,`blob_version`,`size_bytes`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TownBlobEntity entity) {
        statement.bindString(1, entity.getDioramaId());
        statement.bindString(2, entity.getLogRelativePath());
        statement.bindLong(3, entity.getLogCrc());
        statement.bindLong(4, entity.getBlobVersion());
        statement.bindLong(5, entity.getSizeBytes());
      }
    };
  }

  @Override
  public Object upsert(final TownBlobEntity entity, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTownBlobEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object get(final String dioramaId,
      final Continuation<? super TownBlobEntity> $completion) {
    final String _sql = "SELECT * FROM town_blob WHERE diorama_id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dioramaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TownBlobEntity>() {
      @Override
      @Nullable
      public TownBlobEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDioramaId = CursorUtil.getColumnIndexOrThrow(_cursor, "diorama_id");
          final int _cursorIndexOfLogRelativePath = CursorUtil.getColumnIndexOrThrow(_cursor, "log_relative_path");
          final int _cursorIndexOfLogCrc = CursorUtil.getColumnIndexOrThrow(_cursor, "log_crc");
          final int _cursorIndexOfBlobVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "blob_version");
          final int _cursorIndexOfSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "size_bytes");
          final TownBlobEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDioramaId;
            _tmpDioramaId = _cursor.getString(_cursorIndexOfDioramaId);
            final String _tmpLogRelativePath;
            _tmpLogRelativePath = _cursor.getString(_cursorIndexOfLogRelativePath);
            final long _tmpLogCrc;
            _tmpLogCrc = _cursor.getLong(_cursorIndexOfLogCrc);
            final int _tmpBlobVersion;
            _tmpBlobVersion = _cursor.getInt(_cursorIndexOfBlobVersion);
            final long _tmpSizeBytes;
            _tmpSizeBytes = _cursor.getLong(_cursorIndexOfSizeBytes);
            _result = new TownBlobEntity(_tmpDioramaId,_tmpLogRelativePath,_tmpLogCrc,_tmpBlobVersion,_tmpSizeBytes);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
