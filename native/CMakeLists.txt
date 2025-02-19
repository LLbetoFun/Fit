cmake_minimum_required(VERSION 3.28)
project(native)
SET(LIBRARY_OUTPUT_PATH  ${PROJECT_BINARY_DIR}/../build)

set(CMAKE_C_STANDARD 17)

add_library(native SHARED src/main/library.c
        src/hooker/hook.c
        )
