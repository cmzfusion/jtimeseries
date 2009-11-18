This directory contains jar dependencies for jtimeseries-ui which are otherwise not available 
in the central maven repositories. 

To enable your maven build to work correctly you will need to install these jars into your local
maven repository (e.g. using the .bat files provided if you are using windows as your development 
platform).

OD-SWING:
This is Object Definitions Ltd. library of swing utilities and helper classes also available under 
the lgpl license

SWINGCOMMAND:
This is a Object Definitions Ltd. framework for asynchronous swing commands, similar in purpose to 
swingworker, available under the apache 2.0 open source license.

JIDE JARS:
The jtimeseries project has an open source license, granted by jidesoft, to use the jide components and
jide jars which are included in this directory, although in fact only jide-grids and jide-common are 
currently required. Other projects which link to jtimeseries-ui will need to package these jars,
although such projects will need a separate developer license from jide if they wish to add 
new features which make use of the jide components directly, rather than through jtimeseries 
n.b. Some elements of jide are themselves open source, see the http://www.jidesoft.com/ website 
for details.



