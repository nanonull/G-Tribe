Test types:
1) acceptance tests: game\src\test\tests\acceptance
Useful run args / VM options:
-DTEST_MEMORY_LEAKS=n
-Xmx2000m
-Dlog4j.configuration=log4j-test.properties
-DsecondsPerTestLimit=30
-DfreeMemoryLimit=1000000

Use case 'regression'/ all tests:
-DfreeMemoryLimit=1000000 -Xmx2000m -DsecondsPerTestLimit=20

where:
secondsPerTestLimit can be used to interrupt stuck test
freeMemoryLimit - stop testing due to free memory limit (less memory == less testing speed)


2) unit tests: game\src\test\conversion7
3) debug configs: game\src\test\tests\debug

* How to run tests
Run tests in './src/main/test/conversion7/tests.acceptance' with JUnit

=====
Mockito hints:
1) mocked object will have another hashCode than real object, be careful with logic in sets etc.
re-implementation of equals and hashcode on mocked objects will not help as Mockito overrides hashcode to System.identityHashCode on mocked object