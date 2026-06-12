package com.studio.gladetowns.core.data.di;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.io.File;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata({
    "javax.inject.Named",
    "dagger.hilt.android.qualifiers.ApplicationContext"
})
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
public final class DatabaseModule_FilesDirFactory implements Factory<File> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_FilesDirFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public File get() {
    return filesDir(contextProvider.get());
  }

  public static DatabaseModule_FilesDirFactory create(Provider<Context> contextProvider) {
    return new DatabaseModule_FilesDirFactory(contextProvider);
  }

  public static File filesDir(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.filesDir(context));
  }
}
