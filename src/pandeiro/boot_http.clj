(ns pandeiro.boot-http
  {:boot/export-tasks true}
  (:require
   [boot.pod           :as pod]
   [boot.util          :as util]
   [boot.core          :as core :refer [deftask]]
   [boot.task.built-in :as task]))

(def default-port 3000)

(def serve-deps
  '[[ring/ring-core "1.3.2"]
    [ring/ring-devel "1.3.2"]])

(def jetty-dep
  '[ring/ring-jetty-adapter "1.3.2"])

(def httpkit-dep
  '[http-kit "2.1.19"])

(defn- silence-jetty! []
  (.put (System/getProperties) "org.eclipse.jetty.LEVEL" "WARN"))

(deftask serve
  "Start a web server on localhost, serving resources and optionally a directory.
  Listens on port 3000 by default."

  [d dir           PATH str  "The directory to serve; created if doesn't exist."
   H handler       SYM  sym  "The ring handler to serve."
   i init          SYM  sym  "A function to run prior to starting the server."
   c cleanup       SYM  sym  "A function to run after the server stops."
   r resource-root ROOT str  "The root prefix when serving resources from classpath"
   p port          PORT int  "The port to listen on. (Default: 3000)"
   k httpkit            bool "Use Http-kit server instead of Jetty"
   s silent             bool "Silent-mode (don't output anything)"
   R reload             bool "Reload modified namespaces on each request."
   n nrepl         REPL edn  "nREPL server parameters e.g. \"{:port 3001}\""]

  (let [port        (or port default-port)
        deps        (conj serve-deps (if httpkit httpkit-dep jetty-dep))
        worker      (pod/make-pod (update-in (core/get-env) [:dependencies]
                                             into deps))
        server-name (if httpkit "HTTP Kit" "Jetty")
        start       (delay
                     (pod/with-eval-in worker
                       (require '[pandeiro.boot-http.impl :as http]
                                '[pandeiro.boot-http.util :as u]
                                '[clojure.tools.nrepl.server :refer [start-server]])
                       (when '~init
                         (u/resolve-and-invoke '~init))
                       (def server
                         (http/server
                          {:dir ~dir, :port ~port, :handler '~handler,
                           :reload '~reload, :httpkit ~httpkit,
                           :resource-root ~resource-root}))
                       (def repl-server
                         (if ~nrepl
                           (let [bind (if (:bind ~nrepl) (:bind ~nrepl) "127.0.0.1")
                                 repl-server (if (:port ~nrepl)
                                               (start-server :port (:port ~nrepl) :bind bind)
                                               (start-server :bind bind))]
                             (println "boot-http nREPL started on " bind " port " (:port repl-server))
                             repl-server))))
                     (when-not silent
                       (util/info
                        "<< started %s on http://localhost:%d >>\n"
                        server-name port)))]
    (when (and silent (not httpkit))
      (silence-jetty!))
    (core/cleanup
     (pod/with-eval-in worker
       (when server
         (when-not silent
           (util/info "<< stopping %s... >>\n" server-name)))
       (when repl-server
         (println "stopping boot-http nREPL server")
         (.stop repl-server)))
     (pod/with-eval-in worker
       (if ~httpkit
         (server)
         (.stop server))
       (when '~cleanup
         (u/resolve-and-invoke '~cleanup))))
    (core/with-pre-wrap fileset
      @start
      fileset)))
