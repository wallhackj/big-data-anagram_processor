### Anagram Finder: Design and Scalability Decisions
I utilize Hadoop MapReduce (Java) on a distributed cluster. I perform computation and storage using HDFS, since HDFS promotes scalability and reliability on commodity hardware. In my Map function, I sort letters of each word and emit sorted string as key (all anagrams share this same key). This setup naturally groups anagrams in the Reduce step. The code is modular (one Mapper/Reducer class), simple and easy to work with. I just use the standard library – the Hadoop API and Java String with bit manipulation – so there are no bulky external libraries.

Maintainability: MapReduce paradigm separates concerns (mapping, reducing, job initialization), and therefore the logic is easy to grasp. I keep the code concise, I did not introduce other frameworks in addition to Hadoop's libraries in order not to add gratuitous complexity.

### Scalability to Large Data
MapReduce is a scale-out design. HDFS splits a big file into fixed-size blocks (e.g., 64 MB) and assigns one mapper per block. A 640 MB text file might then run 10 mappers simultaneously. Hadoop scales out, not up – I can expand nodes in the cluster to increase parallelism. The algorithm once coded can run on any cluster size with almost linear scalability. For medium-sized input (e.g., ~10 million words), a small cluster (few machines) suffices: the task would finish quickly with minimal tuning.

For extremely large inputs (e.g., 100 billion words), I must scale aggressively and tune parameters. I would:

Expand parallelism: use a large Hadoop cluster (dozens or hundreds of nodes). Each additional DataNode adds more mappers and reducers running in parallel.

Tune HDFS configuration: can set block size larger (128 MB or greater) to reduce overhead, and set additional reducers so that the job will be spread out evenly.

Minimize I/O: enable intermediate compression and skip unnecessary data. 

Load balancing monitoring: ensure keys (ordered strings) are well distributed. If an anagram-key is extremely frequent, a special partitioner may distribute its values to multiple reducers in order to avoid the bottleneck.

In fact, processing 10M words is a piece of cake – a handful of nodes can accomplish it instantly. For 100B words (hundreds of gigabytes to several terabytes of text), I would employ a very large cluster and rely on Hadoop's batch scheduling. Horizontal scaling (scale-out) is the game name.

### Horizontal vs. Vertical Scaling
My approach is meant to scale horizontally (scale-out). I add machines to the Hadoop cluster to handle more data. Each new node adds more CPU, memory, and local disk space; Hadoop redistributes data blocks and tasks among all nodes. This does better fault tolerance (the failed node loses its share of data; other nodes continue) and throughput can grow close to linear with more nodes added.

Vertical scaling (scale-up) – more CPU/RAM for a single machine – is limited and usually costly. For small datasets (e.g., 10M words), local processing can be sped up by adding memory on a node. But it is not feasible beyond some point (e.g., hundreds of gigabytes). Cloud providers can scale automatically in both directions: one may use larger instances (vertical) or allocate nodes (horizontal). Horizontal scaling is usually favored in big-data settings.

### Framework Alternatives: Spark vs. Spring
I did consider other platforms. Apache Spark is the natural fit: it has support for executing the same sort-and-group logic with Resilient Distributed Datasets (RDDs). Spark's in-memory strategy beats disk-based Hadoop at large-scale iterative workloads by buffering data in RAM and reusing it rather than having to re-read disk I/O over and over. In the future, using Spark could speed up processing on extremely large data (hundreds of gigabytes or larger) and simplify through RDD operations or DataFrame group-by.

I did not use Spring Boot or Spring Batch to address this problem. Those are designed for traditional enterprise batch or web services, not large-scale distributed computing. Spring applications will operate in one JVM or a handful of servers and hence would not be able to handle hundreds of nodes and petabytes of data out-of-the-box. Unlike this, Spark was designed for big data. So, for large-scale anagram identification, Spark or Hadoop would be better alternatives to Spring.

### Summary and Recommendations
Maintainability: Hadoop MapReduce code is clean and modular; no additional dependencies were added.

Scalability: The job parallelizes easily. For 10M words, no special modifications are necessary. For 100B words, I'd utilize many more nodes, tune HDFS block size and number of reducers, and investigate migrating to Spark for in-memory performance.

Performance: I use an efficient key (sorted word) so that each word is processed once in the map phase. Data locality and commodity hardware make costs low.

Framework choice: Spark is a future improvement for speed; I didn't use Spring because i don't have experience with it.

Scaling strategy: I leverage horizontal scaling (more nodes) for throughput. Hadoop architecture (scale out, not up) enables me to scale the cluster effectively and cheaply. Vertical scaling (bigger servers) is hardware- and cost-limited.

In summary, my MapReduce anagram finder is built for big data. With work division across nodes, the use of combiners, and the ability to transition to Spark, it can grow from millions to billions of words while remaining efficient and sustainable.
