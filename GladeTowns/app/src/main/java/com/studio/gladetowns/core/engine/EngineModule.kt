package com.studio.gladetowns.core.engine

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class EngineModule {
    @Binds
    abstract fun sceneCommandSink(impl: PlaceholderEngineController): SceneCommandSink
}
