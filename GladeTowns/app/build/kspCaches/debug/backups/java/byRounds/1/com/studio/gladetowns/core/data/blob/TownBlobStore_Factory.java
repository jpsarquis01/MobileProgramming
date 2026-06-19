package com.studio.gladetowns.core.data.blob;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.io.File;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineDispatcher;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("javax.inject.Named")
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
public final class TownBlobStore_Factory implements Factory<TownBlobStore> {
  private final Provider<File> filesDirProvider;

  private final Provider<CoroutineDispatcher> ioProvider;

  public TownBlobStore_Factory(Provider<File> filesDirProvider,
      Provider<CoroutineDispatcher> ioProvider) {
    this.filesDirProvider = filesDirProvider;
    this.ioProvider = ioProvider;
  }

  @Override
  public TownBlobStore get() {
    return newInstance(filesDirProvider.get(), ioProvider.get());
  }

  public static TownBlobStore_Factory create(Provider<File> filesDirProvider,
      Provider<CoroutineDispatcher> ioProvider) {
    return new TownBlobStore_Factory(filesDirProvider, ioProvider);
  }

  public static TownBlobStore newInstance(File filesDir, CoroutineDispatcher io) {
    return new TownBlobStore(filesDir, io);
  }
}
