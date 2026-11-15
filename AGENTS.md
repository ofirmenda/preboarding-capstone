# how to run the project
cd tasks
./mvnw spring-boot:run

# how to run tests
cd tasks
./mvnw test 

# things to know
-Tasks are stored in memory in the controller (no database); state resets on restart
- Controller state (task list, id counter) is shared across tests; the test class uses `@DirtiesContext`, so each test must create its own data.
- Spring Boot 4: `@WebMvcTest` comes from the `spring-boot-starter-webmvc-test` dependency. If the import is missing, add the dependency; never create a local `WebMvcTest` annotation.
 