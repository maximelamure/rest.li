Rest.li Client Test Suite
=========================

This directory contains a language-independent Rest.li client test suite.
It is designed to be shared across languages, and provides test data and testing guidelines that can be followed when 
testing a particular language implementation of the Rest.li client.

This project also contains a Java TestNG suite that uses the test suite's data and test guidelines. 
This example demonstrates how to use the test suite for a specific language implementation of the Rest.li client. 


Motivation
----
This test suite allows us to compare implementations of the Rest.li client across
languages.
It will help us achieve consistently high quality implementations of the Rest.li client in multiple
languages by:

* Reducing the work required to add a new Rest.li client implementation
* Providing a common ground where test cases can be shared amongst multiple Rest.li client implementations
* "Certifying" Rest.li client implementations with a quantifiable measure of quality
* Making it easier to make compatible Rest.li implementations, so a Rest.li client in one language can be used with a Rest.li server in another language. 

To this effect, we've added test suite support for Python and Java, and used the test suite to create this
[Compatibility Matrix](testsuite_compatibility_matrix.md), which compares the features supported by the implementations.

Getting Started
----
This documentation assumes a background in Rest.li. Implementers are encouraged to explore the
test data and reference documentation in tandem to learn the details of Rest.li.

* .pdscs - https://github.com/linkedin/rest.li/wiki/DATA-Data-Schema-and-Templates
* .restspec.json - https://github.com/linkedin/rest.li/wiki/Rest.li-.restspec.json-Format
* wire protocol - https://github.com/linkedin/rest.li/wiki/Rest.li-Protocol

There are two main ways of interacting with this test suite: 

1. Expanding the language-independent test suite by testing new features or cases
2. Adding test suite support for a new language to test a Rest.li implementation

These distinct ways of using the test suite point to an important distinction between language-independent
and language-specific components within this project. The test suite consists of language-independent data and test guidelines, meant to be
used for standardizing cross-platform testing. Yet, because it's language independent, the test suite cannot immediately be used to test a
specific language implementation of the Rest.li client. As a user, you must first add support for the test suite in your
desired language. That is why we refer to a general "test suite", and to a "Java TestNG test suite" 
or a "Python pytest test suite". The general test suite contains the shared data and guidelines, and the language-specific
test suites use the data and guidelines for testing in a single language.


#### How to Run and Add Tests
For new test suite users, take a look at the [Test Suite: How To](testsuite_how_to.md). This will walk you through running
the provided Java TestNG suite. It will also explain how to add new test cases to the language-independent test suite.

#### How to Add a New Language
If you would like to add support for a new language, refer to the [Test Suite New Language Guide](testsuite_new_language.md). This is
a more involved process, and it will be helpful to go over the Design and Code Structure section of this document first.


Design and Code Structure
----
The suite is composed of:

* Files in various language independent formats such as .json, .pdsc and .restspec.json.
* A manifest.json file containing a listing of all the test files to help drive automated test execution.
* Guidelines on how to use the test suite to validate a rest.li client implementation.
* A Java TestNG suite that uses this test data to validate the Java Rest.li client implementation.


The test suite is structured as follows: 
```
.
|—— client-testsuite
|   |—— manifest.json
|   |—— data/
|   |—— requests/
|   |—— requests-v2/
|   |—— responses/
|   |—— responses-v2/
|   |—— restspecs/
|   |—— schemas/
|   |—— snapshots/
|   |—— src/
|   |—— ...
|—— restli-testsuite-server
|   |—— src
|   |—— ...
|—— build.gradle
|... 
```
### client-testsuite
The client-testsuite directory contains testing data and the language-specific testing suites, such as the Java TestNG
suite. 

##### manifest.json
This manifest file provides machine readable information about all the automated tests provided in this suite.
It is shared across languages, and does not need to be changed when adding support for a new language. 
It should be changed when adding new test cases.

This file is intended to help drive portions of the test suite execution. Although it does help, writing additional
assertions by hand will still be needed in most languages to validate correctness of in-memory representations and language
bindings.

The file is broken down into a few main sections:

* jsonTestData - list of JSON data files.
* schemaTestData - list of Rest.li data schema files (.pdsc) as well as JSON data files matching the schemas.
* wireProtocolTestData - list of Rest.li interface definition files (.restspec.json) as well as test HTTP requests and responses,
  in the form of files, for operations supported by the interface definition.

