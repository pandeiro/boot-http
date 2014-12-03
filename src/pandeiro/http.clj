(ns pandeiro.http
  {:boot/export-tasks true}
  (:require
   [boot.pod           :as pod]
   [boot.util          :as util]
   [boot.core          :as core]
   [boot.task.built-in :as task]))

(core/deftask serve
  "Start a web server on localhost and serve a directory, blocking
   the boot task pipeline by default.

   If no directory is specified the current one is used.  Listens
   on port 3000 by default."
  [d dir      PATH     str  "The directory to serve."
   p port     PORT     int  "The port to listen on."
   b block             bool "Blocking (for standalone use)"]
  (let [worker   (pod/make-pod
                  {:dependencies '[[ring/ring-jetty-adapter "1.3.1"]
                                   [compojure "1.2.1"]]})
        dir      (or dir ".")
        port     (or port 3000)
        block    (or block false)]
    (core/cleanup
     (util/info "\n<< stopping Jetty... >>\n")
     (pod/eval-in worker (.stop server)))
    (comp
     (core/with-pre-wrap
       (pod/eval-in worker
                    (require '[ring.adapter.jetty :refer [run-jetty]]
                             '[compojure.route    :refer [files]])
                    (def server
                      (run-jetty (files "/" {:root ~dir}) {:port ~port :join? false})))
       (util/info "<< started Jetty on http://localhost:%d (serving: %s) >>\n" port dir))
     (if block
       (task/wait)
       identity))))
