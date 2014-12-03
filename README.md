# boot-http

A simple HTTP `serve` task for use with [the boot build tool][boot]. Based almost in its
entirety on the example at [boot-cljs-example][boot-cljs-example].

## Usage

(The following assumes you have [boot installed][installboot] and updated.)

### Command line, no setup

This serves the **current directory** at port **3000**:

```bash
boot -d pandeiro/boot-http serve -b
```

### Composable by default

By default, the serve task doesn't block, so it can be chained with
other tasks in a pipeline. However if you just want to serve files,
you need to block to keep the task from exiting immediately. You can
do this with the -b or --block flags, as above.

### Within a project

In your `build.boot`, add `[pandeiro/boot-http "0.2.0"]` to :dependencies and
`(require '[pandeiro.http :refer :all])`. Then the command is shorter:

```bash
boot serve -b
```

### Other options

You can set the directory and port to serve on, too:

```bash
boot serve -d target -p 8888 -b
```

Once included in `build.boot` as a dependency, all the `serve`
task's options can be inspected:

```bash
boot serve -h
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


