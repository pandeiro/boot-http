# boot-http

A simple HTTP `serve` task for use with [the boot build tool][boot]
that can serve resources, directories or a typical ring handler.


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

If you already have a `build.boot`, add `[pandeiro/boot-http "0.4.1"]` to `:dependencies` and
`(require '[pandeiro.boot-http :refer :all])`.

You can use boot-http for three different use cases:

#### 1. Serve classpath resources

```bash
boot serve wait
# or from build.boot or repl
(serve)
```

#### 2. Serve files on chosen directory

```bash
boot serve -d target wait
# or
(serve :dir "target")
```

That would serve the `target` directory if it exists. Instead of specifying a directory,
you can also specify a ring handler:

#### 3. Start server with given Ring handler

```bash
boot serve -H myapp.server/app wait
or
(serve :handler 'myapp.server/app)
```

### Composability

You may have noticed the `wait` task being used after all the command-line invocations so far. This is
because by itself, the `serve` task does not block and exits immediately.

What good is that? It means you can compose with other tasks.

In [boot-cljs-example][boot-cljs-example], for example, `serve` is
invoked like so:

```bash
boot serve watch speak reload cljs-repl cljs -usO none
# or
(comp (serve)
      (watch)
      (speak)
      (reload)
      (cljs-repl)
      (cljs :optimizations :none))
```

In that case, since serve is given a directory, it serves the directory and whatever
resources can be found on the classpath, and then gets out of the way.

### Other options

You can set the port to serve on, too:

```bash
boot -d pandeiro/http serve -d . -p 8888 wait
```


## API and Roadmap

Right now that is about it. It basically blends the functionality of `python3 -m http.server` and
a subset of `lein ring server`.

Feel free to add issues or comment [here][boot-discourse] if
you have any ideas.


## Acknowledgements

The boot guys basically wrote all of this or walked me through any parts I had to change. Thanks!


## License

Copyright © 2015 Murphy McMahon

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[boot]:              https://github.com/boot-clj/boot
[boot-cljs-example]: https://github.com/adzerk/boot-cljs-example
[installboot]:       https://github.com/boot-clj/boot#install
[boot-discourse]:    http://hoplon.discoursehosting.net/t/boot-http-0-4-0/361
