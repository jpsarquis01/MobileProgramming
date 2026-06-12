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
public final class ListDioramasUseCase_Factory implements Factory<ListDioramasUseCase> {
  private final Provider<DioramaRepository> dioramasProvider;

  public ListDioramasUseCase_Factory(Provider<DioramaRepository> dioramasProvider) {
    this.dioramasProvider = dioramasProvider;
  }

  @Override
  public ListDioramasUseCase get() {
    return newInstance(dioramasProvider.get());
  }

  public static ListDioramasUseCase_Factory create(Provider<DioramaRepository> dioramasProvider) {
    return new ListDioramasUseCase_Factory(dioramasProvider);
  }

  public static ListDioramasUseCase newInstance(DioramaRepository dioramas) {
    return new ListDioramasUseCase(dioramas);
  }
}
