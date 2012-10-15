
JTimeSeries consists of

- an API for collecting and publishing timeseries data,
- A Round Robin database to subscribe to UDP published series data and store it to flat files
- A rich client front end UI (TimeSerious) to view it
- An agent, to run server-side, collect data and publish that data to a server instance
- The API also includes a simple embedded web server (based on NanoHTTP) which allows to provide a restful web service so that data can be viewed in a browser or queried by a remote process

 Up to this point, jtimeseries has mostly been used to collect and analyze component performance metrics, although there is no reason why the series data recorded could not represent something else entirely

 JTimeSeries consists of a number of maven modules

 Key Modules:
  jtimeseries               This is the API itself, which allows timeseries data to be recorded locally and/or published
  jtimeseries-server        A Round Robin timeseries database.
  jtimeseries-agent         Agent to collect and publish stats from a server, parses standard input to capture numeric values
  jtimeseries-ui            This is TimeSerious - A rich client UI to subscribe to and graph data from a jtimeseries http service

 Also included are:
  jtimeseries-parent -      This is a parent pom which has shared settings inherited by each jtimeseries module. Build and install it first
  jtimeseries-component -   Common classes shared by jtimeseries server-side components, the agent and server
  jtimeseries-webstart -    Builds and signs the jnlp and jars for a java webstart project to provide web-based access to the UI
  jtimeseries-webapp -      Wraps jtimeseries-webstart jars into a .war archive for deployment onto a servlet container


Notes on Building JTimeSeries

You will need Maven 3.0.x  and a JDK 1.5+

1. First build and install the jtimeseries-parent module first into your local maven repo, by locating it and doing a mvn install
Nothing else will build without this

2. For jtimeseries-component, make sure your local maven repo contains a copy of the Sun jmxtools.jar version 1.2.1
This is an unfortunate manual step, which is required because Sun/Oracle don't allow jmxtools to be published in the central Maven repo
I may remove this dependency, in the meantime you can download and install the jar manually from here:
http://www.oracle.com/technetwork/java/javase/tech/javamanagement-140525.html

3. For jtimeseries-ui, a number of other jars, swingcommand.jar, od-configutil.jar and od-swing.jar are dependencies
These are not yet in maven central (and some may never make it that far..)
The source for all the above is accessible on github, but to make things easier, prebuilt jars are also included with
the checked-out jtimeseries codebase
The .jar files, and .bat file scripts to install them into your local maven repo, are provided at /jtimeseries-ui/src/main/lib.

Once all Dependencies are installed you should be able to build the modules

The top level pom.xml jtimeseries-project will attempt to build all the modules together, apart from the parent
The first time it's probably less confusing to try them individually one by one, in the order defined in the project pom


