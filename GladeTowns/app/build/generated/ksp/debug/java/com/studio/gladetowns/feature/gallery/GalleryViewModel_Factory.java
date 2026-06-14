package com.studio.gladetowns.feature.gallery;

import com.studio.gladetowns.core.domain.usecase.DeleteDioramaUseCase;
import com.studio.gladetowns.core.domain.usecase.ListDioramasUseCase;
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
public final class GalleryViewModel_Factory implements Factory<GalleryViewModel> {
  private final Provider<ListDioramasUseCase> listDioramasProvider;

  private final Provider<DeleteDioramaUseCase> deleteDioramaProvider;

  private final Provider<LoadTownLayoutUseCase> loadLayoutProvider;

  public GalleryViewModel_Factory(Provider<ListDioramasUseCase> listDioramasProvider,
      Provider<DeleteDioramaUseCase> deleteDioramaProvider,
      Provider<LoadTownLayoutUseCase> loadLayoutProvider) {
    this.listDioramasProvider = listDioramasProvider;
    this.deleteDioramaProvider = deleteDioramaProvider;
    this.loadLayoutProvider = loadLayoutProvider;
  }

  @Override
  public GalleryViewModel get() {
    return newInstance(listDioramasProvider.get(), deleteDioramaProvider.get(), loadLayoutProvider.get());
  }

  public static GalleryViewModel_Factory create(Provider<ListDioramasUseCase> listDioramasProvider,
      Provider<DeleteDioramaUseCase> deleteDioramaProvider,
      Provider<LoadTownLayoutUseCase> loadLayoutProvider) {
    return new GalleryViewModel_Factory(listDioramasProvider, deleteDioramaProvider, loadLayoutProvider);
  }

  public static GalleryViewModel newInstance(ListDioramasUseCase listDioramas,
      DeleteDioramaUseCase deleteDiorama, LoadTownLayoutUseCase loadLayout) {
    return new GalleryViewModel(listDioramas, deleteDiorama, loadLayout);
  }
}
