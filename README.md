# GCSim

1. [What is this?](https://github.com/orenwf/gcsim/blob/master/README.md#a-simulation-of-a-virtual-machine-using-generational-tracing-garbage-collection)
2. [What is garbage collection?](https://github.com/orenwf/gcsim/blob/master/README.md#a-high-level-overview-of-gc)
3. [How GCSim works.](https://github.com/orenwf/gcsim/blob/master/README.md#how-our-vm-simulates-gc)
4. [The probability model in GCSim.](https://github.com/orenwf/gcsim/blob/master/README.md#the-probability-model-and-mathematical-justification)
5. [How do I use GCSim?](https://github.com/orenwf/gcsim/blob/master/README.md#how-to-install-and-run-gcsim)

## Simulating a virtual machine using generational tracing garbage collection.
A high level implementation of a mark-sweep garbage collector (GC).

## A high level overview of GC
In computer science, garbage collection (GC) is a form of automatic memory management. The garbage collector, or just collector, attempts to reclaim garbage, or memory occupied by objects that are no longer in use by the program.

### Reference counting
As a collection algorithm, reference counting tracks, for each object, a count of the number of references to it held by other objects. If an object's reference count reaches zero, the object has become inaccessible, and can be destroyed.

### Mark and Sweep 
Mark and sweep is a technique in Garbage Collection to free all unreferenced objects.
The first stage is the mark stage which does a tree traversal of the entire 'root set' and marks each object that is pointed to by a root as being 'in-use'. All objects that those objects point to, and so on, are marked as well, so that every object that is reachable via the root set is marked.
In the second stage, the sweep stage, all memory is scanned from start to finish, examining all blocks; those not marked as being 'in-use' are not reachable by any roots, and their memory is freed. For objects which were marked in-use, the in-use flag is cleared, preparing for the next cycle.

### Java Heap Memory

It is essential to understand the role of heap memory in JVM memory model. At runtime the Java instances are stored in the heap memory area. When an object is not referenced anymore it becomes eligible for eviction from heap memory. During the garbage collection process, those objects are evicted from heap memory and the space is reclaimed. Heap memory has three major areas.

![HotspotMM](https://javapapers.com/wp-content/uploads/2014/10/Java-Heap-Memory.jpg)

#### Young Generation

- Eden Space (any instance enters the runtime memory area through eden)

  - S0 Survivor Space (older instances moved from eden to S0)

  - S1 Survivor Space (older instances moved from S0 to S1)

#### Old Generation (instances promoted from S1 to tenured)

#### Permanent Generation (contains meta information like class, method detail)

## How our VM simulates GC
A model of a stack based VM is created where the stack holds references to all objects ever allocated. A freeList of memory is maintained from which all allocations are made.

The reachable objects are those which, starting from those on the stack, can be traced by following references. All unreachable objects are deemed garbage and are collected by the GC upon it's next invocation. Once an object is popped from the VM's stack it is unreachable and becomes garbage.

The parameters of the VM that can be configured are:
- The threshold for GC invocation - The minimum number of objects needed to trigger a GC.
- The heap size - The number of blocks available to the VM for allocation initially.
These parameters can be configured via the VM's constructor VM(threshold, heapSize). The VM supports interfaces to push and pop objects from the stack.

## The probability model and mathematical justification

## How to install and run GCSim:
1. Have a computer with Java JDK version 1.8 or higher - download from: http://openjdk.java.net/install/
2. Pull up a terminal 
3. Clone this repository - for instructions: https://help.github.com/articles/cloning-a-repository/
4. Issue the following commands in your terminal:
* `cd gcsim/src`
* `javac gcsim/*`
* `cd ../bin`
* `java gcsim.GCSim`
5. Follow the interactive command line instructions
6. Profit!
