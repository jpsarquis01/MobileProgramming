package com.studio.gladetowns.feature.menu;

import com.studio.gladetowns.core.domain.repository.DioramaRepository;
import com.studio.gladetowns.core.domain.usecase.ListDioramasUseCase;
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
public final class MenuViewModel_Factory implements Factory<MenuViewModel> {
  private final Provider<ListDioramasUseCase> listDioramasProvider;

  private final Provider<DioramaRepository> dioramasProvider;

  public MenuViewModel_Factory(Provider<ListDioramasUseCase> listDioramasProvider,
      Provider<DioramaRepository> dioramasProvider) {
    this.listDioramasProvider = listDioramasProvider;
    this.dioramasProvider = dioramasProvider;
  }

  @Override
  public MenuViewModel get() {
    return newInstance(listDioramasProvider.get(), dioramasProvider.get());
  }

  public static MenuViewModel_Factory create(Provider<ListDioramasUseCase> listDioramasProvider,
      Provider<DioramaRepository> dioramasProvider) {
    return new MenuViewModel_Factory(listDioramasProvider, dioramasProvider);
  }

  public static MenuViewModel newInstance(ListDioramasUseCase listDioramas,
      DioramaRepository dioramas) {
    return new MenuViewModel(listDioramas, dioramas);
  }
}
