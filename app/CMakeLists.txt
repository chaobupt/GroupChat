cmake_minimum_required(VERSION 3.4.1)

#编译运行后，会在jniLibs 中生成相应 abi 的so 库
#set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})


#创建总的库jpegtran并链接.c文件，可以直接引用第三方库的头文件
add_library( # Sets the name of the library.
             native-lib
             # Sets the library as a shared library.
             SHARED
             # Provides a relative path to your source file(s).
        src/main/cpp/native-lib.c
        src/main/cpp/cdjpeg.c
        src/main/cpp/transupp.c
        src/main/cpp/rdswitch.c)

#链接第三方库的头文件
target_include_directories(native-lib
        PRIVATE
        ${CMAKE_SOURCE_DIR}/src/main/cpp/include)

#set_target_properties()是用来链接库文件，与add_library()配套使用
#导入第三方库
add_library(jpeg
            SHARED
            IMPORTED)
#添加第三方库路径
set_target_properties(jpeg
                      PROPERTIES IMPORTED_LOCATION
                      ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libjpeg.so)

#添加NDK库
find_library(log-lib
             log)

#连接第三方库,把所有库文件都引入工程
target_link_libraries( # Specifies the target library.
                       native-lib
                       jpeg
                       # Links the log library to the target library.
                       ${log-lib} )

