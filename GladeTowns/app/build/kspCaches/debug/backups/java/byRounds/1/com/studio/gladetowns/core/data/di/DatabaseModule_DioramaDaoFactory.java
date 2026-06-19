package com.studio.gladetowns.core.data.di;

import com.studio.gladetowns.core.data.db.GladeDatabase;
import com.studio.gladetowns.core.data.db.dao.DioramaDao;
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
public final class DatabaseModule_DioramaDaoFactory implements Factory<DioramaDao> {
  private final Provider<GladeDatabase> dbProvider;

  public DatabaseModule_DioramaDaoFactory(Provider<GladeDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public DioramaDao get() {
    return dioramaDao(dbProvider.get());
  }

  public static DatabaseModule_DioramaDaoFactory create(Provider<GladeDatabase> dbProvider) {
    return new DatabaseModule_DioramaDaoFactory(dbProvider);
  }

  public static DioramaDao dioramaDao(GladeDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.dioramaDao(db));
  }
}
