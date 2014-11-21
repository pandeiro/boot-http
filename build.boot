(set-env!
 :src-paths    #{"src"}
 :dependencies '[[org.clojure/clojure       "1.6.0"       :scope "provided"]
                 [boot/core                 "2.0.0-pre9"  :scope "provided"]
                 [tailrecursion/boot-useful "0.1.3"       :scope "test"]])

(require
 '[tailrecursion.boot-useful :refer :all]
 '[boot.pod                  :as pod]
 '[boot.util                 :as util]
 '[boot.core                 :as core])

(def +version+ "0.1.0")

(useful! +version+)

(task-options!
 pom [:project 'pandeiro/boot-http
      :version +version+
      :description "Boot task to serve a directory over HTTP."
      :url         "https://github.com/pandeiro/boot-http"
      :scm         {:url "https://github.com/pandeiro/boot-http"}
      :license     {:name "Eclipse Public License"
                    :url  "http://www.eclipse.org/legal/epl-v10.html"}])


