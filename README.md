# ONgDB-LAB-HTTP
# TRANSACTION HTTP API [RECOMMENDED USE]
- Root discovery
```
GET http://localhost:7474/
{
  "bolt_routing": "bolt+routing://pro-ongdb-1:7687",
  "data": "http://pro-ongdb-1:7474/db/data/",
  "management": "http://pro-ongdb-1:7474/db/manage/",
  "bolt": "bolt://pro-ongdb-1:7687"
}
```
- Begin and commit a transaction in one request
```
http://localhost:7474/db/data/transaction/commit
{
  "statements" : [ {
    "statement" : "CREATE (n) RETURN id(n)"
  } ]
}
{
  "results" : [ {
    "columns" : [ "id(n)" ],
    "data" : [ {
      "row" : [ 6 ],
      "meta" : [ null ]
    } ]
  } ],
  "errors" : [ ]
}
```
- Execute multiple statements
```
http://localhost:7474/db/data/transaction/commit
{
  "statements" : [ {
    "statement" : "CREATE (n) RETURN id(n)"
  }, {
    "statement" : "CREATE (n {props}) RETURN n",
    "parameters" : {
      "props" : {
        "name" : "My Node"
      }
    }
  } ]
}
{
  "results" : [ {
    "columns" : [ "id(n)" ],
    "data" : [ {
      "row" : [ 2 ],
      "meta" : [ null ]
    } ]
  }, {
    "columns" : [ "n" ],
    "data" : [ {
      "row" : [ {
        "name" : "My Node"
      } ],
      "meta" : [ {
        "id" : 3,
        "type" : "node",
        "deleted" : false
      } ]
    } ]
  } ],
  "errors" : [ ]
}
```
-  Begin a transaction
```
http://localhost:7474/db/data/transaction
{
  "statements" : [ {
    "statement" : "CREATE (n {props}) RETURN n",
    "parameters" : {
      "props" : {
        "name" : "My Node"
      }
    }
  } ]
}
{
  "commit" : "http://localhost:7474/db/data/transaction/10/commit",
  "results" : [ {
    "columns" : [ "n" ],
    "data" : [ {
      "row" : [ {
        "name" : "My Node"
      } ],
      "meta" : [ {
        "id" : 10,
        "type" : "node",
        "deleted" : false
      } ]
    } ]
  } ],
  "transaction" : {
    "expires" : "Tue, 14 Apr 2020 09:27:52 +0000"
  },
  "errors" : [ ]
}
```
- Execute statements in an open transaction
```
http://localhost:7474/db/data/transaction/12
{
  "statements" : [ {
    "statement" : "CREATE (n) RETURN n"
  } ]
}
{
  "commit" : "http://localhost:7474/db/data/transaction/12/commit",
  "results" : [ {
    "columns" : [ "n" ],
    "data" : [ {
      "row" : [ { } ],
      "meta" : [ {
        "id" : 11,
        "type" : "node",
        "deleted" : false
      } ]
    } ]
  } ],
  "transaction" : {
    "expires" : "Tue, 14 Apr 2020 09:27:52 +0000"
  },
  "errors" : [ ]
}
```
- Reset transaction timeout of an open transaction
```
http://localhost:7474/db/data/transaction/2
{
  "statements" : [ ]
}
{
  "commit" : "http://localhost:7474/db/data/transaction/2/commit",
  "results" : [ ],
  "transaction" : {
    "expires" : "Tue, 14 Apr 2020 09:27:51 +0000"
  },
  "errors" : [ ]
}
```
- Commit an open transaction
```
http://localhost:7474/db/data/transaction/6/commit
{
  "statements" : [ {
    "statement" : "CREATE (n) RETURN id(n)"
  } ]
}
{
  "results" : [ {
    "columns" : [ "id(n)" ],
    "data" : [ {
      "row" : [ 5 ],
      "meta" : [ null ]
    } ]
  } ],
  "errors" : [ ]
}
```
- Rollback an open transaction
```
DELETE http://localhost:7474/db/data/transaction/3
{
  "results" : [ ],
  "errors" : [ ]
}
```
- Include query statistics
```
http://localhost:7474/db/data/transaction/commit
{
  "statements" : [ {
    "statement" : "CREATE (n) RETURN id(n)",
    "includeStats" : true
  } ]
}
{
  "results" : [ {
    "columns" : [ "id(n)" ],
    "data" : [ {
      "row" : [ 4 ],
      "meta" : [ null ]
    } ],
    "stats" : {
      "contains_updates" : true,
      "nodes_created" : 1,
      "nodes_deleted" : 0,
      "properties_set" : 0,
      "relationships_created" : 0,
      "relationship_deleted" : 0,
      "labels_added" : 0,
      "labels_removed" : 0,
      "indexes_added" : 0,
      "indexes_removed" : 0,
      "constraints_added" : 0,
      "constraints_removed" : 0
    }
  } ],
  "errors" : [ ]
}
```
- Return results in graph format
```
http://localhost:7474/db/data/transaction/commit
{
  "statements" : [ {
    "statement" : "CREATE ( bike:Bike { weight: 10 } ) CREATE ( frontWheel:Wheel { spokes: 3 } ) CREATE ( backWheel:Wheel { spokes: 32 } ) CREATE p1 = (bike)-[:HAS { position: 1 } ]->(frontWheel) CREATE p2 = (bike)-[:HAS { position: 2 } ]->(backWheel) RETURN bike, p1, p2",
    "resultDataContents" : [ "row", "graph" ]
  } ]
}
{
  "results" : [ {
    "columns" : [ "bike", "p1", "p2" ],
    "data" : [ {
      "row" : [ {
        "weight" : 10
      }, [ {
        "weight" : 10
      }, {
        "position" : 1
      }, {
        "spokes" : 3
      } ], [ {
        "weight" : 10
      }, {
        "position" : 2
      }, {
        "spokes" : 32
      } ] ],
      "meta" : [ {
        "id" : 7,
        "type" : "node",
        "deleted" : false
      }, [ {
        "id" : 7,
        "type" : "node",
        "deleted" : false
      }, {
        "id" : 0,
        "type" : "relationship",
        "deleted" : false
      }, {
        "id" : 8,
        "type" : "node",
        "deleted" : false
      } ], [ {
        "id" : 7,
        "type" : "node",
        "deleted" : false
      }, {
        "id" : 1,
        "type" : "relationship",
        "deleted" : false
      }, {
        "id" : 9,
        "type" : "node",
        "deleted" : false
      } ] ],
      "graph" : {
        "nodes" : [ {
          "id" : "7",
          "labels" : [ "Bike" ],
          "properties" : {
            "weight" : 10
          }
        }, {
          "id" : "8",
          "labels" : [ "Wheel" ],
          "properties" : {
            "spokes" : 3
          }
        }, {
          "id" : "9",
          "labels" : [ "Wheel" ],
          "properties" : {
            "spokes" : 32
          }
        } ],
        "relationships" : [ {
          "id" : "0",
          "type" : "HAS",
          "startNode" : "7",
          "endNode" : "8",
          "properties" : {
            "position" : 1
          }
        }, {
          "id" : "1",
          "type" : "HAS",
          "startNode" : "7",
          "endNode" : "9",
          "properties" : {
            "position" : 2
          }
        } ]
      }
    } ]
  } ],
  "errors" : [ ]
}
```
- Handling errors
```
http://localhost:7474/db/data/transaction/11/commit
{
  "statements" : [ {
    "statement" : "This is not a valid Cypher Statement."
  } ]
}
{
  "results" : [ ],
  "errors" : [ {
    "code" : "Neo.ClientError.Statement.SyntaxError",
    "message" : "Invalid input 'T': expected <init> (line 1, column 1 (offset: 0))\n\"This is not a valid Cypher Statement.\"\n ^"
  } ]
}
```
- Handling errors in an open transaction
```
http://localhost:7474/db/data/transaction/9
{
  "statements" : [ {
    "statement" : "This is not a valid Cypher Statement."
  } ]
}
{
  "commit" : "http://localhost:7474/db/data/transaction/9/commit",
  "results" : [ ],
  "errors" : [ {
    "code" : "Neo.ClientError.Statement.SyntaxError",
    "message" : "Invalid input 'T': expected <init> (line 1, column 1 (offset: 0))\n\"This is not a valid Cypher Statement.\"\n ^"
  } ]
}
```
# OTHER REFERENCE
# Streaming
```
GET http://localhost:7474/db/data/
Accept: application/json
X-Stream: true

Example response
200: OK
Content-Type: application/json; charset=UTF-8; stream=true

{
  "extensions" : { },
  "node" : "http://localhost:7474/db/data/node",
  "relationship" : "http://localhost:7474/db/data/relationship",
  "node_index" : "http://localhost:7474/db/data/index/node",
  "relationship_index" : "http://localhost:7474/db/data/index/relationship",
  "extensions_info" : "http://localhost:7474/db/data/ext",
  "relationship_types" : "http://localhost:7474/db/data/relationship/types",
  "batch" : "http://localhost:7474/db/data/batch",
  "cypher" : "http://localhost:7474/db/data/cypher",
  "indexes" : "http://localhost:7474/db/data/schema/index",
  "constraints" : "http://localhost:7474/db/data/schema/constraint",
  "transaction" : "http://localhost:7474/db/data/transaction",
  "node_labels" : "http://localhost:7474/db/data/labels",
  "neo4j_version" : "3.4.2-SNAPSHOT"
}
```
- Get service root
```
GET http://localhost:7474/db/data/
{
  "extensions" : { },
  "node" : "http://localhost:7474/db/data/node",
  "relationship" : "http://localhost:7474/db/data/relationship",
  "node_index" : "http://localhost:7474/db/data/index/node",
  "relationship_index" : "http://localhost:7474/db/data/index/relationship",
  "extensions_info" : "http://localhost:7474/db/data/ext",
  "relationship_types" : "http://localhost:7474/db/data/relationship/types",
  "batch" : "http://localhost:7474/db/data/batch",
  "cypher" : "http://localhost:7474/db/data/cypher",
  "indexes" : "http://localhost:7474/db/data/schema/index",
  "constraints" : "http://localhost:7474/db/data/schema/constraint",
  "transaction" : "http://localhost:7474/db/data/transaction",
  "node_labels" : "http://localhost:7474/db/data/labels",
  "neo4j_version" : "3.4.2-SNAPSHOT"
}
```
- Use parameters
```
http://localhost:7474/db/data/cypher
{
  "query" : "MATCH (x {name: {startName}})-[r]-(friend) WHERE friend.name = {name} RETURN TYPE(r)",
  "params" : {
    "startName" : "I",
    "name" : "you"
  }
}
{
  "columns" : [ "TYPE(r)" ],
  "data" : [ [ "know" ] ]
}
```
- Create a node
```
http://localhost:7474/db/data/cypher
{
  "query" : "CREATE (n:Person { name : {name} }) RETURN n",
  "params" : {
    "name" : "Andres"
  }
}
{
  "columns" : [ "n" ],
  "data" : [ [ {
    "metadata" : {
      "id" : 3,
      "labels" : [ "Person" ]
    },
    "data" : {
      "name" : "Andres"
    },
    "paged_traverse" : "http://localhost:7474/db/data/node/3/paged/traverse/{returnType}{?pageSize,leaseTime}",
    "outgoing_relationships" : "http://localhost:7474/db/data/node/3/relationships/out",
    "outgoing_typed_relationships" : "http://localhost:7474/db/data/node/3/relationships/out/{-list|&|types}",
    "labels" : "http://localhost:7474/db/data/node/3/labels",
    "create_relationship" : "http://localhost:7474/db/data/node/3/relationships",
    "traverse" : "http://localhost:7474/db/data/node/3/traverse/{returnType}",
    "extensions" : { },
    "all_relationships" : "http://localhost:7474/db/data/node/3/relationships/all",
    "all_typed_relationships" : "http://localhost:7474/db/data/node/3/relationships/all/{-list|&|types}",
    "property" : "http://localhost:7474/db/data/node/3/properties/{key}",
    "self" : "http://localhost:7474/db/data/node/3",
    "incoming_relationships" : "http://localhost:7474/db/data/node/3/relationships/in",
    "properties" : "http://localhost:7474/db/data/node/3/properties",
    "incoming_typed_relationships" : "http://localhost:7474/db/data/node/3/relationships/in/{-list|&|types}"
  } ] ]
}
```
- Create a node with multiple properties
```
http://localhost:7474/db/data/cypher
{
  "query" : "CREATE (n:Person { props } ) RETURN n",
  "params" : {
    "props" : {
      "position" : "Developer",
      "name" : "Michael",
      "awesome" : true,
      "children" : 3
    }
  }
}
{
  "columns" : [ "n" ],
  "data" : [ [ {
    "metadata" : {
      "id" : 0,
      "labels" : [ "Person" ]
    },
    "data" : {
      "awesome" : true,
      "children" : 3,
      "name" : "Michael",
      "position" : "Developer"
    },
    "paged_traverse" : "http://localhost:7474/db/data/node/0/paged/traverse/{returnType}{?pageSize,leaseTime}",
    "outgoing_relationships" : "http://localhost:7474/db/data/node/0/relationships/out",
    "outgoing_typed_relationships" : "http://localhost:7474/db/data/node/0/relationships/out/{-list|&|types}",
    "labels" : "http://localhost:7474/db/data/node/0/labels",
    "create_relationship" : "http://localhost:7474/db/data/node/0/relationships",
    "traverse" : "http://localhost:7474/db/data/node/0/traverse/{returnType}",
    "extensions" : { },
    "all_relationships" : "http://localhost:7474/db/data/node/0/relationships/all",
    "all_typed_relationships" : "http://localhost:7474/db/data/node/0/relationships/all/{-list|&|types}",
    "property" : "http://localhost:7474/db/data/node/0/properties/{key}",
    "self" : "http://localhost:7474/db/data/node/0",
    "incoming_relationships" : "http://localhost:7474/db/data/node/0/relationships/in",
    "properties" : "http://localhost:7474/db/data/node/0/properties",
    "incoming_typed_relationships" : "http://localhost:7474/db/data/node/0/relationships/in/{-list|&|types}"
  } ] ]
}
```
- Create multiple nodes with properties
```
http://localhost:7474/db/data/cypher
{
  "query" : "UNWIND {props} AS properties CREATE (n:Person) SET n = properties RETURN n",
  "params" : {
    "props" : [ {
      "name" : "Andres",
      "position" : "Developer"
    }, {
      "name" : "Michael",
      "position" : "Developer"
    } ]
  }
}
{
  "columns" : [ "n" ],
  "data" : [ [ {
    "metadata" : {
      "id" : 4,
      "labels" : [ "Person" ]
    },
    "data" : {
      "name" : "Andres",
      "position" : "Developer"
    },
    "paged_traverse" : "http://localhost:7474/db/data/node/4/paged/traverse/{returnType}{?pageSize,leaseTime}",
    "outgoing_relationships" : "http://localhost:7474/db/data/node/4/relationships/out",
    "outgoing_typed_relationships" : "http://localhost:7474/db/data/node/4/relationships/out/{-list|&|types}",
    "labels" : "http://localhost:7474/db/data/node/4/labels",
    "create_relationship" : "http://localhost:7474/db/data/node/4/relationships",
    "traverse" : "http://localhost:7474/db/data/node/4/traverse/{returnType}",
    "extensions" : { },
    "all_relationships" : "http://localhost:7474/db/data/node/4/relationships/all",
    "all_typed_relationships" : "http://localhost:7474/db/data/node/4/relationships/all/{-list|&|types}",
    "property" : "http://localhost:7474/db/data/node/4/properties/{key}",
    "self" : "http://localhost:7474/db/data/node/4",
    "incoming_relationships" : "http://localhost:7474/db/data/node/4/relationships/in",
    "properties" : "http://localhost:7474/db/data/node/4/properties",
    "incoming_typed_relationships" : "http://localhost:7474/db/data/node/4/relationships/in/{-list|&|types}"
  } ], [ {
    "metadata" : {
      "id" : 5,
      "labels" : [ "Person" ]
    },
    "data" : {
      "name" : "Michael",
      "position" : "Developer"
    },
    "paged_traverse" : "http://localhost:7474/db/data/node/5/paged/traverse/{returnType}{?pageSize,leaseTime}",
    "outgoing_relationships" : "http://localhost:7474/db/data/node/5/relationships/out",
    "outgoing_typed_relationships" : "http://localhost:7474/db/data/node/5/relationships/out/{-list|&|types}",
    "labels" : "http://localhost:7474/db/data/node/5/labels",
    "create_relationship" : "http://localhost:7474/db/data/node/5/relationships",
    "traverse" : "http://localhost:7474/db/data/node/5/traverse/{returnType}",
    "extensions" : { },
    "all_relationships" : "http://localhost:7474/db/data/node/5/relationships/all",
    "all_typed_relationships" : "http://localhost:7474/db/data/node/5/relationships/all/{-list|&|types}",
    "property" : "http://localhost:7474/db/data/node/5/properties/{key}",
    "self" : "http://localhost:7474/db/data/node/5",
    "incoming_relationships" : "http://localhost:7474/db/data/node/5/relationships/in",
    "properties" : "http://localhost:7474/db/data/node/5/properties",
    "incoming_typed_relationships" : "http://localhost:7474/db/data/node/5/relationships/in/{-list|&|types}"
  } ] ]
}
```
- Set all properties on a node using Cypher
```
http://localhost:7474/db/data/cypher
{
  "query" : "CREATE (n:Person { name: 'this property is to be deleted' } ) SET n = { props } RETURN n",
  "params" : {
    "props" : {
      "position" : "Developer",
      "firstName" : "Michael",
      "awesome" : true,
      "children" : 3
    }
  }
}
{
  "columns" : [ "n" ],
  "data" : [ [ {
    "metadata" : {
      "id" : 31,
      "labels" : [ "Person" ]
    },
    "data" : {
      "awesome" : true,
      "firstName" : "Michael",
      "children" : 3,
      "position" : "Developer"
    },
    "paged_traverse" : "http://localhost:7474/db/data/node/31/paged/traverse/{returnType}{?pageSize,leaseTime}",
    "outgoing_relationships" : "http://localhost:7474/db/data/node/31/relationships/out",
    "outgoing_typed_relationships" : "http://localhost:7474/db/data/node/31/relationships/out/{-list|&|types}",
    "labels" : "http://localhost:7474/db/data/node/31/labels",
    "create_relationship" : "http://localhost:7474/db/data/node/31/relationships",
    "traverse" : "http://localhost:7474/db/data/node/31/traverse/{returnType}",
    "extensions" : { },
    "all_relationships" : "http://localhost:7474/db/data/node/31/relationships/all",
    "all_typed_relationships" : "http://localhost:7474/db/data/node/31/relationships/all/{-list|&|types}",
    "property" : "http://localhost:7474/db/data/node/31/properties/{key}",
    "self" : "http://localhost:7474/db/data/node/31",
    "incoming_relationships" : "http://localhost:7474/db/data/node/31/relationships/in",
    "properties" : "http://localhost:7474/db/data/node/31/properties",
    "incoming_typed_relationships" : "http://localhost:7474/db/data/node/31/relationships/in/{-list|&|types}"
  } ] ]
}
```
- Send a query
```
http://localhost:7474/db/data/cypher
{
  "query" : "MATCH (x {name: 'I'})-[r]->(n) RETURN type(r), n.name, n.age",
  "params" : { }
}
{
  "columns" : [ "type(r)", "n.name", "n.age" ],
  "data" : [ [ "know", "you", null ], [ "know", "him", 25 ] ]
}
```
- Return paths
```
http://localhost:7474/db/data/cypher
{
  "query" : "MATCH path = (x {name: 'I'})--(friend) RETURN path, friend.name",
  "params" : { }
}
{
  "columns" : [ "path", "friend.name" ],
  "data" : [ [ {
    "relationships" : [ "http://localhost:7474/db/data/relationship/13" ],
    "nodes" : [ "http://localhost:7474/db/data/node/29", "http://localhost:7474/db/data/node/30" ],
    "directions" : [ "->" ],
    "start" : "http://localhost:7474/db/data/node/29",
    "length" : 1,
    "end" : "http://localhost:7474/db/data/node/30"
  }, "you" ] ]
}
```
- Nested results
```
http://localhost:7474/db/data/cypher
{
  "query" : "MATCH (n) WHERE n.name in ['I', 'you'] RETURN collect(n.name)",
  "params" : { }
}
{
  "columns" : [ "collect(n.name)" ],
  "data" : [ [ [ "I", "you" ] ] ]
}
```
- Retrieve query metadata
```
http://localhost:7474/db/data/cypher?includeStats=true
{
  "query" : "MATCH (n {name: 'I'}) SET n:Actor REMOVE n:Director RETURN labels(n)",
  "params" : { }
}
{
  "columns" : [ "labels(n)" ],
  "data" : [ [ [ "Actor" ] ] ],
  "stats" : {
    "nodes_deleted" : 0,
    "relationship_deleted" : 0,
    "nodes_created" : 0,
    "labels_added" : 1,
    "relationships_created" : 0,
    "indexes_added" : 0,
    "properties_set" : 0,
    "contains_updates" : true,
    "indexes_removed" : 0,
    "constraints_added" : 0,
    "labels_removed" : 1,
    "constraints_removed" : 0
  }
}
```
- Errors
```
http://localhost:7474/db/data/cypher
{
  "query" : "MATCH (x {name: 'I'}) RETURN x.dummy/0",
  "params" : { }
}
{
  "message": "/ by zero",
  "exception": "BadInputException",
  "fullname": "org.neo4j.server.rest.repr.BadInputException",
  "stackTrace": [
    "org.neo4j.server.rest.repr.RepresentationExceptionHandlingIterable.exceptionOnNext(RepresentationExceptionHandlingIterable.java:48)",
    "org.neo4j.helpers.collection.ExceptionHandlingIterable$1.next(ExceptionHandlingIterable.java:76)",
    "org.neo4j.helpers.collection.IteratorWrapper.next(IteratorWrapper.java:49)",
    "org.neo4j.server.rest.repr.ListRepresentation.serialize(ListRepresentation.java:64)",
    "org.neo4j.server.rest.repr.Serializer.serialize(Serializer.java:78)",
    "org.neo4j.server.rest.repr.MappingSerializer.putList(MappingSerializer.java:66)",
    "org.neo4j.server.rest.repr.CypherResultRepresentation.serialize(CypherResultRepresentation.java:58)",
    "org.neo4j.server.rest.repr.MappingRepresentation.serialize(MappingRepresentation.java:41)",
    "org.neo4j.server.rest.repr.OutputFormat.assemble(OutputFormat.java:232)",
    "org.neo4j.server.rest.repr.OutputFormat.formatRepresentation(OutputFormat.java:172)",
    "org.neo4j.server.rest.repr.OutputFormat.response(OutputFormat.java:155)",
    "org.neo4j.server.rest.repr.OutputFormat.ok(OutputFormat.java:70)",
    "org.neo4j.server.rest.web.CypherService.cypher(CypherService.java:140)",
    "java.lang.reflect.Method.invoke(Method.java:498)",
    "org.neo4j.server.rest.transactional.TransactionalRequestDispatcher.dispatch(TransactionalRequestDispatcher.java:147)",
    "org.neo4j.server.rest.dbms.AuthorizationDisabledFilter.doFilter(AuthorizationDisabledFilter.java:49)",
    "org.neo4j.server.rest.web.CorsFilter.doFilter(CorsFilter.java:115)",
    "org.neo4j.server.rest.web.CollectUserAgentFilter.doFilter(CollectUserAgentFilter.java:69)",
    "java.lang.Thread.run(Thread.java:748)"
  ],
  "cause": {
    "exception": "QueryExecutionException",
    "cause": {
      "exception": "QueryExecutionKernelException",
      "cause": {
        "exception": "ArithmeticException",
        "cause": {
          "exception": "ArithmeticException",
          "fullname": "org.neo4j.cypher.internal.util.v3_4.ArithmeticException",
          "stackTrace": [
            "org.neo4j.cypher.internal.runtime.interpreted.commands.expressions.Divide.apply(Divide.scala:38)",
            "org.neo4j.cypher.internal.runtime.interpreted.pipes.ProjectionPipe$$anonfun$internalCreateResults$1$$anonfun$apply$1.apply(ProjectionPipe.scala:36)",
            "org.neo4j.cypher.internal.runtime.interpreted.pipes.ProjectionPipe$$anonfun$internalCreateResults$1$$anonfun$apply$1.apply(ProjectionPipe.scala:34)",
            "scala.collection.immutable.Map$Map1.foreach(Map.scala:116)",
            "org.neo4j.cypher.internal.runtime.interpreted.pipes.ProjectionPipe$$anonfun$internalCreateResults$1.apply(ProjectionPipe.scala:34)",
            "org.neo4j.cypher.internal.runtime.interpreted.pipes.ProjectionPipe$$anonfun$internalCreateResults$1.apply(ProjectionPipe.scala:33)",
            "scala.collection.Iterator$$anon$11.next(Iterator.scala:410)",
            "scala.collection.Iterator$$anon$11.next(Iterator.scala:410)",
            "org.neo4j.cypher.internal.compatibility.v3_4.runtime.ClosingIterator.next(ResultIterator.scala:74)",
            "org.neo4j.cypher.internal.compatibility.v3_4.runtime.ClosingIterator.next(ResultIterator.scala:48)",
            "org.neo4j.cypher.internal.compatibility.v3_4.runtime.PipeExecutionResult$$anon$2.next(PipeExecutionResult.scala:72)",
            "org.neo4j.cypher.internal.compatibility.v3_4.runtime.PipeExecutionResult$$anon$2.next(PipeExecutionResult.scala:70)",
            "org.neo4j.cypher.internal.compatibility.ClosingExecutionResult$$anon$2$$anonfun$next$1.apply(ClosingExecutionResult.scala:65)",
            "org.neo4j.cypher.internal.compatibility.ClosingExecutionResult$$anon$2$$anonfun$next$1.apply(ClosingExecutionResult.scala:64)",
            "org.neo4j.cypher.exceptionHandler$runSafely$.apply(exceptionHandler.scala:89)",
            "org.neo4j.cypher.internal.compatibility.ClosingExecutionResult$$anon$2.next(ClosingExecutionResult.scala:64)",
            "org.neo4j.cypher.internal.compatibility.ClosingExecutionResult$$anon$2.next(ClosingExecutionResult.scala:58)",
            "org.neo4j.cypher.internal.javacompat.ExecutionResult.next(ExecutionResult.java:236)",
            "org.neo4j.cypher.internal.javacompat.ExecutionResult.next(ExecutionResult.java:57)",
            "org.neo4j.helpers.collection.ExceptionHandlingIterable$1.next(ExceptionHandlingIterable.java:72)",
            "org.neo4j.helpers.collection.IteratorWrapper.next(IteratorWrapper.java:49)",
            "org.neo4j.server.rest.repr.ListRepresentation.serialize(ListRepresentation.java:64)",
            "org.neo4j.server.rest.repr.Serializer.serialize(Serializer.java:78)",
            "org.neo4j.server.rest.repr.MappingSerializer.putList(MappingSerializer.java:66)",
            "org.neo4j.server.rest.repr.CypherResultRepresentation.serialize(CypherResultRepresentation.java:58)",
            "org.neo4j.server.rest.repr.MappingRepresentation.serialize(MappingRepresentation.java:41)",
            "org.neo4j.server.rest.repr.OutputFormat.assemble(OutputFormat.java:232)",
            "org.neo4j.server.rest.repr.OutputFormat.formatRepresentation(OutputFormat.java:172)",
            "org.neo4j.server.rest.repr.OutputFormat.response(OutputFormat.java:155)",
            "org.neo4j.server.rest.repr.OutputFormat.ok(OutputFormat.java:70)",
            "org.neo4j.server.rest.web.CypherService.cypher(CypherService.java:140)",
            "java.lang.reflect.Method.invoke(Method.java:498)",
            "org.neo4j.server.rest.transactional.TransactionalRequestDispatcher.dispatch(TransactionalRequestDispatcher.java:147)",
            "org.neo4j.server.rest.dbms.AuthorizationDisabledFilter.doFilter(AuthorizationDisabledFilter.java:49)",
            "org.neo4j.server.rest.web.CorsFilter.doFilter(CorsFilter.java:115)",
            "org.neo4j.server.rest.web.CollectUserAgentFilter.doFilter(CollectUserAgentFilter.java:69)",
            "java.lang.Thread.run(Thread.java:748)"
          ],
          "message": "/ by zero",
          "errors": [
            {
              "code": "Neo.DatabaseError.General.UnknownError",
              "stackTrace": "org.neo4j.cypher.internal.util.v3_4.ArithmeticException: / by zero\n\tat org.neo4j.cypher.internal.runtime.interpreted.commands.expressions.Divide.apply(Divide.scala:38)\n\tat org.neo4j.cypher.internal.runtime.interpreted.pipes.ProjectionPipe$$anonfun$internalCreateResults$1$$anonfun$apply$1.apply(ProjectionPipe.scala:36)\n\tat org.neo4j.cypher.internal.runtime.interpreted.pipes.ProjectionPipe$$anonfun$internalCreateResults$1$$anonfun$apply$1.apply(ProjectionPipe.scala:34)\n\tat scala.collection.immutable.Map$Map1.foreach(Map.scala:116)\n\tat org.neo4j.cypher.internal.runtime.interpreted.pipes.ProjectionPipe$$anonfun$internalCreateResults$1.apply(ProjectionPipe.scala:34)\n\tat org.neo4j.cypher.internal.runtime.interpreted.pipes.ProjectionPipe$$anonfun$internalCreateResults$1.apply(ProjectionPipe.scala:33)\n\tat scala.collection.Iterator$$anon$11.next(Iterator.scala:410)\n\tat scala.collection.Iterator$$anon$11.next(Iterator.scala:410)\n\tat org.neo4j.cypher.internal.compatibility.v3_4.runtime.ClosingIterator.next(ResultIterator.scala:74)\n\tat org.neo4j.cypher.internal.compatibility.v3_4.runtime.ClosingIterator.next(ResultIterator.scala:48)\n\tat org.neo4j.cypher.internal.compatibility.v3_4.runtime.PipeExecutionResult$$anon$2.next(PipeExecutionResult.scala:72)\n\tat org.neo4j.cypher.internal.compatibility.v3_4.runtime.PipeExecutionResult$$anon$2.next(PipeExecutionResult.scala:70)\n\tat org.neo4j.cypher.internal.compatibility.ClosingExecutionResult$$anon$2$$anonfun$next$1.apply(ClosingExecutionResult.scala:65)\n\tat org.neo4j.cypher.internal.compatibility.ClosingExecutionResult$$anon$2$$anonfun$next$1.apply(ClosingExecutionResult.scala:64)\n\tat org.neo4j.cypher.exceptionHandler$runSafely$.apply(exceptionHandler.scala:89)\n\tat org.neo4j.cypher.internal.compatibility.ClosingExecutionResult$$anon$2.next(ClosingExecutionResult.scala:64)\n\tat org.neo4j.cypher.internal.compatibility.ClosingExecutionResult$$anon$2.next(ClosingExecutionResult.scala:58)\n\tat org.neo4j.cypher.internal.javacompat.ExecutionResult.next(ExecutionResult.java:236)\n\tat org.neo4j.cypher.internal.javacompat.ExecutionResult.next(ExecutionResult.java:57)\n\tat org.neo4j.helpers.collection.ExceptionHandlingIterable$1.next(ExceptionHandlingIterable.java:72)\n\tat org.neo4j.helpers.collection.IteratorWrapper.next(IteratorWrapper.java:49)\n\tat org.neo4j.server.rest.repr.ListRepresentation.serialize(ListRepresentation.java:64)\n\tat org.neo4j.server.rest.repr.Serializer.serialize(Serializer.java:78)\n\tat org.neo4j.server.rest.repr.MappingSerializer.putList(MappingSerializer.java:66)\n\tat org.neo4j.server.rest.repr.CypherResultRepresentation.serialize(CypherResultRepresentation.java:58)\n\tat org.neo4j.server.rest.repr.MappingRepresentation.serialize(MappingRepresentation.java:41)\n\tat org.neo4j.server.rest.repr.OutputFormat.assemble(OutputFormat.java:232)\n\tat org.neo4j.server.rest.repr.OutputFormat.formatRepresentation(OutputFormat.java:172)\n\tat org.neo4j.server.rest.repr.OutputFormat.response(OutputFormat.java:155)\n\tat org.neo4j.server.rest.repr.OutputFormat.ok(OutputFormat.java:70)\n\tat org.neo4j.server.rest.web.CypherService.cypher(CypherService.java:140)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.lang.reflect.Method.invoke(Method.java:498)\n\tat com.sun.jersey.spi.container.JavaMethodInvokerFactory$1.invoke(JavaMethodInvokerFactory.java:60)\n\tat com.sun.jersey.server.impl.model.method.dispatch.AbstractResourceMethodDispatchProvider$ResponseOutInvoker._dispatch(AbstractResourceMethodDispatchProvider.java:205)\n\tat com.sun.jersey.server.impl.model.method.dispatch.ResourceJavaMethodDispatcher.dispatch(ResourceJavaMethodDispatcher.java:75)\n\tat org.neo4j.server.rest.transactional.TransactionalRequestDispatcher.dispatch(TransactionalRequestDispatcher.java:147)\n\tat com.sun.jersey.server.impl.uri.rules.HttpMethodRule.accept(HttpMethodRule.java:302)\n\tat com.sun.jersey.server.impl.uri.rules.ResourceClassRule.accept(ResourceClassRule.java:108)\n\tat com.sun.jersey.server.impl.uri.rules.RightHandPathRule.accept(RightHandPathRule.java:147)\n\tat com.sun.jersey.server.impl.uri.rules.RootResourceClassesRule.accept(RootResourceClassesRule.java:84)\n\tat com.sun.jersey.server.impl.application.WebApplicationImpl._handleRequest(WebApplicationImpl.java:1542)\n\tat com.sun.jersey.server.impl.application.WebApplicationImpl._handleRequest(WebApplicationImpl.java:1473)\n\tat com.sun.jersey.server.impl.application.WebApplicationImpl.handleRequest(WebApplicationImpl.java:1419)\n\tat com.sun.jersey.server.impl.application.WebApplicationImpl.handleRequest(WebApplicationImpl.java:1409)\n\tat com.sun.jersey.spi.container.servlet.WebComponent.service(WebComponent.java:409)\n\tat com.sun.jersey.spi.container.servlet.ServletContainer.service(ServletContainer.java:558)\n\tat com.sun.jersey.spi.container.servlet.ServletContainer.service(ServletContainer.java:733)\n\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:790)\n\tat org.eclipse.jetty.servlet.ServletHolder.handle(ServletHolder.java:860)\n\tat org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1650)\n\tat org.neo4j.server.rest.dbms.AuthorizationDisabledFilter.doFilter(AuthorizationDisabledFilter.java:49)\n\tat org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1637)\n\tat org.neo4j.server.rest.web.CorsFilter.doFilter(CorsFilter.java:115)\n\tat org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1637)\n\tat org.neo4j.server.rest.web.CollectUserAgentFilter.doFilter(CollectUserAgentFilter.java:69)\n\tat org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1637)\n\tat org.eclipse.jetty.servlet.ServletHandler.doHandle(ServletHandler.java:533)\n\tat org.eclipse.jetty.server.handler.ScopedHandler.nextHandle(ScopedHandler.java:188)\n\tat org.eclipse.jetty.server.session.SessionHandler.doHandle(SessionHandler.java:1595)\n\tat org.eclipse.jetty.server.handler.ScopedHandler.nextHandle(ScopedHandler.java:188)\n\tat org.eclipse.jetty.server.handler.ContextHandler.doHandle(ContextHandler.java:1253)\n\tat org.eclipse.jetty.server.handler.ScopedHandler.nextScope(ScopedHandler.java:168)\n\tat org.eclipse.jetty.servlet.ServletHandler.doScope(ServletHandler.java:473)\n\tat org.eclipse.jetty.server.session.SessionHandler.doScope(SessionHandler.java:1564)\n\tat org.eclipse.jetty.server.handler.ScopedHandler.nextScope(ScopedHandler.java:166)\n\tat org.eclipse.jetty.server.handler.ContextHandler.doScope(ContextHandler.java:1155)\n\tat org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:141)\n\tat org.eclipse.jetty.server.handler.HandlerList.handle(HandlerList.java:61)\n\tat org.eclipse.jetty.server.handler.HandlerWrapper.handle(HandlerWrapper.java:132)\n\tat org.eclipse.jetty.server.Server.handle(Server.java:530)\n\tat org.eclipse.jetty.server.HttpChannel.handle(HttpChannel.java:347)\n\tat org.eclipse.jetty.server.HttpConnection.onFillable(HttpConnection.java:256)\n\tat org.eclipse.jetty.io.AbstractConnection$ReadCallback.succeeded(AbstractConnection.java:279)\n\tat org.eclipse.jetty.io.FillInterest.fillable(FillInterest.java:102)\n\tat org.eclipse.jetty.io.ChannelEndPoint$2.run(ChannelEndPoint.java:124)\n\tat org.eclipse.jetty.util.thread.strategy.EatWhatYouKill.doProduce(EatWhatYouKill.java:247)\n\tat org.eclipse.jetty.util.thread.strategy.EatWhatYouKill.produce(EatWhatYouKill.java:140)\n\tat org.eclipse.jetty.util.thread.strategy.EatWhatYouKill.run(EatWhatYouKill.java:131)\n\tat org.eclipse.jetty.util.thread.ReservedThreadExecutor$ReservedThread.run(ReservedThreadExecutor.java:382)\n\tat org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:708)\n\tat org.eclipse.jetty.util.thread.QueuedThreadPool$2.run(QueuedThreadPool.java:626)\n\tat java.lang.Thread.run(Thread.java:748)\n",
              "message": "/ by zero"
            }
          ]
        },
        "fullname": "org.neo4j.cypher.ArithmeticException",
        "stackTrace": [
          "org.neo4j.cypher.exceptionHandler$.arithmeticException(exceptionHandler.scala:29)",
          "org.neo4j.cypher.exceptionHandler$.arithmeticException(exceptionHandler.scala:26)",
          "org.neo4j.cypher.internal.util.v3_4.ArithmeticException.mapToPublic(CypherException.scala:125)",
          "org.neo4j.cypher.exceptionHandler$runSafely$.apply(exceptionHandler.scala:94)",
          "org.neo4j.cypher.internal.compatibility.ClosingExecutionResult$$anon$2.next(ClosingExecutionResult.scala:64)",
          "org.neo4j.cypher.internal.compatibility.ClosingExecutionResult$$anon$2.next(ClosingExecutionResult.scala:58)",
          "org.neo4j.cypher.internal.javacompat.ExecutionResult.next(ExecutionResult.java:236)",
          "org.neo4j.cypher.internal.javacompat.ExecutionResult.next(ExecutionResult.java:57)",
          "org.neo4j.helpers.collection.ExceptionHandlingIterable$1.next(ExceptionHandlingIterable.java:72)",
          "org.neo4j.helpers.collection.IteratorWrapper.next(IteratorWrapper.java:49)",
          "org.neo4j.server.rest.repr.ListRepresentation.serialize(ListRepresentation.java:64)",
          "org.neo4j.server.rest.repr.Serializer.serialize(Serializer.java:78)",
          "org.neo4j.server.rest.repr.MappingSerializer.putList(MappingSerializer.java:66)",
          "org.neo4j.server.rest.repr.CypherResultRepresentation.serialize(CypherResultRepresentation.java:58)",
          "org.neo4j.server.rest.repr.MappingRepresentation.serialize(MappingRepresentation.java:41)",
          "org.neo4j.server.rest.repr.OutputFormat.assemble(OutputFormat.java:232)",
          "org.neo4j.server.rest.repr.OutputFormat.formatRepresentation(OutputFormat.java:172)",
          "org.neo4j.server.rest.repr.OutputFormat.response(OutputFormat.java:155)",
          "org.neo4j.server.rest.repr.OutputFormat.ok(OutputFormat.java:70)",
          "org.neo4j.server.rest.web.CypherService.cypher(CypherService.java:140)",
          "java.lang.reflect.Method.invoke(Method.java:498)",
          "org.neo4j.server.rest.transactional.TransactionalRequestDispatcher.dispatch(TransactionalRequestDispatcher.java:147)",
          "org.neo4j.server.rest.dbms.AuthorizationDisabledFilter.doFilter(AuthorizationDisabledFilter.java:49)",
          "org.neo4j.server.rest.web.CorsFilter.doFilter(CorsFilter.java:115)",
          "org.neo4j.server.rest.web.CollectUserAgentFilter.doFilter(CollectUserAgentFilter.java:69)",
          "java.lang.Thread.run(Thread.java:748)"
        ],
        "message": "/ by zero",
        "errors": [
          {
            "code": "Neo.ClientError.Statement.ArithmeticError",
            "message": "/ by zero"
          }
        ]
      },
      "fullname": "org.neo4j.kernel.impl.query.QueryExecutionKernelException",
      "stackTrace": [
        "org.neo4j.cypher.internal.javacompat.ExecutionResult.converted(ExecutionResult.java:405)",
        "org.neo4j.cypher.internal.javacompat.ExecutionResult.next(ExecutionResult.java:240)",
        "org.neo4j.cypher.internal.javacompat.ExecutionResult.next(ExecutionResult.java:57)",
        "org.neo4j.helpers.collection.ExceptionHandlingIterable$1.next(ExceptionHandlingIterable.java:72)",
        "org.neo4j.helpers.collection.IteratorWrapper.next(IteratorWrapper.java:49)",
        "org.neo4j.server.rest.repr.ListRepresentation.serialize(ListRepresentation.java:64)",
        "org.neo4j.server.rest.repr.Serializer.serialize(Serializer.java:78)",
        "org.neo4j.server.rest.repr.MappingSerializer.putList(MappingSerializer.java:66)",
        "org.neo4j.server.rest.repr.CypherResultRepresentation.serialize(CypherResultRepresentation.java:58)",
        "org.neo4j.server.rest.repr.MappingRepresentation.serialize(MappingRepresentation.java:41)",
        "org.neo4j.server.rest.repr.OutputFormat.assemble(OutputFormat.java:232)",
        "org.neo4j.server.rest.repr.OutputFormat.formatRepresentation(OutputFormat.java:172)",
        "org.neo4j.server.rest.repr.OutputFormat.response(OutputFormat.java:155)",
        "org.neo4j.server.rest.repr.OutputFormat.ok(OutputFormat.java:70)",
        "org.neo4j.server.rest.web.CypherService.cypher(CypherService.java:140)",
        "java.lang.reflect.Method.invoke(Method.java:498)",
        "org.neo4j.server.rest.transactional.TransactionalRequestDispatcher.dispatch(TransactionalRequestDispatcher.java:147)",
        "org.neo4j.server.rest.dbms.AuthorizationDisabledFilter.doFilter(AuthorizationDisabledFilter.java:49)",
        "org.neo4j.server.rest.web.CorsFilter.doFilter(CorsFilter.java:115)",
        "org.neo4j.server.rest.web.CollectUserAgentFilter.doFilter(CollectUserAgentFilter.java:69)",
        "java.lang.Thread.run(Thread.java:748)"
      ],
      "message": "/ by zero",
      "errors": [
        {
          "code": "Neo.ClientError.Statement.ArithmeticError",
          "message": "/ by zero"
        }
      ]
    },
    "fullname": "org.neo4j.graphdb.QueryExecutionException",
    "stackTrace": [
      "org.neo4j.kernel.impl.query.QueryExecutionKernelException.asUserException(QueryExecutionKernelException.java:35)",
      "org.neo4j.cypher.internal.javacompat.ExecutionResult.converted(ExecutionResult.java:405)",
      "org.neo4j.cypher.internal.javacompat.ExecutionResult.next(ExecutionResult.java:240)",
      "org.neo4j.cypher.internal.javacompat.ExecutionResult.next(ExecutionResult.java:57)",
      "org.neo4j.helpers.collection.ExceptionHandlingIterable$1.next(ExceptionHandlingIterable.java:72)",
      "org.neo4j.helpers.collection.IteratorWrapper.next(IteratorWrapper.java:49)",
      "org.neo4j.server.rest.repr.ListRepresentation.serialize(ListRepresentation.java:64)",
      "org.neo4j.server.rest.repr.Serializer.serialize(Serializer.java:78)",
      "org.neo4j.server.rest.repr.MappingSerializer.putList(MappingSerializer.java:66)",
      "org.neo4j.server.rest.repr.CypherResultRepresentation.serialize(CypherResultRepresentation.java:58)",
      "org.neo4j.server.rest.repr.MappingRepresentation.serialize(MappingRepresentation.java:41)",
      "org.neo4j.server.rest.repr.OutputFormat.assemble(OutputFormat.java:232)",
      "org.neo4j.server.rest.repr.OutputFormat.formatRepresentation(OutputFormat.java:172)",
      "org.neo4j.server.rest.repr.OutputFormat.response(OutputFormat.java:155)",
      "org.neo4j.server.rest.repr.OutputFormat.ok(OutputFormat.java:70)",
      "org.neo4j.server.rest.web.CypherService.cypher(CypherService.java:140)",
      "java.lang.reflect.Method.invoke(Method.java:498)",
      "org.neo4j.server.rest.transactional.TransactionalRequestDispatcher.dispatch(TransactionalRequestDispatcher.java:147)",
      "org.neo4j.server.rest.dbms.AuthorizationDisabledFilter.doFilter(AuthorizationDisabledFilter.java:49)",
      "org.neo4j.server.rest.web.CorsFilter.doFilter(CorsFilter.java:115)",
      "org.neo4j.server.rest.web.CollectUserAgentFilter.doFilter(CollectUserAgentFilter.java:69)",
      "java.lang.Thread.run(Thread.java:748)"
    ],
    "message": "/ by zero",
    "errors": [
      {
        "code": "Neo.ClientError.Statement.ArithmeticError",
        "message": "/ by zero"
      }
    ]
  },
  "errors": [
    {
      "code": "Neo.ClientError.Request.InvalidFormat",
      "message": "/ by zero"
    }
  ]
}
```
- List all property keys
```
GET http://localhost:7474/db/data/propertykeys
[
    "id",
    "age",
    "name",
    "country",
    "sex"
]
```
