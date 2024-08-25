# What's this?
A Spring boot project that implement a simple kafka consumer using spring @KafkaListener annotation. 
It also produces messages automatically after start.

# How to prepare?
1. prepare your development environment (like your favorite IDE, ...)
2. compile the project and sync dependencies
3. start docker-compose to download the required images ```docker-compose up``` or `docker compose up`
---
# Discovery Documentation (To Better Understand The Context)
### 1. Base Model
- **Message**: A base model with two parameters:
    - `userId`: The ID of the user.
    - `content`: The content of the message.

### 2. Kafka Topic
- **Configuration**: A Kafka topic has been declared as spring bean in the `InterviewApplication` using `NewTopic` class. Using the command below we can identify the topic configuration:
  ```shell
  docker exec -it kafka /bin/bash
  kafka-topics --describe --topic interview --bootstrap-server localhost:9092
  ```
- **Topic Name**: `interview`
- **Configuration**:
    - **Partition Count**: 1 (all the messages for this topic are stored in a single partition; limits parallel processing)
    - **Replication Factor**: 1 (there is only one copy of the data; no redundancy)
    - **Segment Bytes**: 1073741824 (log segment size is 1GB)
    - **Leader**: 1001 (handles all read and write requests for the partition)
    - **Replicas**: 1001 (list of broker IDs that replicate the partition's data)
    - **In-Sync Replicas**: 1001 (list of broker IDs that are currently in sync with the leader)

### 3. Kafka Producer
- **Configuration**: The Kafka producer is declared in `InterviewApplication`. When application starts, the producer sends `${message.count}` number of messages to `${topic.name}`.

### 4. UserService
- **Method**:
    - `fetchPhoneById(userId)`: Retrieves the phone number associated with `userId` with a `.5s` delay to simulate interprocess communication, such as a database query or external service call.

### 5. SmsService
- **Methods**:
    - `sendMessage(phoneNo, message)`: Send a single SMS message, with a `1s` delay to simulate an external service call.
    - `sendMessages(messages)`: Send multiple SMS messages in a batch, with a `1s` delay per `100` messages to simulate an external service call.

### 6. MessageProcessorService
- **Method**:
    - `processMessage(message)`:
        1. Fetch the phone number of the user using `UserService`.
        2. Send an SMS using `SmsService.sendMessage(phoneNo, message)`.
        3. This method includes a synchronized counter to track the number of processed messages.

### 7. MessageConsumer
- **Method**:
    - `consume(message)`: Listens to the Kafka topic `${topic.name}` and passes incoming messages to `MessageProcessorService`.
---
# Performance Tuning
1. **batch.size**: 
   - Larger `batch.size` improve throughput but increases latency because it waits longer to fill the batch. (less resource consumption)
   - Shorter `batch.size` reduce waiting time and latency but increase the number of requests and throughput. (more resource consumption)
2. **linger.ms**:
   - Higher `linger.ms` means that producer will wait longer before sending message. (better throughput, higher latency). *For real-time applications, it's better to have `linger.ms=0`.*
   - Lower `linger.ms` means that messages will be transmitted more quickly. (low latency, high resource/network overhead)
3. **acks**: 
   - Setting `acks=1` means that producer only waits for the leader's approval. (low latency, potential data loss)
   - Setting `acks=all` means that producer waits for all the replica's approval. (high latency, more resilient system)
4. **compression.type**:
   - Setting `compression.type` reduce the size of message but the compression process itself may consume resources.
5. **fetch.min.bytes**:
   - Lower value means that broker will return the data when available which may increase the load of the consumer.
6. **fetch.max.wait.ms**:
   - Lower value means that broker will send data more quickly which may increase the load of the consumer.
7. **max.poll.records**: 
   - Lower value means reducing the time taken to process messages which may increase the load of the consumer.
8. **enable.auto.commit**:
   - enabling this config will automatically set the offset of last consumed message. Using `auto.commit.interval.ms` we can set the interval of the commit. (if the consumer crashes between auto-commits, it may lead to duplicate processing)
9. **Partitioning**:
   - Increasing the number of partitions means that consumers can operate in parallel.
10. **replication.factor**:
   - How many copies of data can be stored across multiple brokers. Setting lower `replication.factor` improve the write latency but if a broker goes down there is risk of data loss.
11. **Other**:
   - **Network Latency**: High speed network = Low latency Kafka architecture
   - **Service Logic**: Optimising `processMessage(message)` method can reduce processing time per message.
   - **Concurrent Listener**: Increasing concurrency can reduce latency by allowing multiple messages to be processed in parallel.
   - **JVM Heap Size**: By increasing heap size, we will be able to handle more data in memory. (large heap size = longer GC interval) 