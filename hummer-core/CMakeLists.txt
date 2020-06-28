# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
include_directories(
        src/main/jni/cpp
        src/main/jni/include)

aux_source_directory( # Define variable for directory of sources
        src/main/jni/cpp/CppWrapper
        # Set variable name of sources directory
        DIR_SRC1
        )
aux_source_directory( # Define variable for directory of sources
        src/main/jni/cpp/JavaInterface
        # Set variable name of sources directory
        DIR_SRC2
        )

aux_source_directory( # Define variable for directory of dependency libraries
        libs
        # Set variable name of dependency libraries directory
        DIR_LIBS
        )

add_library( # Sets the name of the library.
        android-jsc
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        ${DIR_SRC1} ${DIR_SRC2} ${DIR_LIBS}
        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        android-jsc

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}

        # JavaScriptCore
        ${CMAKE_CURRENT_SOURCE_DIR}/libs/${ANDROID_ABI}/libc++_shared.so
        ${CMAKE_CURRENT_SOURCE_DIR}/libs/${ANDROID_ABI}/libjscore.so
        )