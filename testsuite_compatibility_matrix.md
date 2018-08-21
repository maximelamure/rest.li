Rest.li Multi-language Compatibility Matrix
--------------------------
Using the test suite on the Python and Java Rest.li implementation, we've made the following compatibility matrix:

### Supported Resources

| Resource | Java | Python | Additional Information |
|--------|------|----------------|----------------|
| ActionSet | x | x | |
| Association | x | | Python generates code using a template engine, which doesn’t support Association |
| Collection | x | x | |
| Simple | x | x | Simple resource is supported by Python, but Python requires a top-level "namespace". If no namespace is specified by the Java-generated restspec, it cannot generate request builders. |


### Supported Resources

| Key Feature | Java | Python | Additional Information |
|--------|------|----------------|----------------|
| ComplexKey | x |  |In Java, ComplexResourceKey is a map of complex keys and params. In Python, complex key is supported, but only as a record, not a map with params |
| Key With Union | x | x | In Python, Key with Union is supported, but union member cannot be complex key with params |
| Query parmas | x | x |


### Supported Data Templates in Test Suite

| Template | Java | Python | Additional Information |
|--------|------|----------------|----------------|
| Array of maps | x | x | |
| Complex types | x | x | |
| Defaults | x | x | In the test suite, Defaults has a fixed field, and Python doesn't support Fixed in the same way as Java. This field must be removed for Python | 
| Enums | x | x | |
| Enum with props | x | x | |
| Fixed | x | | Python does not support Fixed in the same way as Java|
| Fixed5 | x | | Python does not support Fixed in the same way as Java|
| Fruits | x | x | |
| Includes | x | x | |
| Large record | x | x | |
| Map of ints| x | x | |
| MD5| x | x | |
| Message | x | x | | 
| Optionals| x | x | |
| Primitives | x | x | |
| Record with props | x | x | |
| Record with typeref field | x | x | |
| Time | x | x | |
| Type defined before Include | x | x | |
| Type defined in Include | x | x | |
| Typeref Message | x | x | |
| Typerefs | x | x | Typerefs.pdsc may need a different name in Python to avoid a naming conflict. If the filename is not changed, Python testsuite will have a folder for typeref resources, such as collectionTyperef, and it will also generate typeref.py from Typerefs.pdsc. |
| Union of complex types | x | x | |
| Union of primitives | x | x | |
| Union of same types | x | | Python does not support union of same types |
| Url | x | x | |


#### Supported HTTP Headers 
| Header | Java | Python | Additional Information |
|--------|------|----------------|----------------|
| Content-Type | x | | optional header|
| Accept | x | | optional header
| User-Agent | | x | optional header | 
| X-RestLi-Method | x | x | According to Rest.li protocol, X-RestLi-Method is only required for BATCH_CREATE and BATCH_PARTIAL_UPDATE. Java always includes it, and Python uses the header only when required.|

### Request Format Differences
| Request feature | Java | Python |
|--------|------|----------------|
|Unfilled Optional Fields in Request|If optional field is not specified in a multipleInputs action, it is not included in the request body.|If optional field is not specified in a multipleInputs action, it is still included in the request body. For an example, see the actionset-multiple-inputs-no-optional test, in which Python includes " 'optionalString': null" in the request.|
| Scheme and host components of URL | URL in request-line is relative (e.g. /collection/1)| URL scheme is hard-coded to http in requesturlbuilders.py, and requests.models.py requires a host