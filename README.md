# boot-http

A simple HTTP `serve` task for use with [the boot build tool][boot]. Based almost in its
entirety on the example at [boot-cljs-example][boot-cljs-example].

## Usage

(The following assumes you have [boot installed][installboot] and updated.)

### Command line, no setup

This serves the **current directory** at port **3000**:

```bash
boot -d pandeiro/boot-http serve
```

### Within a project

In your `build.boot`, add `[pandeiro/boot-http "0.1.0"]` to :dependencies and
`(require '[pandeiro.http :refer :all])`. Then the command is shorter:

```bash
boot serve
```

### Other options

You can set the directory and port to serve on, too:

```bash
boot serve -d target -p 8888
```

Once included in `build.boot` as a dependency, all the `serve`
task's options can be inspected:

```bash
boot serve -h
```

### Chaining the serve task within a pipeline

By default, the serve task blocks, similar to most simple file servers.
If you want to carry out other tasks in a pipeline, however, you
can specify it to **NOT block** by adding the (-n | --no-block) flag, eg:

```bash
boot serve -n watch cljs
```

## Acknowledgements

The boot guys basically wrote all of this or walked me through any parts I had to change. Thanks!

## License

Copyright © 2014 Murphy McMahon

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[boot]:              https://github.com/boot-clj/boot
[boot-cljs-example]: https://github.com/adzerk/boot-cljs-example
[installboot]:       https://github.com/boot-clj/boot#install


