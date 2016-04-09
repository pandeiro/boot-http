(ns pandeiro.boot-http-tests
  (:require
   [clojure.test :refer :all]
   [clojure.java.io :as io]
   [peridot.core :refer [session request]]
   [pandeiro.boot-http.impl :refer :all]
   [pandeiro.boot-http.util :as u]))

(deftest detect-index-files
  (let [index-html  (io/file "index.html")
        index-htm   (io/file "index.htm")
        INDEX-HTML  (io/file "INDEX.HTML")
        index-shtml (io/file "index.shtml")]
    (testing "recognize index files"
      (is (= index-html (index-file-exists? [index-html])))
      (is (= index-htm  (index-file-exists? [index-htm])))
      (is (= INDEX-HTML (index-file-exists? [INDEX-HTML]))))
    (testing "ignore non-index files"
      (is (nil? (index-file-exists? [index-shtml]))))))

(deftest resolve-symbols
  (testing "can resolve ns-qualified symbols"
    (is (= "sample"
           @(u/resolve-sym 'pandeiro.boot-http-tests-sample/sample-var))))
  (testing "can invoke ns-qualified functions"
    (is (= :sample
           (u/resolve-and-invoke 'pandeiro.boot-http-tests-sample/sample-fn)))))

(def body
  (comp :body :response))

(def slurp-body
  (comp slurp body))

(deftest builtin-handlers
  (testing "default resources handler returns index.html resource"
    (let [req (-> (session (resources-handler {}))
                  (request "/"))]
      (is (= (slurp-body req)
             (slurp (io/resource "index.html"))))))
  (testing "default directory handler returns index.html file"
    (let [dir "test-extra/directory"
          req (-> (session (dir-handler {:dir dir}))
                  (request "/"))]
      (is (= (body req)
             (slurp (io/file (str dir "/index.html"))))))))

(defn teapot-app [request]
  {:status 418})

(deftest not-found-handler-test
  (let [req {:uri "/missing"}]
    (testing "is called when serving a directory"
      (is (= 404
             (:status ((dir-handler {:dir "public"}) req))))
      (is (= 418
             (:status ((dir-handler {:dir "public" :not-found `teapot-app}) req)))))
    (testing "is called when serving a resource"
      (is (= 404
             (:status ((resources-handler {}) req))))
      (is (= 418
             (:status ((resources-handler {:not-found `teapot-app}) req)))))))
