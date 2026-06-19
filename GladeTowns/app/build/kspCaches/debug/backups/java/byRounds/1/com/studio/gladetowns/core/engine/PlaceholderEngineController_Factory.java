package com.studio.gladetowns.core.engine;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class PlaceholderEngineController_Factory implements Factory<PlaceholderEngineController> {
  @Override
  public PlaceholderEngineController get() {
    return newInstance();
  }

  public static PlaceholderEngineController_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PlaceholderEngineController newInstance() {
    return new PlaceholderEngineController();
  }

  private static final class InstanceHolder {
    private static final PlaceholderEngineController_Factory INSTANCE = new PlaceholderEngineController_Factory();
  }
}
