(ns pandeiro.boot-http.impl
  (:import  [java.net URLDecoder])
  (:require [clojure.java.io :as io]
            [clojure.string  :as s]
            [ring.util.response :refer [resource-response content-type]]
            [ring.middleware
             [file :refer [wrap-file]]
             [resource :refer [wrap-resource]]
             [content-type :refer [wrap-content-type]]
             [not-modified :refer [wrap-not-modified]]]
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
(defn resolve-ring-handler [{:keys [handler]}]
  (when handler
    (require (symbol (namespace handler)) :reload)
    (resolve handler)))

(defn dir-handler [{:keys [dir]}]
  (when dir
    (-> (index-for dir)
      (wrap-file dir {:index-files? false})
      (wrap-resource ""))))

(defn resources-handler [{:keys [resource-root]
                          :or {resource-root ""}}]
  (-> (fn [{:keys [request-method uri] :as req}]
        (if (and (= request-method :get) (= uri "/"))
          (some-> (resource-response "index.html" {:root resource-root})
                  (content-type "text/html"))))
      (wrap-resource resource-root)))

;;
;; Jetty
;;
(defn server [{:keys [port] :as opts}]
  (let [handler (or (resolve-ring-handler opts)
                    (dir-handler opts)
                    (resources-handler opts))]
    (run-jetty (-> handler
                   (wrap-content-type)
                   (wrap-not-modified))
               {:port port :join? false})))