##### Language-independent data folders
These folders contain testing data for the test suite. Most of them were generated by Java Rest.li, and should be used to
test your Rest.li implementation. 
* **data**: input JSON data for testing schemas and json serialization
* **requests**: correct http requests for wire protocol tests (Rest.li protocol 1)
* **requests-v2**: correct http requests for wire protocol tests (Rest.li protocol 2)
* **responses**: input http responses for wire protocol tests (Rest.li protocol 1)
* **responses-v2**: input http responses for wire protocol tests (Rest.li protocol 2)
* **restspecs**: restspecs generated from Java resources. These should be used to make request builders in other languages.
* **snapshots**: snapshots for resources

##### Java TestNG suite
In src/test/java, you can find the java TestNG suite. You can add support for new languages in src/test/. The Java suite
is broken into two folders:
* **test**: Java files containing the tests and utility methods for running tests. 
TestRestClientAgainstStandardTestSuite and TestRestClientWithManualAssertions contain the tests,
while StandardTestSuiteBase has utility methods that load files and compare requests.

* **testsuite**: Java files for building and loading requests and responses.

### restli-testsuite-server
This directory contains code for a Rest.li java server. Using the java resources and keys, Java Rest.li will generate 
language-independent restspecs in the client-testsuite/restspecs folder. Using these restspecs,
other Rest.li implementations can generate request builders without modifying server code.
When adding a new test, you may want to update or add a resource to restli-testsuite-server/src/main/java/testsuite.

Tests Covered
------------------
The test suite is intended to cover:

* **JSON data: Serializing and deserializaing**  
  Tests cover basic JSON and JSON corner cases such as large numbers, special characters and encodings.
  
* **Data Schemas: Generating data templates from schemas**  
  Tests cover schema types (records, unions, enums, typerefs, ...), primitive types, optionals and defaults.
  Backward compatibility rules are also covered.
  
* **Wire protocol: Building requests and decoding responses**  
  Tests cover serializing/deserializing of URLs, 
  Headers and bodies, Escaping/encoding, batch formats, projections, and partial updates.

### JSON Tests
These are tests for serialization and deserialization. 

| JSON feature | Details |
|--------------|---------|
| basic types| string, number, boolean, null, object, array|
| Empty collections| empty object, empty array, object with empty arrays, object with empty objects, array of empty object, array of empty arrays| 
| large numbers |i32 and i64|
| special characters| periods in key|
| unicode| Chinese character and e with an accent mark|


### Data Schema Tests
These are tests for data template generation from schema. 

| Schema feature | Details|
|------------------|--------|
| Primitive types|int, long, float, double, bytes, string |
| Complex types | array of maps, map of ints, record with props |
| Complex type: Unions | union of complex types, union of primitives, union of same types |
| Enums | With props and with alias|
| Fixed type | |
| Typerefs | for string, array, chained typeref, array, map, field, union|
| Include | include, and include with include |
| Default Fixup | to see if required fields are "fixed up" with defaults|
| Optional fields | |

### Wire Protocol Tests
These are tests for building requests and decoding responses. 

We test for well-formed requests by comparing the built http request
with the expected http request in the requests/ or requests-v2/ folder. We compare url, method, headers, and body.
We test that Rest.li can decode a Rest.li response from an http response by checking the decoded Rest.li response for
the correct values. We compare the response's status and error message with the status and error message specified by 
manifest.json. The body of the response is tested through manual assertions that check for the correct values.

##### Basic Resource Method Tests for Requests/Responses

| Rest.li Method | Collection | Simple | Association | Action Set|
|--------|------|----------------|----------------|------------------|
| get | x | x | x | |
| batch-get | x |  | x | |
| finder | x | | x | |
| create | x | | | |
| create with returned entity | x | | | |
| batch-create | x | | | |
| update | x | x | x | |
| partial update| x | |o| |
| batch-update| x |  | x | |
| batch-partial-update| x| | o | |
| delete | x | x | x | | 
| batch-delete | x | | x | | 
| action | o |  o | o | x | 

"x" - test is included in test suite  
"o" - test is not included but method should be supported by the resource  
" " - test is not included and method should NOT be supported by resource

