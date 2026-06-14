package com.studio.gladetowns.feature.play;

import androidx.lifecycle.SavedStateHandle;
import com.studio.gladetowns.core.domain.repository.TownRepository;
import com.studio.gladetowns.core.domain.usecase.CreateTownUseCase;
import com.studio.gladetowns.core.domain.usecase.DrawShapeUseCase;
import com.studio.gladetowns.core.domain.usecase.LoadTownUseCase;
import com.studio.gladetowns.core.domain.usecase.RenameTownUseCase;
import com.studio.gladetowns.core.domain.usecase.SealTownUseCase;
import com.studio.gladetowns.core.engine.SceneCommandSink;
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
public final class PlayViewModel_Factory implements Factory<PlayViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<CreateTownUseCase> createTownProvider;

  private final Provider<LoadTownUseCase> loadTownProvider;

  private final Provider<RenameTownUseCase> renameTownProvider;

  private final Provider<SealTownUseCase> sealTownProvider;

  private final Provider<DrawShapeUseCase> drawShapeProvider;

  private final Provider<TownRepository> townsProvider;

  private final Provider<SceneCommandSink> sceneProvider;

  public PlayViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<CreateTownUseCase> createTownProvider, Provider<LoadTownUseCase> loadTownProvider,
      Provider<RenameTownUseCase> renameTownProvider, Provider<SealTownUseCase> sealTownProvider,
      Provider<DrawShapeUseCase> drawShapeProvider, Provider<TownRepository> townsProvider,
      Provider<SceneCommandSink> sceneProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.createTownProvider = createTownProvider;
    this.loadTownProvider = loadTownProvider;
    this.renameTownProvider = renameTownProvider;
    this.sealTownProvider = sealTownProvider;
    this.drawShapeProvider = drawShapeProvider;
    this.townsProvider = townsProvider;
    this.sceneProvider = sceneProvider;
  }

  @Override
  public PlayViewModel get() {
    return newInstance(savedStateHandleProvider.get(), createTownProvider.get(), loadTownProvider.get(), renameTownProvider.get(), sealTownProvider.get(), drawShapeProvider.get(), townsProvider.get(), sceneProvider.get());
  }

  public static PlayViewModel_Factory create(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<CreateTownUseCase> createTownProvider, Provider<LoadTownUseCase> loadTownProvider,
      Provider<RenameTownUseCase> renameTownProvider, Provider<SealTownUseCase> sealTownProvider,
      Provider<DrawShapeUseCase> drawShapeProvider, Provider<TownRepository> townsProvider,
      Provider<SceneCommandSink> sceneProvider) {
    return new PlayViewModel_Factory(savedStateHandleProvider, createTownProvider, loadTownProvider, renameTownProvider, sealTownProvider, drawShapeProvider, townsProvider, sceneProvider);
  }

  public static PlayViewModel newInstance(SavedStateHandle savedStateHandle,
      CreateTownUseCase createTown, LoadTownUseCase loadTown, RenameTownUseCase renameTown,
      SealTownUseCase sealTown, DrawShapeUseCase drawShape, TownRepository towns,
      SceneCommandSink scene) {
    return new PlayViewModel(savedStateHandle, createTown, loadTown, renameTown, sealTown, drawShape, towns, scene);
  }
}
