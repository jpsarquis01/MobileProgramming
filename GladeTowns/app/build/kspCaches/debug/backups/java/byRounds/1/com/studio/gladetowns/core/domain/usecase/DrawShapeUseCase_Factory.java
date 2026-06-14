package com.studio.gladetowns.core.domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class DrawShapeUseCase_Factory implements Factory<DrawShapeUseCase> {
  @Override
  public DrawShapeUseCase get() {
    return newInstance();
  }

  public static DrawShapeUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DrawShapeUseCase newInstance() {
    return new DrawShapeUseCase();
  }

  private static final class InstanceHolder {
    private static final DrawShapeUseCase_Factory INSTANCE = new DrawShapeUseCase_Factory();
  }
}
