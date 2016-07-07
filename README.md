# MyZ80 IDE project with ASM and Simulation

This project aim to be an IDE for the electronics projects I want to create around the Z80
Also is a way to improve my Java skills. I want to use later the experiment in order to
include more CPU's (Arduino?, STM?)

.The project was created using ItelliJ IDEA community version

### You have several test's points along the code (you can just execute them).

#### /src/assembler/tests

    TestAssembler -> assembles src/samples/test.asm file
    TestExpressionParser
    TestProgramParser
    
#### /src/myZ80
    MyZ80 => first IDE rudiments
    DialogDemo => a demo for a dialog
    ProjectConfigDialog => another simplistic dialog demo
    
#### /src/momo
    MainWindow  =>  first proof of concept of IDE
    

### structure of folders

#### /src

#### /src/di

Dependenciy Injection

#### /src/assembler

assembler related stuff

#### /src/fileFormat

about the file format the IDE will be able to recognize

#### /src/samples

all the z80 that can be used on the tests

#### /src/hardware

that wants to be an abstraction layer for the hardwere

#### /src/achines.simpleZ80

this is a concrete hardware for the examples, here you can see declaration of memory and IO mapping 

#### /src/momo

my proof of concept for the IDE



#### /Tests

I am right now obsesed about unit testing and I have been created several tests in /Tests folder

# Collaboration

Any kind of collaboration will be welcomed.

# More info

Please, take a look over the doc folder, I've put there things that I found related with z80.

I hope there won't be any problem to link that kind of files to the project. In any case all the files linked in 
this doc area are property of their respectives authors, and are linked to this project in order to document it, NOT
with the intention of make my the owner. If you find some copyright documentation and you want that be removed please let me know.