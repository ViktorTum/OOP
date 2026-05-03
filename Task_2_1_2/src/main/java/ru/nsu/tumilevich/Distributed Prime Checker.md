# Distributed Prime Checker

This project implements a distributed version of a prime checking task in Java, without using external libraries or Java RMI. The goal is to determine if an array of integers contains at least one non-prime number, with the computation distributed across multiple nodes in a subnet.

## Design Considerations:

### 1. Network Topology: Star Topology

*   **Coordinator Node:** A central node responsible for:
    *   Accepting client connections from worker nodes.
    *   Dividing the input array into smaller chunks.
    *   Distributing these chunks to available worker nodes.
    *   Collecting results from worker nodes.
    *   Aggregating the final result (whether a non-prime number was found).
    *   Handling worker failures and re-assigning tasks.

*   **Worker Nodes:** Multiple nodes that:
    *   Connect to the coordinator node.
    *   Receive a chunk of numbers from the coordinator.
    *   Perform primality checks on their assigned chunk.
    *   Send the result (e.g., `true` if a non-prime is found, `false` otherwise) back to the coordinator.

### 2. Inter-node Communication: Java Sockets

*   **Mechanism:** Standard `java.net.Socket` and `java.net.ServerSocket` will be used for communication between the coordinator and worker nodes.
*   **Data Transfer:** `ObjectInputStream` and `ObjectOutputStream` will be employed over the socket streams to facilitate the transfer of serialized Java objects (e.g., `int[]` for number chunks, `Boolean` for results).

### 3. Data Persistence: In-memory for current task

*   **Intermediate Results:** For this specific problem, intermediate results (whether a non-prime number is found within a chunk) are simple boolean values. These are held in memory by the coordinator until the final result is determined.
*   **Coordinator State:** The coordinator maintains an in-memory state tracking assigned chunks, received results, and the overall status of the computation. No long-term storage of intermediate results is required by the problem statement.
*   **Fault Tolerance Implication:** If the coordinator fails, the entire computation needs to be restarted, as its state is not persisted externally.

### 4. Fault Tolerance: Handling Worker Node Failures

*   **Detection:** The coordinator will implement mechanisms to detect unresponsive worker nodes:
    *   **Timeouts:** A timeout will be set for receiving results from worker nodes. If a worker fails to respond within this period, it will be considered unresponsive.
    *   **Heartbeats (Optional):** Workers could periodically send 
heartbeat messages to the coordinator to indicate they are still alive. This would allow for proactive detection of failures.
*   **Re-assignment:** If a worker node fails or becomes unresponsive, the coordinator will re-assign its uncompleted tasks (the chunk of numbers it was processing) to another available worker node. This ensures that the computation can continue even if some worker nodes fail.
*   **Coordinator Failure:** The current design does not address coordinator failure. If the coordinator fails, the entire distributed computation will halt and need to be restarted manually.

### 5. Identity and State of Distributed Objects

*   **Identity:** In this client-server model, worker nodes are identified by their network address (IP address and port) when they connect to the coordinator. The coordinator maintains a list of active worker connections.
*   **State:**
    *   **Coordinator State:** The coordinator's state includes:
        *   The original input array (or a reference to it).
        *   The list of chunks to be processed.
        *   A mapping of chunks to worker nodes (which worker is processing which chunk).
        *   A record of results received from each worker for their assigned chunks.
        *   A global boolean flag indicating if a non-prime number has been found anywhere in the array.
        *   A list of available/active worker connections.
    *   **Worker State:** Each worker node's state is relatively simple:
        *   The chunk of numbers it is currently processing.
        *   A local boolean flag indicating if a non-prime number has been found within its assigned chunk.
        *   Its connection to the coordinator.

## Implementation Details:

*   **No External Libraries/Java RMI:** As per the requirements, the implementation will strictly use standard Java APIs, primarily `java.net.Socket`, `java.net.ServerSocket`, `java.io.ObjectInputStream`, and `java.io.ObjectOutputStream` for network communication.
*   **Serialization:** All data transferred between coordinator and workers (e.g., integer arrays, boolean results) will be serialized using Java's built-in serialization mechanism.

## Differences from the First Task:

The first task focused on different parallelization strategies within a single process (sequential, `java.lang.Thread`, `parallelStream()`). The second task, this distributed version, introduces significant complexities related to network communication, fault tolerance, and managing state across multiple independent processes. The core `isPrime` logic remains the same, but the way the `hasNonPrime` function is implemented changes drastically to accommodate the distributed nature of computation.
