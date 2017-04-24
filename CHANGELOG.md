# 0.8.0

- Fix: cleanup handlers properly shut down Jetty and nREPL servers

# 0.7.6

- Add: load user-specific Boot nREPL middleware

# 0.7.5

- Add: -t/--ssl and -T/--ssl-props args for HTTPS support

# 0.7.4

- Fix: not-found handler applies to resource handler

# 0.7.3

- Add: not-found handler option and test

# 0.7.2

- Add: accurate reporting / documentation of port 0
- Add: initial tests for resources and directory handlers
- Remove: explicit boot/core dependency
- Remove: scope "provided" for clojure dependency

# 0.7.1

- Add: boot.properties file
- Bump Boot version to 2.4.2
- Fix: reload option now watches all source directories

# 0.7.0

- Add: -n/--nrepl flag for nREPL access to http server
- Bumped dependencies

# 0.6.3

- Add: automatic handling of slashes on index.html resources

# 0.6.2

- Add: -R/--reload flag to reload modified namespaces on each request

# 0.6.1

- Bump HTTP Kit to 2.1.19 to fix RejectedExecutionException

# 0.6.0

- Add: init and cleanup arguments for running functions before and after server

# 0.5.2

- Fix: reduce Jetty logging level when --silent

# 0.5.1

- Add: silent mode (-s/--silent) to silence output

# 0.5.0

- Fix: directory listings and files take precedence over resources with :dir option
- Add: -k/--httpkit option to use Http-kit instead of Jetty

# 0.4.2

- Fix: create a pod for each task instance instead of sharing one

# 0.4.1

- Add -r/--resource-root to specify a route prefix for resources

# 0.4.0

- Remove -b/--block option (use wait instead)
- Add -H/--handler to serve a ring handler
- Bump boot to 2.0.0-rc5
