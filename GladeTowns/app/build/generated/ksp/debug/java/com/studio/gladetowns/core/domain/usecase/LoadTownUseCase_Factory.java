package com.studio.gladetowns.core.domain.usecase;

import com.studio.gladetowns.core.domain.repository.DioramaRepository;
import com.studio.gladetowns.core.domain.repository.TownRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class LoadTownUseCase_Factory implements Factory<LoadTownUseCase> {
  private final Provider<DioramaRepository> dioramasProvider;

  private final Provider<TownRepository> townsProvider;

  public LoadTownUseCase_Factory(Provider<DioramaRepository> dioramasProvider,
      Provider<TownRepository> townsProvider) {
    this.dioramasProvider = dioramasProvider;
    this.townsProvider = townsProvider;
  }

  @Override
  public LoadTownUseCase get() {
    return newInstance(dioramasProvider.get(), townsProvider.get());
  }

  public static LoadTownUseCase_Factory create(Provider<DioramaRepository> dioramasProvider,
      Provider<TownRepository> townsProvider) {
    return new LoadTownUseCase_Factory(dioramasProvider, townsProvider);
  }

  public static LoadTownUseCase newInstance(DioramaRepository dioramas, TownRepository towns) {
    return new LoadTownUseCase(dioramas, towns);
  }
}
