(ns pandeiro.boot-http
  {:boot/export-tasks true}
  (:require
   [boot.pod           :as pod]
   [boot.util          :as util]
   [boot.core          :as core :refer [deftask]]
   [boot.task.built-in :as task]))

(def default-port 3000)

(def serve-deps
  '[[ring/ring-core "1.3.2"]])

(def jetty-dep
  '[ring/ring-jetty-adapter "1.3.2"])

(def httpkit-dep
  '[http-kit "2.1.18"])

(deftask serve
  "Start a web server on localhost, serving resources and optionally a directory.
  Listens on port 3000 by default."

  [d dir           PATH str  "The directory to serve."
   H handler       SYM  sym  "The ring handler to serve."
   r resource-root ROOT str  "The root prefix when serving resources from classpath"
   p port          PORT int  "The port to listen on. (Default: 3000)"
   k httpkit            bool "Use Http-kit server instead of Jetty"]

  (let [port        (or port default-port)
        deps        (conj serve-deps (if httpkit httpkit-dep jetty-dep))
        worker      (pod/make-pod (update-in (core/get-env) [:dependencies]
                                             into deps))
        server-name (if httpkit "HTTP Kit" "Jetty")
        start       (delay
                     (pod/with-eval-in worker
                       (require '[pandeiro.boot-http.impl :as http])
                       (def server
                         (http/server
                          {:dir ~dir, :port ~port, :handler '~handler
                           :httpkit ~httpkit, :resource-root ~resource-root})))
                     (util/info
                      "<< started %s on http://localhost:%d >>\n"
                      server-name port))]
    (core/cleanup
      (util/info "<< stopping %s... >>\n" server-name)
      (pod/with-eval-in worker
        (if ~httpkit
          (server)
          (.stop server))))
    (core/with-pre-wrap fileset
      @start
      fileset)))
