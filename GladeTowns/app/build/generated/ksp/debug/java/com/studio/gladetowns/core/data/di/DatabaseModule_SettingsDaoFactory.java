package com.studio.gladetowns.core.data.di;

import com.studio.gladetowns.core.data.db.GladeDatabase;
import com.studio.gladetowns.core.data.db.dao.SettingsDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class DatabaseModule_SettingsDaoFactory implements Factory<SettingsDao> {
  private final Provider<GladeDatabase> dbProvider;

  public DatabaseModule_SettingsDaoFactory(Provider<GladeDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SettingsDao get() {
    return settingsDao(dbProvider.get());
  }

  public static DatabaseModule_SettingsDaoFactory create(Provider<GladeDatabase> dbProvider) {
    return new DatabaseModule_SettingsDaoFactory(dbProvider);
  }

  public static SettingsDao settingsDao(GladeDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.settingsDao(db));
  }
}
