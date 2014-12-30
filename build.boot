(set-env!
 :source-paths #{"src"}
 :dependencies '[[org.clojure/clojure "1.6.0"     :scope "provided"]
                 [boot/core           "2.0.0-rc1" :scope "provided"]
                 [adzerk/bootlaces    "0.1.5"     :scope "test"]])

(require
 '[adzerk.bootlaces :refer :all]
 '[boot.pod         :as pod]
 '[boot.util        :as util]
 '[boot.core        :as core])

(def +version+ "0.3.0")

(bootlaces! +version+)

(task-options!
 pom {:project 'pandeiro/boot-http
      :version +version+
      :description "Boot task to serve a directory over HTTP."
      :url         "https://github.com/pandeiro/boot-http"
      :scm         {:url "https://github.com/pandeiro/boot-http"}
      :license     {:name "Eclipse Public License"
                    :url  "http://www.eclipse.org/legal/epl-v10.html"}})

(require '[pandeiro.http :refer :all])

