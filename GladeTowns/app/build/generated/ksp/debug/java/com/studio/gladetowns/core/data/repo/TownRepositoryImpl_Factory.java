package com.studio.gladetowns.core.data.repo;

import com.studio.gladetowns.core.data.blob.TownBlobStore;
import com.studio.gladetowns.core.data.db.dao.DioramaDao;
import com.studio.gladetowns.core.data.db.dao.TownBlobDao;
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
public final class TownRepositoryImpl_Factory implements Factory<TownRepositoryImpl> {
  private final Provider<TownBlobStore> blobStoreProvider;

  private final Provider<TownBlobDao> blobDaoProvider;

  private final Provider<DioramaDao> dioramaDaoProvider;

  public TownRepositoryImpl_Factory(Provider<TownBlobStore> blobStoreProvider,
      Provider<TownBlobDao> blobDaoProvider, Provider<DioramaDao> dioramaDaoProvider) {
    this.blobStoreProvider = blobStoreProvider;
    this.blobDaoProvider = blobDaoProvider;
    this.dioramaDaoProvider = dioramaDaoProvider;
  }

  @Override
  public TownRepositoryImpl get() {
    return newInstance(blobStoreProvider.get(), blobDaoProvider.get(), dioramaDaoProvider.get());
  }

  public static TownRepositoryImpl_Factory create(Provider<TownBlobStore> blobStoreProvider,
      Provider<TownBlobDao> blobDaoProvider, Provider<DioramaDao> dioramaDaoProvider) {
    return new TownRepositoryImpl_Factory(blobStoreProvider, blobDaoProvider, dioramaDaoProvider);
  }

  public static TownRepositoryImpl newInstance(TownBlobStore blobStore, TownBlobDao blobDao,
      DioramaDao dioramaDao) {
    return new TownRepositoryImpl(blobStore, blobDao, dioramaDao);
  }
}
