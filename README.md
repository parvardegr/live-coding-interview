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
  kafka-topics.sh --describe --topic interview --bootstrap-server localhost:9092
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
