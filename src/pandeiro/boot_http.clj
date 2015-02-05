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

  [d dir           PATH str  "The directory to serve."
   H handler       SYM  sym  "The ring handler to serve."
   i init          SYM  sym  "A function to run prior to starting the server."
   c cleanup       SYM  sym  "A function to run after the server stops."
   r resource-root ROOT str  "The root prefix when serving resources from classpath"
   p port          PORT int  "The port to listen on. (Default: 3000)"
   k httpkit            bool "Use Http-kit server instead of Jetty"
   s silent             bool "Silent-mode (don't output anything)"
   R reload             bool "Reload modified namespaces on each request."]

  (let [port        (or port default-port)
        deps        (conj serve-deps (if httpkit httpkit-dep jetty-dep))
        worker      (pod/make-pod (update-in (core/get-env) [:dependencies]
                                             into deps))
        server-name (if httpkit "HTTP Kit" "Jetty")
        start       (delay
                     (pod/with-eval-in worker
                       (require '[pandeiro.boot-http.impl :as http]
                                '[pandeiro.boot-http.util :as u])
                       (when '~init
                         (u/resolve-and-invoke '~init))
                       (def server
                         (http/server
                          {:dir ~dir, :port ~port, :handler '~handler,
                           :reload '~reload, :httpkit ~httpkit,
                           :resource-root ~resource-root})))
                     (when-not silent
                       (util/info
                        "<< started %s on http://localhost:%d >>\n"
                        server-name port)))]
    (when (and silent (not httpkit))
      (silence-jetty!))
    (core/cleanup
     (when-not silent
       (util/info "<< stopping %s... >>\n" server-name))
     (pod/with-eval-in worker
       (if ~httpkit
         (server)
         (.stop server))
       (when '~cleanup
         (u/resolve-and-invoke '~cleanup))))
    (core/with-pre-wrap fileset
      @start
      fileset)))
