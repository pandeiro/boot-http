(set-env!
 :source-paths #{"src" "test"}
 :dev-dependencies '[[peridot "0.4.3"]]
 :dependencies     '[[org.clojure/clojure "1.7.0"]
                     [adzerk/bootlaces    "0.1.12" :scope "test"]
                     [adzerk/boot-test    "1.0.4"  :scope "test"]])

(require
 '[adzerk.bootlaces :refer :all] ;; tasks: build-jar push-snapshot push-release
 '[adzerk.boot-test :refer :all]
 '[pandeiro.boot-http :refer :all])

(def +version+ "0.7.3")

(bootlaces! +version+)

(task-options!
 pom {:project     'pandeiro/boot-http
      :version     +version+
      :description "Boot task to serve HTTP."
      :url         "https://github.com/pandeiro/boot-http"
      :scm         {:url "https://github.com/pandeiro/boot-http"}
      :license     {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask test-boot-http []
  (merge-env!
   :dependencies (concat (get-env :dev-dependencies) serve-deps)
   :resource-paths #{"test-extra/resources"})
  (test :namespaces #{'pandeiro.boot-http-tests}))
