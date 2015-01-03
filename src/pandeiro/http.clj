(ns pandeiro.http
  {:boot/export-tasks true}
  (:require
   [boot.pod           :as pod]
   [boot.util          :as util]
   [boot.core          :as core :refer [deftask]]
   [boot.task.built-in :as task]
   [pandeiro.http.impl :as http]))

(def default-port 3000)

(def serve-deps
  '[[ring/ring-jetty-adapter "1.3.1"]
    [ring/ring-core "1.3.1"]])

(deftask serve
  "Start a web server on localhost, serving resources and optionally a directory.
  Listens on port 3000 by default."

  [d dir  PATH str "The directory to serve."
   p port PORT int "The port to listen on. (Default: 3000)"]

  (let [worker (pod/make-pod (assoc-in (core/get-env) [:dependencies] serve-deps))
        port   (or port default-port)]

    (core/cleanup
     (util/info "\n<< stopping Jetty... >>\n")
     (pod/with-eval-in worker (.stop server)))

    (core/with-pre-wrap fileset
      (pod/with-call-in worker
        (def server (http/server {:dir ~dir, :port ~port})))
      (util/info "<< started Jetty on http://localhost:%d >>\n" port)
      fileset)))

