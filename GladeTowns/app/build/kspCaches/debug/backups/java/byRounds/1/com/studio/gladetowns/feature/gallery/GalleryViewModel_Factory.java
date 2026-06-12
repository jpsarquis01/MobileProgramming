package com.studio.gladetowns.feature.gallery;

import com.studio.gladetowns.core.domain.usecase.DeleteDioramaUseCase;
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
public final class GalleryViewModel_Factory implements Factory<GalleryViewModel> {
  private final Provider<ListDioramasUseCase> listDioramasProvider;

  private final Provider<DeleteDioramaUseCase> deleteDioramaProvider;

  public GalleryViewModel_Factory(Provider<ListDioramasUseCase> listDioramasProvider,
      Provider<DeleteDioramaUseCase> deleteDioramaProvider) {
    this.listDioramasProvider = listDioramasProvider;
    this.deleteDioramaProvider = deleteDioramaProvider;
  }

  @Override
  public GalleryViewModel get() {
    return newInstance(listDioramasProvider.get(), deleteDioramaProvider.get());
  }

  public static GalleryViewModel_Factory create(Provider<ListDioramasUseCase> listDioramasProvider,
      Provider<DeleteDioramaUseCase> deleteDioramaProvider) {
    return new GalleryViewModel_Factory(listDioramasProvider, deleteDioramaProvider);
  }

  public static GalleryViewModel newInstance(ListDioramasUseCase listDioramas,
      DeleteDioramaUseCase deleteDiorama) {
    return new GalleryViewModel(listDioramas, deleteDiorama);
  }
}
