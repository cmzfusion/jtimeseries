This module only exists as a mechanism to roll up the jtimeseries-ui classes and all 
other dependencies into one jar for deployment, which can be convenient in some cases.

Use the maven assembly:assembly target to generate the roll-up jars in the target directory,
having built and installed the other modules first.

(There must exist a simpler way to get the assembly to work as part of the main ui module, 
but I couldn't find a way to do this with the version of the maven assembly plugin I was 
using at the time - if you find a way to do this please let me know and we can clean this 
module up)