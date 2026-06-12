package com.studio.gladetowns.core.data.di;

import com.studio.gladetowns.core.data.db.GladeDatabase;
import com.studio.gladetowns.core.data.db.dao.TownBlobDao;
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
public final class DatabaseModule_TownBlobDaoFactory implements Factory<TownBlobDao> {
  private final Provider<GladeDatabase> dbProvider;

  public DatabaseModule_TownBlobDaoFactory(Provider<GladeDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TownBlobDao get() {
    return townBlobDao(dbProvider.get());
  }

  public static DatabaseModule_TownBlobDaoFactory create(Provider<GladeDatabase> dbProvider) {
    return new DatabaseModule_TownBlobDaoFactory(dbProvider);
  }

  public static TownBlobDao townBlobDao(GladeDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.townBlobDao(db));
  }
}
