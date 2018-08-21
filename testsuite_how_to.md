How to Run the Test Suite and Add Tests
========================
This page is meant to help familiarize a new user with using the test suite. It explains how to run the example Java test suite,
and how to add more tests to the suite. 

For an overview of the test suite, refer to [Test Suite Overview](testsuite_overview.md)

How to Run the Example Java Suite
--------------------
The test suite itself is language-independent, and requires additional language support before tests are run on a Rest.li
client implementation. 
 
To validate that these tests all pass using the Java implementation of the Rest.li client, *and* to demonstrate how
to use the test suite, this project contains a Java TestNG suite, located in the client-testsuite/src/test/java folder.

To run the provided TestNG suite:

```
gradle test
```

When you run ```gradle test```, TestNG runs tests provided from two Java classes in the example test suite:
* TestRestClientWithManualAssertions: manual assertion tests that have to be built by hand. 
* TestRestClientAgainstStandardTestSuite: automated tests driven by manifest.json. Within each category of test (i.e. json,
schema, or wire protocol), the same testing logic is shared, and testing data is assigned by manifest.json. 


```gradle test``` will run successfully, since the Java implementation of the Rest.li client passes all the tests.  

This is how to run the Java test suite, but because the build is successful, it's not immediately clear what is 
happening when ```gradle test```  is run. For better illustration, this guide will help you make tests that fail 
when run.


##### Manual Assertion Tests
Manual assertions are used to verify that Rest.li responses are decoded properly from http responses.
Try adding an untrue manual assertion to TestRestClientWithManualAssertions.java. For example, the following assertion
is untrue because the response should have 1 as its id:

  ```java  
  @Test
  public void testCollectionCreateAgain() throws Exception
  {
      Response<EmptyRecord> response = loadResponse("collection-create", "responses/collection-create.res");
   
      Assert.assertEquals(response.getId(), "2");
  }
  ```
  
Run ```gradle test``` again, and this test should fail, while the other tests pass.

##### Automated Tests
testSchema() verifies that Rest.li generates data templates from schemas. 
Try adding an untrue assertion to testSchema() in TestRestClientAgainstStandardTestSuite.java. 
Running ```gradle test``` again will result in multiple tests failing, because testSchema() is used for all the schema
tests defined in manifest.json



Adding tests to the test suite
-------------------------------
This section explains how to add a new language-independent test to the test suite, which can be shared across
languages.
Please do not modify existing tests, multiple language implementations are using these test and changing existing tests 
may break their test suites.

Keep tests simple!  They should test a single case and test it well.

### Add a JSON test
To add a new json corner case, e.g. corner-case.json:
1. In client-testsuite/src/data/, add corner-case.json.
2. Find the "jsonTestData" list in manifest.json, and add a list entry, {"data": "data/[filename].json"}
    ```json
     "jsonTestData": [
           ...
           {"data": "data/corner-case.json"},
           ...
      ],
    ```
### Add a Data Schema test
To test a new schema, NewSchema.pdsc :
1. In client-testsuite/src/schemas/testsuite/, add a new .pdsc file, NewSchema.pdsc, with fields and
   field types supported by Rest.li. Set "namespace" to "testsuite". Rest.li client will generate a data template from 
   this .pdsc file. 
2. In client-testsuite/src/data/, add a corresponding .json file, new-schema.json, with test data to fill your new data template's fields. 
3. In manifest.json, find the schemaTestData list. Add an entry for your new schema, following the general format:
{"schema": "testsuite.[SchemaName]", "data": "data/[json-name].json"}
    ```  
      "schemaTestData": [
        ...
        {"schema": "testsuite.NewSchema", "data": "data/new-schema.json"}
        ...
      ],
    ```
4. Run ```gradle build``` before running the test, so Rest.li will generate the data binding.


### Add a Wire Protocol test 
When adding a new wire protocol test, you also need to add its associated flat .req and .res files. 

