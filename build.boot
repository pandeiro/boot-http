(set-env!
 :source-paths #{"src" "test"}
 :dependencies '[[org.clojure/clojure     "1.6.0"     :scope "provided"]
                 [boot/core               "2.0.0-rc6" :scope "provided"]
                 [adzerk/bootlaces        "0.1.8"     :scope "test"]
                 [adzerk/boot-test        "1.0.3"     :scope "test"]
                 [ring/ring-jetty-adapter "1.3.1"     :scope "test"]
                 [ring/ring-core          "1.3.1"     :scope "test"]])

(require
 '[adzerk.bootlaces :refer :all] ;; tasks: build-jar push-snapshot push-release
 '[adzerk.boot-test :refer :all]
 '[pandeiro.boot-http :refer :all])

(def +version+ "0.4.2")
(bootlaces! +version+)

(task-options!
 pom {:project     'pandeiro/boot-http
      :version     +version+
      :description "Boot task to serve HTTP."
      :url         "https://github.com/pandeiro/boot-http"
      :scm         {:url "https://github.com/pandeiro/boot-http"}
      :license     {:name "Eclipse Public License"
                    :url  "http://www.eclipse.org/legal/epl-v10.html"}})
