(ns pandeiro.http
  {:boot/export-tasks true}
  (:require
   [boot.pod           :as pod]
   [boot.util          :as util]
   [boot.core          :as core :refer [deftask]]
   [boot.task.built-in :as task]))

(def ^:private serve-deps
  '[[ring/ring-jetty-adapter "1.3.1"]
    [ring/ring-core "1.3.1"]])

(def default-port 3000)

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
      (pod/with-eval-in worker
        (import  '[java.net URLEncoder URLDecoder])
        (require '[clojure.java.io :as io]
                 '[clojure.string  :as s]
                 '[ring.adapter.jetty :refer [run-jetty]]
                 '[ring.util response]
                 '[ring.middleware file resource content-type not-modified])

        ;;
        ;; Directory serving
        ;;
        (def root-path
          "Used to resolve URIs to filepaths when serving from a specific directory"
          ~(if dir
             (.getPath (io/file dir))
             ""))

        (defn path-diff [path]
          (s/replace path (re-pattern (str "^" root-path)) ""))

        (defn filepath-from-uri [uri]
          (str root-path (java.net.URLDecoder/decode uri "UTF-8")))
        
        (defn list-item [file]
          (format "<li><a href=\"%s\">%s</a></li>"
                  (path-diff (.getPath file))
                  (.getName file)))

        (defn index-file-exists? [files]
          (first (filter #(#{"index.html" "index.htm"} (.getName %)) files)))

        (defn index [{:keys [uri] :as req}]
          (let [directory (io/file (filepath-from-uri uri))]
            (when (.exists directory)
              (let [files (sort (.listFiles directory))]
                {:status  200
                 :headers {"Content-Type" "text/html"}
                 :body    (if-let [index-file (index-file-exists? files)]
                            (slurp index-file)
                            (format (str "<!doctype html><meta charset=\"utf-8\">"
                                         "<body><h1>Directory listing</h1><hr>"
                                         "<ul>%s</ul></body>")
                                    (apply str (map list-item files))))}))))

        ;;
        ;; Handlers
        ;;
        (defn resources [req]
          (ring.middleware.resource/resource-request req ""))

        (def directories-and-resources
          (when ~dir
            (-> index
              (ring.middleware.file/wrap-file ~dir {:index-files? false})
              (ring.middleware.resource/wrap-resource ""))))

        (def handler
          "If DIR present, serve directories and resources; otherwise just resources"
          (if ~dir
            directories-and-resources
            resources))

        ;;
        ;; Jetty
        ;;
        (def server
          (run-jetty (-> handler
                       (ring.middleware.content-type/wrap-content-type)
                       (ring.middleware.not-modified/wrap-not-modified))
                     {:port ~port :join? false})))

      (util/info "<< started Jetty on http://localhost:%d >>\n" port)
      fileset)))