##### Resource Key Tests
| Key Feature | Rest.li Method used|
|-------------|---------------|
|Key with Union| get | 
|Query Params| get | 
|Complex Key| get, create, delete, update batch-create, batch-delete, batch-get, batch-update, partial-update |
|Special Chars in ComplexKey strings | get, batch-get|

##### Error tests
| Error | Resource used | Rest.li Method | Details |
|-----|-----|----|----|
|404 | Collection | get | Send empty get request|
|400 | Collection | update | Request has a missing required field | 
|500 | Collection | create | Create request with id field |
|Error Details | Collection | create | CreateResponse with Error Details| 
|Batch Results with Errors | Collection | batch_update | Batch update with one good and two bad requests| 

##### Misc. Tests
| Feature | Resource tested| Method used|
|---------|---------|------|
| Typeref | Collection, Association | get |
| Subresource | Collection, Association | get |
| Projection | Collection, Association, ComplexKeyResource | get | 


Development Status
------------------
* [x] Basic JSON tests added
* [x] Basic schema tests added
* [x] Basic collection request/response tests added
* [x] Basic complex key request/response tests added
* [x] Basic association request/response tests added
* [x] Basic simple request/response tests added
* [x] Basic actionset request/response tests added
* [x] 404, validation error and other client side error tests added
* [x] 500 and other server side error tests added
* [x] Batch tests added
* [x] Partial update tests added
* [x] Projection tests added
* [x] Subresource tests added
* [x] Unions and maps and lists in complex keys tests added
* [x] Finder tests added
* [x] complex action tests added
* [x] Query params on methods tests added
* [x] Unions and maps and lists in query params tests added
* [x] Error details tests added
* [x] Batch results with errors tests added
* [x] partial update of a nested field
* [x] special chars in strings tests added
* [x] test finder that uses association entity key part
* [x] test finder with metadata
* [x] Resource Typeref support tests added
* [x] generate 2.0.0 wire protocol requests for all tests into separate directories
* [x] Union of same types in complex types added
* [x] Basic request/response test for collection with returning entity in create method added
* [ ] add test for record with lowercase name 
* [ ] add test for tunneled query params
* [ ] add test that includes unicode (non-ascii) characters on wire
* [ ] make input for wire protocol tests language independent
* [ ] expand manual assertions for Java test suite


Troubleshooting: What should I do when...
------------------------------
### ...my test suite doesn't run at all:
While the tests in this suite are helpful for finding incompatibilities between Rest.li implementations, some incompatibilities prevent 
the project from building. When adding a new language, or adding a new test, it may be necessary to omit some tests, schemas, or resources so that
the rest of the suite can run properly.


##### DataTemplate generation fails
A .pdsc schema may not be supported by your Rest.li implementation. Ignore this schema when generating code so that the 
code generation does not fail and stop. 


##### RequestBuilder generation fails
In other languages, requestBuilders are generated from language-independent restspecs for a particular resources. 
This generation can fail (causing your build to fail) if the resource is not supported in your Rest.li implementation. 
If this is the case, you can skip requestBuilder generation for that resource, and skip wire protocol tests that use that
requestBuilder. 

##### Building requests in RequestResponseTestCases (or equivalent file) fails
Requests are built before running wire protocol tests, so an error here will prevent all the tests from running. Make 
sure the attributes of the request you are building are actually supported by your language. 

### ...my test suite runs but certain tests fail:

##### Consistent Behavior Incompatibilities
Failing tests can indicate that your Rest.li implementation has some incompatibilities with Java Rest.li. However, some
incompatibilities have no effect on Rest.li performance. For example, consider wire protocol requests.
When comparing the flat http request with your Rest.li built request, you should not look for a carbon copy. Some differences
are acceptable. For instance, order may be different, or your implementation may use an optional header that is not included
in the flat http request.
If your wire protocol tests fail due to acceptable differences, you may wish to note the incompatibility and 
make your tests more lenient. 
 
##### Inconsistent Behavior Incompatibilities
Failing tests can also indicate incompatibilities that affect behavior. 
For instance, the keywithunion wire protocol test fails because Python Rest.li does not support complex key
with params, while Java Rest.li does. This test should fail, as it indicates a gap in Python Rest.li's implementation of
the Rest.li Protocol. 
 
