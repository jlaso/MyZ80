TEMPLATE = app
CONFIG += console c++11
CONFIG -= app_bundle
CONFIG -= qt

SOURCES += main.cpp \
    Instruction.cpp \
    memory.cpp

HEADERS += \
    Instruction.h \
    memory.h \
    general.h

