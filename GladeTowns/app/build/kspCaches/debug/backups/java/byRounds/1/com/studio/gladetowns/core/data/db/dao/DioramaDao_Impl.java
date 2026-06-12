package com.studio.gladetowns.core.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.studio.gladetowns.core.data.db.entity.DioramaEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DioramaDao_Impl implements DioramaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DioramaEntity> __insertionAdapterOfDioramaEntity;

  private final SharedSQLiteStatement __preparedStmtOfRename;

  private final SharedSQLiteStatement __preparedStmtOfTouch;

  private final SharedSQLiteStatement __preparedStmtOfSoftDelete;

  private final SharedSQLiteStatement __preparedStmtOfPurgeExpired;

  public DioramaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDioramaEntity = new EntityInsertionAdapter<DioramaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `diorama` (`id`,`name`,`status`,`grid_size`,`master_seed`,`generator_version`,`created_at`,`updated_at`,`sealed_at`,`structure_count`,`cell_coverage`,`time_of_day`,`thumb_version`,`is_deleted`,`delete_after`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DioramaEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getStatus());
        statement.bindLong(4, entity.getGridSize());
        statement.bindLong(5, entity.getMasterSeed());
        statement.bindLong(6, entity.getGeneratorVersion());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getUpdatedAt());
        if (entity.getSealedAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getSealedAt());
        }
        statement.bindLong(10, entity.getStructureCount());
        statement.bindDouble(11, entity.getCellCoverage());
        statement.bindString(12, entity.getTimeOfDay());
        statement.bindLong(13, entity.getThumbVersion());
        final int _tmp = entity.isDeleted() ? 1 : 0;
        statement.bindLong(14, _tmp);
        if (entity.getDeleteAfter() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getDeleteAfter());
        }
      }
    };
    this.__preparedStmtOfRename = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE diorama SET name = ?, updated_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfTouch = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE diorama SET updated_at = ?, structure_count = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSoftDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE diorama SET is_deleted = 1, delete_after = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfPurgeExpired = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM diorama WHERE is_deleted = 1 AND delete_after < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final DioramaEntity entity, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDioramaEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object rename(final String id, final String name, final long now,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRename.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, name);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, now);
        _argIndex = 3;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfRename.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object touch(final String id, final long now, final int structures,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfTouch.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, now);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, structures);
        _argIndex = 3;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfTouch.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object softDelete(final String id, final long purgeAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSoftDelete.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, purgeAt);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSoftDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object purgeExpired(final long now, final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfPurgeExpired.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, now);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfPurgeExpired.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DioramaEntity>> observeAll() {
    final String _sql = "SELECT * FROM diorama WHERE is_deleted = 0 ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"diorama"}, new Callable<List<DioramaEntity>>() {
      @Override
      @NonNull
      public List<DioramaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfGridSize = CursorUtil.getColumnIndexOrThrow(_cursor, "grid_size");
          final int _cursorIndexOfMasterSeed = CursorUtil.getColumnIndexOrThrow(_cursor, "master_seed");
          final int _cursorIndexOfGeneratorVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "generator_version");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSealedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "sealed_at");
          final int _cursorIndexOfStructureCount = CursorUtil.getColumnIndexOrThrow(_cursor, "structure_count");
          final int _cursorIndexOfCellCoverage = CursorUtil.getColumnIndexOrThrow(_cursor, "cell_coverage");
          final int _cursorIndexOfTimeOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "time_of_day");
          final int _cursorIndexOfThumbVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "thumb_version");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
          final int _cursorIndexOfDeleteAfter = CursorUtil.getColumnIndexOrThrow(_cursor, "delete_after");
          final List<DioramaEntity> _result = new ArrayList<DioramaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DioramaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpGridSize;
            _tmpGridSize = _cursor.getInt(_cursorIndexOfGridSize);
            final long _tmpMasterSeed;
            _tmpMasterSeed = _cursor.getLong(_cursorIndexOfMasterSeed);
            final int _tmpGeneratorVersion;
            _tmpGeneratorVersion = _cursor.getInt(_cursorIndexOfGeneratorVersion);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Long _tmpSealedAt;
            if (_cursor.isNull(_cursorIndexOfSealedAt)) {
              _tmpSealedAt = null;
            } else {
              _tmpSealedAt = _cursor.getLong(_cursorIndexOfSealedAt);
            }
            final int _tmpStructureCount;
            _tmpStructureCount = _cursor.getInt(_cursorIndexOfStructureCount);
            final float _tmpCellCoverage;
            _tmpCellCoverage = _cursor.getFloat(_cursorIndexOfCellCoverage);
            final String _tmpTimeOfDay;
            _tmpTimeOfDay = _cursor.getString(_cursorIndexOfTimeOfDay);
            final int _tmpThumbVersion;
            _tmpThumbVersion = _cursor.getInt(_cursorIndexOfThumbVersion);
            final boolean _tmpIsDeleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp != 0;
            final Long _tmpDeleteAfter;
            if (_cursor.isNull(_cursorIndexOfDeleteAfter)) {
              _tmpDeleteAfter = null;
            } else {
              _tmpDeleteAfter = _cursor.getLong(_cursorIndexOfDeleteAfter);
            }
            _item = new DioramaEntity(_tmpId,_tmpName,_tmpStatus,_tmpGridSize,_tmpMasterSeed,_tmpGeneratorVersion,_tmpCreatedAt,_tmpUpdatedAt,_tmpSealedAt,_tmpStructureCount,_tmpCellCoverage,_tmpTimeOfDay,_tmpThumbVersion,_tmpIsDeleted,_tmpDeleteAfter);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<DioramaEntity> observeLastDraft() {
    final String _sql = "SELECT * FROM diorama WHERE is_deleted = 0 AND status = 'DRAFT' ORDER BY updated_at DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"diorama"}, new Callable<DioramaEntity>() {
      @Override
      @Nullable
      public DioramaEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfGridSize = CursorUtil.getColumnIndexOrThrow(_cursor, "grid_size");
          final int _cursorIndexOfMasterSeed = CursorUtil.getColumnIndexOrThrow(_cursor, "master_seed");
          final int _cursorIndexOfGeneratorVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "generator_version");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSealedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "sealed_at");
          final int _cursorIndexOfStructureCount = CursorUtil.getColumnIndexOrThrow(_cursor, "structure_count");
          final int _cursorIndexOfCellCoverage = CursorUtil.getColumnIndexOrThrow(_cursor, "cell_coverage");
          final int _cursorIndexOfTimeOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "time_of_day");
          final int _cursorIndexOfThumbVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "thumb_version");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
          final int _cursorIndexOfDeleteAfter = CursorUtil.getColumnIndexOrThrow(_cursor, "delete_after");
          final DioramaEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpGridSize;
            _tmpGridSize = _cursor.getInt(_cursorIndexOfGridSize);
            final long _tmpMasterSeed;
            _tmpMasterSeed = _cursor.getLong(_cursorIndexOfMasterSeed);
            final int _tmpGeneratorVersion;
            _tmpGeneratorVersion = _cursor.getInt(_cursorIndexOfGeneratorVersion);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Long _tmpSealedAt;
            if (_cursor.isNull(_cursorIndexOfSealedAt)) {
              _tmpSealedAt = null;
            } else {
              _tmpSealedAt = _cursor.getLong(_cursorIndexOfSealedAt);
            }
            final int _tmpStructureCount;
            _tmpStructureCount = _cursor.getInt(_cursorIndexOfStructureCount);
            final float _tmpCellCoverage;
            _tmpCellCoverage = _cursor.getFloat(_cursorIndexOfCellCoverage);
            final String _tmpTimeOfDay;
            _tmpTimeOfDay = _cursor.getString(_cursorIndexOfTimeOfDay);
            final int _tmpThumbVersion;
            _tmpThumbVersion = _cursor.getInt(_cursorIndexOfThumbVersion);
            final boolean _tmpIsDeleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp != 0;
            final Long _tmpDeleteAfter;
            if (_cursor.isNull(_cursorIndexOfDeleteAfter)) {
              _tmpDeleteAfter = null;
            } else {
              _tmpDeleteAfter = _cursor.getLong(_cursorIndexOfDeleteAfter);
            }
            _result = new DioramaEntity(_tmpId,_tmpName,_tmpStatus,_tmpGridSize,_tmpMasterSeed,_tmpGeneratorVersion,_tmpCreatedAt,_tmpUpdatedAt,_tmpSealedAt,_tmpStructureCount,_tmpCellCoverage,_tmpTimeOfDay,_tmpThumbVersion,_tmpIsDeleted,_tmpDeleteAfter);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final String id, final Continuation<? super DioramaEntity> $completion) {
    final String _sql = "SELECT * FROM diorama WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DioramaEntity>() {
      @Override
      @Nullable
      public DioramaEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfGridSize = CursorUtil.getColumnIndexOrThrow(_cursor, "grid_size");
          final int _cursorIndexOfMasterSeed = CursorUtil.getColumnIndexOrThrow(_cursor, "master_seed");
          final int _cursorIndexOfGeneratorVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "generator_version");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSealedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "sealed_at");
          final int _cursorIndexOfStructureCount = CursorUtil.getColumnIndexOrThrow(_cursor, "structure_count");
          final int _cursorIndexOfCellCoverage = CursorUtil.getColumnIndexOrThrow(_cursor, "cell_coverage");
          final int _cursorIndexOfTimeOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "time_of_day");
          final int _cursorIndexOfThumbVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "thumb_version");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
          final int _cursorIndexOfDeleteAfter = CursorUtil.getColumnIndexOrThrow(_cursor, "delete_after");
          final DioramaEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpGridSize;
            _tmpGridSize = _cursor.getInt(_cursorIndexOfGridSize);
            final long _tmpMasterSeed;
            _tmpMasterSeed = _cursor.getLong(_cursorIndexOfMasterSeed);
            final int _tmpGeneratorVersion;
            _tmpGeneratorVersion = _cursor.getInt(_cursorIndexOfGeneratorVersion);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Long _tmpSealedAt;
            if (_cursor.isNull(_cursorIndexOfSealedAt)) {
              _tmpSealedAt = null;
            } else {
              _tmpSealedAt = _cursor.getLong(_cursorIndexOfSealedAt);
            }
            final int _tmpStructureCount;
            _tmpStructureCount = _cursor.getInt(_cursorIndexOfStructureCount);
            final float _tmpCellCoverage;
            _tmpCellCoverage = _cursor.getFloat(_cursorIndexOfCellCoverage);
            final String _tmpTimeOfDay;
            _tmpTimeOfDay = _cursor.getString(_cursorIndexOfTimeOfDay);
            final int _tmpThumbVersion;
            _tmpThumbVersion = _cursor.getInt(_cursorIndexOfThumbVersion);
            final boolean _tmpIsDeleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp != 0;
            final Long _tmpDeleteAfter;
            if (_cursor.isNull(_cursorIndexOfDeleteAfter)) {
              _tmpDeleteAfter = null;
            } else {
              _tmpDeleteAfter = _cursor.getLong(_cursorIndexOfDeleteAfter);
            }
            _result = new DioramaEntity(_tmpId,_tmpName,_tmpStatus,_tmpGridSize,_tmpMasterSeed,_tmpGeneratorVersion,_tmpCreatedAt,_tmpUpdatedAt,_tmpSealedAt,_tmpStructureCount,_tmpCellCoverage,_tmpTimeOfDay,_tmpThumbVersion,_tmpIsDeleted,_tmpDeleteAfter);
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
