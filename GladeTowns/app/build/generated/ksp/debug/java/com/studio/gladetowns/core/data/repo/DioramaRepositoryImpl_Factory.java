package com.studio.gladetowns.core.data.repo;

import com.studio.gladetowns.core.data.db.dao.DioramaDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class DioramaRepositoryImpl_Factory implements Factory<DioramaRepositoryImpl> {
  private final Provider<DioramaDao> daoProvider;

  public DioramaRepositoryImpl_Factory(Provider<DioramaDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public DioramaRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static DioramaRepositoryImpl_Factory create(Provider<DioramaDao> daoProvider) {
    return new DioramaRepositoryImpl_Factory(daoProvider);
  }

  public static DioramaRepositoryImpl newInstance(DioramaDao dao) {
    return new DioramaRepositoryImpl(dao);
  }
}
