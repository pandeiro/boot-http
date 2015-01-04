(ns pandeiro.boot-http.impl
  (:import  [java.net URLDecoder])
  (:require [clojure.java.io :as io]
            [clojure.string  :as s]
            [ring.middleware file resource content-type not-modified]
            [ring.adapter.jetty :refer [run-jetty]]))

;;
;; Directory serving
;;
(def index-files #{"index.html" "index.htm"})

(defn index-file-exists? [files]
  (first (filter #(index-files (s/lower-case (.getName %))) files)))

(defn path-diff [root-path path]
  (s/replace path (re-pattern (str "^" root-path)) ""))

(defn filepath-from-uri [root-path uri]
  (str root-path (URLDecoder/decode uri "UTF-8")))

(defn list-item [root-path]
  (fn [file]
    (format "<li><a href=\"%s\">%s</a></li>"
            (path-diff root-path (.getPath file))
            (.getName file))))

(defn index-for [dir]
  (let [root-path (or (.getPath (io/file dir)) "")]
    (fn [{:keys [uri] :as req}]
      (let [directory (io/file (filepath-from-uri root-path uri))]
        (when (.exists directory)
          (let [files (sort (.listFiles directory))]
            {:status  200
             :headers {"Content-Type" "text/html"}
             :body    (if-let [index-file (index-file-exists? files)]
                        (slurp index-file)
                        (format (str "<!doctype html><meta charset=\"utf-8\">"
                                     "<body><h1>Directory listing</h1><hr>"
                                     "<ul>%s</ul></body>")
                                (apply str (map (list-item root-path) files))))}))))))

;;
;; Handlers
;;
(defn resources [req]
  (ring.middleware.resource/resource-request req ""))

(defn directories-and-resources [dir]
  (-> (index-for dir)
    (ring.middleware.file/wrap-file dir {:index-files? false})
    (ring.middleware.resource/wrap-resource "")))

;;
;; Jetty
;;
(defn server [{:keys [dir port handler]}]
  (let [handler (if handler
                  (do
                    (require (symbol (namespace handler)) :reload)
                    (resolve handler))
                  (if dir
                    (directories-and-resources dir)
                    resources))]
    (run-jetty (-> handler
                 (ring.middleware.content-type/wrap-content-type)
                 (ring.middleware.not-modified/wrap-not-modified))
               {:port port :join? false})))
