package com.studio.gladetowns.core.domain.usecase;

import com.studio.gladetowns.core.domain.repository.DioramaRepository;
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
public final class CreateTownUseCase_Factory implements Factory<CreateTownUseCase> {
  private final Provider<DioramaRepository> dioramasProvider;

  public CreateTownUseCase_Factory(Provider<DioramaRepository> dioramasProvider) {
    this.dioramasProvider = dioramasProvider;
  }

  @Override
  public CreateTownUseCase get() {
    return newInstance(dioramasProvider.get());
  }

  public static CreateTownUseCase_Factory create(Provider<DioramaRepository> dioramasProvider) {
    return new CreateTownUseCase_Factory(dioramasProvider);
  }

  public static CreateTownUseCase newInstance(DioramaRepository dioramas) {
    return new CreateTownUseCase(dioramas);
  }
}
