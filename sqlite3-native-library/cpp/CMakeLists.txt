cmake_minimum_required(VERSION 3.14)
project(
    sqlite3_native_library
    VERSION 3
#    LANGUAGES C
)

include(FetchContent)

FetchContent_Declare(
  sqlite3
  URL https://sqlite.org/2023/sqlite-autoconf-3430100.tar.gz
)
FetchContent_MakeAvailable(sqlite3)

FetchContent_Declare(
        libsimple
        URL https://github.com/wangfenjin/simple/archive/refs/heads/master.zip
        PATCH_COMMAND patch -p1 < ${CMAKE_CURRENT_LIST_DIR}/libsimple.patch
)
set(BUILD_TEST_EXAMPLE OFF)
set(SIMPLE_WITH_JIEBA OFF)
set(BUILD_STATIC ON)
FetchContent_MakeAvailable(libsimple)

set(CMAKE_C_FLAGS "-O3")

add_library(sqlite3 SHARED "${sqlite3_SOURCE_DIR}/sqlite3.c" src/extension_load.c)
target_include_directories(sqlite3 PRIVATE ${sqlite3_SOURCE_DIR})

target_compile_definitions(sqlite3 PUBLIC
    SQLITE_ENABLE_FTS5
    SQLITE_ENABLE_RTREE
    SQLITE_DQS=0
    SQLITE_DEFAULT_MEMSTATUS=0
    SQLITE_TEMP_STORE=2
    SQLITE_MAX_EXPR_DEPTH=0
    SQLITE_OMIT_AUTHORIZATION
    SQLITE_OMIT_DECLTYPE
    SQLITE_OMIT_DEPRECATED
    SQLITE_OMIT_PROGRESS_CALLBACK
    SQLITE_OMIT_SHARED_CACHE
    SQLITE_OMIT_TCL_VARIABLE
    SQLITE_OMIT_TRACE
    SQLITE_USE_ALLOCA
    SQLITE_UNTESTABLE
    SQLITE_HAVE_ISNAN
    SQLITE_HAVE_LOCALTIME_R
    SQLITE_HAVE_LOCALTIME_S
    SQLITE_HAVE_MALLOC_USABLE_SIZE
    SQLITE_HAVE_STRCHRNUL
    SQLITE_EXTRA_INIT=sqlite3_extra_init
    SQLITE_CORE
)

target_link_libraries(sqlite3 simple)