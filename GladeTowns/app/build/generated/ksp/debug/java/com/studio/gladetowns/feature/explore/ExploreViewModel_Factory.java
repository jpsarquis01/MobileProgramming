package com.studio.gladetowns.feature.explore;

import androidx.lifecycle.SavedStateHandle;
import com.studio.gladetowns.core.domain.usecase.LoadTownLayoutUseCase;
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
public final class ExploreViewModel_Factory implements Factory<ExploreViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<LoadTownLayoutUseCase> loadLayoutProvider;

  public ExploreViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<LoadTownLayoutUseCase> loadLayoutProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.loadLayoutProvider = loadLayoutProvider;
  }

  @Override
  public ExploreViewModel get() {
    return newInstance(savedStateHandleProvider.get(), loadLayoutProvider.get());
  }

  public static ExploreViewModel_Factory create(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<LoadTownLayoutUseCase> loadLayoutProvider) {
    return new ExploreViewModel_Factory(savedStateHandleProvider, loadLayoutProvider);
  }

  public static ExploreViewModel newInstance(SavedStateHandle savedStateHandle,
      LoadTownLayoutUseCase loadLayout) {
    return new ExploreViewModel(savedStateHandle, loadLayout);
  }
}
