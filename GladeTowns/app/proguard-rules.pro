# kotlinx.serialization — keep serializers for the command-log sealed hierarchy.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class com.studio.gladetowns.core.domain.** {
    *** Companion;
}
-keepclasseswithmembers class com.studio.gladetowns.core.domain.** {
    kotlinx.serialization.KSerializer serializer(...);
}
