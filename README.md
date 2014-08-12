# sommers [![Build Status](https://travis-ci.org/rksm/sommers.svg?branch=master)](https://travis-ci.org/rksm/sommers)

[![Clojars Project](http://clojars.org/rksm/sommers/latest-version.svg)](http://clojars.org/rksm/sommers)

Because [spinoffs](https://www.youtube.com/watch?v=qcba-ZgtsT4) are fun.

![](https://dl.dropboxusercontent.com/u/13564951/screenshots/sommers.png)

This project provides easy setup for nrepl-based browser sessions. It is
currently used for testing nrepl-cljs extensions. You can do other things with
it or just use [austin](https://github.com/cemerick/austin).

## Usage

```clojure
(require '[rksm.sommers.repl-env :as repl-env])
(repl-env/boot-cljs)
;; now do stuff like
(repl-env/eval '(+ 20 3))
(repl-env/eval '(.log js/console js/Array))
;; ...
(repl-env/shutdown)
```

## License

Copyright © 2014 Robert Krahn

Distributed under the Eclipse Public License version 1.0.
