# Galaxy Tool Maker

A Java program to generate the tool XML wrappers for Galaxy.  The tool first queries a Service Manager instance for a list of services, then queries each service to obtain the service's metadata. Using this information it is possible to generate XML wrappers for each service given the appropriate template.

## Usage

```
java -jar tool-wrapper-x.y.z.jar -s [vassar|brandeis] <OPTIONS> /path/to/template/file
```

where `x.y.z` is the current version number.

### Required Parameters

* -s, --server<br/>The service manager instance that will be queried for available services. Must be one of *vassar* or *brandeis*. 

### Options

* -e, --engine<br/>The template engine to use. Must be one of *jsp* or *groovy*. If not specified the *groovy* template engine will be used.
* -o, --output<br/>The directory where the generated XML files will be written.  If omitted the XML output will be written to STDOUT.
* -q, --query<br/>A sequece of name/value pairs that will be used as search criteria when querying the service manager.
* -v, --version<br/>Displays the current application version string.
* -h, --help<br/>Displays a simple usage message.

When saving the tool XML to the `--output` directory the filename will be derived by adding an `.xml` suffix to the `serviceId` (minus the gridId prefix) of the service. For example the XML for a service with the ID `foo:sample.service_1.0.0` will be saved to a file named `sample.service_1.0.0.xml`

The `--query` string is a whitespace delimited sequence of name=value pairs where the *name* is one of the values:
* active
* endpointUrl
* instanceType
* ownerUserId
* registeredDate
* serviceDescription
* serviceId
* serviceName
* serviceType
* serviceTypeDomain
* updatedDate

**Note** These are the fields of the JSON document returned by the service manager. For example, see [http://api.lappsgrid.org/services/brandeis](http://api.lappsgrid.org/services/brandeis). The query matches if the the field value contains *value* as a substring.  Matches are case-insensitive.

**Example**

The query to find all GATE services version 2.2.0:

```
-q 'serviceName=gate serviceId=2.2.0' ...
```

### Bindings

The following variables are available to be used in a template:

|Name  |Type  | Value |
|-----|------|-------|
| gridId | String | ID of the service manager hosting the service |
| serviceId | String | ID of the service on the service manager |
| serviceName | String | service name |
| serviceDescription | String | service description |
| toolVersion | String | value of the --version command line parameter |
| inputFormats | String | input formats accepted by the tool |
| outputFormat | String | output format produced by the tool |
| requires | List&lt;String&gt; | list of required annotation types |
| produces | List&lt;String&gt; | list of annotation types produced by the tool |


The *gridId*, *serviceId*, *serviceName*, and *serviceDescription* values are obtained from the JSON document returned by the Service Manager.  For example, see [http://api.lappsgrid.org/services/brandeis](http://api.lappsgrid.org/services/brandeis). The *inputFormats*, *outputFormat*, *requires*, and *produces* values are obtains from the metadata retrieved from each service.  For example see [http://api.lappsgrid.org/metadata?id=anc:gate.tokenizer_2.2.0](http://api.lappsgrid.org/metadata?id=anc:gate.tokenizer_2.2.0).

### Engines

Users have the option of using either the [groovy.text.SimpleTemplateEngine](http://docs.groovy-lang.org/2.4.10/html/documentation/template-engines.html#_simpletemplateengine) (jsp) or [groovy.xml.MarkupBuilder](http://docs.groovy-lang.org/2.4.10/html/documentation/template-engines.html#_the_markuptemplateengine) (groovy) template engines to generate the output files.  

The engine used will determine the syntax of the template files.  The `groovy` template engine uses a Groovy DSL syntax for the template.

```
tool(id:$serviceId, name:$serviceName, version:$toolVersion) {
    description $serviceDescription
    ...
}
```

While the `jsp` template engine uses a JSP (Java Server Pages) like syntax for the template:
```
<tool id="$serviceId" name="$serviceName" version="$toolVersion">
    <description><%= serviceDescription %></description>
    ...
    <% requires.each { %> * $it
    <% } %>
    ...
</tool>
```

In both syntaxes variable substitution is done with `$variable` while in the JSP syntax `<%= variable %>` can also be used.  See the [gate-template.groovy](gate-template.groovy) and [gate-template.xml](gate-template.xml) files for complete examples using flow of control and conditionals.

