package com.studio.gladetowns.core.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.studio.gladetowns.core.data.db.dao.DioramaDao;
import com.studio.gladetowns.core.data.db.dao.DioramaDao_Impl;
import com.studio.gladetowns.core.data.db.dao.SettingsDao;
import com.studio.gladetowns.core.data.db.dao.SettingsDao_Impl;
import com.studio.gladetowns.core.data.db.dao.TownBlobDao;
import com.studio.gladetowns.core.data.db.dao.TownBlobDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GladeDatabase_Impl extends GladeDatabase {
  private volatile DioramaDao _dioramaDao;

  private volatile TownBlobDao _townBlobDao;

  private volatile SettingsDao _settingsDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `diorama` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `status` TEXT NOT NULL, `grid_size` INTEGER NOT NULL, `master_seed` INTEGER NOT NULL, `generator_version` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `sealed_at` INTEGER, `structure_count` INTEGER NOT NULL, `cell_coverage` REAL NOT NULL, `time_of_day` TEXT NOT NULL, `thumb_version` INTEGER NOT NULL, `is_deleted` INTEGER NOT NULL, `delete_after` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_diorama_status_sealed_at` ON `diorama` (`status`, `sealed_at`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_diorama_is_deleted_delete_after` ON `diorama` (`is_deleted`, `delete_after`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_diorama_updated_at` ON `diorama` (`updated_at`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `town_blob` (`diorama_id` TEXT NOT NULL, `log_relative_path` TEXT NOT NULL, `log_crc` INTEGER NOT NULL, `blob_version` INTEGER NOT NULL, `size_bytes` INTEGER NOT NULL, PRIMARY KEY(`diorama_id`), FOREIGN KEY(`diorama_id`) REFERENCES `diorama`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `settings` (`key` TEXT NOT NULL, `value` TEXT NOT NULL, PRIMARY KEY(`key`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'fabed339d8c9393a034d1b732bc5dcc5')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `diorama`");
        db.execSQL("DROP TABLE IF EXISTS `town_blob`");
        db.execSQL("DROP TABLE IF EXISTS `settings`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsDiorama = new HashMap<String, TableInfo.Column>(15);
        _columnsDiorama.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiorama.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiorama.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiorama.put("grid_size", new TableInfo.Column("grid_size", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiorama.put("master_seed", new TableInfo.Column("master_seed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiorama.put("generator_version", new TableInfo.Column("generator_version", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiorama.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiorama.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiorama.put("sealed_at", new TableInfo.Column("sealed_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiorama.put("structure_count", new TableInfo.Column("structure_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiorama.put("cell_coverage", new TableInfo.Column("cell_coverage", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiorama.put("time_of_day", new TableInfo.Column("time_of_day", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiorama.put("thumb_version", new TableInfo.Column("thumb_version", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiorama.put("is_deleted", new TableInfo.Column("is_deleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiorama.put("delete_after", new TableInfo.Column("delete_after", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDiorama = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDiorama = new HashSet<TableInfo.Index>(3);
        _indicesDiorama.add(new TableInfo.Index("index_diorama_status_sealed_at", false, Arrays.asList("status", "sealed_at"), Arrays.asList("ASC", "ASC")));
        _indicesDiorama.add(new TableInfo.Index("index_diorama_is_deleted_delete_after", false, Arrays.asList("is_deleted", "delete_after"), Arrays.asList("ASC", "ASC")));
        _indicesDiorama.add(new TableInfo.Index("index_diorama_updated_at", false, Arrays.asList("updated_at"), Arrays.asList("ASC")));
        final TableInfo _infoDiorama = new TableInfo("diorama", _columnsDiorama, _foreignKeysDiorama, _indicesDiorama);
        final TableInfo _existingDiorama = TableInfo.read(db, "diorama");
        if (!_infoDiorama.equals(_existingDiorama)) {
          return new RoomOpenHelper.ValidationResult(false, "diorama(com.studio.gladetowns.core.data.db.entity.DioramaEntity).\n"
                  + " Expected:\n" + _infoDiorama + "\n"
                  + " Found:\n" + _existingDiorama);
        }
        final HashMap<String, TableInfo.Column> _columnsTownBlob = new HashMap<String, TableInfo.Column>(5);
        _columnsTownBlob.put("diorama_id", new TableInfo.Column("diorama_id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTownBlob.put("log_relative_path", new TableInfo.Column("log_relative_path", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTownBlob.put("log_crc", new TableInfo.Column("log_crc", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTownBlob.put("blob_version", new TableInfo.Column("blob_version", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTownBlob.put("size_bytes", new TableInfo.Column("size_bytes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTownBlob = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysTownBlob.add(new TableInfo.ForeignKey("diorama", "CASCADE", "NO ACTION", Arrays.asList("diorama_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTownBlob = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTownBlob = new TableInfo("town_blob", _columnsTownBlob, _foreignKeysTownBlob, _indicesTownBlob);
        final TableInfo _existingTownBlob = TableInfo.read(db, "town_blob");
        if (!_infoTownBlob.equals(_existingTownBlob)) {
          return new RoomOpenHelper.ValidationResult(false, "town_blob(com.studio.gladetowns.core.data.db.entity.TownBlobEntity).\n"
                  + " Expected:\n" + _infoTownBlob + "\n"
                  + " Found:\n" + _existingTownBlob);
        }
        final HashMap<String, TableInfo.Column> _columnsSettings = new HashMap<String, TableInfo.Column>(2);
        _columnsSettings.put("key", new TableInfo.Column("key", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSettings.put("value", new TableInfo.Column("value", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSettings = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSettings = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSettings = new TableInfo("settings", _columnsSettings, _foreignKeysSettings, _indicesSettings);
        final TableInfo _existingSettings = TableInfo.read(db, "settings");
        if (!_infoSettings.equals(_existingSettings)) {
          return new RoomOpenHelper.ValidationResult(false, "settings(com.studio.gladetowns.core.data.db.entity.SettingsEntity).\n"
                  + " Expected:\n" + _infoSettings + "\n"
                  + " Found:\n" + _existingSettings);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "fabed339d8c9393a034d1b732bc5dcc5", "e8e61edbcf59e300113b13016bf5c146");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "diorama","town_blob","settings");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `diorama`");
      _db.execSQL("DELETE FROM `town_blob`");
      _db.execSQL("DELETE FROM `settings`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(DioramaDao.class, DioramaDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TownBlobDao.class, TownBlobDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SettingsDao.class, SettingsDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public DioramaDao dioramaDao() {
    if (_dioramaDao != null) {
      return _dioramaDao;
    } else {
      synchronized(this) {
        if(_dioramaDao == null) {
          _dioramaDao = new DioramaDao_Impl(this);
        }
        return _dioramaDao;
      }
    }
  }

  @Override
  public TownBlobDao townBlobDao() {
    if (_townBlobDao != null) {
      return _townBlobDao;
    } else {
      synchronized(this) {
        if(_townBlobDao == null) {
          _townBlobDao = new TownBlobDao_Impl(this);
        }
        return _townBlobDao;
      }
    }
  }

  @Override
  public SettingsDao settingsDao() {
    if (_settingsDao != null) {
      return _settingsDao;
    } else {
      synchronized(this) {
        if(_settingsDao == null) {
          _settingsDao = new SettingsDao_Impl(this);
        }
        return _settingsDao;
      }
    }
  }
}
