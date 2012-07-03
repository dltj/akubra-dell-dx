# Project Overview

The akubra-dell-dx plugin is designed to allow you to use a the Dell DX Object Storage Platform to store data from an application that uses the Akubra framework to store blobs (most notably the Fedora Repository software). The plugin was developed against Dell DX Storage and we will use Dell terminology throughout the instructions here, but it is likely that everything will work with Caringo storage with the appropriate substitutions (e.g. using the Caringo SDK instead of the Dell Object Storage SDK, etc.).

Moreover the plugin was developed with the intent of using it with Fedora for storage of digital objects in a repository, but it should be able to function in any application that uses Akubra with at most minimal modification. Once again, the instructions are written with the idea of using the plugin with Fedora, but you should be able to get a general sense of how that adapts to other applications.

# Overview of Use

We assume that you are familiar with your DX Storage and with the Fedora repository system. (or whatever other Akubra client you are using, but we will assume Fedora for our examples). Note that these instructions are written for Fedora 3.4.2 and 3.5 (with note of the differences) - small modifications may be needed for later versions of Fedora. 

These instructions are a work in progress - if you try them and have difficulty please contact us and we'll see what we can do. The most up to date documentation will be on the akubra-dell-dx Github wiki at https://github.com/medusa-project/akubra-dell-dx/wiki/Home.

To use the akubra-dell-dx plugin, you need to do the following things:

1. [[Set up DX Storage | Set up storage]]
2. [[Download and compile the akubra-dell-dx code, or use the precompiled jar | Download and compile]]
3. [[Install the jar]] into your Fedora installation
4. [[Configure your fedora installation]] to use the akubra-dell-dx plugin
5. [[Confirm]] that everything is working

# Set up Storage

The akubra-dell-dx adapter uses named objects in the Dell DX Storage and hence requires that you have a bucket available to store them. We also recommend that you have a tenant/domain where the bucket resides (the adapter may work with the default domain, but this is not confirmed). 

In addition, if you wish you may have limited authorization. Specifically the plugin may be configured to use a user and password from the security realm for operating on objects in the bucket. If you choose to do this then this user must be able to create objects in the bucket. When an object is created the same user/password/realm will be used to allow that user all permissions on the created object.

Consult the DX Storage documentation for how to accomplish these things - it's beyond the scope of this documentation. Later in this guide we will cover how to configure the plugin to reflect the choices that you have made here.

# Get jar

## Using precompiled jar

