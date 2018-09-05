---
layout: api_reference
title: API Reference
permalink: /spec/return_entity
index: 2
---

# Return Entity

This page describes returning the entity for resource methods that are not originally intended to return the entity.
For example, returning the entity is normal behavior for GET and FINDER, so this page does not apply to them.

For methods such as CREATE, however, the created entity is not returned in the response because the client
already has the entity when sending the request. Despite this, there are use cases where the server will
attach additional data to the new entity. Returning the entity in the CREATE response saves the client
from having to make an extra GET request.

## Contents

- 	[Supported Methods](#supported-methods)
-   [How to Enable](#how-to-enable)
-	[Query Parameter](#query-parameter)

## Supported Methods

Currently, this extra functionality is supported for the following resource methods:

- CREATE
- PARTIAL_UPDATE
- BATCH_CREATE

## How to Enable

To enable returning the entity for a given resource method, there are two requirements
that must be fulfilled.

First, the resource method must be annotated with the @`ReturnEntity` annotation.
This applies to all resource methods.

Second, the return type of the method must be a valid "Return Entity" return type.
This is specific to each resource method. The following table lists which "Return Entity"
return type corresponds to which resource method:

| Resource Method | Standard Return Type | "Return Entity" Return Type |
|-----------------|----------------------|-----------------------------|
| CREATE          | `CreateResponse`     | `CreateKVResponse`          |
| PARTIAL_UPDATE  | `UpdateResponse`     | `UpdateEntityResponse`      |
| BATCH_CREATE    | `BatchCreateResult`  | `BatchCreateKVResult`       |

Here is an example method signature for a CREATE resource method that will enable the entity to be returned.
Note how both the annotation and the required return type are present:

```java
@ReturnEntity
public CreateKVResponse create(V entity);
```

If both of these requirements are fulfilled, then the entity will be returned in the response by default.

## Query Parameter

By default, all requests to a "Return Entity" resource method will return the entity in the response.
However, if the client decides that it doesn't want the entity to be returned (to reduce network traffic, for instance),
then the query parameter `$returnEntity` can be used to indicate this.

The value of this query parameter must be a boolean value, otherwise the server will treat it
as a bad request. A value of `true` indicates that the entity should be returned, a value of
`false` indicates that the entity shouldn't be returned, and omitting the query parameter
altogether defaults to treating the value as if it were `true`. Note that if the resource
method doesn't have a "Return Entity" return type, then the `$returnEntity` parameter will
be ignored, regardless of its value.

Here is an example of a PARTIAL_UPDATE curl request indicating that the entity shouldn't be returned in the response:

<code>
curl -X POST localhost:/fortunes/1?$returnEntity=false -d '{"patch": {"$set": {"fortune": "you will strike it rich!"}}}'
</code>

Here is an example in Java of how one would use this parameter when building a CREATE request:

```java
CreateIdEntityRequest<Long, Greeting> request = builders.createAndGet()
	.input(greeting)
	.addParam(RestConstants.RETURN_ENTITY_PARAM, false)
	.build();
```

This can be harnessed by an application developer to optimize their service.
The obvious optimization is that potentially large payloads don't have to be
transmitted over the wire, reducing latency and network traffic. Another possible
optimization comes from the fact that the application developer can access this
query parameter from the resource method, allowing them to conditionally avoid
upstream service calls that would cause unnecessary slowdown.

<a id="BATCH_PARTIAL_UPDATE"></a>