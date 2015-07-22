(set-env!
 :source-paths #{"src" "test"}
 :dependencies '[[org.clojure/clojure     "1.6.0" :scope "provided"]
                 [boot/core               "2.1.2" :scope "provided"]
                 [adzerk/bootlaces        "0.1.9" :scope "test"]
                 [adzerk/boot-test        "1.0.3" :scope "test"]
                 [ring/ring-jetty-adapter "1.3.2" :scope "test"]
                 [ring/ring-core          "1.3.2" :scope "test"]
                 [ring/ring-devel         "1.3.2" :scope "test"]])

(require
 '[adzerk.bootlaces :refer :all] ;; tasks: build-jar push-snapshot push-release
 '[adzerk.boot-test :refer :all]
 '[pandeiro.boot-http :refer :all])

(def +version+ "0.6.3")

(bootlaces! +version+)

(task-options!
 pom {:project     'pandeiro/boot-http
      :version     +version+
      :description "Boot task to serve HTTP."
      :url         "https://github.com/pandeiro/boot-http"
      :scm         {:url "https://github.com/pandeiro/boot-http"}
      :license     {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}})