A precompiled jar is available on [the Downloads page](https://github.com/medusa-project/akubra-dell-dx/downloads). 

##  Download and compile

If you wish to do this you'll need the jars that come with the DX Object Storage SDK. CAStorSDK.jar is the jar directly used by this plugin, but it depends on the other included jars, which may have modifications from their canonical counterparts (and hence cannot be replaced by them). 

Ingest all of these into your local maven repository, e.g. 

mvn install:install-file -Dfile=/path/to/jmdns-2.1.jar -DgroupId=com.caringo
-DartifactId=jmdns -Dversion=2.1 -Dpackaging=jar

Clone the code for the akubra-dell-dx project. Depending on the version of the Dell Object Storage SDK that you have you may need to modify the root level pom.xml in order to make the versions match those of the jars you've ingested and that go along with your version of the CAStorSDK.jar jar. 

Once that is done you should be able to build the jar that you need by doing 'mvn package -DskipTests' at the root level. If you want to do the tests you need to go to src/test/java/edu/illinois/medusa, copy test-config.properties.example to test-config.properties, and fill in appropriate configuration options to access a bucket on your DX Storage that can be used for testing. Then just do 'mvn package' at the root level.

After this is complete a 'target' directory should have been created at the root level which will contain the jar that is needed, akubra-dell-dx-x.y-jar-with-dependencies.jar.

# Install jar

After you have compiled or downloaded the jar you should have jar file with a name like akubra-dell-dx-x.y-jar-with-dependencies.jar (if you compiled it yourself it will be in the target directory). This needs to be copied into the Fedora installation.

We recommend that you have Fedora set up but that it be empty (if there are objects in another store then Fedora's database and triple store may be confused if you start using the DX Storage). You will, however, need to start up Fedora once in order to create some directories in the Fedora installation that don't come into being until after the first start up. So if you haven't yet done so, start and stop Fedora.

When this is done copy the above jar into the appropriate directory in the tomcat running fedora, typically $FEDORA_HOME/tomcat/webapps/fedora/WEB-INF/lib. We assume an analogous procedure would work for other servlet containers, although we have not confirmed this. If you are developing this plugin you may just want to soft-link to the copy in your development checkout instead.

# Configure Fedora

As noted previously the exact location and format of the config file may vary. This is for Fedora 3.4.2 or 3.5.

We assume that on installation you configured Fedora to use Akubra and not the legacy storage adapter.

## Overview

There are two steps - first we configure Fedora to use the akubra-dell-dx blob stores. As part of that configuration we specify a properties file that each of the blob stores will use to complete its configuration when Fedora loads it. This properties file will contain information needed to connect to the DX Storage.

## Configuring akubra-llstore.xml

In $FEDORA_HOME/server/config there is a file called akubra-llstore.xml (in Fedora 3.5 this has moved into the spring subdirectory of the above directory, i.e. $FEDORA_HOME/server/config/spring). This is a Spring Bean config file for Akubra. Back it up. If you read it you'll see that Fedora uses two separate blob stores - one for FOXML objects and one for managed datastreams. Each of these is an org.akubraproject.map.IdMappingBlobStore, and is itself constructed from an underlying blob store and id mapper. In the default implementation these are org.akubraproject.fs.FSBlobStore and org.fcrepo.server.storage.lowlevel.akubra.HashPathIdMapper. 

You'll find two beans in the file that look like:

    <bean name="objectStore" class="org.akubraproject.map.IdMappingBlobStore" singleton="true">
        <constructor-arg value="urn:example.org:objectStore"/>
        <constructor-arg><ref bean="fsObjectStore"/></constructor-arg>
        <constructor-arg><ref bean="fsObjectStoreMapper"/></constructor-arg>
    </bean>

    <bean name="datastreamStore" class="org.akubraproject.map.IdMappingBlobStore" singleton="true">
        <constructor-arg value="urn:fedora:datastreamStore"/>
        <constructor-arg><ref bean="fsDatastreamStore"/></constructor-arg>
        <constructor-arg><ref bean="fsDatastreamStoreMapper"/></constructor-arg>
    </bean>

You want to replace these with something like this:

    <bean name="objectStore" class="org.akubraproject.map.IdMappingBlobStore" singleton="true">
        <constructor-arg value="urn:example.org:objectStore"/>
        <constructor-arg><ref bean="caringoObjectStore"/></constructor-arg>
        <constructor-arg><ref bean="caringoObjectStoreMapper"/></constructor-arg>
    </bean>

    <bean name="datastreamStore" class="org.akubraproject.map.IdMappingBlobStore" singleton="true">
        <constructor-arg value="urn:fedora:datastreamStore"/>
        <constructor-arg><ref bean="caringoDatastreamStore"/></constructor-arg>
        <constructor-arg><ref bean="caringoDatastreamStoreMapper"/></constructor-arg>
    </bean>

Now we need to define the stores and id mappers. The mappers are easy:

    <bean name="caringoObjectStoreMapper"
        class="edu.illinois.medusa.FedoraIdMapper"
        singleton="true">
    </bean>

 and similarly for the datastream store mapper using the name caringoDatastreamStoreMapper.

Now for the actual object stores, you need to specify a name for the store (as far as we know the exact name doesn't matter) and a path to the properties file that will have the rest of the configuration.

    <bean name="caringoObjectStore"
    	  class="edu.illinois.medusa.FedoraObjectBlobStore"
	  singleton="true">
      <constructor-arg value="caringo"/>
      <constructor-arg value="/path/to/config.properties" />
    </bean>

Do the same for the datastream object store, using the class FedoraDatastreamBlobStore instead of FedoraObjectBlobStore and the name caringoDatastreamStore. You might also change the store name, but we haven't noticed that it makes a difference if you do not. In most cases you can use the same properties file for both.

Delete the old fsWhatever bean definitions or comment them out.

## Properties file

In the previous step you configured the BlobStores to point to a properties file. This can live anywhere on your filesystem. The same directory as akubra-llstore.xml is a fine choice. This is a standard Java properties file with keys explained below (in logically related groups). There is an example in the examples directory of the akubra-caringo source distribution.

### BlobStore configuration

Currently this is only necessary for the FedoraBlobStore (and subclasses).

* store.repository-name - the value will be used to set the x-fedora-meta-repository-name header on all stored objects. This allows for enumeration of objects in the Fedora repository by the DX Content Router Software. Ideally it should be unique in your DX Storage (i.e. if you use it for more than one instance of Fedora the values should be different for each), although there may be ways to finesse this if needed.

### Locator/Connection configuration 

Required except as noted. Needed to actually connect to your DX Storage. There are two aspects to this, configuring a "locator" that helps the plugin find and maintain storage nodes, and configuring how it then connects to storage to transact objects with the DX Storage.

#### Locator configuration

The locator enables the plugin to monitor traffic in order to discover new storage nodes and refrain from using any storage nodes that may be experiencing difficulty. Four different types are available as detailed below.

 * connection.locator_type - this must be one of the following: static, round_robin, scsp_proxy, or zeroconf. Each connector requires slightly different configuration:
   1. static - this uses a static list of IP addresses or hosts
     * connection.host (required) - this is a comma separated list of IP addresses or hosts of storage nodes in your cluster (required)
     * connection.port (default 80) - the port on which to connect to storage nodes. Note that this is the same as in the connection section
     * connection.locator_retry_timeout (default 300) - determines for how many seconds the locator will remove an IP from the pool of storage nodes used after it experiences a problem.
   2. round_robin - used if you have associated round-robin DNS with your storage nodes
     * connection.host (required) - the DNS name used for your storage nodes. 
     * connection.port (default 80) - same as for static locator
   3. scsp_proxy - may be used if you are using the DX SCSP proxy
     * connection.cluster_name (required) - the name of the SCSP cluster
     * connection.proxy_address (required) - the IP or DNS name of your SCSP proxy
     * connection.proxy_port (default 80) - the port for you SCSP proxy
     * connection.locator_retry_time (default 300) - same as for static locator
   4. zeroconf
     * connection.cluster_name (required) - cluster name

#### Connection configuration

* connection.bucket (required) - the bucket where your objects will reside. You must create this before using the adapter - it does not do so
* connection.domain (required) - the tenant/domain for your bucket. If the bucket is in the default domain it may be possible to omit this, but that is untested. You must create this before using the adapter - it does not do so.
* connection.port (default 80) - the port on which to connect to storage nodes. Note that this is the same as in the locator section for static or round_robin locators.
* connection.maxConnectionPoolSize (default 4) - maximum size of connection pool from plugin to storage
* connection.maxRetries (default 4) - number of retries for HTTP request between plugin and storage 
* connection.connectionTimeout (default 120) - connection timeout time (in seconds) for HTTP request between  plugin and storage
* connection.poolTimeout (default 1) - amount of time (in seconds) an unused open connection remains in the connection pool before being closed

### Authentication configuration 

(Optional, only used if all values are supplied) 

Use if your bucket is protected by authentication. The supplied user must be able to perform all operations (including creation) on objects in the bucket using the supplied security realm. Note that all of the actual authentication realms, users and passwords must be set up in the normal way for DX Storage - the plugin does not do any of that.

 * authentication.user - a user in the supplied realm satisfying the above
 * authentication.password - password for authentication.user
 * authentication.realm - security realm used to protect the bucket

### Content Router configuration 

(only applicable to FedoraBlobStores and subclasses, optional, only used if enough values are supplied in the obvious sense). 

You need this if you want to be able to list all blob ids, e.g. for recovery of the fedora database and indexes from raw object in storage. The simplest and typical usage would be to create a channel on the DX Content Router that publishes everything with the x-fedora-meta-repository-name header equal to store.repository-name. Then a FedoraBlobStore will be able to enumerate all of the objects, and the subclasses will be able to enumerate objects or datastreams as appropriate (they know how to filter all objects into just the FOXML objects or managed datastreams using the stored x-fedora-meta-stream-id header). In this case both of the BlobStores can share the same content-router channel. It is conceivable that someone may want to route these two classes of objects separately anyway - in this case it's fine to use two different channels as afforded by the configuration parameters below. But if you don't know why you need this it's very unlikely that you do. 

 * content-router.host - the host or ip for the DX Content Router
 * content-router.port - the port for the DX Content Router
 * content-router.channel - channel on the DX Content Router if a more specific value is not set
 * content-router.object-channel - channel on the DX Content Router used by FedoraObjectBlobStore (defaults to content-router.channel)
 * content-router.datastream-channel - channel on the DX Content Router used by FedoraDatastreamBlobStore (defaults to content-router.channel)

### Fixed Headers 

(optional, used only by HintedBlobStores or subclasses, including the FedoraBlobStores) 

These give you the ability to set metadata that will be added to every object that you store through the plugin. The key will be header.name-of-dx-header and the value simply the value you want for the header. [If you want multiple headers with the same name you can pass a value that separates them with the | character; the \ character functions as an escape - whatever character follows it appears in the value (so using it allows | or \ itself to be incorporated into a value). Note that because of the way that properties files themselves use \ as a quote character you'll have to double it up to get the desired effect]. Examples follow.
 * Let's say you want to set a Lifepoint header on every object indicating that there should be three copies in your DX Storage. You could do that by setting:
`header.Lifepoint = [] reps=3`
which would send the following header with each object:
`Lifepoint: [] reps=3`
 * Let's say you want the set the x-fedora-meta-example header twice, once with the value abc and once with the value cde. Then you'd set `header.x-fedora-meta-example = abc|cde`.
 * If you want to set a blank header, use | as the value alone. I.e. to set x-fedora-meta-blank at blank, use `header.x-fedora-meta-blank = |`.
 * Finally, let's say you want to set the x-fedora-meta-example header with the values `ab\\c`, `de|`, and `fg`. Then you'd do `header.x-fedora-meta-example = ab\\\\\\\\c|de\\||fg`. Because of the way that Java properties file quoting works, this will pass the value `ab\\\\c|de\||fg` to the plugin. Because the plugin interprets \ as a quoting character and parses from left to right it will see the value `ab\\c` then a | separating values, then `de|` (it sees the first pipe as part of the value because it is quoted), then another | separating values, and finally `fg`. 

# Check installation

You should now be able to start Fedora, pull up the admin interface in a web browser, and create objects. Fedora pids map in a standard way to storage urls, so you can also check your objects directly in a web browser. For example, if you create an object with the pid 'test:1' and your Fedora is running at localhost:8080/fedora, then you can see the object through Fedora at http://localhost:8080/fedora/objects/test:1. If you are running your DX Storage at storage.url.edu with domain my.storage.domain and bucket mybucket, then you should also be able to see your object (if you've enabled authentication that will be required) at:

http://storage.url.edu/mybucket/info:fedora/test:1?domain=my.storage.domain