The java suite contains a convenience tool to generate .restspec.json, .req and .res files, which will be used across 
languages. To use it:

1. Add or update the *Resource.java classes in the restli-testsuite-server project under src/main/java/testsuite.
   You can override a new method in an existing resource, or add a completely new resource class. For example, the 
   following class is a simple collection resource that only overrides create() using the option to return the created 
   entity.
   ```java

    @RestLiCollection(name = "collectionReturnEntity", namespace = "testsuite")
    public class CollectionReturnEntityResource extends CollectionResourceTemplate<Long, Message>
    {
    
      @ReturnEntity
      @Override
      public CreateKVResponse create(Message entity) {
    
        if(entity.getMessage().equals("test message"))
        {
          return new CreateKVResponse<Long, Message>(1l, entity, HttpStatus.S_201_CREATED);
        }
        else if(entity.getMessage().equals("another message"))
        {
          return new CreateKVResponse<Long, Message>(3l, entity, HttpStatus.S_201_CREATED);
        }
        else
        {
          return new CreateKVResponse<Long, Message>(null, entity, HttpStatus.S_404_NOT_FOUND);
        }
      }
    }
    
    ```
2. Re-generate the .restspec.json and .snapshot.json files:

    ```
    gradle publishRestIdl
    gradle publishRestSnapshot
    ```

3. Run the test server using:

    ```
    gradle JettyRunWar
    ```
    This will use the new restspecs to generate or update the appropriate RequestBuilders, which are used to make the
    requests in the following step.

4. Update RequestResponseTestCases in the language-specific suites of client-testsuite by adding a new request and
  test name to the map of Rest.li requests to be tested. 

   For the java implementation, this is done by modifying the builtRequests map in the buildRequests() function,
   in client-testsuite/src/test/java/com/linkedin/pegasus/testsuite/RequestResponseTestCases.java.
   ```java
    builtRequests.put("collectionReturnEntity-create", new CollectionReturnEntityRequestBuilders(_options).create().input(testMessage).build());
    ```

5. Re-generate the request and response files.  Files will be written to the "requests" and "responses" directories:

    ```
    gradle generateRequestAndResponseFiles
    ```
    Note that Java's requestBuilders are generating the request files with the desired output. 
    These flat files can be used to test the other implementations for well-formed requests.

6. Update the "wireProtocolTestData" entry of manifest.json to include test data references to all the files you've added.
 
   If overriding a new method for an existing resource: 
   * Find name of your resource in the wireProtocolTestData list. Under "operations", add test for the new method that you added
   to RequestResponseTestCases in Step 4. The new operation test should look something like this:
 
   ```
   { "name":"collection-get", "method": "get", "request": "requests/collection-get.req", "response": "responses/collection-get.res", "status": 200 },
   ```
   where the test name is usually the resource and method. "status" is the expected response status.
   
   If adding a new resource:
    * Create a new entry for your resource in wireProtocolTestData list. This should follow the 
    general format
    ```json
      "wireProtocolTestData": [
      { 
        "name": "collectionReturnEntity",
        "restspec": "restspecs/testsuite.collectionReturnEntity.restspec.json",
        "snapshot": "snapshots/testsuite.collectionReturnEntity.snapshot.json",
        "operations": [
        { "name":"collectionReturnEntity-create", "method": "create", "request": "requests/collectionReturnEntity-create.req", "response": "responses/collectionReturnEntity-create.res", "status": 201 }
       ] 
      },
      ...
     ]
     ```
    * For the list of operation tests, follow the instructions for overriding a new method for an existing resource.
     
7. Update the tests in TestRestClientWithManualAssertions (or equivalent file) with any manual assertions you would like
   to test. You will want
   to write a manual assertion for decoding the flat http response. This should ensure that the Rest.li client can decode
   an http response to a Rest.li response, and that the Rest.li response is a correct representation of the http response.

