# GCSim

1. [What is this?](https://github.com/orenwf/gcsim/blob/master/README.md#a-simulation-of-a-virtual-machine-using-generational-tracing-garbage-collection)
2. [An overview of computer memory model](https://github.com/orenwf/gcsim/blob/master/README.md#an-overview-of-a-computers-memory-model)
2. [What is garbage collection?](https://github.com/orenwf/gcsim/blob/master/README.md#an-overview-of-garbage-collection)
3. [How GCSim works.](https://github.com/orenwf/gcsim/blob/master/README.md#how-our-vm-simulates-gc)
4. [The probability model in GCSim.](https://github.com/orenwf/gcsim/blob/master/README.md#the-probability-model-and-mathematical-justification)
5. [How do I use GCSim?](https://github.com/orenwf/gcsim/blob/master/README.md#how-to-install-and-run-gcsim)
6. [References to references](https://github.com/orenwf/gcsim/blob/master/README.md#further-reading-and-useful-links)

## Simulating tracing garbage collection in a Virtual Machine.
This program simulates a high level implementation of a mark-and-sweep garbage collector (GC) performing automatic memory management for a virtual machine. The GC is implemented as a generational mark-and-sweep system, with three generations in total. The goal of the simulation is to answer questions about **how the duration and variance of pause times during garbage collection depend on the design of the garbage collection algorithm**, given some assumptions made about the distributions of frequency of object allocation in heap memory, object sizes, and object lifetimes. This simulation will produce results which could indicate the optimal choices for relative generation size when measuring performance in terms of total GC pause times and variance in GC pause times for a single executable task involving dynamic memory allocations.

[This project is being completed as part of the work for **Prof. Felisa J. Vázquez-Abad's** *Modeling and Simulation* graduate course at the **Hunter College of the City University of New York**.](http://www.cs.hunter.cuny.edu/~felisav/StochasticProcesses/Course_Description.html)

## An overview of a computer's memory model
Computers based on the Von Neumann model are designed with persistent random access memory laid out in addressable associative arrays, or tables. While this can be abstracted away from direct connection with physical locations, the logical model of memory is organized thusly. When a process is executed by the operating system or virtual machine running on computer hardware, it reserves some portion of the machine's available resources for the process. The memory space of an executing process is typically divided into *code memory* where the static pre-compiled instruction set for the process is kept on hand, and *dynamic memory* which is used by the executing process to save results of computation.

![computer memory](https://i.stack.imgur.com/nlb7A.jpg)

### An Object
An **arbitrarily sized** aggregation of contiguous and non-contiguous memory whose value **represents information** relevant to the executing program. An example would be an **integer, a string, or a reference to an other object**. More complex objects can exist and contain other types of objects or contain references to other objects.
```
class Object {

    List<Reference> otherObjects;
    Integer size;
    boolean marked;
    Integer age;
    ...
 }
```

![reference-object-model](https://i.stack.imgur.com/i6k0Z.png)

### A Reference
An **object whose value is the address in logical memory of an other object**. A reference may also carry with it other types of information, such as 'metadata'. A directed graph of references pointing to objects (sometimes containing other references) is typically referred to as a reference graph or tree.
```
class Reference {
	
	Object addressOf;
    ...
}

```

### The 'Stack'
![the Stack](https://i.pinimg.com/originals/7e/09/6f/7e096ff379d91080e637483214f4a230.jpg)

The stack is the section of a process' dynamic memory organized as an ordered array of **objects of fixed size**, with newer objects at the top. In aggregate, the stack represents the entirety of the state of the currently executing program, in that it contains all of the roots of all reference graphs, which emanate from it. Typically the stack is segmented into partitions called stack-frames which segregate access from one part of the stack to an other, and are allocated and deallocated based on the flow control of the process. Objects on the topmost frame of the stack will be automatically removed as the stack grows and shrinks deterministically based on the state of the process (such as returning from a function call). We will simulate this abstractly, not explicitly. **In a memory-managed programming language such as Java, the stack only holds references to other objects, which are allocated on the heap.**

### The 'Heap'
![the Heap](https://guidetoiceland.is/image/319166/x/0/the-old-tradition-of-creating-stone-cairns-in-iceland-please-don-t-stack-any-more-new-stone-piles-7.jpg)

Because you can't just put everything in the stack, the heap is an unordered collection of **objects of varying sizes**. When a process executes, the machine running it allocates some fixed amount of virtual memory to it. After space for the code memory, static allocations and stack are taken into account, the balance of the space (usually the bulk of it) is devoted to the heap. Objects which are not statically allocated by code, whose size are not known prior to execution, and which must be stored for longer duration than the currently executing stack frame are allocated during execution are stored on the heap.

![reference graph](https://i.stack.imgur.com/yZK6t.png)

### Allocation
When the executing process needs to create an object that may or may not be just a reference, and whose size is not known from the static code, it asks the machine running it to reserve some amount of memory on the heap and then store and maintains information about the state of the heap after the event. When a new object is allocated on the heap, a reference may be placed on the stack which points to it.
```
Reference allocate(Object_T obj) {
    ...
    if (free.size() >= obj.size()) {
        memory.add(obj);
	free.subtract(obj.size());
	return Reference.init(obj);
    }	
    else throw new OutOfMemoryException(this);
}
```
This object will continue to exist in heap dynamic memory independent of the state of execution of the process, unless it is explicitly removed.

### Why manage memory?
![badprogramming](https://evergreensmallbusiness.com/wp-content/uploads/2014/10/iStock_000044333992Mediumsystemerror.jpg)

Since memory is a fixed resource, typically, when the state of the process no longer depends on any value saved in memory, that memory can and should be reclaimed in order to use it for a different purpose. This freeing operation may be explicitly specified in the process's code. However, due to the large and complex nature of modern software, it is unreliable in many cases to depend on the author of a program to manually manage the memory of a process. **When a process never frees it's dynamically allocated memory, we call this a memory leak. Memory leaks can cause a process' memory footprint to balloon and cause it to quickly run out of memory.** It is often more efficient and reliable to employ an automatic system which reclaims memory that is no longer in use.

## An overview of Garbage Collection
Broadly, garbage collection (GC) is a form of automatic memory management. Garbage collection is performed by a procedure which attempts to reclaim memory occupied by objects that were once dynamically allocated (during program execution, rather than prior) from a finite pool of memory (the heap) reserved by the operating system for the executing program, but are no longer in use by the program.

## Garbage Collection Paradigms

There exist a small number of algorithms for performing garbage collection. An implementation of one of these algorithms is sometimes referred to as a "Garbage Collector". Generally, these implementations fall into two archetypes.

![garbage](http://www.corvallisadvocate.com/wp-content/uploads/2013/01/Landfill-For-Interior.jpg)

### Reference counting
A count of the number of references to each object held by other objects is tracked by the process. If an object's reference count reaches zero, the object has become inaccessible, and the memory that contains it can be used for other purposes. Reference counting algorithms have an advantage in that they do not cause pauses in execution or a process that are common in the other types of garbage collectors, such as the one detailed in the next paragraph. One notable disadvantage of this type of garbage collection is the possibility of circular references, by which an object maintains a reference to itself, while no other reference to it exists. Therefore the object's reference count can never reach zero, but no other object references it, so it cannot be directly accessed in order to fix the problem. Another disadvantage is that throughout execution, a process incurs a constant burden of keeping track of all objects' reference counts, and this can lead to worse performance, especially as the programs become very large and complex, with many small objects who hold many references and persist throghout the process. Our simulation does not implement this type of garbage collection.

### Tracing / Mark and Sweep GC
Tracing, or mark and sweep garbage collection is a technique first implemented by McCarthy for the Lisp programming language in 1959. Tracing GC allows a process to free all unreferenced objects by pausing the process and performing a garbage collection. When the process dynamically allocates an object, if the remaining space on the heap is smaller than size of the object, a mark and sweep phase is triggered.
The garbage collector begins by tracing the reference graph, beginning with the set of references held in the stack, the "root set", and marking any visited object as "in use". After completing the tracing and marking, the garbage collector then traverses the entirety of the heap, reclaiming the memory space of any object that is not marked. For objects which were marked "in use", the "in use" flag is then cleared, preparing for the next cycle.
```
void trace(Reference ref) {
    if (object.notMarked()) {
        object.mark();
        List<Reference> g = object.getRefs();
        for (Reference r : g) trace(r);
    }
}
```
A recursive strategy for tracing phase.
```
void traverseRootSet(Stack stk) {
    for (Reference r : stk) trace(r);
}
```
Sweeping the heap.
```
void sweep(Heap h) {
    for (object o : h) 
        if (!o.marked()) free(o.memory());
}
```
Tracing GC does not incur the overhead of keeping track of reference counts for each object in existence, and avoids the bugs possible from circular references. However, the mark and sweep phase causes a pause in the execution of the program, that is linear in proportion to the size of the heap plus the number of objects to be traversed in the reference graph.

```
int pauseTime(func trace(heap), func sweep(heap)) {
   return time(trace(heap)) + time(sweep(heap));
}
```
### Generational Tracing GC and our Simulation

A number of strategies exist to manage the duration of pause times. These strategies essentially fall into two sets. One set tries to reduce the overall size of the heap space that is being garbage collected at any one time, by partitioning the heap space into smaller spaces and managing them separately. The other set use complex applications of concurrent programming in order to collect garbage during program execution, without any explicit pausing. Our simulation considers partitioning heaps into multiple parts, and collecting garbage only in sequential execution of the process. Specifically, we consider a strategy called Generational Tracing GC.

![HotspotMM](https://2.bp.blogspot.com/-ZEyygkTuw-c/WlL5A_riU4I/AAAAAAAAVKw/R06IHQ-X_r481ldKR2KBsd8hVBGMBntVQCLcBGAs/s1600/Java%2BHeap.png)

Generational heap garbage collection involves partitioning the heap into some number of mutually exclusive areas where objects can be stored. Objects are maintained in one of these generations between garbage collection events, and during a garbage collection may be transitioned into an other generation, based on the **age of the object**, or the total number of garbage collection events during which the object has been present in the heap. These transitions can be modeled by a directional graph, since objects begin in the "young" generation, and migrate over the course of process execution until reaching the "mature" generation.

Partitioning the heap allows for differentiation between garbage collection events. Rather than all garbage collections operating on the entire heap space (**major garbage collection**), a garbage collection event may only operate on some smaller fraction of the total heap, depending on how many generations are involved.

#### Young Generation
Objects are all initally allocated onto this space. Upon reaching an age that meets the requirement, the objects here will be copied to the Tenured generation during the next garbage collection. In the Java Virtual Machine, specifically, this generation is futher partitioned into smaller spaces for greater efficiency, but we do not simulate this here. Things like temporary variables, loop variables, and other short lived objects typically are allocated here and are collected before they migrate to the next generation. When this generation triggers a garbage collection due to being full, this is known as a **minor garbage collection**.

#### Tenured Generation
Objects are not initially allocated to this generation. Objects reaching this generation have survived at least one garbage collection, and are expected to survive for a longer duration. If a garbage collection is triggered in the Tenured Generation, it will be directly subsequent to a garbage collection in the Young Generation. A garbage collection of this generation qualifies as a **major GC**. Objects that reach the minimum age required in the Tenured Generation are promoted to the next generation during a garbage collection.

#### Permanent or Mature Generation
The oldest objects which have survived at least two garbage collections will become members of the permanent or mature generation. Objects that are allocated based on static library imports and language level allocations will also typically be contained here, since they are likely to be needed throughout the process, and their memory will not be available for freeing. Since this generation is the final one, and there are no futher migrations possible, this generation performs **compaction** along with garbage collection.

### How our VM simulates GC
- Parameters:
  - The number of objects to be simulated, `N`.
  - The generation size configuration, `x`, `y`, `z`.
- Our simulation generates `N` triples of: 
  - `Arrival` time
  - `Lifetime` of the object's `stack` `Reference`
  - `Size` of the object and maximum number of references to other objects in the stack, if any exist.

- The virtual machine is intialized with a copy of the entire set of `3 * N` random variables, and a `Clock` time set to `0`
- The virtual machine checks the `arrival` time in the first triple
- If the `arrival` time is greater than the current clock time, the `allocate(Object)` function is called
- The `allocate(object)` function attempts to allocate the object on the `Young` generation.
  - if there is space, the allocation is successful, and a `Reference` is returned to the VM and placed in the `stack`
  - if there is no space, `pause(Clock)` is called, and a garbage collection is triggered on the `Young` heap
  
```
void GC(Heap target) {
    for (Object o : Young) 
        if (o.isMarked()) (promote(target);
    sweep();
}
```
- The `promote(target)` function calls the private allocator of the `Tenured` generation.
  - if there is space, the objects are transitioned from the `Young` generation to the `Tenured` generation
  - if there is not space, a garbage collection is triggered on the `Tenured` generation
  - `promote(target)` with `target = Mature` generation
  
- If there is not sufficient space in the `Mature` generation, the `compact(Mature)` is called.
  - `compact` is a major garbage collection event where all valid objects are shifted to reduce internal fragmentation

- After all garbage collection is finished, the `resume(Clock)` function is called.
- The balance of time `resume(Clock) - pause(Clock)` is then recorded in the log, and also added to all valid lifetimes.

## The probability model of GCSim

### What are we simulating?
We tested five models of diffenrent heap sizes and ran 100 simulations on each with 500 objects and calculated the expected total pause time for each. To reduce the variance of the expected total pause time we used control random variables across the sampling distributions,

### Variance Reduction Technique
**We modeled 3 control random variables.**

1. `Arrival` of object --> uniform

2. `Lifetime` of object --> uniform 

3. `Size` of object --> uniform 


**The expected values for the 5 sampling distribution, conffidence intervals and some other statistics**

**[4.0, 16.0, 80.0]**

Expected total pause time: 133133.43

Expected variance of pause times: 2555.1638272832793

We are 95% confidence that the value lies between :[133123, 133143]

Variance total pause time: 10432.078729816985


**[5.0, 25.0, 70.0]**

Expected total pause time: 133446.47

Expected variance of pause times: 2347.1853611160595

We are 95% confidence that the value lies between : [133438, 133455]

Variance total pause time: 15868.024475311979


**[6.0, 36.0, 58.0]**

Expected total pause time: 119636.82

Expected variance of pause times: 1607.3163441431666

We are 95% confidence that the value lies between : [119629, 119645]

Variance total pause time: 8111.29159798857


**[33.0, 33.0, 34.0]**

Expected total pause time: 53806.19

Expected variance of pause times: 1779.5470772703225

We are 95% confidence that the value lies between: [53798, 53815]

Variance total pause time: 5437.480684462245


**[50.0, 30.0, 20.0]**

Expected total pause time: 47312.18

Expected variance of pause times: 2009.6598962015173

We are 95% confidence that the value lies between: [47303, 47321]

Variance total pause time: 4648.216254392646

![picture](https://image.ibb.co/gFKRty/2018_05_16_15_39_07_1.jpg)

## How to install and run GCSim:
1. Have a computer with Java JDK version 1.8 or higher - download from: http://openjdk.java.net/install/
2. Pull up a terminal 
3. Clone this repository - for instructions: https://help.github.com/articles/cloning-a-repository/
4. Issue the following commands in your terminal:
* `cd gcsim/src`
* `javac gcsim/*`
* `java gcsim.GCSim`
5. Follow the interactive command line instructions
6. View the results of the simulation in the terminal output, or in the generated text file.

## Further reading and useful links
- https://blogs.msdn.microsoft.com/ericlippert/2009/04/27/the-stack-is-an-implementation-detail-part-one/
- https://simpy.readthedocs.io/en/latest/
- https://github.com/Deborah-Digges/mark-sweep-simulation
- https://github.com/tylertreat/comcast
- https://blogs.msdn.microsoft.com/oldnewthing/20100809-00/?p=13203
- http://fileadmin.cs.lth.se/cs/Personal/Sven_Gestegard_Robertz/publ/gcsimul.pdf
- http://www.cs.williams.edu/~dbarowy/cs334s18/assets/p87-zorn.pdf
- https://blogs.msdn.microsoft.com/abhinaba/2009/01/25/back-to-basic-series-on-dynamic-memory-management/
- https://stackoverflow.com/questions/79923/what-and-where-are-the-stack-and-heap
