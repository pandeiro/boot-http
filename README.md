# boot-http [![Build Status][badge]][build]

A simple HTTP `serve` task for use with [the boot build tool][boot]
that can serve resources, directories or a typical ring handler.

[](dependency)
```clojure
[pandeiro/boot-http "0.7.4-SNAPSHOT"] ;; latest release
```
[](/dependency)

## Usage

(The following examples assume you have [boot installed][installboot] and updated.)

### Command line, no setup

This serves the **current directory** at port **3000**:

```bash
boot -d pandeiro/boot-http serve -d . wait
```

To inspect the meanings of the flags and other tasks, use boot's built-in
documentation mechanism:

```bash
boot -d pandeiro/boot-http -h       # show all tasks on the classpath
boot -d pandeiro/boot-http serve -h # show serve's usage
```

### Within a project

If you already have a `build.boot`, add
`pandeiro/boot-http` to `:dependencies`
and `(require '[pandeiro.boot-http :refer :all])`.

You can use boot-http for three different use cases:

#### 1. Serve classpath resources

```bash
boot serve wait   # or from REPL: (boot (serve) (wait))
```

#### 2. Serve files on chosen directory

```bash
boot serve -d target wait   # or at the REPL: (boot (serve :dir "target") (wait))
```

That would serve the `target` directory if it exists.

Instead of specifying a directory, you can also specify a ring handler:

#### 3. Start server with given Ring handler

```bash
boot serve -H myapp.server/app -R wait   # (boot (serve :handler 'myapp.server/app :reload true) (wait))
```

### Composability

You may have noticed the `wait` task being used after all the
command-line invocations so far. This is because by itself, the
`serve` task does not block and thus exits immediately.

What good is that? It means you can compose with other tasks.

In [boot-cljs-example][boot-cljs-example], for example, `serve` is
invoked like so:

```bash
boot serve watch speak reload cljs-repl cljs -usO none
```

which is, again, the same as:

```clojure
(comp (serve)
      (watch)
      (speak)
      (reload)
      (cljs-repl)
      (cljs :optimizations :none))
```

In that case, since `serve` is given a directory, it serves the directory and whatever
resources can be found on the classpath, and then gets out of the way.

### Other options

#### -p / --port

Use a specific port. A value of `0` will automatically bind to a free port.
The actual port number being used is available as `:http-port` on the fileset.

```bash
boot -d pandeiro/boot-http serve -d . -p 8888 wait
```

#### -k / --httpkit

Use the HTTP Kit webserver instead of Jetty.

```bash
boot -d pandeiro/boot-http serve -d . -k wait  # uses httpkit
```

#### -n / --nrepl

Start an nREPL server for access to the http server. Accepts
`:port` and `:bind` options for setting nREPL server IP
and port.

```bash
boot -d pandeiro/boot-http serve -d . -n "{:port 3001}"
```

#### -i / --init and -c / --cleanup

Setup and teardown functions to run.

#### -s / --silent

Silences all output.

#### -R / --reload

Wrap provided ring handler with ring-reload.

#### -N / --not-found

Use the provided symbol's function to handle requests for results that
are not found.

```bash
boot serve -d target -N myapp.server/custom-not-found wait
```

## API and Roadmap

Right now that is about it. It basically blends the functionality of
`python3 -m http.server` and a subset of `lein ring server`.

Feel free to add issues or comment [here][boot-discourse] if
you have any ideas.


## Acknowledgements

The boot guys basically wrote all of this or walked me through any
parts I had to change. Thanks!


## License

Copyright © 2015 Murphy McMahon

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[boot]:              https://github.com/boot-clj/boot
[boot-cljs-example]: https://github.com/adzerk/boot-cljs-example
[installboot]:       https://github.com/boot-clj/boot#install
[boot-discourse]:    http://hoplon.discoursehosting.net/t/boot-http-0-4-0/361
[build]:             https://travis-ci.org/pandeiro/boot-http
[badge]:             https://travis-ci.org/pandeiro/boot-http.png?branch=master
