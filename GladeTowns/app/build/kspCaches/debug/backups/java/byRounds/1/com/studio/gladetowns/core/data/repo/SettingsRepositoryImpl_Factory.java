package com.studio.gladetowns.core.data.repo;

import com.studio.gladetowns.core.data.db.dao.SettingsDao;
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
public final class SettingsRepositoryImpl_Factory implements Factory<SettingsRepositoryImpl> {
  private final Provider<SettingsDao> daoProvider;

  public SettingsRepositoryImpl_Factory(Provider<SettingsDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public SettingsRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static SettingsRepositoryImpl_Factory create(Provider<SettingsDao> daoProvider) {
    return new SettingsRepositoryImpl_Factory(daoProvider);
  }

  public static SettingsRepositoryImpl newInstance(SettingsDao dao) {
    return new SettingsRepositoryImpl(dao);
  }
}
