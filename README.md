# TinyLedger Implementation

A hexagonal architecture implementation of a simple Ledger with withdrawals and deposits.


## Architecture Overview

This implementation follows principles of Domain-Driven Design and Hexagonal Architecture to create a clean, maintainable, and extensible system.

### Features
- Supports simple deposit and withdrawals
- Aggregate calculation of balance and validation for withdrawals
- Basic field validation using Jakarta validations
- Basic validation for insufficient balances
- In-memory repository for storing all transactions. Find all returns sorted by latest

### Key Design Patterns

- **Immutable Value Objects**: Domain entities such as `Transaction` cannot be modified after creation
- **Repository Pattern**: Provides an abstraction layer over data persistence mechanisms
- **Dependency Injection**: Services receive dependencies through their constructors rather than creating them internally

## Extensibility
The system supports extensibility that allows: 
- Implementation of any type of persistence through the port interface `LedgerRepository`
- Implementation of any type of service through the service interface `LedgerService`
- The use of hexagonal architecture allows swapping the web layer with other implementations like Quarkus instead of the Spring boot layer in current implementation

## Design Decisions

### Immutable Transactions
- Transactions are immutable to provide thread safety and simplify state management
- This might cause slightly higher memory storage if we keep updating transactions, but will work for the purposes of the challenge

### In-Memory Repository
- Implements an in-memory repository to simplify persistence
- Implemented using a `ConcurrentHashMap` to support Thread-safety.
- The interface `LedgerRepository` provides methods and the contract to extend to other persistence stores

### Exception Handling
- Provides a simple implementation of a Global Exception Handler using the Spring Boot `ControllerAdvice` in the `GlobalExceptionHandler` class
- Provides a list of messages in the enum `ErrorMessages`
- Custom Exception types `InsufficientBalanceException` and `TransactionFailedException` to model common exceptions.
- Provides handling for Not Found Bad Request and Insufficient balance exceptions. Handles the rest of exceptions using a generic method on `Exception.class`
- More comprehensive coverage possible, but to save time only these implementations were done.

### Testing 
- There is decent test coverage between 85-100%. (85% Class Level, 84% Method Level, 88% Line level)
- Majority of critical tests are implemented .
- Did not go for full 100% coverage because it might have had diminishing returns.

You can run the tests using 
```bash
./gradlew test
```

## Tradeoffs
- Did not implement atomic transactions as was part of the specifications. It would make more sense.
- Current balance implementation is an aggregate function. This could be remedied using a thread safe balance object and optimistic locking
- Test coverage is provided for pragmatic cases and covers a lot of code. More tests can be implemented for edge cases.
- Did not implement accounts even though it made sense in this case. Deposit and withdrawals are only tied to one account
- I could add extensive Javadoc to improve maintainability. I didn't to save time. But it is something I would do.
- Used an in-memory repository but the interface can easily be extended to databases.
- Thread-safety is implemented to some degree in the InMemoryRepository, but it can be improved using atomic operations



## Building and Running
### Dependencies
- `Java >=19` and `Gradle >=8.5` needed for running. `JAVA_HOME` variable should be set up
- Pulls dependencies for Spring Boot using Gradle, so there is no need to have them present.
- Uses JUnit5 for testing

### Using Gradle/Java Directly
For Running directly from console you can execute the following from the root of the project(Powershell and Bash)
```bash
./gradlew run
```

### Intellij Idea
- This should be pretty straightforward.
- Import the root of the project and the IDE should detect the gradle project and build
- Run the project using `TinyLedgerApplication` main class
- Run tests from the test package.


### Using Docker
```bash
docker build -t tinyledger .
docker run -p 8080:8080 tinyledger
```
### Testing the API
To test the API, you can use any API testing client like POSTMAN or use the command-line curl.

### Deposit Money

```bash
curl -X POST http://localhost:8080/api/ledger/transactions \
  -H "Content-Type: application/json" \
  -d '{"type":"DEPOSIT","amount":100.00,"description":"Salary payment"}'
```

### Withdraw Money

```bash
curl -X POST http://localhost:8080/api/ledger/transactions \
  -H "Content-Type: application/json" \
  -d '{"type":"WITHDRAWAL","amount":50.00,"description":"Grocery shopping"}'
```

### Check Balance

```bash
curl -X GET http://localhost:8080/api/ledger/balance
```

### View Transaction History

```bash
curl -X GET http://localhost:8080/api/ledger/transactions
```
