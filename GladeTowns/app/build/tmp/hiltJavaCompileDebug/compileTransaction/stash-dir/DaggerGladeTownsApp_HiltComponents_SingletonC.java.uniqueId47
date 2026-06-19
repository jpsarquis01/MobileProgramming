package com.studio.gladetowns;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.studio.gladetowns.core.data.blob.TownBlobStore;
import com.studio.gladetowns.core.data.db.GladeDatabase;
import com.studio.gladetowns.core.data.db.dao.DioramaDao;
import com.studio.gladetowns.core.data.db.dao.TownBlobDao;
import com.studio.gladetowns.core.data.di.DatabaseModule_DioramaDaoFactory;
import com.studio.gladetowns.core.data.di.DatabaseModule_FilesDirFactory;
import com.studio.gladetowns.core.data.di.DatabaseModule_IoDispatcherFactory;
import com.studio.gladetowns.core.data.di.DatabaseModule_ProvideDatabaseFactory;
import com.studio.gladetowns.core.data.di.DatabaseModule_TownBlobDaoFactory;
import com.studio.gladetowns.core.data.repo.DioramaRepositoryImpl;
import com.studio.gladetowns.core.data.repo.TownRepositoryImpl;
import com.studio.gladetowns.core.domain.usecase.CreateTownUseCase;
import com.studio.gladetowns.core.domain.usecase.DeleteDioramaUseCase;
import com.studio.gladetowns.core.domain.usecase.DrawShapeUseCase;
import com.studio.gladetowns.core.domain.usecase.ListDioramasUseCase;
import com.studio.gladetowns.core.domain.usecase.LoadTownLayoutUseCase;
import com.studio.gladetowns.core.domain.usecase.LoadTownUseCase;
import com.studio.gladetowns.core.domain.usecase.RenameTownUseCase;
import com.studio.gladetowns.core.domain.usecase.SealTownUseCase;
import com.studio.gladetowns.core.engine.PlaceholderEngineController;
import com.studio.gladetowns.feature.explore.ExploreViewModel;
import com.studio.gladetowns.feature.explore.ExploreViewModel_HiltModules;
import com.studio.gladetowns.feature.gallery.GalleryViewModel;
import com.studio.gladetowns.feature.gallery.GalleryViewModel_HiltModules;
import com.studio.gladetowns.feature.menu.MenuViewModel;
import com.studio.gladetowns.feature.menu.MenuViewModel_HiltModules;
import com.studio.gladetowns.feature.play.PlayViewModel;
import com.studio.gladetowns.feature.play.PlayViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerGladeTownsApp_HiltComponents_SingletonC {
  private DaggerGladeTownsApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public GladeTownsApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements GladeTownsApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public GladeTownsApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements GladeTownsApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public GladeTownsApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements GladeTownsApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public GladeTownsApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements GladeTownsApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public GladeTownsApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements GladeTownsApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public GladeTownsApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements GladeTownsApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public GladeTownsApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements GladeTownsApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public GladeTownsApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends GladeTownsApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends GladeTownsApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends GladeTownsApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends GladeTownsApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(4).put(LazyClassKeyProvider.com_studio_gladetowns_feature_explore_ExploreViewModel, ExploreViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_studio_gladetowns_feature_gallery_GalleryViewModel, GalleryViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_studio_gladetowns_feature_menu_MenuViewModel, MenuViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_studio_gladetowns_feature_play_PlayViewModel, PlayViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_studio_gladetowns_feature_menu_MenuViewModel = "com.studio.gladetowns.feature.menu.MenuViewModel";

      static String com_studio_gladetowns_feature_play_PlayViewModel = "com.studio.gladetowns.feature.play.PlayViewModel";

      static String com_studio_gladetowns_feature_explore_ExploreViewModel = "com.studio.gladetowns.feature.explore.ExploreViewModel";

      static String com_studio_gladetowns_feature_gallery_GalleryViewModel = "com.studio.gladetowns.feature.gallery.GalleryViewModel";

      @KeepFieldType
      MenuViewModel com_studio_gladetowns_feature_menu_MenuViewModel2;

      @KeepFieldType
      PlayViewModel com_studio_gladetowns_feature_play_PlayViewModel2;

      @KeepFieldType
      ExploreViewModel com_studio_gladetowns_feature_explore_ExploreViewModel2;

      @KeepFieldType
      GalleryViewModel com_studio_gladetowns_feature_gallery_GalleryViewModel2;
    }
  }

  private static final class ViewModelCImpl extends GladeTownsApp_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<ExploreViewModel> exploreViewModelProvider;

    private Provider<GalleryViewModel> galleryViewModelProvider;

    private Provider<MenuViewModel> menuViewModelProvider;

    private Provider<PlayViewModel> playViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private LoadTownLayoutUseCase loadTownLayoutUseCase() {
      return new LoadTownLayoutUseCase(singletonCImpl.dioramaRepositoryImplProvider.get(), singletonCImpl.townRepositoryImplProvider.get());
    }

    private ListDioramasUseCase listDioramasUseCase() {
      return new ListDioramasUseCase(singletonCImpl.dioramaRepositoryImplProvider.get());
    }

    private DeleteDioramaUseCase deleteDioramaUseCase() {
      return new DeleteDioramaUseCase(singletonCImpl.dioramaRepositoryImplProvider.get());
    }

    private CreateTownUseCase createTownUseCase() {
      return new CreateTownUseCase(singletonCImpl.dioramaRepositoryImplProvider.get());
    }

    private LoadTownUseCase loadTownUseCase() {
      return new LoadTownUseCase(singletonCImpl.dioramaRepositoryImplProvider.get(), singletonCImpl.townRepositoryImplProvider.get());
    }

    private RenameTownUseCase renameTownUseCase() {
      return new RenameTownUseCase(singletonCImpl.dioramaRepositoryImplProvider.get());
    }

    private SealTownUseCase sealTownUseCase() {
      return new SealTownUseCase(singletonCImpl.dioramaRepositoryImplProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.exploreViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.galleryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.menuViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.playViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(4).put(LazyClassKeyProvider.com_studio_gladetowns_feature_explore_ExploreViewModel, ((Provider) exploreViewModelProvider)).put(LazyClassKeyProvider.com_studio_gladetowns_feature_gallery_GalleryViewModel, ((Provider) galleryViewModelProvider)).put(LazyClassKeyProvider.com_studio_gladetowns_feature_menu_MenuViewModel, ((Provider) menuViewModelProvider)).put(LazyClassKeyProvider.com_studio_gladetowns_feature_play_PlayViewModel, ((Provider) playViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_studio_gladetowns_feature_gallery_GalleryViewModel = "com.studio.gladetowns.feature.gallery.GalleryViewModel";

      static String com_studio_gladetowns_feature_menu_MenuViewModel = "com.studio.gladetowns.feature.menu.MenuViewModel";

      static String com_studio_gladetowns_feature_explore_ExploreViewModel = "com.studio.gladetowns.feature.explore.ExploreViewModel";

      static String com_studio_gladetowns_feature_play_PlayViewModel = "com.studio.gladetowns.feature.play.PlayViewModel";

      @KeepFieldType
      GalleryViewModel com_studio_gladetowns_feature_gallery_GalleryViewModel2;

      @KeepFieldType
      MenuViewModel com_studio_gladetowns_feature_menu_MenuViewModel2;

      @KeepFieldType
      ExploreViewModel com_studio_gladetowns_feature_explore_ExploreViewModel2;

      @KeepFieldType
      PlayViewModel com_studio_gladetowns_feature_play_PlayViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.studio.gladetowns.feature.explore.ExploreViewModel 
          return (T) new ExploreViewModel(viewModelCImpl.savedStateHandle, viewModelCImpl.loadTownLayoutUseCase());

          case 1: // com.studio.gladetowns.feature.gallery.GalleryViewModel 
          return (T) new GalleryViewModel(viewModelCImpl.listDioramasUseCase(), viewModelCImpl.deleteDioramaUseCase(), viewModelCImpl.loadTownLayoutUseCase());

          case 2: // com.studio.gladetowns.feature.menu.MenuViewModel 
          return (T) new MenuViewModel(viewModelCImpl.listDioramasUseCase(), singletonCImpl.dioramaRepositoryImplProvider.get());

          case 3: // com.studio.gladetowns.feature.play.PlayViewModel 
          return (T) new PlayViewModel(viewModelCImpl.savedStateHandle, viewModelCImpl.createTownUseCase(), viewModelCImpl.loadTownUseCase(), viewModelCImpl.renameTownUseCase(), viewModelCImpl.sealTownUseCase(), new DrawShapeUseCase(), singletonCImpl.townRepositoryImplProvider.get(), singletonCImpl.placeholderEngineControllerProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends GladeTownsApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends GladeTownsApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends GladeTownsApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<GladeDatabase> provideDatabaseProvider;

    private Provider<DioramaRepositoryImpl> dioramaRepositoryImplProvider;

    private Provider<TownBlobStore> townBlobStoreProvider;

    private Provider<TownRepositoryImpl> townRepositoryImplProvider;

    private Provider<PlaceholderEngineController> placeholderEngineControllerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private DioramaDao dioramaDao() {
      return DatabaseModule_DioramaDaoFactory.dioramaDao(provideDatabaseProvider.get());
    }

    private File namedFile() {
      return DatabaseModule_FilesDirFactory.filesDir(ApplicationContextModule_ProvideContextFactory.provideContext(applicationContextModule));
    }

    private TownBlobDao townBlobDao() {
      return DatabaseModule_TownBlobDaoFactory.townBlobDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<GladeDatabase>(singletonCImpl, 1));
      this.dioramaRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<DioramaRepositoryImpl>(singletonCImpl, 0));
      this.townBlobStoreProvider = DoubleCheck.provider(new SwitchingProvider<TownBlobStore>(singletonCImpl, 3));
      this.townRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<TownRepositoryImpl>(singletonCImpl, 2));
      this.placeholderEngineControllerProvider = DoubleCheck.provider(new SwitchingProvider<PlaceholderEngineController>(singletonCImpl, 4));
    }

    @Override
    public void injectGladeTownsApp(GladeTownsApp gladeTownsApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.studio.gladetowns.core.data.repo.DioramaRepositoryImpl 
          return (T) new DioramaRepositoryImpl(singletonCImpl.dioramaDao());

          case 1: // com.studio.gladetowns.core.data.db.GladeDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.studio.gladetowns.core.data.repo.TownRepositoryImpl 
          return (T) new TownRepositoryImpl(singletonCImpl.townBlobStoreProvider.get(), singletonCImpl.townBlobDao(), singletonCImpl.dioramaDao());

          case 3: // com.studio.gladetowns.core.data.blob.TownBlobStore 
          return (T) new TownBlobStore(singletonCImpl.namedFile(), DatabaseModule_IoDispatcherFactory.ioDispatcher());

          case 4: // com.studio.gladetowns.core.engine.PlaceholderEngineController 
          return (T) new PlaceholderEngineController();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
