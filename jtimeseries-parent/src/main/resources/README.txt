JTimeseries Changes and Version Information:
--------------------------------------------

1.0.5
------
First release with new client UI

1.0.6
------
This release changes a config bug for jtimeseries-server in which the logs don't roll correctly
There are changes to the config variables which determine where the logs go
LOG_FILE_DIRECTORY_PATH_PROPERTY now contains the directory path
LOG_FILE_NAME is a new variable which now contains the name of the file separately
This change does not affect the other modules, and does not require a client upgrade

1.0.7
------

A release to fix a performance defect with the aggregated series calculation.
This release is backwards compatible with 1.0.6 components

1.0.8
------

This release changes the identifiers used for the value source and capture when created via the
convenience methods on TimeSeriesContext, removing 'Raw Values' for non-aggregated series.
Backwards compatible with 1.0.6 on the server side.